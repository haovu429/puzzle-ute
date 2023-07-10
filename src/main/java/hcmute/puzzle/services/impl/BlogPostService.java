package hcmute.puzzle.services.impl;

import hcmute.puzzle.configuration.security.CustomUserDetails;
import hcmute.puzzle.exception.*;
import hcmute.puzzle.infrastructure.dtos.olds.BlogPostDto;
import hcmute.puzzle.infrastructure.dtos.olds.CommentDto;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostFilterRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostRequest;
import hcmute.puzzle.infrastructure.dtos.request.BlogPostUpdateRequest;
import hcmute.puzzle.infrastructure.dtos.response.BlogCategoryDto;
import hcmute.puzzle.infrastructure.dtos.response.DataResponse;
import hcmute.puzzle.infrastructure.entities.*;
import hcmute.puzzle.infrastructure.mappers.BlogCategoryMapper;
import hcmute.puzzle.infrastructure.mappers.BlogPostMapper;
import hcmute.puzzle.infrastructure.models.BlogCategoryResponseWithBlogPostAmount;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.models.enums.FileType;
import hcmute.puzzle.infrastructure.repository.BlogCategoryRepository;
import hcmute.puzzle.infrastructure.repository.BlogPostRepository;
import hcmute.puzzle.infrastructure.repository.CategoryRepository;
import hcmute.puzzle.infrastructure.repository.FileRepository;
import hcmute.puzzle.services.FilesStorageService;
import hcmute.puzzle.utils.Constant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BlogPostService {


	@Autowired
	BlogPostRepository blogPostRepository;

	@Autowired
	FileRepository fileRepository;

	@Autowired
	FilesStorageService filesStorageService;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	BlogPostMapper blogPostMapper;

	@Autowired
	BlogCategoryMapper blogCategoryMapper;

	@Autowired
	BlogCategoryRepository blogCategoryRepository;

	@Autowired
	CommentService commentService;

	public DataResponse<BlogPostDto> createBlogPost(BlogPostRequest blogPostRequest) {

		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();
		User author = customUserDetails.getUser();

		BlogCategory blogCategory = blogCategoryRepository.findById(blogPostRequest.getCategoryId())
														  .orElseThrow(() -> new NotFoundDataException(
																  "Not found blog category"));

		BlogPost blogPost = BlogPost.builder()
									.title(blogPostRequest.getTitle())
									.body(blogPostRequest.getBody())
									.tags(blogPostRequest.getTags())
									.blogCategory(blogCategory)
									.author(author)
									.build();
		blogPost = blogPostRepository.save(blogPost);
		//BlogPostMapper.INSTANCE.blogPostDtoToBlogPost(dto);

		String publicName = String.format("%s_%s", blogPost.getId(), Constant.SUFFIX_BLOG_POST_THUMBNAIL);
		String imageUrl = filesStorageService.uploadFileWithFileTypeReturnUrl(publicName,
																			  blogPostRequest.getThumbnail(),
																			  FileType.IMAGE,
																			  FileCategory.THUMBNAIL_BLOGPOST, true)
											 .orElseThrow(() -> new FileStorageException("UPLOAD_THUMBNAIL_FAIL"));
		blogPost.setThumbnail(imageUrl);
		// blogPostRepository.save(blogPost);

		blogPost = blogPostRepository.save(blogPost);
		// process file (update blog id for file saved image)
		List<String> imageUrls = null;
		List<File> savedImages = null;

		imageUrls = detectedImageSrcList(blogPost.getBody());

		if (imageUrls != null && !imageUrls.isEmpty()) {
			savedImages = fileRepository.findAllByUrlInAndDeletedIs(imageUrls, false);
		}

		if (savedImages != null && !savedImages.isEmpty()) {
			BlogPost finalBlogPost = blogPost;
			savedImages.forEach(image -> image.setObjectId(finalBlogPost.getId()));
			fileRepository.saveAll(savedImages);
		}
		BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(blogPost);

		return new DataResponse<>(blogPostDto);
	}

	private void resolveBlogPostDtoWithRightEditOfCommentSubComment(BlogPostDto blogPostDto, long currentUserId) {
		blogPostDto.getComments().forEach(commentDto -> {
			commentDto.getSubComments().forEach(subCommentDto -> {
				if (Objects.equals(subCommentDto.getUserId(), currentUserId)) {
					subCommentDto.setCanEdit(true);
				} else {
					subCommentDto.setCanEdit(false);
				}
			});
			if (Objects.equals(commentDto.getUserId(), currentUserId)) {
				commentDto.setCanEdit(true);
			} else {
				commentDto.setCanEdit(false);
			}
		});
		//    List<CommentDto> commentDtos = blogPostDto.getComments();
		//    if (commentDtos != null && !commentDtos.isEmpty()) {
		//      commentDtos.forEach(commentDto -> {
		//                commentDto.getSubComments()
		//                        .forEach(subCommentDto -> {
		//                                  if (Objects.equals(subCommentDto.getUserId(), currentUserId)) {
		//                                    subCommentDto.setCanEdit(true);
		//                                  } else {
		//                                    subCommentDto.setCanEdit(false);
		//                                  }
		//                                }
		//                        );
		//                if (Objects.equals(commentDto.getUserId(), currentUserId)) {
		//                  commentDto.setCanEdit(true);
		//                } else {
		//                  commentDto.setCanEdit(false);
		//                }
		//              }
		//      );
		//    }
		//    blogPostDto.setComments(commentDtos);
	}

	
	public DataResponse<BlogPostDto> update(BlogPostUpdateRequest blogPostUpdateRequest, long id) {
		BlogPost blogPost = blogPostRepository.findById(id)
											  .orElseThrow(() -> new CustomException(
													  "You don't have rights for this blog post"));

		if (Objects.nonNull(blogPostUpdateRequest.getBlogCategoryId())) {
			BlogCategory blogCategory = blogCategoryRepository.findById(blogPostUpdateRequest.getBlogCategoryId())
															  .orElseThrow(() -> new NotFoundDataException(
																	  "not found blog category"));
			blogPost.setBlogCategory(blogCategory);
		}

		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext()
																					   .getAuthentication()
																					   .getPrincipal();
		User user = customUserDetails.getUser();
		if (user.getId() != blogPost.getAuthor().getId()) {
			throw new UnauthorizedException("UNAUTHORIZED FOR THIS BLOG POST");
		}
		if (blogPost.getBody() != null && blogPostUpdateRequest.getBody() != null) {
			try {
				List<String> oldSrcs = detectedImageSrcList(blogPost.getBody());
				List<String> newSrcs = detectedImageSrcList(blogPostUpdateRequest.getBody());

				// update blog post id for new saved file in db
				List<String> addedSrcs = getUrlOfFirstListWhichSecondListNotContain(newSrcs, oldSrcs);
				updateBlogPostIdForSavedFile(addedSrcs, blogPost.getId());

				// release file don't use
				List<String> urlToDelete = getUrlOfFirstListWhichSecondListNotContain(oldSrcs, newSrcs);
				deleteMultiFileByUrlList(urlToDelete);

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		blogPostMapper.updateBlogPostFromBlogPostRequestWithoutThumbnail(blogPostUpdateRequest, blogPost);
		if (blogPostUpdateRequest.getBlogCategoryId() != null) {
			BlogCategory blogCategory = blogCategoryRepository.findById(blogPostUpdateRequest.getBlogCategoryId())
															  .orElseThrow(() -> new NotFoundDataException(
																	  "Not found blog category"));
			blogPost.setBlogCategory(blogCategory);
		}
		blogPostRepository.save(blogPost);

		if (blogPostUpdateRequest.getThumbnail() != null) {
			String publicName = String.format("%s_%s", blogPost.getId(), Constant.SUFFIX_BLOG_POST_THUMBNAIL);
			String imageUrl = filesStorageService.uploadFileWithFileTypeReturnUrl(publicName,
																				  blogPostUpdateRequest.getThumbnail(),
																				  FileType.IMAGE,
																				  FileCategory.THUMBNAIL_BLOGPOST, true)
												 .orElseThrow(() -> new FileStorageException("UPLOAD_THUMBNAIL_FAIL"));
			blogPost.setThumbnail(imageUrl);
		}
		BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(blogPost);
		resolveBlogPostDtoWithRightEditOfCommentSubComment(blogPostDto, user.getId());
		return new DataResponse<>(blogPostDto);

	}

	private void updateBlogPostIdForSavedFile(List<String> fileSrcs, long blogPostId) {
		List<File> savedImages = null;

		//get current user
		User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext()
																	 .getAuthentication()
																	 .getPrincipal()).getUser();

		if (fileSrcs != null && !fileSrcs.isEmpty()) {
			savedImages = fileRepository.findAllByUrlInAndDeletedIs(fileSrcs, false);
		}
		if (savedImages != null && !savedImages.isEmpty()) {
			savedImages.forEach(image -> {
				image.setObjectId(blogPostId);
				image.setUpdatedBy(currentUser.getEmail());
				image.setUpdatedAt(new Date());
			});
			fileRepository.saveAll(savedImages);
		}
	}

	public void deleteMultiFileByUrlList(List<String> urls) {
		//get current user
		User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext()
																	 .getAuthentication()
																	 .getPrincipal()).getUser();

		List<String> publicIdsToDelete = fileRepository.findAllByUrlInAndDeletedIs(urls, false)
													   .stream()
													   .map(File::getCloudinaryPublicId)
													   .collect(Collectors.toList());
		if (!publicIdsToDelete.isEmpty()) filesStorageService.deleteMultiFile(publicIdsToDelete, currentUser);
	}

	
	public DataResponse<String> delete(long id) {
		BlogPost blogPost = blogPostRepository.findById(id)
											  .orElseThrow(() -> new NotFoundException("NOT_FOUND_BLOG_POST"));
		try {
			List<String> imageSrcs = detectedImageSrcList(blogPost.getBody());

			deleteMultiFileByUrlList(imageSrcs);
		} catch (Exception e) {
			//throw new FileStorageException("DO_NOT_DELETE_FILE_OF_BLOG_POST");
		}
		blogPostRepository.delete(blogPost);
		return new DataResponse<>("Success");
	}

	
	public DataResponse<List<BlogPostDto>> getAll() {

		User currentUser;
		if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext()
																								   .getAuthentication()
																								   .getPrincipal() instanceof CustomUserDetails) {
			currentUser = ((CustomUserDetails) SecurityContextHolder.getContext()
																	.getAuthentication()
																	.getPrincipal()).getUser();
		} else {
			currentUser = null;
		}

		List<BlogPostDto> dtos = blogPostRepository.findAll().stream().map(entity -> {
			BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(entity);// converter.toDTO(entity);
			//blogPostDTO.setBody(null);
			if (currentUser != null) {
				resolveBlogPostDtoWithRightEditOfCommentSubComment(blogPostDto, currentUser.getId());
			}
			return blogPostDto;
		}).collect(Collectors.toList());
		return new DataResponse<>(dtos);
	}

	
	public DataResponse<BlogPostDto> getOneById(long id) {
		BlogPostDto blogPostDto = blogPostMapper.blogPostToBlogPostDto(
				blogPostRepository.findById(id).orElseThrow(() -> new NotFoundException("NOT_FOUND_BLOG_POST")));
		List<CommentDto> commentDtos = commentService.getAllByBlogPostId(blogPostDto.getId());
		blogPostDto.setComments(commentDtos);
		return new DataResponse<>(blogPostDto);
	}

	
	public List<BlogPostDto> getBlogPostByUser(long userId) {
		List<BlogPost> blogPosts = blogPostRepository.findAllByAuthorId(userId);
		List<BlogPostDto> blogPostDtos = new ArrayList<>();
		if (blogPosts != null && !blogPosts.isEmpty()) {
			blogPostDtos = blogPosts.stream().map(entity -> blogPostMapper.blogPostToBlogPostDto(entity)).toList();
		}
		return blogPostDtos;
	}

	public List<String> getUrlOfFirstListWhichSecondListNotContain(List<String> firstList, List<String> secondList) {
		// getDeletedBlogImageUrl
		return firstList.stream().filter(item -> !secondList.contains(item)).collect(Collectors.toList());
	}

	public List<String> detectedImageSrcList(String html) {
		if (html == null) {
			throw new NullPointerException("CONTENT DETECT IMAGE URL IS NULL");
		}
		List<String> imageSrcList = new ArrayList<>();
		final String regex = "<img.*?src=[\"|'](.*?)[\"|']";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			// System.out.println("Full match: " + matcher.group(0));

			for (int i = 1; i <= matcher.groupCount(); i++) {
				imageSrcList.add(matcher.group(i));
				// System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}

		return imageSrcList;
	}

	public List<BlogCategoryResponseWithBlogPostAmount> getBlogCategoryWithBlogPostAmount() {
      List<BlogCategoryResponseWithBlogPostAmount> blogCateWithPostAmounts = new ArrayList<>();
		List<Object[]> rows = blogCategoryRepository.getBlogCategoriesWithAmountBlogPost();
		//    List<Map<String, Object>> response = new ArrayList<>();
		for (Object[] row : rows) {
			BlogCategory blogCategory = (BlogCategory) row[0];
			BlogCategoryDto blogCategoryDto = new BlogCategoryDto();
			if (blogCategory != null) {
				blogCategoryDto = blogCategoryMapper.blogCategoryToBlogCategoryDto(blogCategory);
			}
			Long blogPostAmount = (Long) row[1];
			BlogCategoryResponseWithBlogPostAmount blogCategoryResponseWithBlogPostAmount = BlogCategoryResponseWithBlogPostAmount.builder()
																																  .blogCategory(
																																		  blogCategoryDto)
																																  .blogPostAmount(
																																		  blogPostAmount)
																																  .build();
            blogCateWithPostAmounts.add(blogCategoryResponseWithBlogPostAmount);
		}
		return blogCateWithPostAmounts;
	}

	public Page<BlogPost> filterBlogPost(BlogPostFilterRequest blogPostFilterRequest, Pageable pageable) {
		Specification<BlogPost> blogPostSpecification = doPredicate(blogPostFilterRequest);
		Page<BlogPost> blogPosts = blogPostRepository.findAll(blogPostSpecification, pageable);
		return blogPosts;
	}

	private Specification<BlogPost> doPredicate(BlogPostFilterRequest blogPostFilter) {

		return ((root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new LinkedList<>();

			// Is Active
			if (blogPostFilter.getIsActive() != null) {
				Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isActive"),
																			blogPostFilter.getIsActive());
				predicates.add(withCheckActiveFromSystem);
			}

			// Is Public
			if (blogPostFilter.getIsActive() != null) {
				Predicate withCheckActiveFromSystem = criteriaBuilder.equal(root.get("isPublic"),
																			blogPostFilter.getIsPublic());
				predicates.add(withCheckActiveFromSystem);
			}

			// Category
			if (blogPostFilter.getCategoryId() != null) {
				Join<BlogPost, BlogCategory> categoryJoin = root.join("blogCategory", JoinType.INNER);
				predicates.add(criteriaBuilder.equal(categoryJoin.get("id"), blogPostFilter.getCategoryId()));
			}

			// Search Key
			if (blogPostFilter.getSearchKey() != null && !blogPostFilter.getSearchKey().isEmpty()) {
				List<Predicate> or = new ArrayList<>();
				String keyword = blogPostFilter.getSearchKey();
				Predicate orInTitle = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
														   "%" + keyword + "%");
				Predicate orInDescription = criteriaBuilder.like(criteriaBuilder.lower(root.get("body")),
																 "%" + keyword + "%");
				Predicate orInTags = criteriaBuilder.like(criteriaBuilder.lower(root.get("tags")), "%" + keyword + "%");
				or.add(orInTitle);
				or.add(orInDescription);
				or.add(orInTags);

				predicates.add(criteriaBuilder.or(or.toArray(new Predicate[0])));
			}

			//Create Time
			if (blogPostFilter.getCreatedAtFrom() != null) {
				Timestamp tsCreatedAtFrom = new Timestamp(blogPostFilter.getCreatedAtFrom().getTime());
				Predicate withCreatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),
																				   tsCreatedAtFrom);
				predicates.add(criteriaBuilder.and(withCreatedAtFrom));
			}

			if (blogPostFilter.getCreatedAtTo() != null) {
				Timestamp tsCreatedAtTo = new Timestamp(blogPostFilter.getCreatedAtTo().getTime());
				Predicate withCreatedAtTo = criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), tsCreatedAtTo);
				predicates.add(criteriaBuilder.and(withCreatedAtTo));
			}

			// Update time
			if (blogPostFilter.getUpdatedAtFrom() != null) {
				Timestamp tsUpdatedAtFrom = new Timestamp(blogPostFilter.getUpdatedAtFrom().getTime());
				Predicate withUpdatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"),
																				   tsUpdatedAtFrom);
				predicates.add(criteriaBuilder.and(withUpdatedAtFrom));
			}

			if (blogPostFilter.getUpdatedAtTo() != null) {
				Timestamp tsUpdatedAtTo = new Timestamp(blogPostFilter.getUpdatedAtTo().getTime());
				Predicate withUpdatedAtTo = criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), tsUpdatedAtTo);
				predicates.add(criteriaBuilder.and(withUpdatedAtTo));
			}

			// Sort
			if (Objects.nonNull(blogPostFilter.getIsAscSort()) && blogPostFilter.getIsAscSort()
																				.equals(true) && blogPostFilter.getSortColumn() != null && !blogPostFilter.getSortColumn()
																																						  .isBlank()) {
				switch (blogPostFilter.getSortColumn()) {
					case "createdAt":
						blogPostFilter.setSortColumn("createdAt");
						query.orderBy(criteriaBuilder.asc(root.get(blogPostFilter.getSortColumn())));
						break;
					case "updatedAt":
						blogPostFilter.setSortColumn("updatedAt");
						query.orderBy(criteriaBuilder.asc(root.get(blogPostFilter.getSortColumn())));
						break;
					default:
						query.orderBy(criteriaBuilder.asc(root.get(blogPostFilter.getSortColumn())));
				}
			} else if (Objects.nonNull(blogPostFilter.getIsAscSort()) && blogPostFilter.getIsAscSort()
																					   .equals(false) && blogPostFilter.getSortColumn() != null && !blogPostFilter.getSortColumn()
																																								 .isBlank()) {
				switch (blogPostFilter.getSortColumn()) {
					case "createdAt":
						blogPostFilter.setSortColumn("createdAt");
						query.orderBy(criteriaBuilder.desc(root.get(blogPostFilter.getSortColumn())));
						break;
					case "updatedAt":
						blogPostFilter.setSortColumn("updatedAt");
						query.orderBy(criteriaBuilder.desc(root.get(blogPostFilter.getSortColumn())));
						break;
					default:
						query.orderBy(criteriaBuilder.desc(root.get(blogPostFilter.getSortColumn())));
				}
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
		});
	}
}

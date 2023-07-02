package hcmute.puzzle.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.*;
import com.google.common.io.Files;
import hcmute.puzzle.exception.NotFoundDataException;
import hcmute.puzzle.infrastructure.entities.FileType;
import hcmute.puzzle.infrastructure.models.enums.FileCategory;
import hcmute.puzzle.infrastructure.repository.FileRepository;
import hcmute.puzzle.infrastructure.repository.FileTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-s3-buckets.html
@Slf4j
@Service
public class AmazoneBucketService {

	@Autowired
	FileTypeRepository fileTypeRepository;

	@Autowired
	FileRepository fileRepository;

	private AmazonS3 s3 = null;

//	private S3Client s3Client;

	@Value("${aws.accessKeyId}")
	private String accessKey;

	@Value("${aws.secretAccessKey}")
	private String secretKey;

	@Value("${aws.bucketName}")
	private String bucketName;

	@Value("${aws.endPoint}")
	private String endPoint;



	public AmazonS3 getS3() {
		if (this.s3 != null) {
			return this.s3;
		}

		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		this.s3 = new AmazonS3Client(credentials);
		this.s3.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
		this.s3.setEndpoint(this.endPoint);

		// https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
		//		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
		//										   .withCredentials(new EnvironmentVariableCredentialsProvider())
		//										   .build();
		return s3;
	}

//	public S3Client getS3Client() {
//		if (s3Client != null) {
//			return this.s3Client;
//		}
//		AwsBasicCredentials basicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
//
//		S3Client s3Client = S3Client.builder()
//									.region(Region.US_EAST_1)
//									.credentialsProvider(() -> basicCredentials)
//									.build();
//		return s3Client;
//	}

	public Bucket getBucket(String bucket_name) {
		//final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
		AmazonS3 s3 = this.getS3();
		Bucket named_bucket = null;
		List<Bucket> buckets = s3.listBuckets();
		for (Bucket b : buckets) {
			if (b.getName().equals(bucket_name)) {
				named_bucket = b;
			}
		}
		return named_bucket;
	}

	// https://www.baeldung.com/spring-multipartfile-to-file
	public PutObjectResult uploadObject(String filePath, String bucketName, String keyName) {
		PutObjectResult putObjectResult = null;
		String mess = String.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
		log.info(mess);
		//final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
		AmazonS3 s3 = this.getS3();
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("application/pdf");
			PutObjectRequest request = new PutObjectRequest(bucketName, keyName, new File(filePath));
			request.withBucketName(bucketName).withCannedAcl(CannedAccessControlList.PublicRead);
			putObjectResult = s3.putObject(request);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
		return putObjectResult;
	}

	public String uploadObjectFromInputStream(MultipartFile multipartFile, FileCategory fileCategory, boolean saveDB) {
		return uploadObjectFromInputStream(multipartFile, this.bucketName, fileCategory, saveDB);
	}

	public String uploadObjectFromInputStream(MultipartFile multipartFile, String bucketName, FileCategory fileCategory,
			boolean saveDB) {
		PutObjectResult putObjectResult = null;
		String objectUrl = null;
		//String mess = String.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
		//log.info(mess);
		//final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
		AmazonS3 s3 = this.getS3();

		FileType fileType = fileTypeRepository.findByCategory(fileCategory)
											  .orElseThrow(
													  () -> new NotFoundDataException("Not found file type category"));
		try {
			String contentType = multipartFile.getContentType();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			InputStream inputStream = multipartFile.getInputStream();
			String keyName = fileType.getLocation()
									 .concat("/")
									 .concat(this.processFileName(
											 Objects.requireNonNull(multipartFile.getOriginalFilename())))
									 .concat(".pdf");
			PutObjectRequest request = new PutObjectRequest(bucketName, keyName, inputStream, metadata);
			request.withBucketName(bucketName).withCannedAcl(CannedAccessControlList.PublicRead);
			putObjectResult = s3.putObject(request);

			//S3Client client = this.getS3Client();

			objectUrl = ((AmazonS3Client) s3).getResourceUrl(bucketName, keyName);
			//System.out.println("URL của tệp tin: " + objectUrl);
			//			s3.getObject()
			//			s3Client.getResourceUrl("your-bucket", "some-path/some-key.jpg");

			if (saveDB) {

				// Save file info to db.
				hcmute.puzzle.infrastructure.entities.File fileEntity = hcmute.puzzle.infrastructure.entities.File.builder()
																												  .name(multipartFile.getOriginalFilename()
																																	 .replace(
																																			 " ",
																																			 ""))
																												  .category(
																														  FileCategory.PDF_CV.getValue())
																												  .type(fileType.getType()
																																.getValue())
																												  .location(
																														  fileType.getLocation())
																												  .extension(
																														  Files.getFileExtension(
																																  Objects.requireNonNull(
																																		  multipartFile.getOriginalFilename())))
																												  .url(objectUrl)
																												  .s3BucketKeyName(
																														  keyName)
																												  .provider(
																														  "AWS")
																												  .build();
				fileRepository.save(fileEntity);
			}
		} catch (AmazonServiceException e) {
			log.error(e.getErrorMessage(), e);
			//System.err.println(e.getErrorMessage());
			//System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return objectUrl;
	}

	public List<S3ObjectSummary> listBucketObjects(String bucketName) {
		AmazonS3 s3 = this.getS3();
		System.out.format("Objects in S3 bucket %s:\n", bucketName);
		//final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
		ListObjectsV2Result result = s3.listObjectsV2(bucketName);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		//		for (S3ObjectSummary os : objects) {
		//			System.out.println("* " + os.getKey());
		//		}
		return objects;
	}

	public String processFileName(String keyValue) {
		String pattern = "yyyy_MM_dd-HH_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		// System.out.println(date);
		return keyValue.concat(date).concat("_cv");
	}
}

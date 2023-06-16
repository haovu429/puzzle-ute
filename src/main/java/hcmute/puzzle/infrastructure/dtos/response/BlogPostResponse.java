package hcmute.puzzle.infrastructure.dtos.response;

import java.util.Date;
import java.util.List;

public class BlogPostResponse {
	private long id;
	private String title;
	private String body;
	private String thumbnail;
	private Date createTime;
	private Date lastUpdate;
	private String category;
	private List<String> tags;
	private long userId;
}

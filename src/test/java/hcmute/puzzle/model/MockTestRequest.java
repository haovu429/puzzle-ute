package hcmute.puzzle.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Data
@Builder
public class MockTestRequest {
	private HttpMethod method;
	private String url;
	private MultiValueMap<String, String> param;
	private String content;
	@Builder.Default
	private MediaType mediaType = MediaType.APPLICATION_JSON;
}

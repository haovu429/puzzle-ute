package hcmute.puzzle.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static com.amazonaws.services.elasticloadbalancingv2.model.ActionTypeEnum.Redirect;

@Configuration
public class WebClientConfiguration {
	private String baseUrl = "https://connect.hirize.hr";

	@Bean
	public WebClient webClient() {
		HttpClient httpClient = HttpClient.create()
										  .followRedirect(false)
										  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
//										  .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(15))
//																	 .addHandlerLast(new WriteTimeoutHandler(15)));
		//UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(queryParams);

		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
		return WebClient.builder()
						//.baseUrl(baseUrl)
						.clientConnector(connector)
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.build();
	}

	//@Bean(name = "hirize")
	public WebClient webClientHirize() {
		HttpClient httpClient = HttpClient.create()
										  .tcpConfiguration(
												  client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
																  .doOnConnected(conn -> conn.addHandlerLast(
																									 new ReadTimeoutHandler(10))
																							 .addHandlerLast(
																									 new WriteTimeoutHandler(
																											 10))));
		//		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		//		queryParams.add("api_key", parserToken);
		//UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParams(queryParams);

		ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
		return WebClient.builder()
						.baseUrl(baseUrl)
						.clientConnector(connector)
						.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
						.build();
	}
}
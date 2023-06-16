package hcmute.puzzle.configuration;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Puzzle API",
                version = "v1" ,
                contact = @Contact(
                        name = "Hao Vu",
                        email = "caihoncuagiamnguc@gmail.com",
                        url = "https://www.youtube.com/channel/UC5GCMUwYXZTMKVc8OBvLbvQ"
                ),
        description = "Puzzle API",
        license = @License(
                name = "License 1",
                url = "https://www.youtube.com/channel/UC5GCMUwYXZTMKVc8OBvLbvQ"
        ),
        termsOfService = "Term of service"),
        servers = {
                @Server(
                        description = "LOCAL ENV",
                        url = "http://localhost:8080/api"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "https://puzzle-ute.herokuapp.com/api"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\""
)

public class OpenApi30Config {
    /**
     * https://loda.me/articles/sb25-restful-api-document-voi-spring-boot-openapi-30
     */
}

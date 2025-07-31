package tz.business.eCard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(title = "Sims API", version = "1.0"),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        }
)
public class SwaggerConfig {
    // Additional configuration if needed
}


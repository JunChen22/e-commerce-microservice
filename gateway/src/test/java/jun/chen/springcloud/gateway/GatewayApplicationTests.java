package jun.chen.springcloud.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
		webEnvironment = RANDOM_PORT,
		properties = {
				"spring.security.oauth2.resourceserver.jwt.jwk-set-uri=some-url",
				"eureka.client.enabled=false"
		}
)
class GatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}

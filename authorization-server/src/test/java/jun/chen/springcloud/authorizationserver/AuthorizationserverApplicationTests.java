package jun.chen.springcloud.authorizationserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
		properties = {
				"eureka.client.enabled=false",
				"spring.cloud.config.enabled=false"
		}
)
@AutoConfigureMockMvc
class AuthorizationserverApplicationTests {

	@Autowired
	MockMvc mvc;

	@Test
	void requestTokenUsingClientCredentialsGrantType() throws Exception {

		this.mvc.perform(post("/oauth2/token")
						.param("grant_type", "client_credentials")
						.header("Authorization", "Basic cmVhZGVyOnNlY3JldA=="))
				.andExpect(status().isOk());
	}

	@Test
	void requestOpenidConfiguration() throws Exception {

		this.mvc.perform(get("/.well-known/openid-configuration"))
				.andExpect(status().isOk());
	}

	@Test
	void requestJwkSet() throws Exception {

		this.mvc.perform(get("/oauth2/jwks"))
				.andExpect(status().isOk());
	}
}

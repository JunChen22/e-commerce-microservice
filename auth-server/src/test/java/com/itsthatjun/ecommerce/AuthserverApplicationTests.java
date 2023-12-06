package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.config.MyBatisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MyBatisConfig.class)
@ActiveProfiles("test")
public class AuthserverApplicationTests extends TestContainerConfig {
    @Test
    void contextLoads() {
    }
}

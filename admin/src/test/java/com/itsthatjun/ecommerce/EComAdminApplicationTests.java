package com.itsthatjun.ecommerce;

import com.itsthatjun.ecommerce.config.MyBatisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(MyBatisConfig.class)
@ActiveProfiles("test")
public class EComAdminApplicationTests extends TestContainerConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDataLoadedFromInitScript() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Integer.class);

        assertThat(count).isEqualTo(3);
    }

    @Test
    void contextLoads() {
    }
}

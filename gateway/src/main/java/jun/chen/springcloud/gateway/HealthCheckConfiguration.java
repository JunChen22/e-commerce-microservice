package jun.chen.springcloud.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.logging.Level.FINE;

public class HealthCheckConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);

    private final WebClient webClient;

    @Autowired
    public HealthCheckConfiguration(WebClient webClient) {
        this.webClient = webClient;
    }

    @Bean
    ReactiveHealthContributor healthcheckMicroservices() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();

        registry.put("cms-article", () -> getHealth("http://cms"));

        registry.put("oms-cart", () -> getHealth("http://oms"));
        registry.put("oms-order", () -> getHealth("http://oms"));
        registry.put("oms-return", () -> getHealth("http://oms"));

        registry.put("pms-brand", () -> getHealth("http://pms"));
        registry.put("pms-product", () -> getHealth("http://pms"));
        registry.put("pms-review", () -> getHealth("http://pms"));

        registry.put("sms-coupon", () -> getHealth("http://sms"));
        registry.put("sms-sale", () -> getHealth("http://sms"));

        registry.put("ums-user", () -> getHealth("http://ums"));

        registry.put("app", () -> getHealth("http://app"));

        registry.put("auth-server", () -> getHealth("http://auth-server"));

        registry.put("notification", () -> getHealth("http://notification"));

        registry.put("search", () -> getHealth("http://search"));

        return CompositeReactiveHealthContributor.fromMap(registry);
    }

    private Mono<Health> getHealth(String baseUrl) {
        String url = baseUrl + "/actuator/health";
        LOG.debug("Setting up a call to the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }
}

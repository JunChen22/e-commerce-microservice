package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/user")
@Api(tags = "User related", description = "retrieve user information")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    @Value("${app.UMS-service.host}")
    String userServiceURL;
    @Value("${app.UMS-service.port}")
    int port;

    @Autowired
    public UserController(WebClient.Builder webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "")
    public Mono<Member> getUser(@PathVariable int userId) {
        String url = "http://" + userServiceURL + ":" + port + "/user/" + userId;

        return webClient.get().uri(url).retrieve().bodyToMono(Member.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "")
    public Flux<Member> listAll() {
        String url = "http://" + userServiceURL + ":" + port + "/user/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Member.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @PostMapping("/activate")
    @ApiOperation(value = "")
    public void activateUser(@RequestBody Member member) {
        int memberId = member.getId();
        Mono.fromRunnable(() -> sendMessage("user-out-0", new UmsAdminUserEvent(ACTIVATE_ACCOUNT, memberId, member)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/deactive")
    @ApiOperation(value = "")
    public void deactivateUser(@RequestBody Member member) {
        int memberId = member.getId();
        Mono.fromRunnable(() -> sendMessage("user-out-0", new UmsAdminUserEvent(DEACTIVE_ACCOUNT, memberId, member)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @ApiOperation(value = "")
    public void update(@RequestBody Member member){
        int memberId = member.getId();
        Mono.fromRunnable(() -> sendMessage("user-out-0", new UmsAdminUserEvent(UPDATE_ACCOUNT_INFO, memberId, member)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{memberId}")
    @ApiOperation(value = "")
    public void delete(@PathVariable int memberId) {
        Mono.fromRunnable(() -> sendMessage("user-out-0", new UmsAdminUserEvent(DELETE_ACCOUNT, memberId, null)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    private void sendMessage(String bindingName, UmsAdminUserEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}

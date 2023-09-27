package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.security.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.ums.UmsUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "User controller", description = "user controller")
@RequestMapping("/user")
public class UserAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(UserAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String UMS_SERVICE_URL = "http://ums/user";

    @Autowired
    public UserAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient, StreamBridge streamBridge,
                         @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/getInfo")
    @ApiOperation(value = "")
    Mono<MemberDetail> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        String url = UMS_SERVICE_URL + "/getInfo";
        LOG.debug("Will call the list API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(MemberDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register")
    public Mono<MemberDetail> register(@RequestBody MemberDetail memberDetail){
        return Mono.fromCallable(() -> {
                sendMessage("user-out-0", new UmsUserEvent(NEW_ACCOUNT, memberDetail));
                return memberDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/updatePassword")
    @ApiOperation(value = "")
    public Mono<Void> updatePassword(@RequestBody String newPassword) {
        Member member = new Member();
        member.setPassword(newPassword);
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.setMember(member);

        return Mono.fromRunnable(() ->
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_PASSWORD, memberDetail))
        ).subscribeOn(publishEventScheduler).then();
    }

    @PostMapping("/updateInfo")
    @ApiOperation(value = "password, name, icon")
    public Mono<MemberDetail> updateInfo(@RequestBody MemberDetail memberDetail) {
        return Mono.fromCallable(() -> {
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_ACCOUNT_INFO, memberDetail));
            return memberDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/updateAddress")
    @ApiOperation(value = "")
    public Mono<Address> updateAddress(@RequestBody Address newAddress) {
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.setAddress(newAddress);

        return Mono.fromCallable(() -> {
            sendMessage("user-out-0", new UmsUserEvent(UPDATE_ADDRESS, memberDetail));
            return newAddress;
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/deleteAccount")
    @ApiOperation(value = "")
    public Mono<Void> deleteAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        Member member = new Member();
        member.setId(userId);
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.setMember(member);

        return Mono.fromRunnable(() ->
                sendMessage("user-out-0", new UmsUserEvent(DELETE_ACCOUNT, memberDetail))
        ).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, UmsUserEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("even type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}

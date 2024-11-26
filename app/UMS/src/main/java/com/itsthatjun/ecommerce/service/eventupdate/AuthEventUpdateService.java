package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import com.itsthatjun.ecommerce.repository.MemberActivityLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthEventUpdateService.class);

    private final MemberActivityLogRepository activityLogRepository;

    @Autowired
    public AuthEventUpdateService(MemberActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Update from Auth server, logic done in Auth server already
     * @param activityLog
     * @return
     */
    public Mono<Void> createActivityLog(MemberActivityLog activityLog) {
        return activityLogRepository.saveLog(activityLog).then();
    }
}

package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.CouponHistoryDao;
import com.itsthatjun.ecommerce.dto.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.mapper.CouponHistoryMapper;
import com.itsthatjun.ecommerce.mbg.mapper.CouponMapper;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.CouponExample;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistoryExample;
import com.itsthatjun.ecommerce.service.CouponHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

@Service
public class CouponHistoryServiceImpl implements CouponHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponHistoryServiceImpl.class);

    private final CouponHistoryMapper couponHistoryMapper;

    private final CouponMapper couponMapper;

    private final CouponHistoryDao historyDao;

    private final Scheduler jdbcScheduler;

    @Autowired
    public CouponHistoryServiceImpl(CouponHistoryMapper couponHistoryMapper, CouponMapper couponMapper, CouponHistoryDao historyDao,
                                    @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.couponHistoryMapper = couponHistoryMapper;
        this.couponMapper = couponMapper;
        this.historyDao = historyDao;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<UsedCouponHistory> getAllCouponUsed() {
        return Mono.fromCallable(() -> {
            List<Coupon> couponList = couponMapper.selectByExample(new CouponExample());
            List<UsedCouponHistory> usedCouponHistoryList = new ArrayList<>();
            for (Coupon coupon : couponList) {
                int couponId = coupon.getId();
                int usageCount = historyDao.couponUserUsageCount(couponId);
                CouponHistoryExample couponHistoryExample = new CouponHistoryExample();
                couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId);
                List<CouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(couponHistoryExample);

                UsedCouponHistory usedCouponHistory = new UsedCouponHistory();
                usedCouponHistory.setCoupon(coupon);
                usedCouponHistory.setUserCount(usageCount);
                usedCouponHistory.setCouponHistoryList(couponHistoryList);
                usedCouponHistoryList.add(usedCouponHistory);
            }
            return usedCouponHistoryList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Flux<CouponHistory> getUserCouponUsage(int userId) {
        return Mono.fromCallable(() -> {
            CouponHistoryExample couponHistoryExample = new CouponHistoryExample();
            couponHistoryExample.createCriteria().andMemberIdEqualTo(userId);
            List<CouponHistory> couponHistory = couponHistoryMapper.selectByExample(couponHistoryExample);
            return couponHistory;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }
}

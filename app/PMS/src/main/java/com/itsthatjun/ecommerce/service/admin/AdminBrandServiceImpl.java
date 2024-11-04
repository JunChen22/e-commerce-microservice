package com.itsthatjun.ecommerce.service.admin;

import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.service.AdminBrandService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdminBrandServiceImpl implements AdminBrandService {
    @Override
    public Flux<Brand> listAllBrand() {
        return null;
    }

    @Override
    public Flux<Brand> listBrand(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public Flux<Product> listAllBrandProduct(int brandId) {
        return null;
    }

    @Override
    public Mono<Brand> getBrand(int id) {
        return null;
    }

    @Override
    public Mono<Brand> createBrand(Brand brand, String operator) {
        return null;
    }

    @Override
    public Mono<Brand> updateBrand(Brand brand, String operator) {
        return null;
    }

    @Override
    public Mono<Void> deleteBrand(int brandId, String operator) {
        return null;
    }

    //
//    @Override
//    public Flux<Brand> listAllBrand() {
//        return null;
//    }
//
//    @Override
//    public Flux<Brand> listBrand(int pageNum, int pageSize) {
//        return null;
//    }
//
//    @Override
//    public Flux<Product> listAllBrandProduct(int brandId) {
//        return null;
//    }
//
//    @Override
//    public Mono<Brand> getBrand(int id) {
//        return null;
//    }
//
//    @Override
//    public Mono<Brand> createBrand(Brand brand, String operator) {
//        return null;
//    }
//
//    @Override
//    public Mono<Brand> updateBrand(Brand brand, String operator) {
//        return null;
//    }
//
//    @Override
//    public Mono<Void> deleteBrand(int brandId, String operator) {
//        return null;
//    }
//
//    @Override
//    public Mono<Brand> adminCreateBrand(Brand brand, String operator) {
//        return Mono.fromCallable(() -> {
//            brandMapper.insert(brand);
//            createUpdateLog(brand.getId(), "update brand", operator);
//            return brand; // Use Mono.justOrEmpty to handle potential null values
//        }).subscribeOn(jdbcScheduler);
//    }
//
//    @Override
//    public Mono<Brand> adminUpdateBrand(Brand brand, String operator) {
//        return Mono.fromCallable(() -> {
//            brandMapper.updateByPrimaryKeySelective(brand);
//            createUpdateLog(brand.getId(), "update brand", operator);
//            return brand; // Use Mono.justOrEmpty to handle potential null values
//        }).subscribeOn(jdbcScheduler);
//    }
//
//    @Override
//    public Mono<Void> adminDeleteBrand(int brandId, String operator) {
//        return Mono.fromRunnable(() -> {
//            Brand brand = brandMapper.selectByPrimaryKey(brandId);
//            if (brand == null) throw new BrandException("brand does not exist: " + brandId);
//
//            createUpdateLog(brandId, "delete brand: " +  brand.getName(), operator);
//            brandMapper.deleteByPrimaryKey(brandId);
//        }).subscribeOn(jdbcScheduler).then();
//    }
//
//    private void createUpdateLog(int brandId, String updateAction, String operator) {
//        BrandUpdateLog updateLog = new BrandUpdateLog();
//        updateLog.setBrandId(brandId);
//        updateLog.setUpdateAction(updateAction);
//        updateLog.setOperator(operator);
//        brandUpdateLogMapper.insert(updateLog);
//    }
}

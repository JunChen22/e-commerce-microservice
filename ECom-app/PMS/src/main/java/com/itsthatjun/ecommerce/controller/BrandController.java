package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.impl.BrandServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@RestController
@RequestMapping("/brand")
@Api(tags = "brand related", description = "brand management")
public class BrandController {

    private static final Logger LOG = LoggerFactory.getLogger(BrandController.class);

    private final BrandServiceImpl brandService;

    private final Scheduler scheduler;

    @Autowired
    public BrandController(BrandServiceImpl brandService,
                           @Qualifier("scheduler") Scheduler scheduler) {
        this.brandService = brandService;
        this.scheduler = scheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands")
    public Flux<Brand> getAllBrand(){
        return brandService.listAllBrand().subscribeOn(scheduler);
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get brands with page and size")
    public Flux<Brand> getAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize){
        return brandService.listBrand(pageNum, pageSize).subscribeOn(scheduler);
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<Product> getBrandProduct(@PathVariable int brandId){
        return brandService.listAllBrandProduct(brandId).subscribeOn(scheduler);
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Mono<Brand> getBrand(@PathVariable int brandId){
        return brandService.getBrand(brandId).subscribeOn(scheduler);
    }

    @PostMapping("/admin/create")
    @ApiOperation(value = "Create a brand")
    public Brand createBrand(@RequestBody Brand brand){
        brandService.createBrand(brand);
        return brand;
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "Update a brand")
    public Brand updateBrand(@RequestBody Brand brand){
        brandService.updateBrand(brand);
        return brand;
    }

    @DeleteMapping("/admin/delete/{id}")
    @ApiOperation(value = "Delete a brand")
    public String deleteBrand(@PathVariable int id){
        brandService.deleteBrand(id);
        return "deleted";
    }
}

package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminBrandEvent;
import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.impl.BrandServiceImpl;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminBrandEvent.Type.*;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@RequestMapping("/brand")
@Api(tags = "brand related", description = "brand management")
public class BrandController {

    private static final Logger LOG = LoggerFactory.getLogger(BrandController.class);

    private final BrandServiceImpl brandService;

    @Autowired
    public BrandController(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands")
    public Flux<Brand> getAllBrand() {
        return brandService.getAllBrand();
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get brands with page and size")
    public Flux<Brand> getAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        return brandService.getAllBrand(pageNum, pageSize);
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<Product> getBrandProduct(@PathVariable int brandId) {
        return brandService.getBrandProduct(brandId);
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Mono<Brand> getBrand(@PathVariable int brandId) {
        return brandService.getBrand(brandId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Create a brand")
    public Mono<Brand> createBrand(@RequestBody Brand brand) {
        return brandService.createBrand(brand);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update a brand")
    public Mono<Brand> updateBrand(@RequestBody Brand brand) {
        return brandService.updateBrand(brand);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Delete a brand")
    public Mono<Void> deleteBrand(@PathVariable int brandId) {
        return brandService.deleteBrand(brandId);
    }
}

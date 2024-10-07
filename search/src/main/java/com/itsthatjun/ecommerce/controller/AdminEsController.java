package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import com.itsthatjun.ecommerce.service.AdminEsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/esProduct")
@Api(tags = "AdminESController", description = "Admin Elastic search management")
public class AdminEsController {

    private final AdminEsProductService productService;

    @Autowired
    public AdminEsController(AdminEsProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/importAll") // TODO: maybe trigger it from admin page
    @ApiOperation(value = "import all product from PMS api to elastic search")
    public Mono<String> importAllList() {
        System.out.println("importing all products");
        return productService.importAll();
    }

    @GetMapping("/getAll")
    @ApiOperation(value = "get all imported products")
    public Flux<EsProduct> getAll() {
        return productService.listAllImportedProduct();
    }
}

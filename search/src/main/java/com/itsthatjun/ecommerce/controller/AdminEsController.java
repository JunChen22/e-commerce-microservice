package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import com.itsthatjun.ecommerce.service.AdminEsProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/esProduct")
@Tag(name = "AdminESController", description = "Admin Elastic search management")
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

    @GetMapping("/listAll")
    @ApiOperation(value = "list all imported products")
    public Flux<EsProduct> listAll() {
        return productService.listAllImportedProduct();
    }
}

package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import com.itsthatjun.ecommerce.service.AdminEsProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "list all imported products",
            description = "list all imported products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "No products found or an error occurred during retrieval")    })
    @GetMapping(value = "/listAll", produces = "application/json")
    public Flux<EsProduct> listAll() {
        return productService.listAllImportedProduct();
    }
}

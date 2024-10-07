package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import com.itsthatjun.ecommerce.service.EsProductService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/esProduct")
@Tag(name = "EsProductController", description = "Elastic search management")
public class ESController {

    private final EsProductService productService;

    @Autowired
    public ESController(EsProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    @ApiOperation(value = "search product by keywords")
    @ApiImplicitParam(name = "sort", value = "order:0->relevance；1->new listed；2->top seller；3->price low；4->price high",
            defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    public Flux<EsProduct> search(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false, defaultValue = "0") int category,
                                  @RequestParam(required = false, defaultValue = "0") int pageNum,
                                  @RequestParam(required = false, defaultValue = "5") int pageSize,
                                  @RequestParam(required = false, defaultValue = "0") int sort) {
        // passing back from gateway, it pass in "?keyword=" instead just keyword.
        if (keyword != null && keyword.startsWith("?keyword=")) {
            keyword = keyword.substring("?keyword=".length());
        }

        // TODO: store search history. add user id if possible
        // return productService.search(keyword, category, pageNum, pageSize, sort);
        return productService.search(keyword, pageNum, pageSize);
    }
}

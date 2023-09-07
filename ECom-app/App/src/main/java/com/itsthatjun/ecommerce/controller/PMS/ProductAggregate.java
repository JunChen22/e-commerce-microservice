package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/product")
public class ProductAggregate {
    private final RestTemplate restTemplate;

    @Value("${app.PMS-service.host}")
    String productServiceURL;

    @Value("${app.PMS-service.port}")
    int port;

    @Autowired
    public ProductAggregate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public List<Product> listAllProduct(){
        String url = "http://" + productServiceURL + ":" + port + "/product/listAll";
        System.out.println(url);
        List<Product> result = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Product>>(){}).getBody();
        return result;
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Product listProduct(@PathVariable int id){
        String url = "http://" + productServiceURL + ":" + port + "/product/" + id;
        System.out.println(url);
        return restTemplate.getForObject(url, Product.class);
    }

    /*

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public List<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize){
        return productService.listProduct(pageNum, pageSize);
    }


    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands")
    public List<Brand> getAllBrand(){
        return brandService.listAllBrand();
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get brands with page and size")
    public List<Brand> getAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize){
        return brandService.listBrand(pageNum, pageSize);
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public List<Product> getBrandProduct(@PathVariable int brandId){
        return brandService.listAllBrandProduct(brandId);
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Brand getBrand(@PathVariable int brandId){
        return brandService.getBrand(brandId);
    }
     */
}

package com.itsthatjun.ecommerce.controller.PMS;

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
}

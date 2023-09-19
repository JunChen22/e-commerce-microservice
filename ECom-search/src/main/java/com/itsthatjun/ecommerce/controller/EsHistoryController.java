package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.service.impl.EsHistoryServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/esHistory")
@Api(tags = "EsHistoryController", description = "Elastic search, member search history")
public class EsHistoryController {

    @Autowired
    private EsHistoryServiceImpl searchHistoryService;



}

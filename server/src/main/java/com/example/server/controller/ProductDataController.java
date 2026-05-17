package com.example.server.controller;

import com.example.server.common.Result;
import com.example.server.service.ProductDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class ProductDataController {

    @Autowired
    private ProductDataService productDataService;

    @GetMapping("/pipeline")
    public Result<String> runPipeline() {
        String result = productDataService.runPipeline();
        return Result.success(result);
    }
}

package com.sparkproject.OfferJet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparkproject.OfferJet.SparkService;
import com.sparkproject.OfferJet.model.TableJoinConfig;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final SparkService sparkService;

    public DataController(SparkService sparkService) {
        this.sparkService = sparkService;
    }
    
    @PostMapping("/combined-data")
    public ResponseEntity<Map<String, Object>> getCombinedData(@RequestBody List<TableJoinConfig> joinDefinitions) {
    	sparkService.fetchAndPersistCombinedData(joinDefinitions);
    	
    	Map<String, Object> response = new HashMap<>();
    	response.put("message", "Data is here!");
    	
    	return ResponseEntity.ok(response);
    }

}

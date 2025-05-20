package com.sparkproject.OfferJet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparkproject.OfferJet.model.TableJoinConfig;
import com.sparkproject.OfferJet.service.DynamicJobService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/offers")
@Api(tags = "Offer Jobs")
public class OfferJobController {
	
	@Autowired
	private DynamicJobService jobService;
	
	@ApiOperation("Run job for offer1")
	@PostMapping("/run-offer1")
	public ResponseEntity<String> runOffer1job(@RequestBody List<TableJoinConfig> joins) throws Exception {
		jobService.runOfferJob("offer1", joins);
		return ResponseEntity.ok("Offer1 job started.");
	}
	
	@ApiOperation("Run job for offer1")
	@PostMapping("/run-offer2")
    public ResponseEntity<String> runOffer2Job(@RequestBody List<TableJoinConfig> joins) throws Exception {
        jobService.runOfferJob("offer2", joins);
        return ResponseEntity.ok("Offer2 job started.");
    }

}

package com.sparkproject.OfferJet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparkproject.OfferJet.model.TableJoinConfig;

@RestController
@RequestMapping("/batch")
public class BatchController {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job job;
	
//	@PostMapping("/run")
//    public ResponseEntity<String> runJob() {
//        try {
//            JobParameters params = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis()) // ensures uniqueness
//                    .toJobParameters();
//
//            jobLauncher.run(job, params);
//            return ResponseEntity.ok("Batch job started.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Job failed to start: " + e.getMessage());
//        }
//    }
	
	@PostMapping("/run")
	public ResponseEntity<String> runJob(@RequestBody List<TableJoinConfig> joinDefinitions) {
	    try {
	        String joinParam = joinDefinitions.stream()
	            .map(j -> j.getTableName() + ":" + j.getJoinColumn())
	            .collect(Collectors.joining(","));

	        JobParameters params = new JobParametersBuilder()
	                .addString("joins", joinParam)
	                .addLong("time", System.currentTimeMillis())
	                .toJobParameters();

	        jobLauncher.run(job, params);
	        return ResponseEntity.ok("Batch job started.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("Job failed: " + e.getMessage());
	    }
	}

}

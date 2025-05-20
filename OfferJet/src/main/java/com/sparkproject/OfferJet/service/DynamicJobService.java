package com.sparkproject.OfferJet.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sparkproject.OfferJet.SparkService;
import com.sparkproject.OfferJet.batchconfig.JobTimingListener;
import com.sparkproject.OfferJet.customprocessor.Offer1Processor;
import com.sparkproject.OfferJet.customprocessor.Offer2Processor;
import com.sparkproject.OfferJet.model.TableJoinConfig;

@Service
public class DynamicJobService {
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicJobService.class);

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;
    private final DataSource dataSource;
    private final SparkService sparkService;

    public DynamicJobService(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                             JobLauncher jobLauncher, DataSource dataSource, SparkService sparkService) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
        this.dataSource = dataSource;
        this.sparkService = sparkService;
    }

    public void runOfferJob(String offerType, List<TableJoinConfig> joins) throws Exception {
    	logger.info("=================== Starting offer job for type: {} ===================", offerType);
        List<Map<String, Object>> data = sparkService.fetchAndPersistCombinedData(joins);
        logger.info("=================== Fetched {} rows of combined data from Spark ===================", data.size());

        ItemProcessor<Map<String, Object>, Map<String, Object>> processor = offerType.equals("offer1")
                ? new Offer1Processor()
                : new Offer2Processor();
        logger.info("=================== Selected processor: {} ===================", processor.getClass().getSimpleName());

        ListItemReader<Map<String, Object>> reader = new ListItemReader<>(data);
        logger.info("=================== Initialized ListItemReader with {} items ===================", data.size());

        JdbcBatchItemWriter<Map<String, Object>> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(item -> new MapSqlParameterSource(item));
        writer.setSql("INSERT INTO joined_result_table (order_id, order_date, amount, customer_id, name, email, phone, offer) " +
                      "VALUES (:order_id, :order_date, :amount, :customer_id, :name, :email, :phone, :offer)");
        writer.setDataSource(dataSource);
        writer.afterPropertiesSet();
        logger.info("=================== JdbcBatchItemWriter initialized with SQL insert statement ===================");

        Step step = stepBuilderFactory.get("step-" + offerType)
                .<Map<String, Object>, Map<String, Object>>chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
        logger.info("=================== Step created: step-{} ===================", offerType);

        Job job = jobBuilderFactory.get("job-" + offerType + "-" + System.currentTimeMillis())
                .incrementer(new RunIdIncrementer())
                .listener(new JobTimingListener())
                .start(step)
                .build();
        logger.info("=================== Job built: job-{} ===================", offerType);

         jobLauncher.run(job, new JobParametersBuilder().toJobParameters());

    }
}

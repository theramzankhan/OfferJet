//You can simply avoid this class
package com.sparkproject.OfferJet.batchconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.sparkproject.OfferJet.SparkService;
import com.sparkproject.OfferJet.model.TableJoinConfig;

@Configuration
@EnableBatchProcessing
public class SparkBatchChunkConfig {
	
	int count = 0;
	
	@Bean
	public Job sparkChunkJob(JobBuilderFactory jobBuilderFactory, Step sparkChunkStep, JobTimingListener listener) {
		return jobBuilderFactory.get("sparkChunkJob")
				.listener(listener)
				.incrementer(new RunIdIncrementer())
				.start(sparkChunkStep)
				.build();
	}
	
	@Bean
	public Step sparkChunkStep(StepBuilderFactory stepBuilderFactory,
							   ItemReader<Map<String, Object>> reader,
							   ItemProcessor<Map<String, Object>, Map<String, Object>> processor,
							   ItemWriter<Map<String, Object>> writer,
							   TaskExecutor taskExecutor) {
		return stepBuilderFactory.get("sparkChunkStep")
				.<Map<String, Object>, Map<String, Object>>chunk(2)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.taskExecutor(taskExecutor) //Enabling parallel execution
				.listener(new ChunkListener() {
					
					@Override
					public void beforeChunk(ChunkContext context) {
						synchronized (this) {
							count++;
							System.out.println("Before processing a chunk no: " + count);
						}	
					}
					@Override
					public void afterChunk(ChunkContext context) {
						System.out.println("Finished processing a chunk no: " + count + " successfully.");
					}
					@Override
					public void afterChunkError(ChunkContext context) {
						System.out.println("Error occurred while processing a chunk no: " + count + "!");
					}
					
				})
				.build();
	}
	
	@Bean
	@StepScope
	public ListItemReader<Map<String, Object>> sparkItemReader(
	        SparkService sparkService,
	        @Value("#{jobParameters['joins']}") String joinString) {

	    List<TableJoinConfig> joins = Arrays.stream(joinString.split(","))
	        .map(pair -> {
	            String[] parts = pair.split(":");
	            return new TableJoinConfig(parts[0], parts[1]);
	        })
	        .collect(Collectors.toList());

	    List<Map<String, Object>> data = sparkService.fetchAndPersistCombinedData(joins);
	    return new ListItemReader<>(data);
	}

	
	@Bean
	public ItemProcessor<Map<String, Object>, Map<String, Object>> sparkItemProcessor() {
		return item -> {
			//process each row
			item.put("processed", true);
			return item;
		};
	}

	@Bean
	public ItemWriter<Map<String, Object>> sparkItemWriter(DataSource dataSource) {
	    JdbcBatchItemWriter<Map<String, Object>> delegate = new JdbcBatchItemWriter<>();
	    
	    delegate.setItemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<Map<String, Object>>() {
	        @Override
	        public SqlParameterSource createSqlParameterSource(Map<String, Object> item) {
	            return new MapSqlParameterSource(item);
	        }
	    });

	    delegate.setSql(
	        "INSERT INTO joined_result_table (" +
	        "order_id, order_date, amount, customer_id, name, email, phone" +
	        ") VALUES (" +
	        ":order_id, :order_date, :amount, :customer_id, :name, :email, :phone" +
	        ")"
	    );
	    delegate.setDataSource(dataSource);
	    delegate.afterPropertiesSet(); // Important for JdbcBatchItemWriter

	    return items -> {
	        for (Map<String, Object> item : items) {
	            System.out.println("Writing to DB: " + item);
	        }
	        delegate.write(items); // Actual DB write
	    };
	}

	
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor("spring_batch");
		asyncTaskExecutor.setConcurrencyLimit(2); // Can tune this value
		return asyncTaskExecutor;
	}

}

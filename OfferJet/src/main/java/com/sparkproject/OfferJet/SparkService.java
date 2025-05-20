package com.sparkproject.OfferJet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sparkproject.OfferJet.model.TableJoinConfig;

@Service
public class SparkService {
	
	private static final Logger logger = LoggerFactory.getLogger(SparkService.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final SparkSession sparkSession;

    public SparkService() {
        this.sparkSession = SparkSession.builder()
            .appName("SpringBootSparkApp")
            .master("local[*]")
            .getOrCreate();
        logger.info("SparkSession initialized");
    }
    
    // Method to load the Dataset<Row>
    public Dataset<Row> loadTable(String tableName) {
    	logger.info("Loading table: {}", tableName);
    	return sparkSession.read()
                .format("jdbc")
                .option("url", dbUrl)
                .option("dbtable", tableName) // customer_info as 1,1, orders as 2,1
                .option("user", dbUser)
                .option("password", dbPassword)
                .load();
    }
    
    // Method to convert Dataset<Row> to List<Map<String, Object>>
    public List<Map<String, Object>> convertToListOfMaps(Dataset<Row> df) {
    	logger.info("Converting Dataset<Row> to List<Map<String, Object>>");
    	List<Map<String, Object>> result = new ArrayList<>();
    	StructType schema = df.schema();
    	
    	for(Row row : df.collectAsList()) {
    		Map<String, Object> map = new HashMap<>();
    		for(StructField field : schema.fields()) {
    			map.put(field.name(), row.getAs(field.name()));
    		}
    		result.add(map);
    	}
    	logger.info("Converted {} rows", result.size());
    	return result;
    }   
    
    // Logic for persisting result to DB
    public void persistToDatabase(Dataset<Row> dataset) {
    	logger.info("Persisting dataset to database table: joined_result_table (mode=Overwrite)");
    	dataset.write()
	    	.format("jdbc")
			.option("url", "jdbc:mysql://localhost:3306/spark_db")
			.option("dbtable", "joined_result_table")
			.option("user", dbUser)
			.option("password", dbPassword)
			.mode(SaveMode.Overwrite) // or can SaveMode.Append
			.save();
    	logger.info("Dataset persisted successfully");
    }
    
    // Logic for joining tables
    public Dataset<Row> joinTables(List<TableJoinConfig> joinConfigs) {
    	logger.info("Joining tables based on configs: {}", joinConfigs);
    	
    	if(joinConfigs == null || joinConfigs.size() < 2) {
    		throw new IllegalArgumentException("Atleast two tables are required for joining.");
    	}
    	
    	// Load the first table
    	TableJoinConfig firstConfig = joinConfigs.get(0);
    	Dataset<Row> combined = loadTable(firstConfig.getTableName());
    	
    	// Iteratively join the rest of the tables
    	for(int i=1; i<joinConfigs.size(); i++) {
    		TableJoinConfig config = joinConfigs.get(i);
    		Dataset<Row> nextTable = loadTable(config.getTableName());
    		logger.info("Joining with table: {} on column: {}", config.getTableName(), config.getJoinColumn());
    		combined = combined.join(nextTable, config.getJoinColumn());
    	}
    	logger.info("Join complete. Schema:\n{}", combined.schema().treeString());
    	combined.show(); // For debugging
    	return combined;
    }
    
    //Main entry point
    public List<Map<String, Object>> fetchAndPersistCombinedData(List<TableJoinConfig> joinConfigs) {
    	logger.info("Starting fetchAndPersistCombinedData");
    	long start = System.currentTimeMillis();
    	
    	Dataset<Row> combined = joinTables(joinConfigs);
    	persistToDatabase(combined);
    	List<Map<String, Object>> result = convertToListOfMaps(combined);
    	
    	long end = System.currentTimeMillis();
    	logger.info("Completed fetchAndPersistCombinedData. Time taken: {} ms", (end - start));
    	
    	return result;
    }
    
//     Filters and returns customers whose order amount is greater than the threshold
//    public List<Map<String, Object>> fetchHighValueCustomers(List<TableJoinConfig> joinConfigs, int amountThresold) {
//    	Dataset<Row> combined = joinTables(joinConfigs); //Uses your joinTables() method to get the combined dataset.
//    	// Filter based on amount > threshold
//    	Dataset<Row> filtered = combined.filter(combined.col("amount").gt(amountThresold)); //Applies a filter() using Spark to get only the rows with amount > amountThreshold.
//    	return convertToListOfMaps(filtered); //Reuses convertToListOfMaps() to convert the filtered Spark DataFrame into List<Map<String, Object>> for your ItemReader.
//    }
    
}


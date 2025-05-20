# 🚀 Project Overview:
Features
Architecture
Tech stack
Configuration
How to run
API usage
Sample data
Future enhancements


# 🚀 OfferJet - Intelligent Offer Engine with Spring Batch, Spark, and MySQL

OfferJet is a dynamic batch processing system that applies personalized offers to customers based on real-time order and customer data. Built with **Spring Boot**, **Spring Batch**, **Apache Spark**, and **MySQL**, it supports dynamic job creation, rule-based processing, and runtime offer logic—all exposed through REST APIs.


## 🧠 Key Features

- 📦 Batch processing using **Spring Batch**
- ⚡ Dynamic runtime job creation via REST endpoints
- 🔄 Integration with **Apache Spark** for data joining
- 🧹 Business rules applied via ItemProcessors or Rule Engine
- 🗃️ Reads from MySQL tables `customer_info` and `order_info`
- 🖥️ Writes enriched data to `joined_result_table`
- 🌐 REST APIs to trigger different offer jobs
- 📈 Future-ready for Apache Kafka notifications


## 🏗️ Architecture Overview

+------------+     +------------+         +--------------------+
| MySQL DB| <---> | Apache Spark | <---> | Spring Batch Job   |
| (2 Tables)|    | (Join Logic)  |       | (Chunk Processing) |
+------------+      +------------+        +---------+----------+
                                                   |
                                        +----------v-----------+
                                        | Offer Processors     |
                                        | (Rule-based logic)   |
                                        +----------+------------+
                                                   |
                                        +----------v-----------+
                                        | joined_result_table   |
                                        +------------------------+


🛠️ Tech Stack :
Component Version
Java 1.8
Spring Boot 2.x
Spring Batch 5.x
Apache Spark 2.x
MySQL 8.x
Maven Yes
Postman / cURL For testing


⚙️ Configuration
Make sure your application.yml or application.properties includes:
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spark_db
    username: root
    password: root
  batch:
    initialize-schema: always


🧪 Sample MySQL Tables
customer
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `offer` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`customer_id`)
);

orders
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `order_date` varchar(40) DEFAULT NULL,
  `amount` int DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  PRIMARY KEY (`order_id`)
);

joined_result_table
Willbe automatically created.


🚀 How to Run
Prerequisites:
Java 1.8
Maven
MySQL running with schema spark_db
Spark installed (if using Spark features)


Steps:
1. Clone the repo
2. git clone https://github.com/yourusername/OfferJet.git
   cd OfferJet
3. Configure DB credentials in application.yml


Run the app

mvn clean install
mvn spring-boot:run
Hit REST endpoints (see below)



📡 REST API Usage
Trigger Job with Offer 1
POST /offers/run-offer1

Trigger Job with Offer 2
POST /offers/run-offer2

Each job applies different offer logic based on order amount.

🧠 Business Logic (Sample Rules)
Offer Name                  Condition
10% Discount                Order amount between 100–200
5% Discount                 Specific location (Future)
No Offer                    Others


✅ Sample Output (joined_result_table)
order_id     name             amount         offer
1            Alice Johnson    100            10% Discount
3            Carol Lee        75             No Offer
4            David Kim        180
10% Discount
8
Hank Adams
210
No Offer


🧪 Testing
JUnit 5 based unit and integration tests
SparkService and JobService tested independently
Loggers show start and end time for profiling



📈 Future Enhancements
✅ Switch from hardcoded rules to Drools / Easy Rules
✅ Add Kafka output after job completion
✅ Expose UI dashboard for job monitoring
✅ Upload CSV and trigger job dynamically
✅ Add custom retry + skip logic

📌 Naming Rationale
OfferJet — Fast, rule-driven dynamic offer engine 🚀

👨‍💻 Author
Developed by Ramzan Khan

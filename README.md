````markdown
# 🚀 OfferJet - Intelligent Offer Engine with Spring Batch, Spark, and MySQL

OfferJet is a dynamic batch processing system that applies personalized offers to customers based on real-time order and customer data. Built with **Spring Boot**, **Spring Batch**, **Apache Spark**, and **MySQL**, it supports dynamic job creation, rule-based processing, and runtime offer logic — all triggered via REST APIs.

---

## 🧠 Key Features

- 📦 Batch processing using **Spring Batch**
- ⚡ Dynamic runtime job creation via **REST endpoints**
- 🔄 Integration with **Apache Spark** for table joins
- 🧹 Business rules applied via `ItemProcessor` or **Rule Engine**
- 🗃️ Reads from MySQL: `customer_info` and `order_info`
- 🖥️ Writes enriched data to `joined_result_table`
- 🌐 REST APIs to trigger offer-specific jobs
- 📈 Future-ready for **Apache Kafka** notifications

---

## 🏗️ Architecture Overview

```text
+------------+     +------------+     +--------------------+
| MySQL DB   | <-->| Apache Spark| <--| Spring Batch Job   |
| (2 Tables) |     | (Join Logic)|     | (Chunk Processing) |
+------------+     +------------+     +---------+----------+
                                             |
                            +----------------v---------------+
                            |        Offer Processors         |
                            |     (Rule-based logic)          |
                            +----------------+---------------+
                                             |
                                +------------v------------+
                                |    joined_result_table   |
                                +--------------------------+
````

---

## 🛠️ Tech Stack

| Component    | Version        |
| ------------ | -------------- |
| Java         | 1.8            |
| Spring Boot  | 2.x            |
| Spring Batch | 5.x            |
| Apache Spark | 2.x            |
| MySQL        | 8.x            |
| Maven        | ✅              |
| Testing Tool | Postman / cURL |

---

## ⚙️ Configuration

Ensure your `application.properties` (or `application.yml`) contains:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spark_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.batch.initialize-schema=always
spring.batch.job.enabled=false
```

---

## 🧪 Sample MySQL Schema

### `customer_info`

```sql
CREATE TABLE customer_info (
  customer_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(100),
  phone VARCHAR(15),
  location VARCHAR(100),
  offer VARCHAR(50)
);
```

### `order_info`

```sql
CREATE TABLE order_info (
  order_id INT AUTO_INCREMENT PRIMARY KEY,
  order_date VARCHAR(40),
  amount INT,
  customer_id INT
);
```

> ✅ `joined_result_table` is created automatically during processing.

---

## 🚀 How to Run

### Prerequisites

* Java 1.8+
* Maven
* MySQL running with schema `spark_db`
* Apache Spark installed (optional, if Spark is used)

### Steps

```bash
# Clone the repo
git clone https://github.com/yourusername/OfferJet.git
cd OfferJet

# Configure DB credentials in src/main/resources/application.properties

# Build and run
mvn clean install
mvn spring-boot:run
```

---

## 📡 REST API Usage

Trigger batch jobs by hitting the following endpoints:

Add body as well like:
[
  { "tableName": "customer", "joinColumn": "customer_id" },
  { "tableName": "orders", "joinColumn": "customer_id" }
]

### ▶️ Run Offer 1 Job

```http
POST /offers/run-offer1
```

### ▶️ Run Offer 2 Job

```http
POST /offers/run-offer2
```

> Each job applies different discount logic based on order amount or other criteria.

---


## 🧠 Business Rules (Sample)

| Offer Name        | Condition              | Applied In        |
| ----------------- | ---------------------- | ----------------- |
| 10% Discount      | Order amount **≤ 100** | `Offer1Processor` |
| No Offer          | Order amount **> 100** | `Offer1Processor` |
| Free Shipping     | Order amount **> 100** | `Offer2Processor` |
| Standard Shipping | Order amount **≤ 100** | `Offer2Processor` |

---

## ✅ Sample Output from `joined_result_table`

| customer\_id | name           | email                                                           | phone         | location  | offer         | order\_id | order\_date | amount |
| ------------ | -------------- | --------------------------------------------------------------- | ------------- | --------- | ------------- | --------- | ----------- | ------ |
| 401          | Taimur Butala  | [abhatti@batra.com](mailto:abhatti@batra.com)                   | +912238424521 | Hyderabad | No Offer      | 552       | 2024-02-15  | 141    |
| 422          | Badal Biswas   | [nayantara92@arya-anand.com](mailto:nayantara92@arya-anand.com) | 0095208252    | Raipur    | 10% Discount  | 840       | 2025-03-14  | 449    |
| 517          | Divij Bhasin   | [nlanka@gmail.com](mailto:nlanka@gmail.com)                     | +916896228996 | Pune      | 10% Discount  | 124       | 2024-05-05  | 658    |
| 517          | Divij Bhasin   | [nlanka@gmail.com](mailto:nlanka@gmail.com)                     | +916896228996 | Pune      | 10% Discount  | 478       | 2024-12-30  | 182    |
| 833          | Mishti Gokhale | [zoyabatra@rau.biz](mailto:zoyabatra@rau.biz)                   | 08279061048   | *(null)*  | Free Shipping | 325       | 2023-10-24  | 409    |



---

## 🧪 Testing

* ✔️ JUnit 5-based **unit tests** for processors
* ✔️ Isolated **SparkService** and **DynamicJobService** tests
* ✔️ Logs track **job start & end time** for performance profiling

---

## 📈 Future Enhancements

* ✅ Migrate hardcoded rules to **Drools** / **Easy Rules**
* ✅ Integrate **Apache Kafka** for job notifications
* ✅ Add UI dashboard for job status
* ✅ Allow **CSV uploads** to trigger batch jobs
* ✅ Custom retry / skip logic per business rules

---

## 📌 Naming Rationale

> **OfferJet** — Fast, rule-driven, and dynamic batch engine for offers 🚀

---

## 📌 Documnet For Better Understanding

>  https://code2tutorial.com/tutorial/d9370487-8bbe-460b-8cc5-94b641038dc8/index.md

---

## 👨‍💻 Author

Developed by **Ramzan Khan**

---

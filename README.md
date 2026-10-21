# South African Job Market Data Pipeline 🇿🇦

## Project Overview

The South African Job Market Data Pipeline is a Java-based data engineering project that demonstrates how job-market data can be ingested, cleaned, stored in a PostgreSQL database, and analysed using SQL.

The project uses a sample dataset representing technology jobs across South African cities.

## Project Objectives

- Read job-market data from CSV files.
- Clean and prepare data for processing.
- Store cleaned records in PostgreSQL.
- Analyse job salaries and job distributions using SQL.
- Test the pipeline using automated unit tests.
- Practise data engineering concepts using Java and SQL.

## Technologies Used

- Java 21
- Maven
- OpenCSV
- PostgreSQL 16
- JDBC
- JUnit 5
- SLF4J
- Git and GitHub

## Pipeline Workflow

```text
Raw Job Data (CSV)
        |
        v
   Data Ingestion
        |
        v
    Data Cleaning
        |
        v
  Cleaned CSV File
        |
        v
 PostgreSQL Database
        |
        v
     SQL Analysis
        |
        v
   Results and Insights

job-market-data-pipeline/
│
├── data/
│   ├── input/
│   │   └── jobs.csv
│   └── output/
│       └── cleaned_jobs.csv
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/jobpipeline/
│   │           ├── models/
│   │           │   └── Job.java
│   │           ├── ingestion/
│   │           │   └── CsvIngestor.java
│   │           ├── DataCleaner.java
│   │           └── PostgresLoader.java
│   │
│   └── test/
│       └── java/
│           └── com/jobpipeline/
│
├── pom.xml
└── README.md



1. Project Setup
- Created a Java 21 Maven project.
- Configured the required dependencies.
- Created a sample South African job-market dataset.
- Connected the repository to GitHub using SSH.
2. Data Ingestion
- Implemented CSV ingestion using OpenCSV.
- Mapped CSV records to Java Job objects.
- Added tests for CSV ingestion.
3. Data Cleaning
- Implemented a data-cleaning stage.
- Generated a separate cleaned CSV file.
- Saved the output to data/output/cleaned_jobs.csv.
4. PostgreSQL Database
- Created the job_market_db database.
- Created a jobs table.
- Configured a database user and the required permissions.
5. Data Loading
- Implemented PostgresLoader.java using JDBC.
- Loaded cleaned job records into PostgreSQL.
- Used prepared statements to insert records.
- Used transaction handling and prevented duplicate job IDs from being inserted.
6. SQL Analysis
Executed SQL queries to explore the sample dataset.

Examples include:
- Calculating average salaries by location.
- Identifying the highest-paying jobs.
- Counting jobs by location.
7. Testing
- Added JUnit tests for CSV ingestion and data cleaning.
- Ran the Maven test suite successfully.
Sample Results
The current dataset contains eight sample job records.
Average Salary by Location
Location	Average Salary (ZAR)
Johannesburg	61,666.67
Cape Town	55,000.00
Pretoria	45,000.00
Durban	28,000.00


Number of Jobs by Location
Location	Number of Jobs
Johannesburg	3
Pretoria	2
Cape Town	2
Durban	1


These results are calculated from the current sample dataset and should not be interpreted as representative statistics for the entire South African job market.
Dataset
The sample dataset includes the following fields:
- job_id
- title
- company
- location
- salary
- posted_date
The dataset contains example technology roles in Johannesburg, Pretoria, Cape Town, and Durban.

Salary values are represented in South African Rand (ZAR).

Important: The dataset is sample data created for learning and demonstration purposes. It is not a comprehensive collection of real job vacancies.

How to Run the Project
Prerequisites
Install or configure:
- Java 21
- Maven
- PostgreSQL
- Git
Step 1: Clone the repository
git clone https://github.com/Vicky9403/job-market-data-pipeline.git
cd job-market-data-pipeline

Step 2: Run the tests
mvn clean test

Step 3: Run data cleaning
Run the DataCleaner class from IntelliJ or use the appropriate Maven command if configured.
Step 4: Load data into PostgreSQL
Configure the PostgreSQL database and provide the database password through the DB_PASSWORD environment variable before running PostgresLoader.

Do not commit database passwords or other secrets to GitHub.
Future Improvements
- Add more realistic and comprehensive job-market data.
- Improve data validation and cleaning rules.
- Analyse salary trends by job title and location.
- Explore in-demand technical skills.
- Containerise the application and database using Docker.
- Add automated builds and tests using GitHub Actions.
- Expand test coverage for the cleaning and database-loading stages.
Learning Outcomes
This project demonstrates practical experience with:
- Java application development.
- CSV data ingestion and processing.
- Data cleaning.
- Relational database design.
- PostgreSQL and SQL queries.
- JDBC database connectivity.
- Unit testing.
- Git and GitHub version control.
Author
Developed as part of my Data Engineering elective to practise building a data pipeline using Java, CSV files, PostgreSQL, and SQL.

## Step 2.2: Save and check your changes

After replacing the README in IntelliJ:

1. Press **Ctrl + S** to save the file.
2. Return to your terminal.
3. Run:

```bash
git status

WTC-6EYKBRS6


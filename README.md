# Job Market Data Pipeline 🇿🇦

The goal of this project is to build a data pipeline that collects, cleans, transforms, stores, and analyses job-market data from South Africa.

## Project Goal

The pipeline will eventually follow this process:

```text
Job Market Data
      ↓
Data Ingestion
      ↓
Data Cleaning
      ↓
Data Transformation
      ↓
PostgreSQL Database
      ↓
SQL Analysis
```

## Technologies

* Java 21
* Maven
* OpenCSV
* PostgreSQL
* JDBC
* JUnit 5
* SLF4J
* Git & GitHub

## Project Structure

```text
job-market-data-pipeline/
│
├── data/
│   ├── input/
│   │   └── jobs.csv
│   └── sql/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jobpipeline/
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│           └── com/jobpipeline/
│
├── pom.xml
└── README.md
```

## Progress

### Step 1 — Project Setup ✅

Completed:

* Created the Maven project.
* Configured Java 21.
* Added OpenCSV for CSV processing.
* Added PostgreSQL JDBC driver.
* Added JUnit 5 for testing.
* Added SLF4J for logging.
* Created the project directory structure.
* Created the initial job-market CSV dataset.
* Updated the dataset to represent the South African job market.
* Connected the project to GitHub using SSH.
* Pushed the initial project to GitHub.

### Step 2 — Data Ingestion ⏳

The next step will be to:

* Read the CSV file using Java.
* Convert CSV rows into Java `Job` objects.
* Handle invalid or missing data.
* Validate the imported records.
* Add unit tests for the ingestion process.

### Step 3 — Data Cleaning ⏳

The pipeline will clean and prepare the data for processing.

### Step 4 — Data Transformation ⏳

The data will be transformed into a format suitable for storage and analysis.

### Step 5 — PostgreSQL Database ⏳

The processed job data will be stored in PostgreSQL using JDBC.

### Step 6 — SQL Analysis ⏳

SQL queries will be used to analyse the job-market data.

Examples of questions we will answer:

* Which cities have the most technology jobs?
* Which skills are most requested?
* What is the average salary by job title?
* Which job roles have the highest salaries?
* How are jobs distributed across South African cities?

### Step 7 — Testing ⏳

JUnit tests will be added to verify that the pipeline processes data correctly.

### Step 8 — Docker ⏳

The application and database will eventually be containerised using Docker.

### Step 9 — CI/CD ⏳

GitHub Actions will be used to automatically build and test the project when changes are pushed to GitHub.

## Dataset

The current dataset contains sample South African job-market data.

Example locations include:

* Johannesburg
* Pretoria
* Cape Town
* Durban

Salary values are represented in **South African Rand (ZAR)**.

> Note: The current dataset is sample data created for learning and demonstration purposes.

## Purpose

This project is being developed as part of my **Data Engineering elective** and is intended to demonstra

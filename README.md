# South African Job Market Data Pipeline

A Java-based data engineering project that collects, cleans, stores, and analyses sample technology job-market data from South Africa.

The pipeline uses CSV files for data ingestion, Java for data processing, PostgreSQL for data storage, and SQL for analysis.

This project was developed as part of my Data Engineering elective to practise building a complete data pipeline.

## Project Overview

The pipeline follows these steps:

1. Read job data from a CSV file.
2. Clean and validate the data.
3. Save the cleaned data to a new CSV file.
4. Load the cleaned records into PostgreSQL.
5. Analyse job salaries and locations using SQL.

## Technologies Used

- **Java 21** — data processing and application logic
- **Maven** — project and dependency management
- **OpenCSV** — reading and writing CSV files
- **PostgreSQL 16** — relational database
- **JDBC** — connecting Java to PostgreSQL
- **JUnit 5** — automated testing
- **SLF4J** — application logging
- **Git and GitHub** — version control

## Pipeline Workflow

```text
        Raw Job Data
        jobs.csv
            |
            v
      CSV Ingestion
            |
            v
       Data Cleaning
            |
            v
      Cleaned CSV File
    cleaned_jobs.csv
            |
            v
     PostgreSQL Database
         job_market_db
            |
            v
        SQL Analysis
            |
            v
      Results and Insights
```

## Project Structure

```text
job-market-data-pipeline/
├── data/
│   ├── input/
│   │   └── jobs.csv
│   └── output/
│       └── cleaned_jobs.csv
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
│   └── test/
│       └── java/
│           └── com/jobpipeline/
├── pom.xml
└── README.md
```

## Getting Started

Follow these steps to download, configure, and run the project.

### Prerequisites

Install the following software before starting:

- Java Development Kit (JDK) 21
- Apache Maven
- PostgreSQL 16 or a compatible PostgreSQL version
- Git

You can check your Java and Maven installations using:

```bash
java -version
mvn -version
```

### Step 1: Clone the Repository

Open your terminal and run:

```bash
git clone https://github.com/Vicky9403/job-market-data-pipeline.git
```

Move into the project directory:

```bash
cd job-market-data-pipeline
```

### Step 2: Run the Tests

Run the automated tests to check the CSV ingestion and data-cleaning functionality:

```bash
mvn clean test
```

The build should finish with:

```text
BUILD SUCCESS
```

### Step 3: Clean the Job Data

Run the data cleaner:

```bash
mvn exec:java -Dexec.mainClass="com.jobpipeline.DataCleaner"
```

The program reads:

```text
data/input/jobs.csv
```

It cleans the records and creates:

```text
data/output/cleaned_jobs.csv
```

The program also displays a summary of the records read, valid records kept, invalid records skipped, and duplicates removed.

### Step 4: Set Up PostgreSQL

Make sure your PostgreSQL server is running.

Create a database named:

```text
job_market_db
```

Create a database user named:

```text
job_pipeline_user
```

Configure the user with the required permissions to connect to the database and read and write the `jobs` table.

The database table must match the schema expected by `PostgresLoader.java`.

**Important:** The project expects the following database connection details:

| Setting | Value |
|---|---|
| Database | `job_market_db` |
| Username | `job_pipeline_user` |
| Host | `localhost` |
| Port | `5432` |
| Password | Supplied through an environment variable |

If you need to create the database and table from scratch, check the SQL statements and table structure expected by `PostgresLoader.java` before running the loader.

### Step 5: Configure the Database Password

The application reads the PostgreSQL password from an environment variable named `DB_PASSWORD`.

On Linux, run:

```bash
read -s -p "Enter your PostgreSQL password: " DB_PASSWORD
export DB_PASSWORD
echo
```

Enter the password for `job_pipeline_user` when prompted.

The password will not appear while you type.

This setting applies to the current terminal session. If you open a new terminal, you may need to set it again.

**Security note:** Never commit your database password or other secrets to GitHub.

### Step 6: Load the Data into PostgreSQL

After completing the database setup and setting `DB_PASSWORD`, run:

```bash
mvn exec:java -Dexec.mainClass="com.jobpipeline.PostgresLoader"
```

The loader reads:

```text
data/output/cleaned_jobs.csv
```

It inserts the cleaned records into the PostgreSQL `jobs` table.

The loader uses prepared statements and transaction handling. Existing job IDs are not inserted again, helping prevent duplicate records when the loader is run repeatedly.

### Step 7: Verify the Database

Connect to PostgreSQL:

```bash
psql -U job_pipeline_user -h localhost -d job_market_db
```

Enter the database user's password when prompted.

Check the number of jobs:

```sql
SELECT COUNT(*) FROM jobs;
```

Display all stored jobs:

```sql
SELECT * FROM jobs ORDER BY job_id;
```

For the supplied sample dataset, the expected number of records is eight.

### Step 8: Analyse the Data Using SQL

Run the following queries inside the PostgreSQL terminal.

#### Average Salary by Location

```sql
SELECT location, ROUND(AVG(salary), 2) AS average_salary
FROM jobs
GROUP BY location
ORDER BY average_salary DESC;
```

#### Top Five Highest-Paying Jobs

```sql
SELECT title, company, location, salary
FROM jobs
ORDER BY salary DESC
LIMIT 5;
```

#### Number of Jobs by Location

```sql
SELECT location, COUNT(*) AS total_jobs
FROM jobs
GROUP BY location
ORDER BY total_jobs DESC, location ASC;
```

To leave the PostgreSQL terminal, run:

```sql
\q
```

## Sample Results

The current sample dataset contains eight technology job records from four South African cities.

### Average Salary by Location

| Location | Average Salary (ZAR) |
|---|---:|
| Johannesburg | R61,666.67 |
| Cape Town | R55,000.00 |
| Pretoria | R45,000.00 |
| Durban | R28,000.00 |

### Number of Jobs by Location

| Location | Number of Jobs |
|---|---:|
| Johannesburg | 3 |
| Cape Town | 2 |
| Pretoria | 2 |
| Durban | 1 |
| **Total** | **8** |

These results are calculated from the current sample dataset. They should not be interpreted as representative statistics for the entire South African job market.

## Dataset Description

The input file is:

```text
data/input/jobs.csv
```

It contains the following columns:

| Column | Description |
|---|---|
| `job_id` | Unique identifier for a job record |
| `title` | Job title |
| `company` | Company name |
| `location` | South African city |
| `salary` | Salary in South African Rand (ZAR) |
| `posted_date` | Date the job was posted |

The sample dataset includes locations such as Johannesburg, Pretoria, Cape Town, and Durban.

**Note:** The dataset is created for learning and demonstration purposes. It does not represent a comprehensive collection of real job vacancies.

## Testing

The project includes automated JUnit tests for CSV ingestion and data cleaning.

Run the test suite using:

```bash
mvn clean test
```

The tests help verify that the ingestion and cleaning stages behave as expected.

## Future Improvements

Possible improvements include:

- Add more job records from reliable data sources.
- Expand data validation and cleaning rules.
- Analyse salaries by job title and location.
- Explore demand for different technical skills.
- Add Docker support for the application and database.
- Configure GitHub Actions for automated testing.
- Expand automated tests for database loading.

## Learning Outcomes

This project provides practical experience with:

- Java application development
- CSV ingestion and processing
- Data cleaning and validation
- PostgreSQL database management
- JDBC database connectivity
- SQL querying and aggregation
- Automated testing with JUnit
- Version control using Git and GitHub

## Author

Developed as part of my Data Engineering elective to practise building a data pipeline using Java, CSV files, PostgreSQL, and SQL.

**GitHub Repository:**  
https://github.com/Vicky9403/job-market-data-pipeline

WTC-6EYKBRS6


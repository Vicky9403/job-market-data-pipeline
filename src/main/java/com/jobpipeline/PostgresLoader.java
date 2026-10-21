package com.jobpipeline;

import com.jobpipeline.ingestion.CsvIngestor;
import com.jobpipeline.models.Job;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PostgresLoader {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/job_market_db";

    private static final String USER =
            "job_pipeline_user";

    public static void main(String[] args) {

        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("Error: Please set the DB_PASSWORD environment variable.");
            return;
        }

        CsvIngestor ingestor = new CsvIngestor();

        List<Job> jobs = ingestor.loadJobs(
                "data/output/cleaned_jobs.csv"
        );

        if (jobs.isEmpty()) {
            System.out.println("No jobs found. Check your cleaned CSV file.");
            return;
        }

        String sql = """
                INSERT INTO jobs
                (job_id, title, company, location, salary, posted_date)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (job_id) DO NOTHING
                """;

        int inserted = 0;

        try (Connection connection =
                     DriverManager.getConnection(URL, USER, password)) {

            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                for (Job job : jobs) {

                    statement.setInt(
                            1, Integer.parseInt(job.getJobId())
                    );

                    statement.setString(2, job.getTitle());
                    statement.setString(3, job.getCompany());
                    statement.setString(4, job.getLocation());

                    statement.setBigDecimal(
                            5, new BigDecimal(job.getSalary())
                    );

                    statement.setDate(
                            6, Date.valueOf(job.getPostedDate())
                    );

                    inserted += statement.executeUpdate();
                }

                connection.commit();

                System.out.println("Job data loaded successfully!");
                System.out.println("New records inserted: " + inserted);
                System.out.println("Total records processed: " + jobs.size());

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error loading job data: " + e.getMessage());
        }
    }
}
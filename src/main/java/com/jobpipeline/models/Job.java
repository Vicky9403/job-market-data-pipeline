package com.jobpipeline.models;

import com.opencsv.bean.CsvBindByName;

/**
 * Represents a single job record from the job market data.
 */
public class Job {

    @CsvBindByName(column = "job_id")
    private String jobId;

    @CsvBindByName(column = "title")
    private String title;

    @CsvBindByName(column = "company")
    private String company;

    @CsvBindByName(column = "location")
    private String location;

    @CsvBindByName(column = "salary")
    private String salary;

    @CsvBindByName(column = "posted_date")
    private String postedDate;

    // Default constructor is required by OpenCSV
    public Job() {}

    public Job(String jobId, String title, String company, String location, String salary, String postedDate) {
        this.jobId = jobId;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.postedDate = postedDate;
    }

    // Getters and Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", salary='" + salary + '\'' +
                ", postedDate='" + postedDate + '\'' +
                '}';
    }
}

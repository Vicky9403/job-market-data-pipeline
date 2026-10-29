package com.jobpipeline.controller;

import com.jobpipeline.dto.CitySalary;
import com.jobpipeline.dto.DashboardStats;
import com.jobpipeline.dto.LocationCount;
import com.jobpipeline.dto.TopJob;
import com.jobpipeline.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testGetStatsSuccess() throws Exception {
        DashboardStats mockStats = new DashboardStats(
                8L,
                51625.0,
                List.of(
                        new CitySalary("Johannesburg", 61666.67),
                        new CitySalary("Cape Town", 55000.0)
                ),
                List.of(
                        new TopJob("Senior Data Engineer", "BigTech", "Johannesburg", 75000.0),
                        new TopJob("Cloud Engineer", "Cloud Africa", "Johannesburg", 65000.0)
                ),
                List.of(
                        new LocationCount("Johannesburg", 3),
                        new LocationCount("Cape Town", 2)
                )
        );

        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        mockMvc.perform(get("/api/stats").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobs").value(8))
                .andExpect(jsonPath("$.averageSalary").value(51625.0))
                .andExpect(jsonPath("$.averageSalaryByCity", hasSize(2)))
                .andExpect(jsonPath("$.averageSalaryByCity[0].city").value("Johannesburg"))
                .andExpect(jsonPath("$.averageSalaryByCity[0].averageSalary").value(61666.67))
                .andExpect(jsonPath("$.topJobs", hasSize(2)))
                .andExpect(jsonPath("$.topJobs[0].title").value("Senior Data Engineer"))
                .andExpect(jsonPath("$.topJobs[0].salary").value(75000.0))
                .andExpect(jsonPath("$.jobsByLocation", hasSize(2)))
                .andExpect(jsonPath("$.jobsByLocation[0].location").value("Johannesburg"))
                .andExpect(jsonPath("$.jobsByLocation[0].count").value(3));
    }

    @Test
    void testGetStatsErrorHandling() throws Exception {
        when(dashboardService.getDashboardStats()).thenThrow(new RuntimeException("Database offline"));

        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Database connection or query failed"))
                .andExpect(jsonPath("$.message").value("Database offline"))
                .andExpect(jsonPath("$.hint").isNotEmpty());
    }
}

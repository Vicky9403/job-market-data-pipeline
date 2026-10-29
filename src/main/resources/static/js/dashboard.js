/**
 * South African Job Market Dashboard Controller
 */

// Keep track of Chart instances to cleanly destroy before re-rendering
let salaryChartInstance = null;
let locationChartInstance = null;

// Format numbers as South African Rand (ZAR)
function formatCurrency(amount) {
    if (amount === null || amount === undefined || isNaN(amount)) {
        return 'R 0.00';
    }
    return new Intl.NumberFormat('en-ZA', {
        style: 'currency',
        currency: 'ZAR',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(amount);
}

// Format numbers with commas (e.g. 1,234)
function formatNumber(number) {
    if (number === null || number === undefined || isNaN(number)) {
        return '0';
    }
    return new Intl.NumberFormat('en-ZA').format(number);
}

// Fetch dashboard data from the Spring Boot REST API
async function loadDashboardData() {
    const refreshBtn = document.getElementById('refreshBtn');
    const dbStatusBadge = document.getElementById('dbStatusBadge');
    const dbStatusText = document.getElementById('dbStatusText');
    const statusDot = dbStatusBadge.querySelector('.status-dot');
    const errorAlert = document.getElementById('errorAlert');

    // Set UI to loading state
    refreshBtn.disabled = true;
    statusDot.className = 'status-dot';
    dbStatusText.textContent = 'Querying PostgreSQL...';

    try {
        const response = await fetch('/api/stats');
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || data.error || `HTTP error ${response.status}`);
        }

        // Update database connection indicator
        statusDot.className = 'status-dot online';
        dbStatusText.textContent = 'PostgreSQL: Connected';
        errorAlert.style.display = 'none';

        // Update KPI Metrics Cards
        updateKPICards(data);

        // Render Charts
        renderSalaryByCityChart(data.averageSalaryByCity || []);
        renderJobsByLocationChart(data.jobsByLocation || []);

        // Render Top 5 Table
        renderTopJobsTable(data.topJobs || []);

    } catch (error) {
        console.error('Error fetching dashboard statistics:', error);

        // Update connection status badge
        statusDot.className = 'status-dot error';
        dbStatusText.textContent = 'Database Disconnected';

        // Show friendly error alert box
        errorAlert.style.display = 'flex';
        document.getElementById('alertTitle').textContent = 'Unable to Load Data from PostgreSQL';
        document.getElementById('alertMessage').textContent =
            `Failed to communicate with PostgreSQL 'job_market_db'. Details: ${error.message}`;
        document.getElementById('alertHint').textContent =
            'Hint: Ensure PostgreSQL is running and the DB_PASSWORD environment variable is set (e.g., export DB_PASSWORD=your_password).';

        // Show fallback empty table
        const topJobsBody = document.getElementById('topJobsBody');
        topJobsBody.innerHTML = `
            <tr>
                <td colspan="5" class="table-placeholder">
                    ⚠️ No data available. Please verify PostgreSQL connection and refresh.
                </td>
            </tr>
        `;
    } finally {
        refreshBtn.disabled = false;
    }
}

// Populate the top KPI cards
function updateKPICards(data) {
    // 1. Total Jobs
    document.getElementById('kpiTotalJobs').textContent = formatNumber(data.totalJobs);

    // 2. Average Salary
    document.getElementById('kpiAvgSalary').textContent = formatCurrency(data.averageSalary);

    // 3. Highest Average Salary City
    const topCity = (data.averageSalaryByCity && data.averageSalaryByCity.length > 0)
        ? data.averageSalaryByCity[0]
        : null;

    if (topCity) {
        document.getElementById('kpiTopCity').textContent = topCity.city;
        document.getElementById('kpiTopCityVal').textContent = `${formatCurrency(topCity.averageSalary)} avg`;
    } else {
        document.getElementById('kpiTopCity').textContent = 'N/A';
        document.getElementById('kpiTopCityVal').textContent = 'No data';
    }

    // 4. Top Hiring Location
    const topLoc = (data.jobsByLocation && data.jobsByLocation.length > 0)
        ? data.jobsByLocation[0]
        : null;

    if (topLoc) {
        document.getElementById('kpiTopLocation').textContent = topLoc.location;
        document.getElementById('kpiTopLocationVal').textContent = `${topLoc.count} job${topLoc.count === 1 ? '' : 's'}`;
    } else {
        document.getElementById('kpiTopLocation').textContent = 'N/A';
        document.getElementById('kpiTopLocationVal').textContent = 'No data';
    }
}

// Render the Average Salary by City Bar Chart
function renderSalaryByCityChart(salaryData) {
    const ctx = document.getElementById('salaryByCityChart').getContext('2d');

    if (salaryChartInstance) {
        salaryChartInstance.destroy();
    }

    const labels = salaryData.map(item => item.city);
    const values = salaryData.map(item => item.averageSalary);

    salaryChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Average Salary (ZAR)',
                data: values,
                backgroundColor: [
                    'rgba(37, 99, 235, 0.85)',
                    'rgba(5, 150, 105, 0.85)',
                    'rgba(217, 119, 6, 0.85)',
                    'rgba(124, 58, 237, 0.85)',
                    'rgba(6, 182, 212, 0.85)'
                ],
                borderColor: [
                    '#2563eb',
                    '#059669',
                    '#d97706',
                    '#7c3aed',
                    '#06b6d4'
                ],
                borderWidth: 1.5,
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return ` Avg Salary: ${formatCurrency(context.raw)}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value) {
                            return 'R ' + (value / 1000).toLocaleString() + 'k';
                        }
                    },
                    grid: {
                        color: '#f1f5f9'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// Render Job Counts by Location Doughnut Chart
function renderJobsByLocationChart(locationData) {
    const ctx = document.getElementById('jobsByLocationChart').getContext('2d');

    if (locationChartInstance) {
        locationChartInstance.destroy();
    }

    const labels = locationData.map(item => item.location);
    const values = locationData.map(item => item.count);

    locationChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    '#2563eb',
                    '#059669',
                    '#f59e0b',
                    '#8b5cf6',
                    '#06b6d4',
                    '#ec4899'
                ],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        boxWidth: 14,
                        padding: 16,
                        font: {
                            family: "'Inter', sans-serif",
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const label = context.label || '';
                            const value = context.raw || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                            return ` ${label}: ${value} jobs (${percentage}%)`;
                        }
                    }
                }
            },
            cutout: '65%'
        }
    });
}

// Render Top 5 Highest-Paying Jobs Table
function renderTopJobsTable(topJobs) {
    const tbody = document.getElementById('topJobsBody');

    if (!topJobs || topJobs.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="table-placeholder">No job records found in PostgreSQL jobs table.</td>
            </tr>
        `;
        return;
    }

    const rowsHtml = topJobs.map((job, index) => {
        const rank = index + 1;
        const rankClass = rank === 1 ? 'rank-1' : rank === 2 ? 'rank-2' : rank === 3 ? 'rank-3' : 'rank-other';

        return `
            <tr>
                <td><span class="rank-pill ${rankClass}">${rank}</span></td>
                <td><strong>${escapeHtml(job.title)}</strong></td>
                <td><span class="company-badge">${escapeHtml(job.company)}</span></td>
                <td>📍 ${escapeHtml(job.location)}</td>
                <td class="text-right"><span class="salary-tag">${formatCurrency(job.salary)}</span></td>
            </tr>
        `;
    }).join('');

    tbody.innerHTML = rowsHtml;
}

// Simple HTML escaping helper for security
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// Load data immediately on page load
document.addEventListener('DOMContentLoaded', () => {
    loadDashboardData();
});


package com.diya.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRequest {
    
    @NotNull(message = "Total visitors count is required")
    @Min(value = 0, message = "Total visitors must be non-negative")
    private int totalVisitors;
    
    @NotNull(message = "Unique visitors count is required")
    @Min(value = 0, message = "Unique visitors must be non-negative")
    private int uniqueVisitors;
    
    @NotNull(message = "New users count is required")
    @Min(value = 0, message = "New users must be non-negative")
    private int newUsers;
    
    @NotNull(message = "Page views count is required")
    @Min(value = 0, message = "Page views must be non-negative")
    private int pageViews;
    
    @NotNull(message = "Bounce rate is required")
    private double bounceRate;
    
    @NotNull(message = "Average session duration is required")
    private double averageSessionDuration;
}

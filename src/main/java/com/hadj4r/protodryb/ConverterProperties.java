package com.hadj4r.protodryb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "protodryb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConverterProperties {
    private String packagesToScan;
}

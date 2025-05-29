package org.example.tshirtlabbackend.design.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDesignRequest {
    private String prompt;
    private String style;
}

package org.example.tshirtlabbackend.design.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDesignRequest {
    private Integer page = 1;
    private Integer pageSize = 20;
}
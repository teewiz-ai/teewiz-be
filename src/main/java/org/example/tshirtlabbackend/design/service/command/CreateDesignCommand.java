package org.example.tshirtlabbackend.design.service.command;

import lombok.*;
import org.example.tshirtlabbackend.user.domain.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDesignCommand {
    private String prompt;
    private String style;
    private String size;
    private String quality;
    private String background;
    private String format;
    private Integer n;
    private List<String> sampleImageUrls;
    private User owner;
}
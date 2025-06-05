package org.example.tshirtlabbackend.design.service.command;

import lombok.*;
import org.example.tshirtlabbackend.user.domain.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDesignCommand {
    private int page;
    private int pageSize;
    private User owner;
}
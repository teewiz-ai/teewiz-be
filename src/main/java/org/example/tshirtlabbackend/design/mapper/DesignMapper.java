package org.example.tshirtlabbackend.design.mapper;

import org.example.tshirtlabbackend.common.PaginatedResponse;
import org.example.tshirtlabbackend.common.PaginatedResult;
import org.example.tshirtlabbackend.design.controller.request.*;
import org.example.tshirtlabbackend.design.controller.response.*;
import org.example.tshirtlabbackend.design.data.entity.Design;
import org.example.tshirtlabbackend.design.service.command.*;
import org.example.tshirtlabbackend.design.service.result.*;
import org.example.tshirtlabbackend.user.domain.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DesignMapper {
    DesignMapper INSTANCE = Mappers.getMapper(DesignMapper.class);

    CreateDesignCommand toCommand(CreateDesignRequest request, User owner);

    @Mapping(target = "page", source = "request.page")
    @Mapping(target = "pageSize", source = "request.pageSize")
    @Mapping(target = "owner", source = "owner")
    SearchDesignCommand toCommand(SearchDesignRequest request, User owner);

    PaginatedResponse<DesignResponse> toResponse(PaginatedResult<DesignResult> result);


    default CreateDesignResult toCreateResult(Design d) {
        if (d == null) {
            return null;
        }
        return CreateDesignResult.builder()
                .id(d.getId())
                .s3Key(d.getS3Key())
                .url(d.getUrl())
                .prompt(d.getPrompt())
                .style(d.getStyle())
                .sampleImageUrls(d.getSampleImageUrls())
                .createdAt(d.getCreatedAt())
                .build();
    }

    default DesignResult toResult(Design d) {
        if (d == null) {
            return null;
        }
        List<String> samples = (d.getSampleImageUrls() == null || d.getSampleImageUrls().isBlank())
                ? Collections.emptyList()
                : Arrays.asList(d.getSampleImageUrls().split(","));

        return DesignResult.builder()
                .id(d.getId())
                .s3Key(d.getS3Key())
                .url(d.getUrl())
                .prompt(d.getPrompt())
                .style(d.getStyle())
                .sampleImageUrls(samples)
                .createdAt(d.getCreatedAt())
                .build();
    }



    default DesignResponse toResponse(DesignResult r) {
        if (r == null) {
            return null;
        }
        return DesignResponse.builder()
                .id(r.getId())
                .s3Key(r.getS3Key())
                .url(r.getUrl())
                .prompt(r.getPrompt())
                .style(r.getStyle())
                .sampleImageUrls(r.getSampleImageUrls())
                .createdAt(r.getCreatedAt())
                .build();
    }


    default DesignResponse toDesignResponse(CreateDesignResult r) {
        if (r == null) {
            return null;
        }
        List<String> sampleImageUrls = (r.getSampleImageUrls() == null || r.getSampleImageUrls().isBlank())
                ? Collections.emptyList()
                : Arrays.asList(r.getSampleImageUrls().split(","));

        return DesignResponse.builder()
                .id(r.getId())
                .s3Key(r.getS3Key())
                .url(r.getUrl())
                .prompt(r.getPrompt())
                .style(r.getStyle())
                .sampleImageUrls(sampleImageUrls)
                .createdAt(r.getCreatedAt())
                .build();
    }




}
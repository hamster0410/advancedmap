package com.example.adbanmap.map.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MapPositionDTOResponse {
    List<MapPositionDTO> mapPositionDTOList;
    long total_count;
}
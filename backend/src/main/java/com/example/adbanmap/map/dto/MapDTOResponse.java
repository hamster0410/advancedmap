package com.example.adbanmap.map.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MapDTOResponse {
    private List<MapDTO> MapDTOList;
    private long total_count;
}
package com.example.adbanmap.map.controller;

import com.example.adbanmap.global.dto.ResponseDTO;
import com.example.adbanmap.map.dto.MapDescriptionDTO;
import com.example.adbanmap.map.dto.MapPositionDTOResponse;
import com.example.adbanmap.map.dto.SearchRequestDTO;
import com.example.adbanmap.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {
    private final Logger log = LoggerFactory.getLogger(MapController.class);
    private final MapService mapService;
    @GetMapping("/find")
    public ResponseEntity<?> find_home() {
        ResponseDTO responseDTO = null;
        try {
            mapService.mapFind();
            responseDTO = ResponseDTO.builder()
                    .message("success find")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search_position(@ModelAttribute SearchRequestDTO searchRequestDTO) {
        ResponseDTO responseDTO = null;

        try {
            MapPositionDTOResponse mapPositionDTOResponse = mapService.search(searchRequestDTO);
            System.out.println(mapPositionDTOResponse.toString());
            return ResponseEntity.ok().body(mapPositionDTOResponse);

        }catch (Exception e){
            log.error(e.getMessage());
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> search_detail(@RequestParam long id) {
        ResponseDTO responseDTO = null;

        try {
            MapDescriptionDTO mapDescriptionDTO = mapService.search_detail(id);
            return ResponseEntity.ok().body(mapDescriptionDTO);

        }catch (Exception e){
            log.error(e.getMessage());
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}

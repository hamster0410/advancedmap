package com.example.adbanmap.map.service;

import com.example.adbanmap.map.dto.*;
import com.example.adbanmap.map.entity.MapDescription;
import com.example.adbanmap.map.entity.MapPosition;
import com.example.adbanmap.map.repository.MapDescriptionRepository;
import com.example.adbanmap.map.repository.MapPositionRepository;
import com.example.adbanmap.map.repository.MapPositionSpecification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import javax.transaction.Transactional;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {
    private final Logger log = LoggerFactory.getLogger(MapService.class);
    private final MapPositionRepository mapPositionRepository;
    private final MapDescriptionRepository mapDescriptionRepository;

    @Value("${openApi.serviceKey}")
    private String serviceKey;

    @Value("${openApi.endPoint}")
    private String endPoint;

    @Value("${openApi.dataType}")
    private String dataType;

    private long totalPages;

    private long totalCount;

    @Transactional
    public ResponseEntity<?> mapFind() {

        mapPositionRepository.deleteAll();
        mapDescriptionRepository.deleteAll();

        int pageNo = 1; // 페이지 번호 초기화
        List<MapDTO> resultList = new ArrayList<>(); // 결과를 담을 리스트
        totalCount = 0; // 총 항목 수
        totalPages = Long.MAX_VALUE; // 최대 페이지를 추후 계산

        RestTemplate restTemplate = new RestTemplate();
        String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

        while (pageNo <= totalPages) {
            String API_URL = endPoint + "?serviceKey=" + serviceKey + "&dataType=" + dataType +
                    "&perPage=1000&page=" + pageNo;

            log.info("API 호출 URL: {}", API_URL);
            log.info("전체 페이지 : {}", totalPages);

            try {
                URI uri = URI.create(UriComponentsBuilder.fromHttpUrl(endPoint)
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("dataType", dataType)
                        .queryParam("perPage", 1000)
                        .queryParam("page", pageNo)
                        .build()
                        .toUriString());

                ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
                String response = responseEntity.getBody();

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response);

                // total_count가 존재하면 최대 페이지 계산
                JsonNode totalCountNode = rootNode.path("totalCount");
                if (totalCountNode.isNumber()) {
                    totalCount = totalCountNode.asLong();
                    totalPages = (totalCount + 1000 - 1) / 1000; // 총 페이지 계산
                }

                // 데이터 처리
                JsonNode dataNode = rootNode.path("data");
                if (dataNode.isArray() && dataNode.size() > 0) {
                    for (JsonNode itemNode : dataNode) {
                        MapDTO item = objectMapper.treeToValue(itemNode, MapDTO.class);
                        resultList.add(item);

                        // 엔티티로 변환하여 DB 저장
                        MapPosition mapPosition = item.toMapPosition();
                        mapPositionRepository.save(mapPosition);
                        MapDescription mapDescription = item.toMapDescription(mapPosition);
                        mapDescriptionRepository.save(mapDescription);
                    }
                    log.info("현재 페이지 처리 완료: {}", pageNo);
                    pageNo++; // 다음 페이지로 이동
                } else {
                    log.info("더 이상 데이터가 없습니다. 반복 종료.");
                    break;
                }

            } catch (Exception e) {
                log.error("API 응답 처리 중 오류 발생: ", e);
                break; // 오류 발생 시 반복 종료
            }
        }

        // 결과 로그 확인
        log.info("총 항목 수: {}", totalCount);
        log.info("수집된 데이터 수: {}", resultList.size());

        MapDTOResponse response = MapDTOResponse.builder()
                .MapDTOList(resultList)
                .total_count(totalCount)
                .build();

        return ResponseEntity.ok().body(response);
    }


    public MapPositionDTOResponse search( SearchRequestDTO searchRequestDTO) {

        Specification<MapPosition> specification = Specification.where(null);

        if(searchRequestDTO.getKeyword()!=null){
            specification = specification.and(MapPositionSpecification.searchByKeyword(searchRequestDTO.getKeyword()));
        }

        if(searchRequestDTO.getCategory()!=null){
            specification = specification.and(MapPositionSpecification.searchByCategory(searchRequestDTO.getCategory()));
        }
        if (searchRequestDTO.getOptions() != null) {
            Set<String> options = new HashSet<>(searchRequestDTO.getOptions());

            if (options.contains("outdoor")) {
                specification = specification.and(MapPositionSpecification.searchByOutdoor(true));
            }
            if (options.contains("indoor")) {
                specification = specification.and(MapPositionSpecification.searchByIndoor(true));
            }
            if (options.contains("parking")) {
                specification = specification.and(MapPositionSpecification.searchByParking(true));
            }
        }

//

        if (searchRequestDTO.getSwLatlng() != null && searchRequestDTO.getNeLatlng() != null) {
            specification = specification.and(MapPositionSpecification.searchByRange(
                    searchRequestDTO.getSwLatlng().getLongitude(),
                    searchRequestDTO.getSwLatlng().getLatitude(),
                    searchRequestDTO.getNeLatlng().getLongitude(),
                    searchRequestDTO.getNeLatlng().getLatitude()));
        }


        // 거리 정렬
//        specification = specification.and(MapPositionSpecification.orderByDistance(
//                (searchRequestDTO.getSwLatlng().getLatitude() + searchRequestDTO.getNeLatlng().getLatitude()) / 2,
//                (searchRequestDTO.getSwLatlng().getLongitude() + searchRequestDTO.getNeLatlng().getLongitude()) / 2
//        ));

        List<MapPosition> maps = mapPositionRepository.findAll(specification);
        List<MapPositionDTO> mapPositionDTOList= maps.stream().map(MapPositionDTO::new).collect(Collectors.toList());

        return MapPositionDTOResponse.builder()
                .mapPositionDTOList(mapPositionDTOList)
                .total_count(maps.size())
                .build();
    }

    public MapDescriptionDTO search_detail(long id) {
        MapDescription mapDescription = mapDescriptionRepository.findById(id).orElse(null);

        return new MapDescriptionDTO(mapDescription);
    }
}

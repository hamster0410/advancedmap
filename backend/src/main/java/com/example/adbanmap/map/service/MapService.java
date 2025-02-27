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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public ResponseEntity<?> mapFind() {

        mapPositionRepository.deleteAll();
        int pageNo = 1; // 페이지 번호 초기화
        List<MapDTO> resultList = new ArrayList<>(); // 결과를 담을 리스트
        long totalCount = 0; // 총 항목 수
        long totalPages = Long.MAX_VALUE; // 최대 페이지를 추후 계산

        RestTemplate restTemplate = new RestTemplate();

        while (pageNo <= totalPages) {

            String API_URL = endPoint + "?serviceKey=" + serviceKey + "&returnType=" + dataType +
                    "&perPage=1000&page=" + pageNo;

            log.info("API 호출 URL: {}", API_URL);
            log.info("전체 페이지 : {}", totalPages);
            try {
                URL url=new URL(endPoint + "?serviceKey=" + serviceKey + "&returnType=" + dataType +
                        "&perPage=1000&page=" + pageNo);
                // HttpURLConnection 객체 생성
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int status = connection.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {  // 요청이 성공한 경우
                    // 응답을 읽기 위한 BufferedReader 생성
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(response.toString());
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
                            MapPosition mapPosition = item.toMapPosition();
                            // 엔티티로 변환하여 DB 저장
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


                } else {
                    log.info("API 요청 실패, 상태 코드 : " + status);
                }

            } catch (Exception e) {
                // 결과 로그 확인
                log.info("총 항목 수: {}", totalCount);
                log.info("수집된 데이터 수: {}", resultList.size());

                break; // 오류 발생 시 반복 종료
            }
        }

        // 결과 로그 확인
        MapDTOResponse response = MapDTOResponse.builder()
                .MapDTOList(resultList)
                .total_count(totalCount)
                .build();

        return ResponseEntity.ok().body(response);
    }

    public MapPositionDTOResponse search(String token, SearchRequestDTO searchRequestDTO, int page) {

        Pageable pageable = PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "countLike"));
        Specification<MapPosition> specification = Specification.where(null);

        if(searchRequestDTO.getKeyword()!=null){
            specification = specification.and(MapPositionSpecification.searchByKeyword(searchRequestDTO.getKeyword()));
        }

        if(searchRequestDTO.getCategory()!=null){
            specification = specification.and(MapPositionSpecification.searchByCategory(searchRequestDTO.getCategory()));
        }

        if(searchRequestDTO.getParking()!=null){
            specification = specification.and(MapPositionSpecification.searchByParking(searchRequestDTO.getParking()));
        }

        if(searchRequestDTO.getOutdoor()!=null){
            specification = specification.and(MapPositionSpecification.searchByOutdoor(searchRequestDTO.getOutdoor()));
        }

        if(searchRequestDTO.getIndoor()!=null){
            specification = specification.and(MapPositionSpecification.searchByIndoor(searchRequestDTO.getIndoor()));
        }
        if (searchRequestDTO.getSwLatlng() != null && searchRequestDTO.getNeLatlng() != null) {
            specification = specification.and(MapPositionSpecification.searchByRange(
                    searchRequestDTO.getSwLatlng().getLongitude(),
                    searchRequestDTO.getSwLatlng().getLatitude(),
                    searchRequestDTO.getNeLatlng().getLongitude(),
                    searchRequestDTO.getNeLatlng().getLatitude()));
        }

        // 거리 정렬
        specification = specification.and(MapPositionSpecification.orderByDistance(
                (Double.parseDouble(searchRequestDTO.getSwLatlng().getLatitude()) + Double.parseDouble(searchRequestDTO.getNeLatlng().getLatitude())) / 2,
                (Double.parseDouble(searchRequestDTO.getSwLatlng().getLongitude()) + Double.parseDouble(searchRequestDTO.getNeLatlng().getLongitude())) / 2
        ));

        Page<MapPosition> maps = mapPositionRepository.findAll(specification,pageable);


        return MapPositionDTOResponse.builder()
//                .mapPositionDTOList(mapPositionDTOList)
                .total_count(maps.getTotalElements())
                .build();
    }
}

package com.example.adbanmap.map.service;

import com.example.adbanmap.map.dto.MapDTO;
import com.example.adbanmap.map.dto.MapDTOResponse;
import com.example.adbanmap.map.repository.MapRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
@RequiredArgsConstructor
public class MapService {
    private final MapRepository mapRepository;

    @Value("${openApi.serviceKey}")
    private String serviceKey;

    @Value("${openApi.endPoint}")
    private String endPoint;

    @Value("${openApi.dataType}")
    private String dataType;

    public ResponseEntity<?> mapFind() {

        mapRepository.deleteAll();
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

                            // 엔티티로 변환하여 DB 저장
                            mapRepository.save(item.toEntity());
                        }
                        log.info("현재 페이지 처리 완료: {}", pageNo);
                        pageNo++; // 다음 페이지로 이동
                    } else {
                        log.info("더 이상 데이터가 없습니다. 반복 종료.");
                        break;
                    }


                } else {
                    System.out.println("API 요청 실패, 상태 코드: " + status);
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

}

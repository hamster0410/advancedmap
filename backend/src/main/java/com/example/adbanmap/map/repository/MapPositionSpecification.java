package com.example.adbanmap.map.repository;

import com.example.adbanmap.map.entity.MapPosition;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class MapPositionSpecification {
    public static Specification<MapPosition> searchByKeyword(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("facilityName")), "%" + searchTerm.toLowerCase() + "%");
        };
    }

    public static Specification<MapPosition> searchByCategory(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) return null;
            return criteriaBuilder.equal(root.get("category3"), searchTerm);
        };
    }

    public static Specification<MapPosition> searchByParking(Boolean searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null) return null;
            if(searchTerm){
                return criteriaBuilder.equal(root.get("parkingAvailable"), "Y");
            }else{
                return null;
            }
        };
    }

    public static Specification<MapPosition> searchByIndoor(Boolean searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null) return null;
            if(searchTerm){
                return criteriaBuilder.equal(root.get("indoorAvailable"), "Y");
            }else{
                return null;
            }
        };
    }

    public static Specification<MapPosition> searchByOutdoor(Boolean searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null) return null;
            if(searchTerm){
                return criteriaBuilder.equal(root.get("outdoorAvailable"), "Y");
            }else{
                return null;
            }
        };
    }

    public static Specification<MapPosition> searchByRange(Double sw_x, Double sw_y, Double ne_x, Double ne_y) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (sw_x == null || sw_y == null || ne_x == null || ne_y == null) return null;

            predicates.add(criteriaBuilder.between(root.get("longitude"),sw_x,ne_x));
            predicates.add(criteriaBuilder.between(root.get("latitude"),sw_y,ne_y));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<MapPosition> orderByDistance(double baseLatitude, double baseLongitude) {
        return (root, query, criteriaBuilder) -> {
            // 위도 차이의 절대값
            Expression<Double> latitudeDifference = criteriaBuilder.abs(
                    criteriaBuilder.diff(root.get("latitude"), baseLatitude)
            );

            // 경도 차이의 절대값
            Expression<Double> longitudeDifference = criteriaBuilder.abs(
                    criteriaBuilder.diff(root.get("longitude"), baseLongitude)
            );

            // 절대값 차이의 합
            Expression<Double> totalDifference = criteriaBuilder.sum(latitudeDifference, longitudeDifference);

            // 정렬 추가
            query.orderBy(criteriaBuilder.asc(totalDifference));

            return null; // 정렬만 적용하므로 조건식은 없음
        };
    }
}

package com.example.adbanmap.map.repository;

import com.example.adbanmap.map.entity.MapPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MapPositionRepository extends JpaRepository<MapPosition,Integer> , JpaSpecificationExecutor<MapPosition> {

}

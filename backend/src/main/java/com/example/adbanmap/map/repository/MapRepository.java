package com.example.adbanmap.map.repository;

import com.example.adbanmap.map.entity.MapEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository extends JpaRepository<MapEntity,Integer> {

}

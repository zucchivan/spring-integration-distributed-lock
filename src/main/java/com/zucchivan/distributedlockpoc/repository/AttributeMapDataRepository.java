package com.zucchivan.distributedlockpoc.repository;

import com.zucchivan.distributedlockpoc.model.AttributeMapData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeMapDataRepository extends JpaRepository<AttributeMapData, Integer> {
}
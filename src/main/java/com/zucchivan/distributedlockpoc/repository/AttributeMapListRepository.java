package com.zucchivan.distributedlockpoc.repository;

import com.zucchivan.distributedlockpoc.model.AttributeMapList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeMapListRepository extends JpaRepository<AttributeMapList, Integer> {}
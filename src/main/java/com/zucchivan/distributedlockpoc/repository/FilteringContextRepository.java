package com.zucchivan.distributedlockpoc.repository;

import com.zucchivan.distributedlockpoc.model.FilteringContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilteringContextRepository extends JpaRepository<FilteringContext, Integer> {
}
package com.example.phoebe.repository;

import com.example.phoebe.entity.HomePageBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomePageBlockRepository extends JpaRepository<HomePageBlock, Integer> {
}

package com.dev.carrental_project.repository;

import com.dev.carrental_project.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {



}



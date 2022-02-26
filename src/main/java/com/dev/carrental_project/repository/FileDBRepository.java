package com.dev.carrental_project.repository;


import com.dev.carrental_project.domain.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

}

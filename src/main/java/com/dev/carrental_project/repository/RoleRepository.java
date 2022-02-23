package com.dev.carrental_project.repository;



import com.dev.carrental_project.domain.Role;
import com.dev.carrental_project.domain.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {


    Optional<Role> findByName(UserRole name);
}


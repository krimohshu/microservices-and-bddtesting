package com.api.springboot.repository;

import com.api.springboot.model.ApiObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiObjectRepository extends JpaRepository<ApiObject, Long> {
    List<ApiObject> findByNameContaining(String name);
}

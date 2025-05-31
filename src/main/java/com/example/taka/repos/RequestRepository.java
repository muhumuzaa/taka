package com.example.taka.repos;

import com.example.taka.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    //gives me CRUD (save, findById(), delete, findAll(Pageable), findAll())

    List<Request> findByTitle(String keyword);
}

package com.example.demo.repository;

import com.example.demo.entity.CommentTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentTORepository extends JpaRepository<CommentTO,Long> {
}

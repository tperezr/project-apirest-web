package com.alkemy.ong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alkemy.ong.model.Comment;

import java.util.List;

@Repository("commentRepository")
public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findAllByOrderByCreatedAtDesc();
}
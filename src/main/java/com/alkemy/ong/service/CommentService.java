package com.alkemy.ong.service;

import java.util.List;

import com.alkemy.ong.dto.CommentBodyDto;
import com.alkemy.ong.dto.CommentRequest;
import com.alkemy.ong.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface CommentService {
    List<CommentBodyDto> getAllCommentsOrderedByDate();
    String save(CommentRequest commentRequest);
    ResponseEntity<?> deletCommentById(Long id, String userEmail);
    void updateComment(Long id, CommentBodyDto commentBodyDto);
}

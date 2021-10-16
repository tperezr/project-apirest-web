package com.alkemy.ong.controller;


import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;


import com.alkemy.ong.service.impl.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Locale;
import com.alkemy.ong.dto.CommentBodyDto;
import com.alkemy.ong.dto.CommentRequest;
import com.alkemy.ong.service.CommentService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Comments")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;
    private final MessageSource messageSource;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<?> getCommentsBodyOrdered(){
        List<CommentBodyDto> commentsBody = commentService.getAllCommentsOrderedByDate();
        if(commentsBody.isEmpty()){
            return ResponseEntity.ok(messageSource.getMessage("error.entity.empty",new Object[] {"Comments"}, Locale.US));
        }
        return ResponseEntity.ok(commentService.getAllCommentsOrderedByDate());
    }

    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody @Valid CommentRequest commentRequest) {
    	return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteComment(@PathVariable(value="id", required=true) Long id,Authentication authentication, Principal principal){
        return commentService.deletCommentById(id,authentication.getName());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable @Min(value = 1,message = "Id must be greater than or equal to 1") Long id,
            @RequestBody @Valid CommentBodyDto commentBodyDto){

        commentService.updateComment(id,commentBodyDto);
        return ResponseEntity.ok(messageSource.getMessage("entity.updated", new Object[]{"Comment",id},Locale.US));
    }
}

package com.alkemy.ong.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alkemy.ong.util.ERole;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.alkemy.ong.dto.CommentBodyDto;
import com.alkemy.ong.dto.CommentRequest;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.exception.ForbiddenAction;
import com.alkemy.ong.model.Comment;
import com.alkemy.ong.model.News;
import com.alkemy.ong.model.User;
import com.alkemy.ong.repository.CommentRepository;
import com.alkemy.ong.repository.NewsRepository;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.service.CommentService;
import com.alkemy.ong.util.ERole;
import com.alkemy.ong.dto.CommentBodyDto;
import com.alkemy.ong.dto.CommentRequest;

import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final MessageSource messageSource;
    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentBodyDto> getAllCommentsOrderedByDate() {
        List<Comment> comments = commentRepository.findAllByOrderByCreatedAtDesc();
        return comments.stream().map(comment -> new CommentBodyDto(comment.getBody())).collect(Collectors.toList());
    }

	@Override
	public String save(CommentRequest commentRequest) {
		News news = newsRepository.findById(commentRequest.getNewsId()).orElseThrow(this::makeNewsEntityNotFoundException);
		var principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(this::makeUserEntityNotFoundException);
		Comment comment = new Comment();
		comment.setNews(news);
		comment.setUser(user);
		comment.setBody(commentRequest.getBody());
		comment = commentRepository.save(comment);
		log.info("User {} has post a comment to news {}.", comment.getUser().getFirstName(), comment.getNews().getName());
		Object[] placeholders = new Object[]{"Comment", "News", news.getId()};
		String message = messageSource.getMessage("info.entity.child-entity-created", placeholders, Locale.US);
		return message;
	}

	@Override
	public ResponseEntity<?> deletCommentById(Long id, String userEmail) {

    	Optional<Comment> comment = commentRepository.findById(id);

		if (comment.isEmpty()){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageSource.getMessage("error.entity.not-found", new Object[] {"Comment"}, Locale.US));
		}else {

			User user = userRepository.findByEmail(userEmail).orElseThrow(this::makeUserEntityNotFoundException);

			if (comment.get().getUser().getEmail().equals(user.getEmail()) || user.getRoleId().getName().equals(ERole.ROLE_ADMIN.name())) {
				commentRepository.deleteById(id);
				return ResponseEntity.ok().body(messageSource.getMessage("entity.deleted", new Object[]{"Comment"}, Locale.US));
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(messageSource.getMessage("entity.deleted.notAllowed", new Object[]{"Comment"}, Locale.US));
			}
		}
	}


	private EntityNotFoundException makeNewsEntityNotFoundException() {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"News"}, Locale.US);
		return new EntityNotFoundException(error);
	}

	private EntityNotFoundException makeUserEntityNotFoundException() {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] {"User"}, Locale.US);
		return new EntityNotFoundException(error);
	}

    @Override
    @Transactional
    public void updateComment(Long id, CommentBodyDto commentBodyDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage(
                                "error.entity.not-found-id",
                                new Object[]{"Comment",id},
                                Locale.US)));

        var user = (org.springframework.security.core.userdetails.User)
				SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<GrantedAuthority> rol = user.getAuthorities();
        boolean isAdmin = rol.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ERole.ROLE_ADMIN.name()));

        if(Objects.equals(user.getUsername(), comment.getUser().getEmail()) || isAdmin){
            comment.setBody(commentBodyDto.getComment());
        } else {
			String messageEx = messageSource.getMessage("error.authorities.update.entity", new Object[]{id}, Locale.US);
			throw new ForbiddenAction(messageEx);
		}
    }
}

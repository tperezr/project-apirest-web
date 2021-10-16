package com.alkemy.ong.controller;

import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.alkemy.ong.exception.ForbiddenAction;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.alkemy.ong.dto.ErrorResponse;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.exception.ONGBadRequestException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
@AllArgsConstructor
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private final MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.status(status).body(makeErrorResponse(status, messageSource.getMessage("error.request-body",null,Locale.US), request));
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.status(status).body(makeErrorResponse(status, messageSource.getMessage("error.entity.invalid-id", null, Locale.US), request));
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("\n"));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(HttpStatus.BAD_REQUEST, errors, request));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request){
		return ResponseEntity.status(BAD_REQUEST)
				.body(makeErrorResponse(BAD_REQUEST,e.getMessage(),request));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException e, WebRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(makeErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request));
	}
	
	@ExceptionHandler(ONGBadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequestException(ONGBadRequestException e, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request));
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
		String error = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(makeErrorResponse(HttpStatus.BAD_REQUEST, error, request));
	}

	@ExceptionHandler(ForbiddenAction.class)
	public ResponseEntity<ErrorResponse> handleForbiddenAction(ForbiddenAction e,WebRequest request){
		return ResponseEntity.status(FORBIDDEN)
				.body(makeErrorResponse(FORBIDDEN, e.getMessage(),request));
	}
	
	private ErrorResponse makeErrorResponse(HttpStatus httpStatus, String message, WebRequest request) {
		return ErrorResponse.builder()
				.timestamp(new Date())
				.statusCode(httpStatus.value())
				.message(message)
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
	}

}

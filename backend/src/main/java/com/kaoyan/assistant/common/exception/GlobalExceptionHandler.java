package com.kaoyan.assistant.common.exception;

import com.kaoyan.assistant.common.model.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public org.springframework.http.ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        if (exception.getStatus().is5xxServerError()) {
            log.error("business exception", exception);
        } else {
            log.warn("business exception: status={}, code={}, message={}",
                    exception.getStatus().value(), exception.getCode(), exception.getMessage());
        }
        return org.springframework.http.ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.failure(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("validation failed: {}", message);
        return ApiResponse.failure(4000, message);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            MaxUploadSizeExceededException.class,
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequestException(Exception exception) {
        String message = "invalid input";
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            message = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining("; "));
        } else if (exception instanceof MaxUploadSizeExceededException) {
            message = "file size exceeds limit";
        } else if (exception instanceof MissingServletRequestPartException missingServletRequestPartException) {
            message = missingServletRequestPartException.getRequestPartName() + " is required";
        } else if (exception instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
            message = missingServletRequestParameterException.getParameterName() + " is required";
        } else if (exception instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
            message = methodArgumentTypeMismatchException.getName() + " is invalid";
        } else if (exception instanceof HttpMessageNotReadableException httpMessageNotReadableException
                && httpMessageNotReadableException.getMostSpecificCause() != null) {
            message = httpMessageNotReadableException.getMostSpecificCause().getMessage();
        }
        log.warn("bad request: {}", message);
        return ApiResponse.failure(4000, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException exception) {
        log.warn("access denied: {}", exception.getMessage());
        return ApiResponse.failure(4030, "forbidden");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception exception) {
        log.error("unhandled exception", exception);
        return ApiResponse.failure(5000, "internal server error");
    }
}

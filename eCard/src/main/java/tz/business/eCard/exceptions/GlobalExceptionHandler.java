package tz.business.eCard.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tz.business.eCard.dtos.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidFileException(InvalidFileException e) {
        log.error("Invalid file exception: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("Max upload size exceeded: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("File size exceeds maximum allowed limit")
                        .build());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error("Missing request part: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Required file parameter is missing")
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message("An unexpected error occurred")
                        .error(e.getMessage())
                        .build());
    }
}
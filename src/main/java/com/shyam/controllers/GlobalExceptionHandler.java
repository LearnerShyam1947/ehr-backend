package com.shyam.controllers;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shyam.dto.response.ApiErrorResponse;
import com.shyam.exceptions.AuthorizationMissingException;
import com.shyam.exceptions.CustomAccessDeniedException;
import com.shyam.exceptions.EntityAlreadyExistsException;
import com.shyam.exceptions.RequestedEntityNotFoundException;
import com.shyam.exceptions.TokenExpiredException;
import com.shyam.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest request;

    @ExceptionHandler(value = EntityAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserExistsException(EntityAlreadyExistsException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        errorResponse.setPath(request.getServletPath());
        errorResponse.setError(e.getMessage());
        errorResponse.setStatusCode(HttpStatus.CONFLICT.value());
        errorResponse.setType(Utils.getClassName(e));

        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleOTPExpiredException(TokenExpiredException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        errorResponse.setPath(request.getServletPath());
        errorResponse.setError(e.getMessage());
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setType(Utils.getClassName(e));

        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }
   
    @ExceptionHandler(value = RequestedEntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRequestedEntityNotFoundException(RequestedEntityNotFoundException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        errorResponse.setPath(request.getServletPath());
        errorResponse.setError(e.getMessage());
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setType(Utils.getClassName(e));

        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }
    
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        errorResponse.setPath(request.getServletPath());
        errorResponse.setError("Invalid user credentials provided");
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setType(Utils.getClassName(e));

        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }
    
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        errorResponse.setPath(request.getServletPath());
        errorResponse.setError("Bad request : submit form without error");
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setType(Utils.getClassName(e)); 

        BindingResult bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();

        Map<String, Object> errors = allErrors.stream()
        .collect(Collectors.toMap(
            error -> ((FieldError)error).getField(),
            ObjectError::getDefaultMessage
        ));
        errorResponse.setErrors(errors);

        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }

    @ExceptionHandler(value = SignatureException.class)
    public ResponseEntity<ApiErrorResponse> handleSignatureException(SignatureException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        errorResponse.setPath(request.getServletPath());
        errorResponse.setError("JWT signature does not match locally computed signature.");
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setType(Utils.getClassName(e));

        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }

    @ExceptionHandler(value = CustomAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomAccessDeniedException(CustomAccessDeniedException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();      

        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setError(e.getMessage());
        errorResponse.setPath(e.getPath());
        errorResponse.setType(Utils.getClassName(e));
        
        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }
    
    @ExceptionHandler(value = AuthorizationMissingException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationHeaderMissingException(AuthorizationMissingException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();      

        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setError(e.getMessage());
        errorResponse.setPath(e.getPath());
        errorResponse.setType(Utils.getClassName(e));
        
        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }
    

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRunTimeException(RuntimeException e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();      

        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setError(e.getMessage());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setType(Utils.getClassName(e));
        
        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();      

        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError(e.getMessage());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setType(Utils.getClassName(e));
        
        return ResponseEntity
                .status(errorResponse.getStatusCode())
                .body(errorResponse);
    }

}

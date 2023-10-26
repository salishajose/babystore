package com.brocamp.babystore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFoundException(
            ProductNotFoundException productNotFoundException, WebRequest webRequest
            ){
        ErrorMessage errorMessage = new ErrorMessage(productNotFoundException.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(OrderProductsNotFoundException.class)
    public ResponseEntity<?> handleOrderProductsNotFoundException(
            OrderProductsNotFoundException orderProductsNotFoundException, WebRequest webRequest
    ){
        ErrorMessage errorMessage = new ErrorMessage(orderProductsNotFoundException.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(
            Exception exception, WebRequest webRequest
    ){
        ErrorMessage errorMessage = new ErrorMessage(exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(OrderDetailsNotFoundException.class)
    public ResponseEntity<?> handleOrderDetailsNotFoundException(
            OrderDetailsNotFoundException orderDetailsNotFoundException, WebRequest webRequest
    ){
        ErrorMessage errorMessage = new ErrorMessage(orderDetailsNotFoundException.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<?> handleUserBlockedException(
            UserBlockedException userBlockedException, WebRequest webRequest
    ){
        ErrorMessage errorMessage = new ErrorMessage(userBlockedException.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<?> handleAddressNotFoundException(
            AddressNotFoundException addressNotFoundException, WebRequest webRequest
    ){
        ErrorMessage errorMessage = new ErrorMessage(addressNotFoundException.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DocumentException.class)
    public ResponseEntity<?> handleDocumentException(
            DocumentException documentException, WebRequest webRequest
    ){
        ErrorMessage errorMessage = new ErrorMessage(documentException.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }
}

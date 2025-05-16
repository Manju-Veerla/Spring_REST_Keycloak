package eu.unite.recruiting.exception;

import eu.unite.recruiting.model.dto.CustomError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler class for handling exceptions thrown by the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WorkshopAlreadyExistException.class)
    public ResponseEntity<CustomError> handleUserAlreadyExistException(WorkshopAlreadyExistException ex) {
        CustomError customErrorResponse = CustomError.builder()
                .header("WorkshopAlreadyExist")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(WorkshopNotFoundException.class)
    public ResponseEntity<CustomError> handleWorkshopNotFoundException(WorkshopNotFoundException ex) {
        CustomError customErrorResponse = CustomError.builder()
                .header("WorkshopNotFound")
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.error("Validation failed for workshop : {}", ex);
        String defaultMessage;
        if (ex.getBindingResult() != null && ex.getBindingResult().getFieldError() != null) {
            defaultMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        } else {
            defaultMessage = "Validation error occurred, but no specific message is available.";
        }
        CustomError customError = CustomError.builder()
                .header("Invalid data")
                .message(defaultMessage)
                .build();
        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<?> handleInvalidUserException(InvalidUserException ex) {
        LOGGER.error("User information not available for workshop : {}", ex);
        CustomError customError = CustomError.builder()
                .header("User doesnot exist")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(customError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<?> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        LOGGER.error("User registration failed for workshop : {}", ex);
        CustomError customError = CustomError.builder()
                .header("User already registered")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWorkshopDataException.class)
    public ResponseEntity<?> handleInvalidWorkshopDataException(InvalidWorkshopDataException ex) {
        LOGGER.error("Invalid data for workshop : {}", ex);
        CustomError customError = CustomError.builder()
                .header("Invalid data")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationDoesnotExistException.class)
    public ResponseEntity<?> handleRegistrationDoesnotExistException(RegistrationDoesnotExistException ex) {
        LOGGER.error("Registration data not available : {}", ex);
        CustomError customError = CustomError.builder()
                .header("Registration data not available")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(customError, HttpStatus.NOT_FOUND);
    }

}

package kz.iitu.orken.medical_managament_system.Exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class MyExceptionHandlerTest {
    MyExceptionHandler myExceptionHandler = new MyExceptionHandler();

    @Test
    void testHandleNotAllowedException() {
        ResponseEntity<String> result = myExceptionHandler.handleNotAllowedException(new NotAllowedException("message"), null);
        Assertions.assertEquals(new ResponseEntity<>("message", HttpStatus.FORBIDDEN), result);
    }

    @Test
    void testHandleNotFoundException() {
        ResponseEntity<String> result = myExceptionHandler.handleNotFoundException(new NotFoundException("message"), null);
        Assertions.assertEquals(new ResponseEntity<>("message", HttpStatus.FORBIDDEN), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
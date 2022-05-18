package kz.iitu.orken.medical_managament_system.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class TransactionExceptionTest {
    @Mock
    Object backtrace;
    @Mock
    Throwable cause;
    //Field stackTrace of type StackTraceElement - was not mocked since Mockito doesn't mock a Final class when 'mock-maker-inline' option is not set
    @Mock
    List<Throwable> SUPPRESSED_SENTINEL;
    @Mock
    List<Throwable> suppressedExceptions;
    @InjectMocks
    TransactionException transactionException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
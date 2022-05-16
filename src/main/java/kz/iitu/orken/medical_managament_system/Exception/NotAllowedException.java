package kz.iitu.orken.medical_managament_system.Exception;

public class NotAllowedException extends RuntimeException {
    public NotAllowedException(String message) {
        super(message);
    }

    public NotAllowedException(Throwable throwable) {
        super(throwable);
    }
}

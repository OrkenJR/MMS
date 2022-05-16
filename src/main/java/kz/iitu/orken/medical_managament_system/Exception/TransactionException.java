package kz.iitu.orken.medical_managament_system.Exception;

public class TransactionException extends RuntimeException{
    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(Throwable throwable) {
        super(throwable);
    }
}

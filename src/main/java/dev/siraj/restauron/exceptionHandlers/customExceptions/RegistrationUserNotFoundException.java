package dev.siraj.restauron.exceptionHandlers.customExceptions;

public class RegistrationUserNotFoundException extends RuntimeException{

    public RegistrationUserNotFoundException(){
        super();
    }

    public RegistrationUserNotFoundException(Throwable cause) {
        super(cause);
    }

    public RegistrationUserNotFoundException(String message) {
        super(message);
    }

    public RegistrationUserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationUserNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

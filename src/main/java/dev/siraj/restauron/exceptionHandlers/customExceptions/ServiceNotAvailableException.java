package dev.siraj.restauron.exceptionHandlers.customExceptions;

public class ServiceNotAvailableException extends RuntimeException {

  private final String customerLandingPageMessage;



    public ServiceNotAvailableException(String message, String customerLandingPageMessage) {
        super(message);
        this.customerLandingPageMessage = customerLandingPageMessage;
    }

    public String getCustomerLandingPageMessage() {
        return customerLandingPageMessage;
    }
}

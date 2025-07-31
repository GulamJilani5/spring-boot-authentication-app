package AuthCentral.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super("User with email " + message + " already exists");
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super("User with email " + message + " already exists ", cause);
    }
}

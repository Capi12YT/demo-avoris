package demo.avoris.domain.exception;

public class InvalidCheckIn extends RuntimeException {
    public InvalidCheckIn(String message) {
        super(message);
    }
}

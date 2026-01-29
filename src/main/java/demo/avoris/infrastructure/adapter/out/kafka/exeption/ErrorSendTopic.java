package demo.avoris.infrastructure.adapter.out.kafka.exeption;

public class ErrorSendTopic extends RuntimeException {
    public ErrorSendTopic(String message) {
        super(message);
    }
}

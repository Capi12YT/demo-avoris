package demo.avoris.infrastructure.adapter.in.kafka.exeption;

public class ErrorConsumeTopic extends RuntimeException {
    public ErrorConsumeTopic(String message) {
        super(message);
    }
}

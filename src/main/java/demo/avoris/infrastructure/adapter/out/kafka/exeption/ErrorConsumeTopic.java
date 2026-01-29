package demo.avoris.infrastructure.adapter.out.kafka.exeption;

public class ErrorConsumeTopic extends RuntimeException {
    public ErrorConsumeTopic(String message) {
        super(message);
    }
}

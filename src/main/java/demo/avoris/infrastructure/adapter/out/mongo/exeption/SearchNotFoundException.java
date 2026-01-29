package demo.avoris.infrastructure.adapter.out.mongo.exeption;

public class SearchNotFoundException extends RuntimeException {
    public SearchNotFoundException(String message) {
        super(message);
    }
}

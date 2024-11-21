package verifier;

public class CounterexampleCallOrderException extends RuntimeException {
    public CounterexampleCallOrderException(String message) {
        super(message);
    }
}

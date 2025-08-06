package exceptions;

public class DatabaseException extends RuntimeException {
    private final String errorCode;

    public DatabaseException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
    }

    public DatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DatabaseException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("DatabaseException{errorCode='%s', message='%s'}", errorCode, getMessage());
    }
}

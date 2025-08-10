package exceptions;

/** Dùng khi dữ liệu đầu vào không hợp lệ. */
public class InvalidDataException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String field; // có thể null

    /** field = null */
    public InvalidDataException(String message) {
        super(message);
        this.field = null;
    }

    /** field = null, có cause */
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
    }

    /** chỉ định field vi phạm */
    public InvalidDataException(String field, String message) {
        super(message);
        this.field = field;
    }

    /** chỉ định field + cause */
    public InvalidDataException(String field, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

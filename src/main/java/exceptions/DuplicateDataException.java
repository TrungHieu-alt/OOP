package exceptions;

/** Dùng khi vi phạm unique/trùng dữ liệu . */
public class DuplicateDataException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String field;
    private final String value;

    public DuplicateDataException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.value = null;
    }

    public DuplicateDataException(String field, String value, String message) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public DuplicateDataException(String field, String value, String message, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.value = value;
    }

    public String getField() { return field; }
    public String getValue() { return value; }
}

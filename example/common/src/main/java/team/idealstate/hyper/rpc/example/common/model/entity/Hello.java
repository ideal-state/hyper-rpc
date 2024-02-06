package team.idealstate.hyper.rpc.example.common.model.entity;

import java.time.Instant;

/**
 * <p>Hello</p>
 *
 * <p>创建于 2024/2/6 21:47</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class Hello {

    private String message;
    private Instant timestamp;

    public Hello() {
    }

    public Hello(String message, Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hello)) {
            return false;
        }
        final Hello hello = (Hello) o;

        if (getMessage() != null ? !getMessage().equals(hello.getMessage()) : hello.getMessage() != null) {
            return false;
        }
        return getTimestamp() != null ? getTimestamp().equals(hello.getTimestamp()) : hello.getTimestamp() == null;
    }

    @Override
    public int hashCode() {
        int result = getMessage() != null ? getMessage().hashCode() : 0;
        result = 31 * result + (getTimestamp() != null ? getTimestamp().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

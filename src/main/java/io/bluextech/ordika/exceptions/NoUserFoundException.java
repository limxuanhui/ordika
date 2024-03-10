package io.bluextech.ordika.exceptions;
/* Created by limxuanhui on 14/1/24 */

public class NoUserFoundException extends RuntimeException {

    public NoUserFoundException(String message) {
        super(message);
    }

    public NoUserFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}

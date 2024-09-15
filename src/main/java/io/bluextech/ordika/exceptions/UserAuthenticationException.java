package io.bluextech.ordika.exceptions;
/* Created by limxuanhui on 14/1/24 */

public class UserAuthenticationException extends RuntimeException {

    public UserAuthenticationException(String message) {
        super(message);
    }

    public UserAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}

package cn.seisys.auth.authentication;

import javax.security.auth.login.AccountException;

public class BadCaptchaException extends AccountException {

    private static final long serialVersionUID = 1498349563916294614L;

    /**
     * Constructs a AccountNotFoundException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public BadCaptchaException() {
        super();
    }

    /**
     * Constructs a AccountNotFoundException with the specified
     * detail message. A detail message is a String that describes
     * this particular exception.
     *
     * <p>
     *
     * @param msg the detail message.
     */
    public BadCaptchaException(String msg) {
        super(msg);
    }
}
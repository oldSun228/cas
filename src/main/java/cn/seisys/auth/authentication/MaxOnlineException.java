package cn.seisys.auth.authentication;

import javax.security.auth.login.AccountException;

public class MaxOnlineException extends AccountException {

    private static final long serialVersionUID = 1498349563916294614L;

    /**
     * Constructs a AccountNotFoundException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public MaxOnlineException() {
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
    public MaxOnlineException(String msg) {
        super(msg);
    }
}
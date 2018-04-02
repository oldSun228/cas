package cn.seisys.auth.authentication;

import javax.security.auth.login.LoginException;

/**
 * Created by fgs on 2018/1/11.
 * 类说明
 */
public class FailedLoginAndErrorFiveNumException extends LoginException {
    private static final long serialVersionUID = 802556922354616281L;

    public FailedLoginAndErrorFiveNumException() {
        super();
    }


    public FailedLoginAndErrorFiveNumException(String msg) {
        super(msg);
    }
}

package cn.seisys.auth.web.util;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Service;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
@Service
public class PasswordHelper {

    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    private String algorithmName = "md5";
    private int hashIterations = 2;

    public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }

    public String encryptPassword(String password) {

        String salt = randomNumberGenerator.nextBytes().toHex();

        String newPassword = new SimpleHash(
                algorithmName,
                password,
                ByteSource.Util.bytes(salt),
                hashIterations).toHex();

        return newPassword;
    }
    
    public String encryptPassword(String password, String salt) {

        String newPassword = new SimpleHash(
                algorithmName,
                password,
                ByteSource.Util.bytes(salt),
                hashIterations).toHex();

        return newPassword;
    }
}
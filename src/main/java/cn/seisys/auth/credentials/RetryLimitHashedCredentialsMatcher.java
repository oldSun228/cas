package cn.seisys.auth.credentials;

import java.util.concurrent.atomic.AtomicInteger;


import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */

public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
	
	
    private Cache<String, AtomicInteger> passwordRetryCache;

    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
    	// 获取密码重置的cache
        passwordRetryCache = cacheManager.getCache("passwordRetryCache"); 
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
    	// 获取用户名
        String username = (String)token.getPrincipal(); 
        // retry count + 1
        // 获取重试的次数
        AtomicInteger retryCount = passwordRetryCache.get(username);  
        if(retryCount == null) {
        	// 如果没有则进行初始化操作
            retryCount = new AtomicInteger(0);  
            passwordRetryCache.put(username, retryCount);
        }

        // 密码出错次数>5
        if(retryCount.incrementAndGet() > 5) {
            // if retry count > 5 throw 异常
        	// 10分钟后重置
            throw new ExcessiveAttemptsException(); //返回密码过时的错误
        }

        // 调用super方法密码的测试
        // 调用super的方法进行密码令牌的测试
        boolean matches = super.doCredentialsMatch(token, info); 
        // 如果密码匹配
        if(matches) {
            // clear retry count
        	// 从cache中清除，清楚缓存的对象
        	
            passwordRetryCache.remove(username); 
        }
        
        // 返回是否匹配
        return matches;  
    }
}

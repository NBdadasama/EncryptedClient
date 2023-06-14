package com.middleware.demo.roles;

import com.rabbitmq.client.impl.recovery.RetryContext;
import org.springframework.retry.RetryCallback;

public interface CustomRetryListener {

    /**
     * 最后一次重试失败回调
     * @param context
     * @param callback
     * @param throwable
     * @param <E>
     * @param <T>
     */
    public <E extends Throwable, T> void lastRetry(RetryContext context, RetryCallback<T,E> callback, Throwable throwable);

    /**
     * 每次失败的回调
     * @param context
     * @param callback
     * @param throwable
     * @param <E>
     * @param <T>
     */
    public <E extends Throwable, T> void onRetry(RetryContext context, RetryCallback<T,E> callback, Throwable throwable);
}


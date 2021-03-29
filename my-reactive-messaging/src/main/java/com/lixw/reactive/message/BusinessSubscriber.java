package com.lixw.reactive.message;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author lixw
 * @date 2021/03/29
 */
public class BusinessSubscriber<T> implements Subscriber<T> {

    private Subscription subscription;

    private int count = 0;

    private final int maxRequest;

    public BusinessSubscriber(int maxRequest) {
        this.maxRequest = maxRequest;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(maxRequest);
    }

    @Override
    public void onNext(T t) {
        if (++count > 2) { // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
            subscription.cancel();
            return;
        }
        System.out.println("收到数据：" + t);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("遇到异常：" + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据完成");
    }
}

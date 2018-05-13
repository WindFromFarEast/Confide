package net.confide.factory;

import net.confide.common.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {

    //单例模式
    private static final Factory instance;
    private final Executor executor;

    static {
        instance = new Factory();
    }

    private Factory() {
        //新建一个容量为4的线程池
        executor = Executors.newFixedThreadPool(4);
    }

    public static Application app() {
        return Application.getInstance();
    }

    /**
     * 异步执行
     */
    public static void runOnAsyn(Runnable runnable) {
        //将runnable交给线程池执行
        instance.executor.execute(runnable);
    }
}

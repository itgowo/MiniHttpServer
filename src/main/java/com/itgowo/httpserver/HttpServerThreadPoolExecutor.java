package com.itgowo.httpserver;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class HttpServerThreadPoolExecutor extends ThreadPoolExecutor {


    public HttpServerThreadPoolExecutor() {
        super(1, 4, 2, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), new HttpServerThreadFactory());
    }

    public static class HttpServerThreadFactory implements ThreadFactory {
        private ThreadGroup group;
        private AtomicInteger number=new AtomicInteger(1);
        private HttpServerThreadFactory(){
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(group, r);
            thread.setDaemon(true);
            thread.setName("itgowo.HttpServerThread-" +number.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}

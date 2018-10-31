package com.itgowo.httpserver;

import java.util.concurrent.*;

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
    public static class HttpServerThreadFactory implements ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("itgowo.HttpServerThread");
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}

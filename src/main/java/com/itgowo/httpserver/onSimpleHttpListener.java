package com.itgowo.httpserver;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class onSimpleHttpListener implements onHttpListener {
    @Override
    public void onServerStarted(int port) {

    }

    @Override
    public void onServerStoped() {

    }

    @Override
    public void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {

    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}

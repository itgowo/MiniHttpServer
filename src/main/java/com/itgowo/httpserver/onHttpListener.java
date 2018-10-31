package com.itgowo.httpserver;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public interface onHttpListener {
    void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;

    void onError(Throwable throwable);
}

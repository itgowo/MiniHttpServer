package com.itgowo.httpserver;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * Copyright (c) 2018.
 *  @author lujianchao
 *  MiniHttpServer
 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 */

public class MiniHttpServer extends Thread {
    private static final String TAG = "itgowo.httpNioServer";
    private ThreadPoolExecutor threadPoolExecutor;
    private ServerSocketChannel serverSocketChannel;
    private SocketAddress socketAddress;
    private Selector selector;
    private onHttpListener httpListener;
    private FileManager fileManager;
    private boolean isRunning = false;
    private boolean isBlocking = false;

    public void init(boolean isBlocking, InetSocketAddress inetSocketAddress, String webDir, onHttpListener onHttpListener) {
        this.isBlocking = isBlocking;
        this.socketAddress = inetSocketAddress;
        this.httpListener = onHttpListener;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(this.isBlocking);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            threadPoolExecutor = new HttpServerThreadPoolExecutor();
            fileManager = new FileManager(webDir);
        } catch (IOException e) {
            httpListener.onError(e);
        }
    }

    public void startServer() {
        try {
            serverSocketChannel.bind(socketAddress);
        } catch (IOException e) {
            httpListener.onError(e);
        }
        isRunning = true;
        super.start();
    }

    @Override
    public synchronized void start() {
        startServer();
    }

    public synchronized void stopServer() {
        isRunning = false;
    }

    @Override
    public void run() {
        try {
            loopListener();
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    private void loopListener() throws IOException {
        while (isRunning) {
            int result = selector.select();
            if (result <= 0) continue;

            Iterator keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                // 删除已选的key以防重负处理
                keys.remove();
                // 客户端请求连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    // 获得和客户端连接的通道
                    SocketChannel channel = server.accept();
                    // 设置成非阻塞
                    channel.configureBlocking(isBlocking);
                    // 在客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限
                    channel.register(this.selector, SelectionKey.OP_READ, UUID.randomUUID().toString());
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    threadPoolExecutor.execute(new HttpHander(socketChannel, (String) key.attachment(), fileManager, httpListener));

                }
            }
        }
    }
}
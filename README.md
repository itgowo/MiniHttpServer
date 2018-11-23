#### Mini Http Server for Java (Android)

[MiniHttpServer](https://github.com/itgowo/MiniHttpServer)

[最新版本](https://bintray.com/itgowo/maven/MiniHttpServer)

### 一：开发环境
Mac OS 10、Java 1.8、IDEA（Gradle工程）

### 二：介绍

一款基于Java Nio实现的Http解析框架，支持常见的请求解析和逻辑；采用单线程解析多线程业务处理方案，内置线程池方便线程管理；支持静态文件下载；支持表单参数和文件上传，支持PUT文件上传。解析成功后返回HttpRequest和HttpResponse。除常见的接口请求返回外，HttpResponse可以向客户端发送跨域请求结果，也可以发送文件，支持区分附件模式。

### 三：特点

* 纯Java API实现，性能好
* 基于Java Nio，异步机制，相比传统IO，有更高的性能。
* 体积小，代码少，支持Http部分协议，满足绝大部分需求。
* 支持POST表单数据和多文件上传。
* 支持PUT上传文件，自动保存到file目录。
* 支持发送文件到客户端。
* 支持发送重定向等基本http协议内容。
* 支持反馈跨域请求。
* 支持自定义header。

### 四：引入
1. Maven
```
<dependency>
  <groupId>com.itgowo</groupId>
  <artifactId>MiniHttpServer</artifactId>
  <version>0.0.16</version>
  <type>pom</type>
</dependency>
```

2. Gradle
```
implementation 'com.itgowo:MiniHttpServer:0.0.16'
```

### 五：初始化(库Jar中有Demo类，可以参考)
[Demo.java](https://github.com/itgowo/MiniHttpServer/blob/master/src/main/java/com/itgowo/httpserver/demo.java)

1. 创建MiniHttpServer
MiniHttpServer 继承自Thread，复写了Thread.start()方法，与MiniHttpServer.startServer()方法作用相同，不会冲突。
    
```
MiniHttpServer miniHttpServer = new MiniHttpServer(); 
```
    
    
2. 设置初始信息

 ```
 public void init(boolean isBlocking, InetSocketAddress inetSocketAddress, String webDir, onHttpListener onHttpListener)
 ```
 
|    参数    |       推荐值       |      说明     |
|---|---|---|
|   isBlocking  |     false    |是否用阻塞模式，推荐false，Nio特点就是非阻塞|
|inetSocketAddress|InetSocketAddress(port)|服务使用哪个端口|
|webDir|"/web"|服务器静态目录，file和temp目录会在webDir中|
|onHttpListener|new 实现类|服务器接收Http请求回调，如果是文件则FileList中有文件信息|

3.设置文件存储策略

当有文件上传到服务器时，默认保存在webDir里的file目录下，创建UUID命名的目录，将上传的文件放入其中，文件名已Http信息fileName命名，防止重名文件冲突。例 ***web/file/02e86423-d1bd-4218-8f70-a7c73c71bf62/test.png***
默认每次server初始化后执行清理功能。需要手动执行使用这个方法***miniHttpServer.getFileManager().cleanOldFile();***

```
  httpServer.setFileLimit(long fileSize, long fileLastTime);
```

|参数|推荐值|说明|
|---|---|---|
|fileSize|1024 * 1024 * 500|file文件夹存储阈值，超过执行清理功能，|
|fileLastTime|1000 * 60 * 60 * 24 * 7|最后编辑时间计算存储时间，默认保留7天内文件|

4. onHttpListener类

```
public void onError(Throwable throwable)`

public void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception`
```


### 六：情景

#### 1. 获取header

```
    Map<String, String> headers = httpRequest.getHeaders();
    boolean hasContentType = httpRequest.containsHeader(HttpHeaderNames.CONTENT_TYPE);
    String contentType = httpRequest.getHeaders().get(HttpHeaderNames.CONTENT_TYPE);
```

#### 2. 获取Parms，参数操作，来源一:url中"?"后面解析出来的参数键值对;来源二:POST表单参数解析
```
    Map<String, String> parms = httpRequest.getParms();
    String userId = httpRequest.getParms().get("userId");
```
#### 3. 客户端GET请求资源
``` 
    if (HttpMethod.GET == httpRequest.getMethod()) {
        if (httpRequest.getUri().equalsIgnoreCase("/")) {
            httpRequest.setUri("/index.html");
        }
        //缓存策略，浏览器指定时间内只获取一次文件,如果sendFile()包含cacheControl参数，则不需要在设置，设置了以单独设置为准。没有cacheControl的方法则默认没有此参数
        httpResponse.addHeader(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.MAX_AGE + "=3600");

//      httpResponse.sendFile(httpNioServer.getFileManager().getFile(httpRequest.getUri()));
//      httpResponse.sendFile(httpNioServer.getFileManager().getFile(httpRequest.getUri()), true);
        //cacheControl参数添加了不一定起作用，如果单独加了header，则此方法参数无效
        httpResponse.sendFile(httpNioServer.getFileManager().getFile(httpRequest.getUri()), HttpStatus.OK, 3600, true);
    }
```

#### 4. 客户端POST请求，POST请求，常见的Body传Json文本或者表单上传文件
```
    if (HttpMethod.POST == httpRequest.getMethod()) {
        if (httpRequest.isMultipart_formdata()) {
            Map<String, File> fileMap = httpRequest.getFileList();
          //  httpResponse.sendData(HttpStatus.OK);
            httpResponse.setData(Arrays.toString(fileMap.values().toArray())).sendData(HttpStatus.OK);
        } else {
            String requestBody = httpRequest.getBody();
            httpResponse.setData(requestBody).sendData(HttpStatus.OK);
        }
    }
```
#### 5. 客户端OPTIONS请求，OPTIONS请求，跨域请求最多的是ajax发出的，应对web请求
```
    if (HttpMethod.OPTIONS == httpRequest.getMethod()) {
        httpResponse.sendOptionsResult();
    }
```
#### 6. 客户端PUT请求，跟POST表单上传文件不同，Http中Body默认为一个文件，临时存在temp目录，PUT如果需要传递文件名请在headers中添加自定义数据。
```
    if (HttpMethod.PUT == httpRequest.getMethod()) {
        Map<String, File> fileMap = httpRequest.getFileList();
        httpResponse.sendData(HttpStatus.OK);
    }
```
#### 7. 客户端其他请求，HEAD、DELETE、TRACE、CONNECT、PATCH 等不常见，需要自己返回结果即可，无特殊需求。


#### 8. 重定向
```
    httpResponse.sendRedirect("http://www.baidu.com");
```

## 七：关键类
### HttpRequest

| 变量 | 说明 |
|---|---|
|socketChannel|与客户端连接通信的连接通道|
|clientId|与Nio中Channel绑定，连接唯一标记|
|method|Http报文中的请求方式（GET/POST/PUT和DELETE等|
|uri|Http报文中Method后面的路径，最开始以"/"开始|
|protocolVersion|Http协议版本|
|queryParameterString|url中"?"后面参数原始数据|
|parms|来源一:url中"?"后面解析出来的参数键值对;来源二:POST表单参数解析|
|headers|Http的header参数，Key-Value形式|
|remoteIp|客户端IP|
|contentLength|body长度|
|multipart_formdata|是否是表单数据，如果为true，则需要检查下是否有文件上传，fileList|
|body|PUT方式的Body会存到文件里，此处值为空，POST表单上传文件，Body也为空，请检查fileList|
|fileList|PUT方式的Body会存到fileList中，POST表单上传文件，Body也为空，也会存到fileList|

|其他方法|说明|
|---|---|
|isApplicationJson()|ContentType是不是Json类型|
|containsFile(String key)|fileList中是否包含该文件名的文件|
|containsHeader(String key)|headers中是否包含该参数|
|~~addToFileList(String key, File file)~~|添加文件到fileList，内部方法|
|~~addFileList(Map<String, File> fileList~~)|添加到fileList，内部方法|
|isGzip()|是否启用了Gzip，第一版不考虑加入此功能|
|isKeepAlive()|是否保持连接|
|sendData(ByteBuffer byteBuffer)|向客户端发送消息，最原始方式，http协议格式请用HttpResponse|
### HttpResponse
|变量|说明|
|---|---|
|socketChannel|与客户端连接通信的连接通道|
|httpRequest|httpRequest对象|
|status|HttpStatus常量|
|mimeType|内容类型ContentType|
|data|返回客户端数据，ByteBuffer或其子类|
|header|返回客户端Http的header信息|
|keepAlive|告诉客户端是否维持连接|


|其他方法|说明|
|---|---|
|addHeader(String name, String value)|添加返回客户端Http的header信息|
|sendOptionsResult()|返回Options请求回答，默认允许所有|
|sendRedirect(String newUrl)|让客户端重定向到新地址|
|sendFile(File file, HttpStatus httpStatus, boolean autoHtmltoNotAttachment)|向客户端发送符合Http协议的文件，如果是html文件，则没有attachment标记，浏览器不按附件下载，按网页打开|
|sendData(HttpStatus status)|向客户端发送信息，如果有body需先setBody()|
|getDefaultMimeType(File file)|根据文件扩展名返回ContentType|


### 八：小期待
以下项目都是我围绕远程控制写的子项目。都给star一遍吧。😍

|项目(Github)|语言|其他地址|运行环境|项目说明|
|---|---|---|---|---|
|[PackageMessage](https://github.com/itgowo/PackageMessage)|Java|[简书](https://www.jianshu.com/p/8a4a0ba2f54a)|运行Java的设备|TCP粘包与半包解决方案|
|[ByteBuffer](https://github.com/itgowo/ByteBuffer)|Java|[简书](https://www.jianshu.com/p/ba68224f30e4)|运行Java的设备|二进制处理工具类|
|[RemoteDataControllerForAndroid](https://github.com/itgowo/RemoteDataControllerForAndroid)|Java|[简书](https://www.jianshu.com/p/eb692f5709e3)|Android设备|远程数据调试Android端|
|[RemoteDataControllerForWeb](https://github.com/itgowo/RemoteDataControllerForWeb)|JavaScript|[简书](https://www.jianshu.com/p/75747ff4667f)|浏览器|远程数据调试控制台Web端|
|[RemoteDataControllerForServer](https://github.com/itgowo/RemoteDataControllerForServer)|Java|[简书](https://www.jianshu.com/p/3858c7e26a98)|运行Java的设备|远程数据调试Server端|
|[MiniHttpClient](https://github.com/itgowo/MiniHttpClient)|Java|[简书](https://www.jianshu.com/p/41b0917271d3)|运行Java的设备|精简的HttpClient|
|[MiniHttpServer](https://github.com/itgowo/MiniHttpServer)|Java|[简书](https://www.jianshu.com/p/de98fa07140d)|运行Java的设备|支持部分Http协议的Server|
|[DataTables.AltEditor](https://github.com/itgowo/DataTables.AltEditor)|JavaScript|[简书](https://www.jianshu.com/p/a28d5a4c333b)|浏览器|Web端表格编辑组件|

[我的小站：IT狗窝](http://itgowo.com)
技术联系QQ:1264957104

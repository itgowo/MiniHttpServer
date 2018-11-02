# MiniHttpServer
#### Mini Http Server for Java (android)
##### A Java Nio-based http server-side framework supports form upload and file download and extension.
[最新版本](https://bintray.com/itgowo/maven/MiniHttpServer)

 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 *  QQ:1264957104
### 开发环境
    Mac OS 10、Java 1.8、IDEA（Gradle工程）

### 介绍
        基于Java Nio实现Server，ServerSocket单独使用一个线程处理Selector事件并解析
    Http报文信息创建HttpRequest和HttpResponse，然后创建线程执行回调处理事件。
### 特点
    
* 纯Java API实现，没有引入依赖
* 基于Java Nio，异步实现消息机制。
* Http解析成功后在新线程里返回。
* 支持POST表单数据和多文件上传，支持PUT上传文件，自动保存到file目录。
* 支持发送文件到客户端。
* 支持发送重定向等基本http协议内容。

### 引入
1. Maven
```
<dependency>
  <groupId>com.itgowo</groupId>
  <artifactId>MiniHttpServer</artifactId>
  <version>0.0.8</version>
  <type>pom</type>
</dependency>
```

2. Gradle
```
implementation 'com.itgowo:MiniHttpServer:0.0.8'
```

### 初始化(发布到仓库的Jar中有Demo类，可以参考)
1. 创建MiniHttpServer
MiniHttpServer 继承自Thread，复写了Thread.start()方法，与MiniHttpServer.startServer()方法作用相同，不会冲突。
    
`MiniHttpServer httpNioServer = new MiniHttpServer();` 
    
    
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


 
3. onHttpListener类

`public void onError(Throwable throwable)`

`public void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception`



### 情景

#### 
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

## 关键类
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

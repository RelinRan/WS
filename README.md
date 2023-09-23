#### WS
Android WebSocket  
#### 资源
|名字|资源|
|-|-|
|AAR|[下载](https://github.com/RelinRan/WS/tree/main/aar)|
|GitHub |[查看](https://github.com/RelinRan/WS)|
|Gitee|[查看](https://gitee.com/relin/WS)|
#### Maven
1.build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:WS:2022.2023.9.23.1'
}
```
#### 初始化
配置权限
```
<uses-permission android:name="android.permission.INTERNET" />
```
连接服务器
```
Iws ws = Ws.client().connect("ws://124.222.224.186:8800");
```
#### 监听连接
```
long cid = ws.addConnectListener(new OnConnectListener() {

    @Override
    public void onConnect(boolean isOpen) {

    }
    
});
```
#### 监听消息
```
long mid = ws.addMessageListener(new OnMessageListener() {

    @Override
    public void onReceived(String message) {

    }
    
});
```
#### 监听发送
```
long sid = ws.addSendListener(new OnMessageListener() {

    @Override
    public void onSend(String message) {

    }
    
});
```
#### 移除监听
```
ws.remove(mid,cid,sid);
```
#### 清除监听
```
ws.clear();
```
#### 发送消息
```
ws.send("message");
```

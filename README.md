# **Youyun Android Media_SDK 集成**

## 前期准备
在您阅读此文档之前，我们假定您已具备基础的 Android 应用开发经验，并能够理解相关基础概念。

### 1、注册开发者帐号
开发者在集成游云Media功能前，需前往 [游云官方网站](http://www.17youyun.com) 注册创建游云开发者帐号。

### 2、下载 SDK

您可以到 [游云官方网站](http://www.17youyun.com) 下载游云 Media_SDK。下载包中分为如下两部分：

- Media Lib - 游云 Media库和相关库
- Media Demo

 Demo简单集成了电话会议功能，供您参考。
 
### 3、创建应用

您要进行应用开发之前，需要先在游云开发者平台创建应用。如果您已经注册了游云开发者帐号，请前往 [游云开发者平台](http://www.17youyun.com) 创建应用。

您创建完应用后，首先需要了解的是 App ClientID / Secret，它们是游云 SDK 连接服务器所必须的标识，每一个 App 对应一套 App ClientID / Secret。

## 集成开发

### 1、将您下载的库导入您项目的libs目录，Media_SDK需要如下库 

```
commons-fileupload-1.2.1.jar
commons-httpclient-3.1.jar
commons-lang-2.6.jar
youyun-protobuf-java-2.4.1.jar
youyun-wchat-android-1.0.1.jar
youyun-media-android-0.2.2.jar

armeabi
	liblinphonearmv5noneon.so
	libneon.so
armeabi-v7a
	liblinphonearmv7.so
	liblinphonearmv7noneon.so
	libneon.so
	libsrtp.so
x86
	liblinphonex86.so
	libneon.so
```
### 2、在您项目的AndroidManifest.xml文件中加入以下权限

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.WRITE_CONTACTS" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BROADCAST_STICKY" />
<uses-feature
    android:name="android.hardware.telephony"
    android:required="false" />
```
### 3、在您项目的AndroidManifest.xml文件中加入以下服务

```
<service android:name="com.weimi.media.WMediaService" />
<service android:name="com.ioyouyun.wchat.countly.OpenUDID_service" >
        <intent-filter>
            <action android:name="org.OpenUDID.GETUDID" />
        </intent-filter>
</service>
```
### 4、在您项目的AndroidManifest.xml文件中加入以下广播
```
<receiver android:name="com.ioyouyun.wchat.util.NetworkReceiver" >
    <intent-filter android:priority="2147483647" >
         <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
    </intent-filter>
</receiver>
```

### 5、调用SDK接口即可开发

```
WeimiInstance.getInstance().xxx();
WMedia.getInstance().xxx();
```
## 一、接口文档

### 1、初始化SDK

```
@param context 上下文
@param udid 设备唯一标识
@param clientId ClientId
@param secret 密钥
@param timeout 超时 单位秒
@return AuthResultData{boolean success, String userInfo} 
AuthResultData registerApp(Context context, String udid, String clientId, String secret, int timeout);

eg：WeimiInstance.getInstance().registerApp();
```
### 2、初始化SDK（测试）
```
@param context 上下文
@param udid 设备唯一标识
@param clientId ClientId
@param secret 密钥
@param timeout 超时 单位秒
@return AuthResultData{boolean success, String userInfo}
AuthResultData testRegisterApp(Context context, String udid, String clientId, String secret, int timeout);

eg：WeimiInstance.getInstance().testRegisterApp();
```
### 3、获取用户Id

```
@return 用户Id
String getUID();

eg：WeimiInstance.getInstance().getUID();
```
### 4、注销

```
@return true or false
boolean logout();

eg：WeimiInstance.getInstance().logout();
```
### 5、初始化MediaSDK

```
@param context
@param isOnlinePlatform 是否线上平台
void initWMediaSDK(Context context, boolean isOnlinePlatform);

eg：WMedia.getInstance().initWMediaSDK();
```
### 6、MediaSDK退出，停止WMediaService

```
void exit();

eg：WMedia.getInstance().exit();
```
### 7、 VOIP呼叫单个用户

```
@param toUid 用户Id
@return 是否可用
boolean call(String toUid);

eg：WMedia.getInstance().call();
```
### 8、 VOIP被叫方接听

```
@return 是否可用
boolean answer();

eg：WMedia.getInstance().answer();
```
### 9、 VOIP被叫方拒绝接听

```
@return 是否可用
boolean decline();

eg：WMedia.getInstance().decline();
```
### 10、 结束当前通话

```
@return 是否可用
boolean toHangUp();

eg：WMedia.getInstance().toHangUp();
```
### 11、 扬声器开关

```
@param isSpeake true:打开扬声器
@return 是否可用
boolean toggleSpeaker(boolean isSpeake);

eg：WMedia.getInstance().toggleSpeaker();
```
### 12、 话筒开关

```
@param isMicMuted true:被mute掉,不能说话
@return 是否可用
boolean toggleMicro(boolean isMicMuted);

eg：WMedia.getInstance().toggleMicro();
```
### 13、 conference呼进会议室

```
@param toGUid 会议室roomId
@param key 验证房间用
@return 是否可用
boolean callGroup(String toGUid, String key);

eg：WMedia.getInstance().callGroup();
```
### 14、conference申请房间

```
@param groupId 群Id
void conferenceRequestRoom(String groupId);

eg：WeimiInstance.getInstance().conferenceRequestRoom();
```
### 15、conference邀请用户

```
@param users 用户list
@param groupId 群Id
@param roomId 房间Id
@param key 验证房间用
void conferenceInviteUsers(List users, String groupId, String roomId, String key);

eg：WeimiInstance.getInstance().conferenceInviteUsers();
```
### 16、踢人

```
@param users 用户list
@param roomId 房间Id
void conferenceKick(List users, String roomId);

eg：WeimiInstance.getInstance().conferenceKick();
```
### 17、禁言

```
@param users 用户list
@param roomId 房间Id
void conferenceMute(List users, String roomId);

eg：WeimiInstance.getInstance().conferenceMute();
```
### 18、解除禁言

```
@param users 用户list
@param roomId 房间Id
void conferenceUnmute(List users, String roomId);

eg：WeimiInstance.getInstance().conferenceUnmute();
```
### 19、conference已加会议用户列表

```
@param roomId 房间Id
@param groupId 群Id
void conferenceRoommateList(String roomId, String groupId);

eg：WeimiInstance.getInstance().conferenceRoommateList();
```
## 二、异步接收消息
```
// 阻塞接收文件
WeimiNotice weimiNotice =  (WeimiNotice)NotifyCenter.clientNotifyChannel.take();
NoticeType type = weimiNotice.getNoticeType();
swiwch(type){
		case wmediastate: // 单打
			 String tag= (String) weimiNotice.getObject();
			 if("Connected".equals(tag)){
				 // 电话接通了
				 String IncomingName = weimiNotice.getWithtag();
			 }else if("CallEnd".equals(tag)){}
			 else if("Error".equals(tag)){}
			 else if(...){}
			 break;
		case conferenceResources: // conference
			 String json = (String) weimiNotice.getObject();
			 break;
	}
```

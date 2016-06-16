package com.example.media_sdk_dome;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ioyouyun.wchat.message.NoticeType;
import com.ioyouyun.wchat.message.NotifyCenter;
import com.ioyouyun.wchat.message.WChatException;
import com.ioyouyun.wchat.message.WeimiNotice;

/**
 * 必须接收NotifyCenter.clientNotifyChannel的消息
 * 电话和会议发的消息都在这里接收的
 *
 */
public class MsgHandler implements Runnable {

	private Context context;

	public MsgHandler(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		System.out.println("Message Handler start");
		WeimiNotice weimiNotice = null;
		while (true) {
			try { // 阻塞式的接收文件
				weimiNotice = (WeimiNotice) NotifyCenter.clientNotifyChannel.take();
			} catch (InterruptedException e) {
				break;
			}
			NoticeType type = weimiNotice.getNoticeType();
			Log.e("Bill", "type:"+type);
			if (NoticeType.exception == type) { // 对于异常类型的处理
				WChatException wChatException = (WChatException) weimiNotice.getObject();
				System.out.println("exception:" + wChatException);
				System.out.println(wChatException.getCause());
			} else if (NoticeType.wmediastate == type) {// Media SDK State Change
				Log.e("Bill", "wmediastate:"+weimiNotice.getObject().toString());
				Intent intent = new Intent();
				intent.setAction("MediaSDK_" + weimiNotice.getObject().toString());
				intent.putExtra("IncomingName", weimiNotice.getWithtag());
				intent.setPackage(context.getPackageName());
				context.sendBroadcast(intent);
			}else if (NoticeType.logging == type) {
				System.out.println("lodding...");
			} else if (NoticeType.conferenceResources == type) {
				Intent intent = new Intent();
				intent.setAction("conference_");
				intent.putExtra("content", (String) weimiNotice.getObject());
				intent.setPackage(context.getPackageName());
				context.sendBroadcast(intent);
			}/*else if(){
				// ......
			}*/

		}
		System.out.println("Message Handler stop");
		
	}
}

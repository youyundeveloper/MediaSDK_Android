package com.example.media_sdk_dome;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.message.CmdType;
import com.weimi.media.WMedia;

public class InCallActivity extends Activity implements OnClickListener{
	
	private TextView titleText;
	private Button refreshBtn, hangupBtn;
	private Button gagBtn, speakerBtn;
	private ListView callListView;
	private ArrayAdapter<String> adapter;
	private List<String> userlist = new ArrayList<String>();
	
	private String CallEnd = "MediaSDK_CallEnd";
	private String CallError = "MediaSDK_Error";
	private String CallConnected = "MediaSDK_Connected";
	
	private InnerReceiver receiver;
	private WeimiInstance weimiInstance;
	private WMedia media;
	
	private String groupId, roomId, inviteRoomKey;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		weimiInstance = WeimiInstance.getInstance();
		media = WMedia.getInstance();

		callListView = (ListView) findViewById(R.id.lv_call);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userlist);
		callListView.setAdapter(adapter);
		
		titleText = (TextView)findViewById(R.id.tv_title);
		
		refreshBtn = (Button) findViewById(R.id.btn_refresh);
		refreshBtn.setOnClickListener(this);
		hangupBtn = (Button) findViewById(R.id.btn_hangup);
		hangupBtn.setOnClickListener(this);
		gagBtn = (Button) findViewById(R.id.btn_gag);
		speakerBtn = (Button) findViewById(R.id.btn_speaker);
		gagBtn.setOnClickListener(this);
		speakerBtn.setOnClickListener(this);
		
		// 自定义广播回调接受消息
		IntentFilter filter = new IntentFilter();
		filter.addAction(CallEnd);
		filter.addAction(CallError);
		filter.addAction(CallConnected);
		filter.addAction("conference_");
		receiver = new InnerReceiver();
		registerReceiver(receiver, filter);
		
		
		Intent intent = getIntent();
		if (intent.getBooleanExtra("isConference", false)) {
			groupId = intent.getStringExtra("conferenceGroupId");
			roomId = intent.getStringExtra("conferenceRoomId");
			inviteRoomKey = intent.getStringExtra("conferenceRoomKey");

			titleText.setText("我的ID：" + weimiInstance.getUID() + "，当前房间："
					+ roomId + "，当前群组：" + groupId);
			
			weimiInstance.conferenceRoommateList(roomId, groupId);

		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_hangup:// 主叫挂掉电话
			media.toHangUp();
			finish();
			break;
		case R.id.btn_refresh:
			weimiInstance.conferenceRoommateList(roomId, groupId);
			break;

		case R.id.btn_gag:
			toggleMicro();
			break;
		case R.id.btn_speaker:
			toggleSpeaker();
			break;
		}
	}
	
	private boolean isSpeakerEnabled = false, isMicMuted = false;
	private void toggleSpeaker() {
		isSpeakerEnabled = !isSpeakerEnabled;
		media.toggleSpeaker(isSpeakerEnabled);
		if (isSpeakerEnabled) {
			speakerBtn.setText("扬声器已开启");
		} else {
			speakerBtn.setText("扬声器已关闭");
		}

	}
	
	private void toggleMicro() {
		isMicMuted = !isMicMuted;
		media.toggleMicro(isMicMuted);
		if (isMicMuted) {
			gagBtn.setText("麦已关闭");
		} else {
			gagBtn.setText("麦已打开");
		}
	}
	
	class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(CallEnd) || action.equals(CallError)) {
				finish();
			} else if (action.equals(CallConnected)) {
				System.out.println("电话接通了");
			} else if (action.equals("conference_")) {
				String content = intent.getStringExtra("content");
				try {
					JSONObject jsonObject = new JSONObject(content);
					switch (jsonObject.getInt("cmd")) {
					case CmdType.requesRoom:
						break;

					case CmdType.inviteUsers:
						// 成功
						break;

					case CmdType.beingInvited:
						break;

					case CmdType.list:
						String string = jsonObject.getString("userList");
						Log.v("userlist", string);
						JSONArray array = new JSONArray(string);
						userlist.clear();
						for (int i = 0; i < array.length(); i++) {
							userlist.add(array.getString(i));
						}
						adapter.notifyDataSetChanged();
						break;

					case CmdType.mute:
						break;

					case CmdType.unmute:
						break;

					case CmdType.kick:
						break;

					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}

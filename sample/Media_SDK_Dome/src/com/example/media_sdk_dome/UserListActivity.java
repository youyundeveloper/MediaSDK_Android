package com.example.media_sdk_dome;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.message.CmdType;
import com.weimi.media.WMedia;

public class UserListActivity extends Activity implements OnClickListener{

	private TextView uidText;
	private EditText uidEditText;
	private Button callBtn;
	private Button conferenceBtn;
	private EditText gidEdit;
	private Button addConfrenceBtn;
	private List<String> userIDlist = new ArrayList<String>();
	private WeimiInstance instance = WeimiInstance.getInstance();
	private String groupid = "10086"; // 群组, number，自定义
	
	private MyInnerReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);
		uidText = (TextView)findViewById(R.id.tv_uid);
		uidText.setText("我的Uid：" + instance.getUID());
		uidEditText = (EditText) findViewById(R.id.et_uid);
		callBtn = (Button) findViewById(R.id.btn_call);
		conferenceBtn = (Button) findViewById(R.id.btn_conference);
		callBtn.setOnClickListener(this);
		conferenceBtn.setOnClickListener(this);
		gidEdit = (EditText) findViewById(R.id.et_gid);
		addConfrenceBtn = (Button) findViewById(R.id.btn_add_confrence);
		addConfrenceBtn.setOnClickListener(this);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("conference_");
		filter.addAction("MediaSDK_IncomingReceived");
		receiver = new MyInnerReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_call://打电话
			Intent intent = new Intent();
			intent.setClass(this, InCallActivity.class);
		    startActivity(intent);

			WMedia.getInstance().call(uidEditText.getText().toString().trim());
			break;

		case R.id.btn_conference://会议邀请
			userIDlist.clear();
			userIDlist.add(uidEditText.getText().toString().trim());//将邀请的uid放到list中，申请房间成功后邀请
			instance.conferenceRequestRoom(groupid);//申请房间
			break;
			
		case R.id.btn_add_confrence://主动加入房间
			instance.conferenceRequestRoom(gidEdit.getText().toString().trim());//有房间则返回房间id和key
			// 注：接口返回信息在MyInnerReceiver中回调
			break;
			
		}
		
	}
	
	class MyInnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("conference_")) {
				String content = intent.getStringExtra("content");
				try {
					JSONObject jsonObject = new JSONObject(content);
					switch (jsonObject.getInt("cmd")) {
					case CmdType.requesRoom://申请房间
						String inviteGroupId = jsonObject.getString("groupid");
						JSONObject room = new JSONObject(jsonObject.getString("room"));
						String inviteRoomId = room.getString("id");
						String inviteRoomKey = room.getString("key");
						
						instance.conferenceInviteUsers(userIDlist, inviteGroupId, inviteRoomId, inviteRoomKey);

						Intent intent1 = new Intent();
						intent1.putExtra("isConference", true);
						intent1.putExtra("conferenceGroupId", inviteGroupId);
						intent1.putExtra("conferenceRoomId", inviteRoomId);
						intent1.putExtra("conferenceRoomKey", inviteRoomKey);
						intent1.setClass(UserListActivity.this, InCallActivity.class);
						startActivity(intent1);
						WMedia.getInstance().callGroup(inviteRoomId, inviteRoomKey);
						
						break;

					case CmdType.inviteUsers://成功
						break;

					case CmdType.beingInvited:// 别人邀请自己
						final String invitedFrom = jsonObject.getString("from");
						final String invitedGroupId = jsonObject.getString("groupid");
						String invitedTo = jsonObject.getString("to");
						JSONObject room2 = new JSONObject(jsonObject.getString("room"));
						final String invitedRoomId = room2.getString("id");
						final String invitedRoomKey = room2.getString("key");
						
						Dialog alertDialog = new AlertDialog.Builder(
								UserListActivity.this).setTitle("电话会议邀请").setMessage("用户" + invitedFrom + "邀请您加入群" + invitedGroupId + "的电话会议~")
								.setPositiveButton("接受", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent1 = new Intent();
										intent1.putExtra("isConference", true);
										intent1.putExtra("conferenceGroupId", invitedGroupId);
										intent1.putExtra("conferenceRoomId", invitedRoomId);
										intent1.putExtra("conferenceRoomKey", invitedRoomKey);
										intent1.setClass(UserListActivity.this, InCallActivity.class);
										startActivity(intent1);
										WMedia.getInstance().callGroup(invitedRoomId, invitedRoomKey);
									}
								}).setNegativeButton("拒绝", null).create();

						alertDialog.show();
						break;

					case CmdType.list:
						break;

					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else if (action.equals("MediaSDK_IncomingReceived")) {
				String incomingName = intent.getStringExtra("IncomingName");
				Intent intent2 = new Intent();
				intent2.setClass(UserListActivity.this, IncomingCallActivity.class);
				intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.putExtra("IncomingName", incomingName);
				startActivity(intent2);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
}

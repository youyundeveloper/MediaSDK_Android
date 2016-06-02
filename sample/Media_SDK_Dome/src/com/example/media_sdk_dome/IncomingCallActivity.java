package com.example.media_sdk_dome;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.weimi.media.WMedia;
import com.weimi.media.WMediaManager;

public class IncomingCallActivity extends Activity implements OnClickListener {

	private static IncomingCallActivity instance;
	private TextView callName;
	InnerReceiver receiver;
	String CallEnd = "MediaSDK_CallEnd";
	String CallError = "MediaSDK_Error";
	String CallConnected = "MediaSDK_Connected";

	public static IncomingCallActivity instance() {
		return instance;
	}

	public static boolean isInstanciated() {
		return instance != null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.called);
		instance = this;
		callName = (TextView) findViewById(R.id.tv_called_incallName);
		findViewById(R.id.btn_called_answer).setOnClickListener(this);
		findViewById(R.id.btn_called_hangup).setOnClickListener(this);
		callName.setText(getIntent().getStringExtra("IncomingName"));

		// 自定义广播回调接受消息
		IntentFilter filter = new IntentFilter();
		filter.addAction(CallEnd);
		filter.addAction(CallError);
		filter.addAction(CallConnected);
		receiver = new InnerReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_called_answer:// 接听
			answer();
			finish();
			break;
		case R.id.btn_called_hangup:// 挂断
			decline();
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		instance = this;

	}

	class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(CallEnd) || action.equals(CallError)) {
				finish();
			} else if (action.equals(CallConnected)) {
				System.out.println("电话接通了=====");
			}
		}
	}

	private void decline() {
		WMedia.getInstance().decline();
	}

	private void answer() {
		WMedia.getInstance().answer();
		Intent intent = new Intent(this, InCallActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (WMediaManager.isInstanciated()
				&& (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
			decline();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

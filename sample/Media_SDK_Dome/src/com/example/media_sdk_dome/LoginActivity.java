package com.example.media_sdk_dome;

import java.math.BigInteger;
import java.security.SecureRandom;

import matrix.sdk.WeimiInstance;
import matrix.sdk.data.AuthResultData;
import matrix.sdk.message.WChatException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.weimi.media.WMedia;

public class LoginActivity extends Activity implements OnClickListener {

	private Button loginBtn;
	private boolean isTestPlatform = false;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginBtn = (Button) findViewById(R.id.btn_login);
		loginBtn.setOnClickListener(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage("自动登录中~~~");
		dialog.setCanceledOnTouchOutside(true);
		Thread msgHandler = new Thread(new MsgHandler(getApplicationContext()));
		msgHandler.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			login();
			break;

		}

	}
	
	private void login(){
		if (!dialog.isShowing())
			dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//clientIdDefault和clientSecretDefault 下面提供测试的，以后会用自己的
				/*猪队*/
//				final String clientIdDefault = "1-20046-8c351702e261c7f607ea8020dcb80f41-android";
//				final String clientSecretDefault = "db25eb5508619a34c0ce38b63b4b4c0a";
				/*好像是在上海生成的*/
//				final String clientIdDefault = "1-20086-1bad7e986abdc95410c1c1b26c33a281-android";
//				final String clientSecretDefault = "5b3caffb02cd081f0de4837ee0c1fc43";
				/*顽石*/
				final String clientIdDefault = "1-20096-e2b68ba232f5b4c99adb2a7bff017157-android";
				final String clientSecretDefault = "cb19e5e748a88fa12ca42fb6bd79de84";
				try {
					AuthResultData resultData;
					if (isTestPlatform) {
						resultData = WeimiInstance.getInstance().testRegisterApp(LoginActivity.this.getApplicationContext(),
										generateOpenUDID(),
										clientIdDefault,
										clientSecretDefault, 30);
					} else {
						resultData = WeimiInstance.getInstance()
								.registerApp(LoginActivity.this.getApplicationContext(),
										generateOpenUDID(),
										clientIdDefault,
										clientSecretDefault, 30);
					}

					if (resultData == null) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (dialog != null && dialog.isShowing())
									dialog.dismiss();
								Toast.makeText(LoginActivity.this, "Basic Auth failed！", Toast.LENGTH_SHORT).show();
							}
						});
						System.out.println("Basic Auth failed！");
						return;
					}
					if (resultData.success) {
						WMedia.getInstance().initWMediaSDK(LoginActivity.this.getApplicationContext(), 
								!isTestPlatform);
						runOnUiThread(new Runnable() {
							public void run() {
								if (dialog != null && dialog.isShowing())
									dialog.dismiss();
								Toast.makeText(LoginActivity.this, "Basic Auth successed！", Toast.LENGTH_SHORT).show();
							}
						});
						System.out.println("Basic Auth successed！");
						startActivity(new Intent(LoginActivity.this, UserListActivity.class));
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								if (dialog != null && dialog.isShowing())
									dialog.dismiss();
								Toast.makeText(LoginActivity.this, "Basic Auth failed！", Toast.LENGTH_SHORT).show();
							}
						});
						System.out.println("Basic Auth failed！");
					}
				} catch (WChatException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (dialog != null && dialog.isShowing())
								dialog.dismiss();
							Toast.makeText(LoginActivity.this, "Basic Auth failed！", Toast.LENGTH_SHORT).show();
						}
					});
					System.out.println("Basic Auth failed！");
				}
			}
		}).start();
	}
	
	/**
	 * 根据设备生成一个唯一标识
	 * @return
	 */
	private String generateOpenUDID() {
		// Try to get the ANDROID_ID
		String OpenUDID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		if (OpenUDID == null || OpenUDID.equals("9774d56d682e549c") | OpenUDID.length() < 15) {
			// if ANDROID_ID is null, or it's equals to the GalaxyTab generic
			// ANDROID_ID or bad, generates a new one
			final SecureRandom random = new SecureRandom();
			OpenUDID = new BigInteger(64, random).toString(16);
		}
		return OpenUDID;
	}
	
	
	
}

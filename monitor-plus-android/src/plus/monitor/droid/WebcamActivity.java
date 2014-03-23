package plus.monitor.droid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WebcamActivity extends Activity {
	private String servName;
	private int servPort;
	private RelativeLayout rlScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webcam);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		rlScreen = (RelativeLayout) findViewById(R.id.rlWebcam);
		findViewById(R.id.btnTakeWebcamPic).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						runCommand(Action.WEBCAM_SNAP);
					}
				});
		findViewById(R.id.btnCloseWebcam).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						runCommand(Action.WEBCAM_CLOSE);
					}
				});

	}

	private void runCommand(final int snapControl) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Connect c = new Connect(servName, servPort);
					c.sendCommand(snapControl);
					if (snapControl == Action.WEBCAM_CLOSE)
						return;
					Bitmap screen = null;
					FileOutputStream outToFile = openFileOutput(
							"CurrentWebcam.png", Context.MODE_PRIVATE);

					int bytecount = 2048;
					byte[] buf = new byte[bytecount];
					InputStream IN = c.getInputStream();
					BufferedInputStream BuffIN = new BufferedInputStream(IN,
							bytecount);
					int i = 0;
					int filelength = 0;
					while ((i = BuffIN.read(buf, 0, bytecount)) != -1) {
						filelength += i;
						outToFile.write(buf, 0, i);
						outToFile.flush();
					}
					Log.d("monitor+", "File Length: " + filelength);
					screen = BitmapFactory.decodeFile(getApplication()
							.getFilesDir().getAbsolutePath()
							+ File.separator
							+ "CurrentWebcam.png");
					displayScreenshot(screen);
				} catch (Exception e) {
				}
			}
		});

		thread.start();
	}

	@Override
	protected void onDestroy() {
		if (isFinishing())
			runCommand(Action.WEBCAM_CLOSE);
		super.onStart();
	}

	public void displayScreenshot(final Bitmap screen) {
		runOnUiThread(new Runnable() {
			public void run() {
				BitmapDrawable bdScreen = new BitmapDrawable(getResources(),
						screen);
				Log.d("monitor+", "Version: " + Build.VERSION.SDK_INT);
				if (Build.VERSION.SDK_INT < 16)
					rlScreen.setBackgroundDrawable(bdScreen);
				else {
					try {
						Method setBackground = RelativeLayout.class.getMethod(
								"setBackground", Drawable.class);
						setBackground.invoke(rlScreen, bdScreen);
					} catch (Exception e) {
						Log.i("monitor+", "nosuchmethod");
						e.printStackTrace();
					}

				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screenshot, menu);
		return true;
	}

	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), toast,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

}

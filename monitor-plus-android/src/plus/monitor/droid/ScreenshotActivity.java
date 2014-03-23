package plus.monitor.droid;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ScreenshotActivity extends Activity {
	private String servName;
	private int servPort;
	private RelativeLayout rlScreen;
	private static final int SCREENSHOT_CONTROLS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		rlScreen = (RelativeLayout) findViewById(R.id.rlScreenshot);
		Bitmap screen = null;

		screen = BitmapFactory.decodeFile(getApplication().getFilesDir()
				.getAbsolutePath() + File.separator + "CurrentScreen.png");
		displayScreenshot(screen);
		findViewById(R.id.btnTakeScreenshot).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						runCommand();
					}
				});
		findViewById(R.id.ivScreen).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				openCurrentScreenshotImage(view);
			}
		});

	}

	private void runCommand() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Connect c = new Connect(servName, servPort);
					c.sendCommand(SCREENSHOT_CONTROLS);
					DisplayMetrics displaymetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(
							displaymetrics);
					int height = displaymetrics.heightPixels;
					int width = displaymetrics.widthPixels;
					showToast("" + width + "  " + height);
					c.println(width);
					c.println(height);
					Bitmap screen = null;
					int contentLength = 0;
					BufferedReader br = new BufferedReader(
							new InputStreamReader(c.getInputStream()));
					contentLength = Integer.parseInt(br.readLine());
					byte b[] = new byte[contentLength];
					InputStream is = null;
					is = c.getInputStream();
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						int bytesRead = -1;
						while ((bytesRead = is.read(b)) != -1) {
							baos.write(b, 0, bytesRead);
						}
						baos.close();
						c.close();
						b = baos.toByteArray();
					} catch (IOException e) {
						return;
					}
					screen = BitmapFactory.decodeByteArray(b, 0, b.length);
					if (screen == null) {
						runCommand();
						return;
					}
					displayScreenshot(screen);
					saveCurrentBitmap(screen);
				} catch (Exception e) {
					showToast("Error occurred. Please reconnect.");
				}
			}
		});

		thread.start();
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

	@SuppressLint("WorldReadableFiles")
	public boolean saveCurrentBitmap(Bitmap image) {
		try {
			FileOutputStream fos = openFileOutput("CurrentScreen.png",
					Context.MODE_WORLD_READABLE);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
			return true;
		} catch (Exception e) {
			Log.e("saveToInternalStorage()", e.getMessage());
			return false;
		}
	}

	public void openCurrentScreenshotImage(View v) {
		String filePath = getApplication().getFilesDir().getAbsolutePath()
				+ File.separator + "CurrentScreen.png";
		File f = new File(filePath);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(f), "image/*");
		startActivity(intent);
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

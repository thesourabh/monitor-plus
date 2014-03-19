package plus.monitor.droid;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ScreenshotActivity extends Activity {
	private String servName;
	private int servPort;
	private ImageView ivScreen;
	private static final int SCREENSHOT_CONTROLS = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		ivScreen = (ImageView) findViewById(R.id.ivScreen);
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
				Socket clientSock = null;
				if (servName.length() == 0) {
					finish();
				}
				Log.d("monitor+", "Entered runCommand");
				PrintWriter pw = null;
				try {
					clientSock = new Socket();
					try {
						clientSock.connect(new InetSocketAddress(servName,
								servPort), 8000);
					} catch (Exception e) {
						Log.d("monitor+", "Error in connecting socket");
						return;
					}
					try {
						pw = new PrintWriter(clientSock.getOutputStream(), true);
					} catch (IOException ioe) {
						Log.d("monitor+", "Error in setting printwriter");
						return;
					}
					pw.println(SCREENSHOT_CONTROLS);
					DisplayMetrics displaymetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(
							displaymetrics);
					int height = displaymetrics.heightPixels - 100;
					int width = displaymetrics.widthPixels;
					showToast("" + width + "  " + height);
					pw.println(width);
					pw.println(height);
					Bitmap screen = null;
					int contentLength = 0;
					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(clientSock
										.getInputStream()));
						contentLength = Integer.parseInt(br.readLine());
					} catch (IOException e1) {
					}
					byte b[] = new byte[contentLength];
					InputStream is = null;
					try {
						is = clientSock.getInputStream();
					} catch (IOException e) {
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int bytesRead = -1;
					while ((bytesRead = is.read(b)) != -1) {
						baos.write(b, 0, bytesRead);
					}
					baos.close();
					b = baos.toByteArray();
					Log.d("com.remote.pc", "byte length: " + b.length
							+ ".. content length: " + contentLength);
					screen = BitmapFactory.decodeByteArray(b, 0, b.length);
					if (screen == null) {
						runCommand();
						return;
					}
					displayScreenshot(screen);
					saveCurrentBitmap(screen);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (pw != null)
						pw.close();
					try {
						if (clientSock != null)
							clientSock.close();
					} catch (IOException e) {
					}
				}
			}
		});

		thread.start();
	}

	public void displayScreenshot(final Bitmap pic) {
		runOnUiThread(new Runnable() {
			public void run() {
				ivScreen.setImageBitmap(pic);
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

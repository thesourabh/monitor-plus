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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ActMain extends Activity {

	private EditText etCommand;
	private EditText etProgram;
	private EditText etServName;
	private EditText etServPort;

	private ImageView ivScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		Bundle bundle = getIntent().getExtras();
		etServName = (EditText) findViewById(R.id.eServName);
		etServName.setText("" + bundle.getString("host"));
		etServPort = (EditText) findViewById(R.id.etServPort);
		etServPort.setText("" + bundle.getInt("port"));
		etCommand = (EditText) findViewById(R.id.etCommand);
		etProgram = (EditText) findViewById(R.id.etProgram);
		ivScreen = (ImageView) findViewById(R.id.ivScreen);
		ivScreen.setImageURI(Uri.parse(getApplication().getFilesDir()
				.getAbsolutePath() + File.separator + "CurrentScreen.png"));

		Button bCommandRun = (Button) findViewById(R.id.btnRunCommand);
		bCommandRun.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(1);
			}
		});
		Button bProgramLaunch = (Button) findViewById(R.id.btnLaunchProgram);
		bProgramLaunch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(2);
			}
		});
		Button bTakeScreenshot = (Button) findViewById(R.id.btnTakeScreenshot);
		bTakeScreenshot.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(3);
			}
		});
		Button bCloseServer = (Button) findViewById(R.id.btnCloseServer);
		bCloseServer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(-1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.act_main, menu);
		return true;
	}

	private void runCommand(final int commandCode) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				String s;
				int i;
				Socket clientSock = null;
				s = etServName.getText().toString();
				i = Integer.parseInt(etServPort.getText().toString());
				if (s.length() == 0) {
					s = "192.168.1.2";
					i = 13579;
				}
				PrintWriter pw = null;
				try {
					clientSock = new Socket();
					try {
						clientSock.connect(new InetSocketAddress(s, i), 8000);
					} catch (SocketTimeoutException ste) {
						showToast("Connection timeout. Make sure the port number is correct and the host application is running.");
						return;
					} catch (UnknownHostException uhe) {
						showToast("Host Unknown");
						return;
					} catch (IOException ioe) {
						showToast("Could not connect to target computer.");
						return;
					}
					try {
						pw = new PrintWriter(clientSock.getOutputStream(), true);
					} catch (IOException e) {
						showToast("Target failed to respond.");
						e.printStackTrace();
						return;
					}
					String command = "";
					switch (commandCode) {
					case -1:
						pw.println(commandCode);
						return;
					case 1:
						command = etCommand.getText().toString();
						break;
					case 2:
						command = etProgram.getText().toString();
						break;
					case 3:
						pw.println(commandCode);
						DisplayMetrics displaymetrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
						int height = displaymetrics.heightPixels;
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
							runCommand(3);
							return;
						}
						saveCurrentBitmap(screen);
						displayScreenshot(screen);
						return;
					}
					pw.println(commandCode);
					pw.println(command);
				} catch (IOException e) {
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

	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), toast,
						Toast.LENGTH_SHORT).show();
			}
		});
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
		String filePath = getApplication().getFilesDir()
				.getAbsolutePath() + File.separator + "CurrentScreen.png";
		File f = new File(filePath);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(f), "image/*");
		startActivity(intent);
	}
}

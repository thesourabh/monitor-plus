package plus.monitor.droid;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MediaActivity extends Activity {
	private String servName;
	private int servPort;
	private static final int MEDIA_CONTROLS = 5;
	private static final int VOLUME_UP = 1;
	private static final int VOLUME_DOWN = 2;
	private static final int STOP_MEDIA = 0;
	private static final int PLAY_PAUSE = 5;
	private static final int FULL_SCREEN = 15;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		Log.d("monitor+","Found button");
		findViewById(R.id.btnPlayPause).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(PLAY_PAUSE);
			}
		});
		findViewById(R.id.btnStopMedia).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(STOP_MEDIA);
			}
		});
		findViewById(R.id.btnRewind).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(3);
			}
		});
		findViewById(R.id.btnFastForward).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				runCommand(4);
			}
		});
		findViewById(R.id.btnFullScreen).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				runCommand(FULL_SCREEN);
			}
		});
	}
	
	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			runCommand(VOLUME_UP);
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			runCommand(VOLUME_DOWN);			
			break;
		}
		return super.onKeyUp(keyCode, event);
	}



	private void runCommand(final int mediaCommand) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				Socket clientSock = null;
				if (servName.length() == 0) {
					finish();
				}
				Log.d("monitor+","Entered runCommand");
				PrintWriter pw = null;
				try {
					clientSock = new Socket();
					try {
						clientSock.connect(new InetSocketAddress(servName,
								servPort), 8000);
					} catch (Exception e) {
						Log.d("monitor+","Error in connecting socket");
						return;
					}
					try {
						pw = new PrintWriter(clientSock.getOutputStream(), true);
					} catch (IOException ioe) {
						Log.d("monitor+","Error in printwriter declaration");
						return;
					}
					pw.println(MEDIA_CONTROLS);
					Log.d("monitor+","Printing Media Controls");
					pw.println(mediaCommand);
					Log.d("monitor+","Printing Media Command");
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
}
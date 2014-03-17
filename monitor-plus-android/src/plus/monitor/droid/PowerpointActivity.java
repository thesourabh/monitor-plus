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
import android.widget.Button;

public class PowerpointActivity extends Activity {
	private String servName;
	private int servPort;
	private int pptCommand;
	private static final int PPT_CONTROLS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_powerpoint);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		Log.d("monitor+","Found button");
		findViewById(R.id.btnPPTF5).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(3);
			}
		});
		findViewById(R.id.btnPPTEsc).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(0);
			}
		});
		findViewById(R.id.btnPPTPrev).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				runCommand(1);
			}
		});
		findViewById(R.id.btnPPTNext).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				runCommand(2);
			}
		});
	}
	
	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			runCommand(1);
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			runCommand(2);			
			break;
		}
		return true;
		//return super.onKeyUp(keyCode, event);
	}



	private void runCommand(final int pptCommand) {
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
					pw.println(PPT_CONTROLS);
					Log.d("monitor+","Printing PPT Controls");
					pw.println(pptCommand);
					Log.d("monitor+","Printing PPT Command");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.powerpoint, menu);
		return true;
	}

}

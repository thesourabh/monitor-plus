package plus.monitor.droid;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static String[] MENU_TEXT = new String[] { "Launch", "Run",
			"Powerpoint Controls", "Screenshot", "Media Controls" };
	GridView grid;

	private EditText etCommand;
	private EditText etProgram;
	private String servName;
	private int servPort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		setTitle(servName + ": Monitor+");
		etCommand = (EditText) findViewById(R.id.etCommand);
		grid = (GridView) findViewById(R.id.gridView1);
		grid.setAdapter(new GridMenuAdapter(this, MENU_TEXT));

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				switch (position) {
				case 0:
					runCommand(1);
					break;
				case 1:
					runCommand(2);
					break;
				case 2:
					Intent intent2 = new Intent(MainActivity.this,
							PowerpointActivity.class);
					intent2.putExtra("host", servName);
					intent2.putExtra("port", servPort);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(MainActivity.this,
							ScreenshotActivity.class);
					intent3.putExtra("host", servName);
					intent3.putExtra("port", servPort);
					startActivity(intent3);
					break;
				case 4:
					Intent intent = new Intent(MainActivity.this,
							MediaActivity.class);
					intent.putExtra("host", servName);
					intent.putExtra("port", servPort);
					startActivity(intent);

				}
			}
		});

		/*
		 * 
		 * findViewById(R.id.btnRunCommand).setOnClickListener( new
		 * OnClickListener() { public void onClick(View v) { runCommand(1); }
		 * }); findViewById(R.id.btnLaunchProgram).setOnClickListener( new
		 * OnClickListener() { public void onClick(View v) { runCommand(2); }
		 * });
		 */
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
				Socket clientSock = null;
				if (servName.length() == 0) {
					showToast("Address not received by activity. Relaunch and try again.");
					finish();
				}
				PrintWriter pw = null;
				try {
					clientSock = new Socket();
					try {
						clientSock.connect(new InetSocketAddress(servName,
								servPort), 8000);
					} catch (Exception e) {
						showToast("Error in connection");
						return;
					}
					try {
						pw = new PrintWriter(clientSock.getOutputStream(), true);
					} catch (IOException ioe) {
						showToast("Target failed to respond.");
						ioe.printStackTrace();
						return;
					}
					String command = "";
					switch (commandCode) {
					case -1:
						pw.println(commandCode);
						return;
					case 1:
					case 2:
						command = etCommand.getText().toString();
						break;
					}
					pw.println(commandCode);
					pw.println(command);
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
}

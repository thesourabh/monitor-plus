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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectActivity extends Activity {
	
	private EditText etServName;
	private final int PORT = 13579;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		etServName = (EditText) findViewById(R.id.etServName);
		Button bConnect = (Button) findViewById(R.id.btnConnect);
		bConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				connect();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}
	
	private void connect() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				String host;
				Socket clientSock = null;
				host = etServName.getText().toString();
				try {
					clientSock = new Socket();
					try {
						clientSock.connect(new InetSocketAddress(host, PORT), 8000);
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
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(clientSock.getOutputStream(), true);
					} catch (IOException e) {
						return;
					}
					pw.println("0");
					pw.close();
					Intent intent = new Intent(ConnectActivity.this, ActMain.class);
					intent.putExtra("host", host);
					intent.putExtra("port", PORT);
					startActivity(intent);
					return;
					
				} finally {
					
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

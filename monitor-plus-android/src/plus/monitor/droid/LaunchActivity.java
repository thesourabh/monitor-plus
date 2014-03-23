package plus.monitor.droid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class LaunchActivity extends Activity {
	private String servName;
	private int servPort;
	private EditText etCommand;
	private static final int LAUNCH_ACTIVITY = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		etCommand = (EditText) findViewById(R.id.etProgram);
		findViewById(R.id.btnLaunch).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				runCommand();
			}
		});
	}

	private void runCommand() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Connect c = new Connect(servName, servPort);
					String command = etCommand.getText().toString();
					c.sendCommand(LAUNCH_ACTIVITY);
					c.println(command);
					c.close();
				} catch (Exception e) {
					return;
				}
			}
		});
		thread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch_run, menu);
		return true;
	}

}

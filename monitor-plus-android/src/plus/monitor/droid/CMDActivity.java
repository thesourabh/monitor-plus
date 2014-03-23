package plus.monitor.droid;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CMDActivity extends Activity {
	private String servName;
	private int servPort;
	private EditText etCommand;
	private TextView tvCMDOutput, tvCMDPrompt;
	private static final int CMD_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmd);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
		setTitle("CMD: " + servName + ": Monitor+");
		etCommand = (EditText) findViewById(R.id.etCommand);
		tvCMDPrompt = (TextView) findViewById(R.id.tvCMDPrompt);
		tvCMDOutput = (TextView) findViewById(R.id.tvCMDGuide);
		Button btCommand = (Button) findViewById(R.id.btnRunCommand);
		try {
			runCommand(0);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		btCommand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try {
					runCommand(1);
				runCommand(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void runCommand(final int cmdCode) throws InterruptedException {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Connect c = new Connect(servName, servPort);
					BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
					c.sendCommand(CMD_ACTIVITY);
					String command;
					if (cmdCode == 1)
						command = etCommand.getText().toString();
					else
						command = "cd";
					c.println(command);
					String cmdOutput = "", line;
					while( (line = br.readLine()) != null)
						cmdOutput += line + "\n";
					if (cmdCode == 1)
						displayOutput(cmdOutput);
					else
						displayPrompt(cmdOutput);
					c.close();
				} catch (Exception e) {
					Log.e("monitor+", e.getMessage() + "..." + e.getStackTrace());
				}
			}
		});
		thread.start();
		thread.join();
	}
	


	public void displayOutput(final String output) {
		runOnUiThread(new Runnable() {
			public void run() {
				tvCMDOutput.setText(output);
			}
		});
	}
	public void displayPrompt(final String output) {
		runOnUiThread(new Runnable() {
			public void run() {
				tvCMDPrompt.setText(output + ">");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch_run, menu);
		return true;
	}

}

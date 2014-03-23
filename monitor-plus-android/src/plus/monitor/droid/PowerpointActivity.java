package plus.monitor.droid;


import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class PowerpointActivity extends Activity {
	private String servName;
	private int servPort;
	private static final int PPT_CONTROLS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_powerpoint);
		Bundle bundle = getIntent().getExtras();
		servName = bundle.getString("host");
		servPort = bundle.getInt("port");
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
		return super.onKeyUp(keyCode, event);
	}



	private void runCommand(final int pptCommand) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try{
				Connect c = new Connect(servName, servPort);
				c.sendCommand(PPT_CONTROLS);
				c.println(String.valueOf(pptCommand));
				c.close();
				} catch (Exception e){
					return;
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

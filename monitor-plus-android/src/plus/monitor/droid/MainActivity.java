package plus.monitor.droid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {

	static String[] MENU_TEXT = new String[] { "Launch Program",
			"Command Line (CMD)", "Screenshot", "Webcam", "Media Controls",
			"Powerpoint Controls" };
	GridView grid;

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
		grid = (GridView) findViewById(R.id.gridView1);
		grid.setAdapter(new GridMenuAdapter(this, MENU_TEXT));

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				Intent intent = null;
				switch (position) {
				case 0:
					intent = createIntent(LaunchActivity.class);
					break;
				case 1:
					intent = createIntent(CMDActivity.class);
					break;
				case 2:
					intent = createIntent(ScreenshotActivity.class);
					break;
				case 3:
					intent = createIntent(WebcamActivity.class);
					break;
				case 4:
					intent = createIntent(MediaActivity.class);
					break;
				case 5:
					intent = createIntent(PowerpointActivity.class);
					break;
				}
				launchSocketActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Intent createIntent(Class<?> cls) {
		return new Intent(this, cls);
	}

	private void launchSocketActivity(Intent intent) {
		intent.putExtra("host", servName);
		intent.putExtra("port", servPort);
		startActivity(intent);
	}
}

package plus.monitor.droid;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridMenuAdapter extends BaseAdapter {
	private Context context;
	
	private String menuText[] = {"A", "B", "C"};

	public GridMenuAdapter(Context context, String mT[]) {
        this.context=context;
        this.menuText = mT;
	}

	@Override
	public int getCount() {
		return menuText.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.grid_row, null);
			
			TextView textView = (TextView) row.findViewById(R.id.tvGridMenu);
			textView.setText(menuText[position]);
		}
		return row;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

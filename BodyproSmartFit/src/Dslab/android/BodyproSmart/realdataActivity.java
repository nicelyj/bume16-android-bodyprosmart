package Dslab.android.BodyproSmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class realdataActivity extends Activity implements OnClickListener{
	
	//global var
	private final int BLUETOOTHSEARCH = 1;
	private ArrayAdapter<String> mNewDevice;
	
	String items[] = {"item 1", "item 2"};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.realdata);
		
		ImageButton exitBtn = (ImageButton)findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(this);
		
		ImageButton searchBtn = (ImageButton)findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.exitBtn)
			finish();
		else if(v.getId() == R.id.searchBtn)
			showDialog(BLUETOOTHSEARCH);
	}
	
	protected Dialog onCreateDialog(int id){
		super.onCreateDialog(id);
		
		AlertDialog dlg=null;
		
		switch(id){
		case BLUETOOTHSEARCH:
			dlg = new AlertDialog.Builder(this)
					.setIcon(R.drawable.search_icon)
					.setTitle("블루투스 장치 검색")
					.setMessage("사용가능한 장치")
					.setView(createCustomView())
					.setPositiveButton("버튼", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();							
						}
					})
					.create();
			break;
		}		
		return dlg;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog);
	}
	
	private View createCustomView(){
		
		//LinearLayout linearLayout = new LinearLayout(this);
		
        //ListView newDeviceListView = (ListView)findViewById(R.id.dev_listview);        
        //mNewDevice.add("1234");
        //newDeviceListView.setAdapter(mNewDevice);
        //newDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		//	@Override
		//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		//			long arg3) {
				// TODO Auto-generated method stub				
		//	}        	
		//});		
        
        //linearLayout.setOrientation(LinearLayout.VERTICAL);
        //linearLayout.addView(newDeviceListView);
		//return linearLayout;
		
        LinearLayout linearLayoutView = new LinearLayout(this);
        ListView listview = new ListView(this);

        ArrayAdapter<String> aa = new ArrayAdapter<String> (this,
                                    android.R.layout.simple_list_item_1,
                                    items);

        listview.setAdapter(aa);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strNumber;
                if(position == 0) {
                    strNumber = "1번 아이템 Clicked";
                }
                else {
                    strNumber = "2번 아이템 Clicked";
                }          
                //Toast.makeText(this, strNumber, Toast.LENGTH_SHORT).show();
            }          
        });
  
        TextView tv = new TextView(this);
        tv.setText("  Custom View 영역");
  
        linearLayoutView.setOrientation(LinearLayout.VERTICAL);
        linearLayoutView.addView(tv);
        linearLayoutView.addView(listview);
       
        return linearLayoutView;

	}
	
	
	
}
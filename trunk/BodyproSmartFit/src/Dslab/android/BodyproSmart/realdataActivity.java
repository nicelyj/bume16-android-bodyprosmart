package Dslab.android.BodyproSmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
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
	private final String TAG = "BodyproSmart RDA";
	private final int BLUETOOTHSEARCH = 1;
	private ArrayAdapter<String> mNewDevice;
	//private ComponentName mService;
	private final int MESSAGE_BTSEARCH = 1;
	private IBluetoothService mBtService;
	
	String items[] = {"item 1", "item 2"};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.realdata);
		
		startBtService();
		
		ImageButton exitBtn = (ImageButton)findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(this);
		
		ImageButton searchBtn = (ImageButton)findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		
		ImageButton connectBtn = (ImageButton)findViewById(R.id.connectBtn);
		connectBtn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.exitBtn)
			finish();
		else if(v.getId() == R.id.searchBtn)
		{			
			
			try {
				if(mBtService != null)
				{
					int nd;
					nd = mBtService.startSearch(BLUETOOTHSEARCH);
					//Handler mHandler = new Handler();
					//mHandler.postDelayed(new Runnable(){
				    //	public void run()
				    //	{
				    //		
				    //	}
				    //}, 5000);	
					
					Log.i(TAG,String.format("find devs = %d", nd));
				}
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//try {
			 //   Thread.sleep(5000); // 1초 = 1000밀리초
			//} catch (InterruptedException ignore) {}

			showDialog(BLUETOOTHSEARCH);
		}
		else if(v.getId() == R.id.connectBtn)
		{
			stopBtService();
		}
	}
	
	private void startBtService(){
		Intent intent = new Intent(this, BluetoothService.class);
		boolean ret = bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
		Log.i(TAG, "bindservice :"+ret);
				
	}
	
	private void stopBtService(){
		
		if(mConnection != null)
			unbindService(mConnection);
		//if(mService == null){
		//	Log.i(TAG, "no request BtService");			
		//	return;			
		//}
		
		//Intent intent = new Intent();
		//intent.setComponent(mService);
		//if(stopService(intent))
		//	Log.i(TAG, "Stoped BtService");		
		//else
		//	Log.i(TAG, "already Stoped BtService");
			
		
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
					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						
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
		
		LinearLayout linearLayout = new LinearLayout(this);
        ListView newDeviceListView = new ListView(this);
        
        //mNewDevice.add("1234");
        //mNewDevice.add("5687");
        
        newDeviceListView.setAdapter(mNewDevice);
        newDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub				
			}        	
		});		
        
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(newDeviceListView);
		return linearLayout;       

	}
	
	private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBtService = IBluetoothService.Stub.asInterface(service);
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBtService = null;
			
		}

	};
	
	
}
package Dslab.android.BodyproSmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	//private IBluetoothService mBtService;
	
	//Bluetooth
	private BluetoothAdapter mBtAdapter	=null;
	private BluetoothService mBtService=  null;
	
	//Message
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    private TextView mhrText;
    private ImageView hrImage;
    private Animation mheartbeatAni;
    private ImageView connectState;
    private int hrr = 0;

	
	String items[] = {"item 1", "item 2"};
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.realdata);
		
		//startBtService();
        // Get local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        if(!mBtAdapter.isEnabled()){
        	Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
		
		ImageButton exitBtn = (ImageButton)findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(this);
		
		ImageButton searchBtn = (ImageButton)findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		
		connectState = (ImageView)findViewById(R.id.connectState);
		//connectBtn.setOnClickListener(this);
		
		mhrText = (TextView)findViewById(R.id.hrTextView);
		
		//mheartbeatAni = AnimationUtils.loadAnimation(this, R.anim.testanim);
		mheartbeatAni = AnimationUtils.loadAnimation(this, R.anim.rotate);
		hrImage = (ImageView)findViewById(R.id.heartbeatAni);


		mBtService = new BluetoothService(this,mHandler);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.exitBtn)
			finish();
		else if(v.getId() == R.id.searchBtn)
		{
			
				//int nd;
				//nd = mBtService.startSearch(BLUETOOTHSEARCH);
				Intent findDeviceIntent = new Intent(this, FindDeviceActivity.class);
				startActivityForResult(findDeviceIntent, REQUEST_CONNECT_DEVICE);
				//Log.i(TAG,String.format("find devs = %d", nd));
			
			//showDialog(BLUETOOTHSEARCH);

		}
		//else if(v.getId() == R.id.connectBtn)
		//{
		//	hrImage.startAnimation(mheartbeatAni);
			//stopBtService();
		//}
	}
	/*
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
					.setTitle("釉붾（�ъ뒪 �μ튂 寃�깋")
					.setMessage("�ъ슜媛�뒫���μ튂")
					.setView(createCustomView())
					.setPositiveButton("�뺤씤", new DialogInterface.OnClickListener() {
						
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
			//mBtService = IBluetoothService.Stub.asInterface(service);
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBtService = null;
			
		}

	};
	*/
	
	private final Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case MESSAGE_STATE_CHANGE:
				
				if(mBtService.getState() == mBtService.STATE_CONNECTED || 
						mBtService.getState() == mBtService.STATE_CONNECTING)
				{	
					connectState.setBackgroundResource(R.drawable.connect_icon);					
					
				}
				else
				{
					connectState.setBackgroundResource(R.drawable.connect_icon_over);					
				}
				break;
				
					
						
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer                
        		if(readBuf[1]<0)
        			hrr = (int)readBuf[1]+256;
        		else
        			hrr = (int)readBuf[1];        		
                
                Update();
                
                break;
			
			}
		}
		
	};

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){	
			
		case REQUEST_CONNECT_DEVICE:
			//if(requestCode == Activity.RESULT_OK){
				String address = data.getExtras().getString(FindDeviceActivity.EXTRA_DEVICE_ADDRESS);
				Toast.makeText(this, address, Toast.LENGTH_LONG).show();
				
				BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
				mBtService.connect(device);
				
				break;
			//}
		default : break;
		}
	}
	
	protected void Update(){
		Runnable Updater = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String msg = String.format("%d", hrr);
				mhrText.setText(msg);	
				hrImage.startAnimation(mheartbeatAni);				
			}
			
		};
		mHandler.post(Updater);	
	}
	
	
	
	
}
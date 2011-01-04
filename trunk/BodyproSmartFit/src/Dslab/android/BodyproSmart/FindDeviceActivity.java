package Dslab.android.BodyproSmart;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FindDeviceActivity extends Activity{
	
	private static final String TAG = "FindDevice Activity";
	private static final int RESULT_OK = 1;
	
	//Return intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";
	
	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDeviceArrayAdapter;
	private ArrayAdapter<String> mNewDeviceArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dev_listview);
		
		setResult(Activity.RESULT_CANCELED);
		
		Button scanButton = (Button)findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doDiscover();
				v.setVisibility(View.GONE);
				
			}
		});		
		
		//initial 
		mPairedDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.dev_name);
		mNewDeviceArrayAdapter = new ArrayAdapter<String>(this,R.layout.dev_name);
		
		//find and set up the Listview for pair
		ListView pairedListView = (ListView)findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDeviceArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);
		
		//find and set up the Listview for new
		ListView newDeviceArrayAdapter = (ListView)findViewById(R.id.new_devices);
		newDeviceArrayAdapter.setAdapter(mNewDeviceArrayAdapter);
		newDeviceArrayAdapter.setOnItemClickListener(mDeviceClickListener);
		
		//Register for broadcast
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Set<BluetoothDevice> pairedDevice = mBtAdapter.getBondedDevices();
		
		if(pairedDevice.size() > 0){
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for(BluetoothDevice device : pairedDevice){
				mPairedDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		}else{
			String noDevice = getResources().getText(R.string.none_paired).toString();
			mPairedDeviceArrayAdapter.add(noDevice);
			
		}
		
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mBtAdapter != null){
			mBtAdapter.cancelDiscovery();
		}
		
		//Unregister
		this.unregisterReceiver(mReceiver);
	}
	
	public void doDiscover(){
    	Log.i(TAG,"doDiscover()");
    	
    	setProgressBarIndeterminateVisibility(true);
    	setTitle(R.string.scanning);
    	    	
    	if(mBtAdapter.isDiscovering()){
    		mBtAdapter.cancelDiscovery();
    	}
    	
    	mBtAdapter.startDiscovery();  
	}
	
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			mBtAdapter.cancelDiscovery();
			
			String info = ((TextView)v).getText().toString();
			String address = info.substring(info.length() - 17);
			
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
			
			setResult(RESULT_OK, intent);
			finish();
			
		}		
		
	};
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getBondState() != BluetoothDevice.BOND_BONDED){
					mNewDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
				
			}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if(mNewDeviceArrayAdapter.getCount() == 0){
					String noDevices = getResources().getText(R.string.none_found).toString();
					mNewDeviceArrayAdapter.add(noDevices);
				}
			}
			
		}
		
	};

}

package Dslab.android.BodyproSmart;

import java.util.UUID;

import android.app.PendingIntent;
import android.app.Service;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;



public class BluetoothService extends Service implements Runnable{
	private static final String TAG = "BodyproSmart BTS";
	
	private BluetoothAdapter mBtAdapter;
	private BluetoothSocket	mmSocket;
	private ArrayAdapter<String> mNewDev;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	//private ConnectedThread mConnectedThread;
	
		
	
	@Override
	public void run() {
		// TODO Auto-generated method stub	  
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBtService;
	}
	
	private IBluetoothService.Stub mBtService = new IBluetoothService.Stub() {
		
		@Override
		public int startSearch(int msg) throws RemoteException {
			// TODO Auto-generated method stub			
			doDiscover();
	
			return mNewDev.getCount();
		}

		@Override
		public String getString(int nidx) throws RemoteException {
			// TODO Auto-generated method stub
			if(nidx == 0)
				return "empty";
			else
				return mNewDev.getItem(nidx);
		}

		@Override
		public int getindex(int msg) throws RemoteException {
			// TODO Auto-generated method stub
			return mNewDev.getCount();
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
		BtDeviceSetup();
		
		mNewDev = new ArrayAdapter<String>(this,R.layout.dev_name);
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		
			
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);		
	}
	public void doDiscover(){
    	Log.i(TAG,"doDiscover()");
    	    	
    	if(mBtAdapter.isDiscovering()){
    		mBtAdapter.cancelDiscovery();
    	}
    	
    	mBtAdapter.startDiscovery();  
	}
	
	private void BtDeviceSetup(){	
		
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(mBtAdapter == null){
			Toast.makeText(this,"not found Bluetooth device",Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this, "get Bluetooth device"+"\n"+mBtAdapter.getName(), Toast.LENGTH_LONG).show();
		}
		
		if(!mBtAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, enableBtIntent, 0);
			try{
				pIntent.send();
				
			}catch(CanceledException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			String action = intent.getAction();
			
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.i(TAG, device.getName()+"\n"+device.getAddress());
				mNewDev.add(device.getName()+"\n"+device.getAddress());
				
				
			}
			else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				if(mNewDev.getCount() == 0){					
					mNewDev.add("no searched deveice");
					}
				
				Log.i(TAG, "serach end");
				Log.i(TAG, "serach device : "+mNewDev.getCount());
			}
		}
		
	};
	

}

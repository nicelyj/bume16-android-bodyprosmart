package Dslab.android.BodyproSmart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;




public class BluetoothService{
	private static final String TAG = "BodyproSmart BTS";

	private static final String NAME = "BluetoothService";
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	//member
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	
	//connetction state
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	
	
	public BluetoothService(Context context, Handler handler)
	{
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;		
	}
	
	private synchronized void setState(int state)
	{
		mState = state;
		
		mHandler.obtainMessage(realdataActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
		
	}
	
	private synchronized int getState()
	{
		return mState;
			
	}
	
	public synchronized void start()
	{

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);

		

	}
	
	public synchronized void connect(BluetoothDevice device)
	{
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
		
	}
	
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
	{
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(realdataActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(realdataActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
	
	}
	
	public synchronized void stop()
	{		
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
	
	}
	
	public void write(byte[] out)
	{
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
		
	}

	private void connetionFailed()
	{		
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(realdataActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(realdataActivity.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
	}
	
	private void connectionLost()
	{
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(realdataActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(realdataActivity.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
		
	}
	
	private class AcceptThread extends Thread{

		private final BluetoothServerSocket mmServerSocket;
		
		public AcceptThread(){
			BluetoothServerSocket tmp = null;
			
			try{
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
				
			}catch(IOException e){
				Log.e(TAG,"Listen() failed",e);
			}
			
			mmServerSocket = tmp;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			setName("AcceptThread");
			BluetoothSocket socket = null;
			
			while(mState != STATE_CONNECTED){
				try{
					socket = mmServerSocket.accept();
				}catch(IOException e){
					Log.e(TAG,"Accept() failed",e);		
					break;
				}
				
				if(socket != null){
					synchronized(BluetoothService.this){
						switch(mState){
						case STATE_LISTEN:
						case STATE_CONNECTING:
							connected(socket, socket.getRemoteDevice());
						case STATE_NONE:
						case STATE_CONNECTED:
							try{
								socket.close();
							}catch(IOException e){
								Log.e(TAG,"Could not close unwanted socket",e);
							}
							break;
						}
					}
				}
				
			}
		}
		
		public void cancel(){
			try{
				mmServerSocket.close();
				
			}catch(IOException e){
				Log.e(TAG,"Close() of server failed",e);
			}
		}		
	
	}

	
	
	private class ConnectThread extends Thread{

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		
		public ConnectThread(BluetoothDevice device){
			mmDevice = device;
			BluetoothSocket tmp = null;
			
			try{
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			}catch(IOException e){
				Log.e(TAG,"create() failed",e);
				
			}
			mmSocket = tmp;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConncetThread");
			
			mAdapter.cancelDiscovery();
			
			try{
				mmSocket.connect();
			}catch(IOException e){
				connetionFailed();
				try{
					mmSocket.close();
				}catch(IOException e2){
					Log.e(TAG,"unable to close() socket during connection failure",e2);
				}
				BluetoothService.this.start();
				return;
			}
			synchronized(BluetoothService.this){
				mConnectThread = null;
			}
			connected(mmSocket, mmDevice);
		}
		public void cancel(){
			try{
				mmSocket.close();
			}catch(IOException e){
				Log.e(TAG,"close() of connect socket failed",e);
			}
		}
	}
	
	private class ConnectedThread extends Thread{

		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		
		public ConnectedThread(BluetoothSocket socket){
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			
			try{
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
				
			}catch(IOException e){
				Log.e(TAG,"temp sockets not created",e);
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] buffer = new byte[1024];
            byte[] packet = new byte[4];
            byte[] clearbuff = {0,0,0,0};
	            
            int bytes;
            int cnt = 0;
            boolean tFlag=false;
			
			while(true){
				try{
					bytes = mmInStream.read(buffer);
					/*
					bytes = mmInStream.read(buffer);
					Log.i(TAG,"Rcv Data : "+buffer[0]+buffer[1]+buffer[2]+buffer[3]+buffer[4]);
					mHandler.obtainMessage(realdataActivity.MESSAGE_READ, bytes, -1).sendToTarget();
					*/					
					for(int i=0; i<bytes; i++)
                    {                    	
                  	
                    	packet[cnt] = buffer[i];                    	
                    	cnt++;
                    	if(cnt >= 4)
                    	{
                    		tFlag = true;
                    		break;
                    	}
                    	
                    	
                    }       

                    
                    if(tFlag)
                    { 
                    	if(packet[0] == 0x02 && packet[3] == 0x03)
                    	{      
                    		int hrr=0;
                    		if(packet[1]<0)
                    			hrr = (int)packet[1]+256;
                    		else
                    			hrr = (int)packet[1];
      
                    		packet = clearbuff;
                    		tFlag = false;
                    		cnt=0;
                    		
                    		mHandler.obtainMessage(realdataActivity.MESSAGE_READ, bytes, -1,packet).sendToTarget();
                    
                    	}
                    	else
                    		packet = clearbuff;                    
                    }
                    
                   
    				
				}catch(IOException e){
					Log.e(TAG,"disconnected",e);
					connectionLost();
					break;					
				}
            	String s = String.format("%2x, %2x, %2x, %2x, %2x", 
                    	buffer[0], buffer[1],buffer[2],buffer[3],buffer[4]);
                    	Log.i(TAG, "data get["+Integer.toString(bytes)+"] "+s);      
			}
		}
		
		public void write(byte[] buffer){
			try{
				mmOutStream.write(buffer);
				
				mHandler.obtainMessage(realdataActivity.MESSAGE_WRITE,-1,-1,buffer).sendToTarget();
				
				
			}catch(IOException e){
				Log.e(TAG, "Exception during write",e);
			}
		}
		
		public void cancel(){
			try{
				mmSocket.close();
			}catch(IOException e){
				Log.e(TAG, "Close() of connect socket failed",e);
				
			}
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

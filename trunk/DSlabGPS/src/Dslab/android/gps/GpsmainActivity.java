package Dslab.android.gps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GpsmainActivity extends Activity implements LocationListener {
    /** Called when the activity is first created. */
	private LocationManager locmanager;
	private Geocoder geocoder;
	private Location mylocation = null;
	private double lati=0;
	private double longi=0;
	private float speed=0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //�Ŵ��� �����
        locmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //gps ��ġ���� ��û
        //locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //���������κ��� ��ġ ���� ������Ʈ
        locmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        //�ּ� Ȯ�� geocoder
        geocoder = new Geocoder(this,Locale.KOREA);
        
        Button btn = (Button)findViewById(R.id.StartGpsBtn);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLocations();
				
			}
		});
    }
    
    public void getLocations(){ 
    	StringBuffer juso = new StringBuffer();
    	
    	if(mylocation != null){
    		lati = mylocation.getLatitude();
        	longi = mylocation.getLongitude();
        	speed = mylocation.getSpeed();
        	
        	//�����浵�� ���� ��ġ�� �ּ� ������
        	List<Address> address;
        	try {
    			address = geocoder.getFromLocation(lati, longi, 1);
    			for(Address addr : address){    				
    				juso.append(addr.getAddressLine(addr.getMaxAddressLineIndex()));
    				juso.append(" ");
    			}
    			juso.append("\n");
    			
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		TextView tv = (TextView)findViewById(R.id.latitudeText);
        	tv.setText(Double.toString(lati));
        	tv = (TextView)findViewById(R.id.longitudeText);
        	tv.setText(Double.toString(longi));
        	tv = (TextView)findViewById(R.id.speedText);
        	tv.setText(Double.toString(speed));
        	tv = (TextView)findViewById(R.id.addressText);
        	tv.setText(juso);
    		
    	}
    	else{
    		Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
    	}
    	    	
    }

	@Override  
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mylocation = location;

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	
		
	}
    
    
}
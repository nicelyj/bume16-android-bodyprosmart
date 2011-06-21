package Dslab.android.gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GpsmainActivity extends MapActivity implements LocationListener {
    /** Called when the activity is first created. */
	private LocationManager locmanager;
	private Geocoder geocoder;
	private Location mylocation = null;
	private double lati=0;
	private double longi=0;
	private double alti=0;
	private float speed=0;
	private int mCnt=0;
	private GeoPoint geopoint;
	private MapView mapview;
	private MapController controller;
	private boolean bLog = false;
	private String fileName = "";		
	private BufferedWriter bfw = null;
	
	
	static final int INI1 = 37565263;
	static final int INI2 = 126980667;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //매니저 만든다
        locmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //gps 위치정보 요청
        locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //기지국으로부터 위치 정보 업데이트
        //locmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        //주소 확인 geocoder
        geocoder = new Geocoder(this,Locale.KOREA);
        
        logInit();
        
        Button btn = (Button)findViewById(R.id.StartGpsBtn);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLocations();	
			}
		});
        
   	 //map control
        mapview = (MapView)findViewById(R.id.mapview);
        mapview.setBuiltInZoomControls(true);
        /*
        ZoomControls zoom = (ZoomControls)mapview.getZoomControls();
        ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
        zoom.setLayoutParams(layout);
        zoom.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mapview.addView(zoom);
        */
        
    	controller = mapview.getController();
        geopoint = new GeoPoint(INI1,INI2);       
        controller.setCenter(geopoint);	        
        controller.setZoom(15);
        
        setOverlay(geopoint);
        
    }
    
    public void getLocations(){ 
    	StringBuffer juso = new StringBuffer();
    	
    	if(mylocation != null){
    		lati = mylocation.getLatitude();
        	longi = mylocation.getLongitude();
        	alti = mylocation.getAltitude();
        	speed = mylocation.getSpeed();
        	
        	//위도경도로 현재 위치의 주소 가져옴
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
        	tv = (TextView)findViewById(R.id.cntText);
        	tv.setText(Integer.toString(mCnt));
        	
    		
    	}
    	else{
    		Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
    	}    	   
    	
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mylocation = location;
		
		geopoint = new GeoPoint((int)(mylocation.getLatitude()*1E6),(int)(mylocation.getLongitude()*1E6));		
		controller.animateTo(geopoint);
		
		mCnt++;
		//getLocations();
		//logging
		String str = "";
		Calendar time = Calendar.getInstance();
		String just = String.format("%02d:%02d:%02d.%03d",time.get(Calendar.HOUR_OF_DAY),time.get(Calendar.MINUTE),time.get(Calendar.SECOND),time.get(Calendar.MILLISECOND));
		str = just+ "," + Double.toString(mylocation.getLatitude())+","+Double.toString(mylocation.getLongitude())+","+Float.toString(mylocation.getSpeed())+"\r\n";		
		Log.i("GPSTEST",str);
		try {				
			bfw.write(str);
			bfw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

	//maps method's
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setOverlay(GeoPoint point){
		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
		IconOverlay overlay = new IconOverlay(icon,point);
		
		//오버레이추가
		MapView map_view = (MapView)findViewById(R.id.mapview);
		List<Overlay> overlays = map_view.getOverlays();
		overlays.add(overlay);
	}
	
	//maps overlay
	private class IconOverlay extends Overlay{

		Bitmap mIcon;
		int mOffsetX;
		int mOffsetY;
		
		//icon loc
		GeoPoint mPoint;
		
		IconOverlay(Bitmap icon, GeoPoint initial){
			mIcon = icon;
			mOffsetX = 0 - icon.getWidth()/2;
			mOffsetY = 0 - icon.getHeight()/2;
			mPoint = initial;			
		}
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
			if(!shadow){
				Projection projection = mapview.getProjection();
				Point point = new Point();
				projection.toPixels(mPoint, point);
				point.offset(mOffsetX, mOffsetY);
				
				canvas.drawBitmap(mIcon, point.x,point.y,null);
			}
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			// TODO Auto-generated method stub
			mPoint = p;
			return super.onTap(p, mapView);
		}
		
	}
	
	
	
	private void logInit(){
		String ess = Environment.getExternalStorageState();  
		String sdCardPath = null;
		// get SDcard mount
		if(ess.equals(Environment.MEDIA_MOUNTED)) {  
		    sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/bodypro";		      
		} else {  
		    showMsg("SD Card not ready!");
		    return ;
		}
		// folder is notthing = create
		File fpath = new File(sdCardPath);
		if(!fpath.isDirectory()){
			fpath.mkdir();
		}	
		
		fileName = sdCardPath+"gpstest.txt";
		
		try {
			bfw = new BufferedWriter(new FileWriter(fileName,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			bfw = null;
			showMsg("Log file initail failed");				
			e.printStackTrace();
			return ;
		}
	}
		
	private void showMsg(String msg)  
	{  
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();  
	}
	
    
    
}
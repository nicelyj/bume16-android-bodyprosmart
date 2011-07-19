package Dslab.android.gps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
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
	private float mDistance=0;
	private GeoPoint geopoint;
	private MapView mapview;
	private MapController controller;
	private String fileName = "";		
	private BufferedWriter bfw = null;
	
	private MyOverlay mMyOverlay;
	private historyOverlay mHistoryOverlay;
	
	private TextView latitudeText;
	private TextView longotudeText;
	private TextView altitudeText;
	private TextView speedText;
	private TextView addressText;
	private TextView gpscntText;
	private TextView distanceText;
	
    private String provider;
	
	
	static final int INI1 = 36210148;
	static final int INI2 = 127210104;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //占신댐옙占쏙옙 占쏙옙占쏙옙占�        
        //locmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //gps 占쏙옙치占쏙옙占쏙옙 占쏙옙청
        //locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        
        //locmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        getGPS();
        geocoder = new Geocoder(this,Locale.KOREA);
        
        logInit();
        
        
    	latitudeText = (TextView)findViewById(R.id.latitudeText);
    	longotudeText = (TextView)findViewById(R.id.longitudeText);
    	altitudeText = (TextView)findViewById(R.id.altitudeText);
    	speedText = (TextView)findViewById(R.id.speedText);
    	addressText = (TextView)findViewById(R.id.addressText);
    	gpscntText = (TextView)findViewById(R.id.cntText);
    	distanceText = (TextView)findViewById(R.id.distanceText);
    	
        
        Button btn = (Button)findViewById(R.id.StartGpsBtn);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLocations();	
			}
		});
        
        btn = (Button)findViewById(R.id.LoadTrackBtn);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadLocations();
				
				
			}
		});
        
   	 //map control
        mapview = (MapView)findViewById(R.id.mapview);
        mapview.setBuiltInZoomControls(true);
    	
     	controller = mapview.getController();
        geopoint = new GeoPoint(INI1,INI2);       
        controller.setCenter(geopoint);	        
        controller.setZoom(15);
        
        //setOverlay(geopoint);
        //mMyOverlay = new MyOverlay(this, mapview);
        //mapview.getOverlays().add(mMyOverlay);
        //mMyOverlay.enableCompass();
    	//mMyOverlay.enableMyLocation();
    	
    	
        
    }
    private void loadLocations(){ 
    	
    	loadRawPoint();
    	
    	int size = pointvec.size();
    	Loc loc = new Loc();
    	for(int i = 0; i<size; i++){
    		loc = pointvec.get(i);
    		Log.i("GPSRAWDATA", Double.toString(loc.lati)+", "+Double.toString(loc.longi)+", "+Double.toString(loc.alti));
    		
    	}
    	//mHistoryOverlay.updateRawdata(pointvec);  
    	mHistoryOverlay = new historyOverlay(this,mapview,pointvec);
    	mapview.getOverlays().add(mHistoryOverlay);
    	mHistoryOverlay.enableCompass();
    	mHistoryOverlay.enableMyLocation();
    	
    }
    public class Loc{
    	public double lati = 0 ;
    	public double longi = 0;
    	public double alti = 0;

    }
    Vector<Loc> pointvec = new Vector<Loc>();   
    
    private void loadRawPoint(){    	
    	
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
			fpath.mkdir();		}	
		
		fileName = sdCardPath+"gpstest.txt";
						
		try{
			FileInputStream fis = new FileInputStream(new File(fileName));			
			int filesize = fis.available();
			byte[] tempdata = new byte[(int)filesize];
			fis.read(tempdata);
			fis.close();
			String strtmp = "";			
			int cnt=0;
			boolean bStart=true;
			Loc loc = null;
			for(byte ch : tempdata){
				char cbuff = (char)ch;				
				if(bStart){					
					if(cbuff == ','){
						
						if(cnt == 0)
						{
							loc = new Loc();
							cnt++;
						}
						else if(cnt == 1)
						{							
							loc.longi = Double.parseDouble(strtmp);
							cnt++;
						}
						else if(cnt == 2)
						{
							loc.lati = Double.parseDouble(strtmp);
							cnt++;
						}
						else if(cnt == 3){
							loc.alti = Double.parseDouble(strtmp);							
							pointvec.add(loc);							
							cnt = 0;
							bStart = false;							
							}						
						strtmp = "";
						
						}
					else
						strtmp += cbuff;					
				}
				
				if(cbuff == '\n')
					bStart = true;				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		
    }
    
    private static boolean result = false;
    public void getGPS(){
                
      
        locmanager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        //GPS 환경설정
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);       // 정확도
        criteria.setPowerRequirement(Criteria.POWER_LOW);   // 전원 소비량
        criteria.setAltitudeRequired(false);                // 고도, 높이 값을 얻어 올지를 결정
        criteria.setBearingRequired(false);                 // provider 기본 정보
        criteria.setSpeedRequired(false);                   //속도
        criteria.setCostAllowed(true);                      //위치 정보를 얻어 오는데 들어가는 금전적 비용
         
        //상기 조건을 이용하여 알맞은 GPS선택후 위치정보를 획득
         
        //manifest xml  : <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />//로케이션 메니저의 provider
        provider = locmanager.getBestProvider(criteria, true);
        
        if(provider == null){//GPS 장치가 없는 휴대폰이거나 설정이 꺼져있는 경우 바로 alert 처리하거나 GPS 설정으로 이동
            result = chkGpsService();
            if(result){
            	getGPS();
            }
             
        }else{
        	mylocation = locmanager.getLastKnownLocation(provider);//가장 최근의 로케이션을 가져온다. 안드로이드 폰이 꺼져있었거나 다른 위치로 이동한 경우 값이 없다.
            //location = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
            //이럴경우는 NETWORK_PROVIDER 로 부터 새로운 location을 지정 받는다.
            //특정조건(시간, 거리)이 되면  Listener를 invoke 시킨다.: 여기서는 1초 마다 5km)
        	locmanager.requestLocationUpdates(provider, 0, 0, this);//현재정보를 업데이트
             
            if(mylocation == null){
            	mylocation = locmanager.getLastKnownLocation(provider);
                if(mylocation == null){//그래도 null인경우 alert;
                	Toast.makeText(this, "GPS connect fail", Toast.LENGTH_SHORT).show();
                    
                    
                }

            }
        }	
    }
    private boolean chkGpsService() {
    	//GPS의 설정여부 확인 및 자동 설정 변경
        String gs = android.provider.Settings.Secure.getString(getContentResolver(),
        android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.w("chkGpsService" , "get GPs Service" );
         
        if (gs.indexOf("gps", 0) < 0) {
            Log.w("chkGpsService" , "status: off" );
            // GPS OFF 일때 Dialog 띄워서 설정 화면으로 이동.
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("GPS Status OFF !!!");
            gsDialog.setMessage("Change Setting !!");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            }).create().show();
            return false;
        } else {
            Log.w("chkGpsService" , "status: on" );                
            return true;
        }
    } 
    
    public void getLocations(){ 
    	StringBuffer juso = new StringBuffer();
    	
    	if(mylocation != null){
    		lati = mylocation.getLatitude();
        	longi = mylocation.getLongitude();
        	alti = mylocation.getAltitude();
        	speed = mylocation.getSpeed();
        	
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
    		
    		latitudeText.setText(Double.toString(lati));
    		altitudeText.setText(Double.toString(alti));
    		longotudeText.setText(Double.toString(longi));        	
        	speedText.setText(Double.toString(speed));
        	addressText.setText(juso);        	
        	gpscntText.setText(Integer.toString(mCnt));    	
        	distanceText.setText(Float.toString(mDistance));
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
		str = just+ ","
		+ Double.toString(mylocation.getLongitude())+","
		+ Double.toString(mylocation.getLatitude())+","		
		+ Double.toString(mylocation.getAltitude())+","
		+ Float.toString(mylocation.getSpeed())+","
		+ Float.toString(mDistance)+","
		+"\r\n";		
		
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
		
		//占쏙옙占쏙옙占쏙옙占쏙옙占쌩곤옙
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
			mOffsetX = 0 - icon.getWidth()/10;
			mOffsetY = 0 - icon.getHeight()/10;
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
	
	private class historyOverlay extends MyLocationOverlay{

		Path mPath;
		Paint mPaint;
		MapView mMapview;
		Vector<Loc> mVecloc;
		
		public historyOverlay(Context context, MapView mapView, Vector<Loc> vecloc) {
			super(context, mapView);
			// TODO Auto-generated constructor stub
			mPath = new Path();
			mPath.reset();
			mPaint = new Paint();
	    	mPaint.setAntiAlias(true);
	    	mPaint.setDither(true);
	    	mPaint.setColor(Color.RED);
	    	mPaint.setStyle(Paint.Style.STROKE);
	    	mPaint.setStrokeJoin(Paint.Join.ROUND);
	    	mPaint.setStrokeCap(Paint.Cap.ROUND);
	    	mPaint.setStrokeWidth(3);
	    	mPaint.setTextSize(20);
	    	mPaint.setStyle(Paint.Style.FILL);
	    	mMapview = mapView;
	    	mVecloc = vecloc;
		}
		
		
		
		@Override
		public synchronized boolean draw(Canvas canvas, MapView mapView,
				boolean shadow, long when) {
			// TODO Auto-generated method stub
			mPath.reset();	
			canvas.drawPath(mPath, mPaint);
			Log.i("DARWMAPS","DRAWMAPS");			
			updateRawdata(mVecloc);
			mPaint.setStyle(Paint.Style.STROKE);				
			canvas.drawPath(mPath, mPaint);
			
			return super.draw(canvas, mapView, shadow, when);
		}

		public void updateRawdata(Vector<Loc> vecloc){
			
			Point startPoint = new Point();
			
			Loc loc = vecloc.get(0);
			mMapview.getProjection().toPixels(new GeoPoint((int)(loc.lati*1E6), (int)(loc.longi*1E6)), startPoint);
			
			Path p = new Path();			
			p.reset();
			p.moveTo(startPoint.x, startPoint.y);
			
			for(int i = 1 ; i < vecloc.size(); i++)
			{	
				loc = vecloc.get(i);				
				Point endPoint = new Point();
				mMapview.getProjection().toPixels(new GeoPoint((int)(loc.lati*1E6), (int)(loc.longi*1E6)), endPoint);
				p.lineTo(endPoint.x, endPoint.y);				
				
			}
			mPath.addPath(p);
			
		}
	}
	
	//map path overlay
	
	
	private class MyOverlay extends MyLocationOverlay{
		
		Location MyBeforeLoc;
		Location MyCurrentLoc;
		Path mPath;
		Paint mPaint;
		MapView mMapview;
		Context mCtx;
		
		ArrayList<MyPathLocation> mMyPathLocationArray;
		

		public MyOverlay(Context context, MapView mapView) {
			super(context, mapView);
			// TODO Auto-generated constructor stub
			mPath = new Path();
			mPath.reset();
			mPaint = new Paint();
	    	mPaint.setAntiAlias(true);
	    	mPaint.setDither(true);
	    	mPaint.setColor(Color.RED);
	    	mPaint.setStyle(Paint.Style.STROKE);
	    	mPaint.setStrokeJoin(Paint.Join.ROUND);
	    	mPaint.setStrokeCap(Paint.Cap.ROUND);
	    	mPaint.setStrokeWidth(3);
	    	mPaint.setTextSize(20);
	    	mPaint.setStyle(Paint.Style.FILL);
	    	mMyPathLocationArray = new ArrayList<MyPathLocation>();

	    	mCtx = context;
	    	mMapview = mapView;
		}

		

		@Override
		public boolean draw(Canvas canvas, MapView mapView,
				boolean shadow, long when) {
			// TODO Auto-generated method stub
			
			if(MyBeforeLoc != null && MyCurrentLoc != null)
			{
				mPath.reset();	
				canvas.drawPath(mPath, mPaint);
				updatePath();
				mPaint.setStyle(Paint.Style.STROKE);				
				canvas.drawPath(mPath, mPaint);
				
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawTextOnPath(String.valueOf(mDistance), mPath, 0, 0, mPaint);
			}
			
			return super.draw(canvas, mapView, shadow, when);			
			
		}
		
		
		@Override
		public synchronized void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			super.onLocationChanged(location);
			
			MyBeforeLoc = MyCurrentLoc;
			MyCurrentLoc = location;
			if(MyBeforeLoc != null && MyCurrentLoc != null && MyBeforeLoc != MyCurrentLoc)
			{	
				mMyPathLocationArray.add(new MyPathLocation(MyBeforeLoc, MyCurrentLoc));
				mDistance += MyCurrentLoc.distanceTo(MyBeforeLoc);
								
			}
			else 
				return;
		}

		public void updatePath()
		{
			for(int i = 0 ; i < mMyPathLocationArray.size() ; i++)
			{
				Point startPoint = new Point();
				Point endPoint = new Point();
				MyPathLocation temp = mMyPathLocationArray.get(i);
				mMapview.getProjection().toPixels(new GeoPoint((int)(temp.mMyBeforeLocation.getLatitude()*1E6), (int)(temp.mMyBeforeLocation.getLongitude()*1E6)), startPoint);
				mMapview.getProjection().toPixels(new GeoPoint((int)(temp.mMyCurrentLocation.getLatitude()*1E6), (int)(temp.mMyCurrentLocation.getLongitude()*1E6)), endPoint);
				
				Path p = new Path();			
				p.reset();
				p.moveTo(startPoint.x, startPoint.y);
				p.lineTo(endPoint.x, endPoint.y);				
				mPath.addPath(p);				
			}
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
	
	class MyPathLocation
	{
		public Location mMyBeforeLocation;
		public Location mMyCurrentLocation;
		
		public MyPathLocation(Location myBeforeLocation, Location myCurrentLocation)
		{
			mMyBeforeLocation = myBeforeLocation;
			mMyCurrentLocation = myCurrentLocation;
		}
	}
    
    
}
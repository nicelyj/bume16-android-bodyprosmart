package Dslab.android.BodyproSmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


public class mainActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageButton startBtn = (ImageButton)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.startBtn)
		{
			//realdataActivity¸¦ ¿ÀÇÂÇÑ´Ù.
			Intent intent = new Intent(getApplicationContext(), realdataActivity.class);
			startActivity(intent);		
		}
		
	}
    
    
    
}
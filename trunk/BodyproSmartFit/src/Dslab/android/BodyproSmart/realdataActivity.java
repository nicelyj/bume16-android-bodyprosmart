package Dslab.android.BodyproSmart;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class realdataActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.realdata);
		
		ImageButton exitBtn = (ImageButton)findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.exitBtn)
			finish();
		
	}
	
	
	

}

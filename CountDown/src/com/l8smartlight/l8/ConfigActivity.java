package com.l8smartlight.l8;

import com.l8smartlight.l8.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ConfigActivity extends Activity {

	private int time = 10;
	private Typeface digital = null;
	private TextView timeLabel = null;
	private Button minus = null;
	private Button plus = null;
	private Button reset = null;
	private Button save = null;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		digital = Typeface.createFromAsset(getAssets(), "Digital.ttf");
		this.setContentView(R.layout.config);
		configViews();
	}

	private void configViews() {
		timeLabel = (TextView) this.findViewById(R.id.time);
		timeLabel.setTypeface(digital);
		minus = (Button) this.findViewById(R.id.minus);
		minus.setTypeface(digital);
		minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (time > 1) {
					time--;
				} else {
					time = 60;
				}
				timeLabel.setText(String.valueOf(time));
			}

		});
		plus = (Button) this.findViewById(R.id.plus);
		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (time < 60) {
					time++;
				} else {
					time = 1;
				}
				timeLabel.setText(String.valueOf(time));
			}

		});
		plus.setTypeface(digital);
	
	reset =  (Button) this.findViewById(R.id.reset);
		
		
	save = (Button) this.findViewById(R.id.save);
	
	save.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("time", time);
				ConfigActivity.this.setResult(RESULT_OK, intent);
				finish();
		}
		
	});
	
	
	reset.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("reset",true);
				ConfigActivity.this.setResult(RESULT_OK, intent);
				finish();
		}
		
	});
	}
	
}

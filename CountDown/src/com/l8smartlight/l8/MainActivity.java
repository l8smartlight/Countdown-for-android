package com.l8smartlight.l8;

import com.l8smartlight.sdk.android.AndroidL8Manager;
import com.l8smartlight.sdk.android.AndroidL8ManagerListener;
import com.l8smartlight.sdk.core.Color;
import com.l8smartlight.sdk.core.L8;
import com.l8smartlight.sdk.core.L8Exception;
import com.l8smartlight.l8.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AndroidL8ManagerListener, Handler.Callback {

	private final static int REQUEST_CONFIG = 2;

	private Typeface digital = null;
	private TextView textCounter = null;
	private ImageButton button1 = null;
	private ImageButton button2 = null;
	private ImageButton button3 = null;
	private ImageButton button4 = null;

	private CountDownBar thread = null;

	private AndroidL8Manager l8Manager = null;

	private int time = 10;

	private boolean start = false;

	private String smartlightLogo = "#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#0016ff-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#009bdb-#ffef00-#ffef00-#ffef00-#ff6700-#ff6700-#000000-#000000-#00c9db-#ffef00-#000000-#ffef00-#000000-#ff6700-#000000-#000000-#00d3db-#ffef00-#ffef00-#ffef00-#ff6700-#ff6700-#000000-#000000-#00dbb1-#00dbb1-#00dbb1-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#000000-#0000ff";

	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		digital = Typeface.createFromAsset(getAssets(), "Digital.ttf");
		setContentView(R.layout.activity_main);
		textCounter = (TextView) findViewById(R.id.counter);
		textCounter.setTypeface(digital);
		handler = new Handler(this);
		l8Manager = new AndroidL8Manager(this);
		l8Manager.registerListener(this);
		l8Manager.init(this);
		configViews();
	}

	private void configViews() {

		button1 = (ImageButton) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (l8Manager.getConnectedDevicesCount() > 0) {
					startAndStop();
				} else {
					Toast.makeText(MainActivity.this, "No L8 device connected", 0).show();
				}
			}
		});

		button2 = (ImageButton) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (l8Manager.getConnectedDevicesCount() > 0) {
					reset();
				} else {
					Toast.makeText(MainActivity.this, "No L8 device connected", 0).show();
				}
			}
		});

		button3 = (ImageButton) findViewById(R.id.button3);

		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (l8Manager.getConnectedDevicesCount() > 0) {
					try {
						l8Manager.setMatrix(smartlightLogo);
					} catch (L8Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		button4 = (ImageButton) findViewById(R.id.button4);
		button4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
				startActivityForResult(intent, REQUEST_CONFIG);
			}
		});

	}

	private void startAndStop() {
		String text = (time > 9 ? time : "0" + time) + ":00";
		this.textCounter.setText((time > 9 ? time : "0" + time) + ":00");
		if (this.start && thread != null) {
			start = false;
			if (this.thread.isAlive()) {
				thread.cancelCountDown();
			}
			button1.setImageDrawable(this.getResources().getDrawable(R.drawable.play));
			try {
				l8Manager.setMatrix(smartlightLogo);
			} catch (L8Exception e) {
				e.printStackTrace();
			}
		} else {
			start = true;
			if (thread == null || !thread.isAlive()) {
				thread = new CountDownBar(time);
				thread.start();
			} else {
				thread.cancelCountDown();
				thread = new CountDownBar(time);
				thread.start();
			}
			button1.setImageDrawable(this.getResources().getDrawable(R.drawable.stop));
		}
	}

	private void reset() {
		start = true;
		this.textCounter.setText((time > 9 ? time : "0" + time) + ":00");
		if (thread == null || !thread.isAlive()) {
			thread = new CountDownBar(time);
			thread.start();
		} else {
			thread.cancelCountDown();
			thread = new CountDownBar(time);
			thread.start();
		}
		button1.setImageDrawable(this.getResources().getDrawable(R.drawable.stop));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		l8Manager.onDestroy();
		super.onDestroy();
	}

	@Override
	public void bluetoothNotAvailable() {
	}

	@Override
	public void bluetoothNotEnabled() {
	}

	@Override
	public void noDevicesRegistered() {
		l8Manager.scan(this);
	}

	@Override
	public void deviceConnected(L8 l8) {
		Toast.makeText(this, "l8 connected", 0).show();
	}

	@Override
	public void deviceDisconnected(L8 l8) {
		Toast.makeText(this, "l8 Disconnected", 0).show();
	}

	@Override
	public void noDevicesConnected() {
		Toast.makeText(this, "No L8 device connected", 0).show();
	}

	@Override
	public void allConnectionsDone() {
	}

	@Override
	public void simulatorRequested(L8 l8, boolean newSimulator) {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_CONFIG) {
			l8Manager.onActivityResult(requestCode, resultCode, data);
		} else {
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra("reset", false)) {
					l8Manager.removeRegisteredDevices();
					Toast.makeText(this, "Registered L8 removed, reset app for a new scan", Toast.LENGTH_LONG).show();
				} else {
					this.time = data.getIntExtra("time", 10);
					this.textCounter.setText((time > 9 ? time : "0" + time) + ":00");
					if (start) {
						this.startAndStop();
					}
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		this.textCounter.setText((msg.arg1 < 10 ? "0" + msg.arg1 : msg.arg1) + ":" + (msg.arg2 < 10 ? "0" + msg.arg2 : msg.arg2));
		return true;
	}

	private class CountDownBar extends Thread {

		private int minutes = 0;
		private Color colorBar = null;
		private Color center = null;
		private boolean active = false;
		private boolean cancelled = false;
		private boolean reset = false;

		private WakeLock wakeLock = null;

		private boolean[] upperRow1;
		private boolean[] upperRow2;
		private boolean[] upperRow3;

		private boolean[] bottomRow1;
		private boolean[] bottomRow2;
		private boolean[] bottomRow3;

		public CountDownBar(int minutes) {
			this.minutes = minutes > 60 ? 60 : minutes;
			colorBar = new Color(0, 128, 0);
			center = new Color(128, 0, 0);
			configBoard();
		}

		private void configBoard() {
			upperRow1 = new boolean[8];
			upperRow2 = new boolean[6];
			upperRow3 = new boolean[4];

			bottomRow1 = new boolean[8];
			bottomRow2 = new boolean[6];
			bottomRow3 = new boolean[4];

			resetBoardRow(upperRow1, true);
			resetBoardRow(upperRow2, true);
			resetBoardRow(upperRow3, true);

			resetBoardRow(bottomRow1, false);
			resetBoardRow(bottomRow2, false);
			resetBoardRow(bottomRow3, false);
		}

		private void resetBoardRow(boolean[] row, boolean value) {
			for (int i = 0; i < row.length; i++) {
				row[i] = value;
			}
		}

		@Override
		public void run() {
			try {
				createWakeLock();
				active = true;
				int minutesCounter = minutes;
				int secondsCounter = 0;
				colorBar = this.getColorByTime(minutesCounter);
				l8Manager.setMatrix(generateMatrixBar(minutesCounter));
				while (active && !cancelled) {
					Thread.sleep(1000);
					if (!cancelled) {
						secondsCounter++;
						if (secondsCounter >= 60) {
							secondsCounter = 0;
							minutesCounter--;
							colorBar = this.getColorByTime(minutesCounter);
							l8Manager.setMatrix(generateMatrixBar(minutesCounter));
						}
						Message msg = new Message();
						msg.arg1 = minutesCounter == 0 ? 0 : minutesCounter - 1;
						msg.arg2 = 60 - (secondsCounter > 60 ? 60 : secondsCounter);
						handler.sendMessage(msg);
						if (minutesCounter <= 0) {
							active = false;
						}
					}
				}
				if (!cancelled) {
					Message msg = new Message();
					msg.arg1 = 0;
					msg.arg2 = 0;
					handler.sendMessage(msg);
					l8Manager.setMatrix(generateMatrixBar(0));
					Thread.sleep(1000);
					l8Manager.setMatrix(smartlightLogo);
				}

			} catch (Exception e) {
				Log.e("Error Count down", "Error: " + e);
			} finally {
				if (wakeLock != null && wakeLock.isHeld()) {
					wakeLock.release();
				}
			}
		}

		private void createWakeLock() {
			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CountDown");
			wakeLock.acquire();
		}

		public void cancelCountDown() {
			cancelled = true;
			if (wakeLock != null && wakeLock.isHeld()) {
				wakeLock.release();
			}
		}

		public Color[][] generateMatrixBar(int minutes) {
			int count = 0;
			int index = 0;
			Color[][] matrix = new Color[8][8];
			for (Color[] row : matrix) {
				for (int i = 0; i < row.length; i++) {
					if (count < minutes) {
							row[i] = colorBar;
							count++;
					} else {
						row[i] = Color.black;
					}
					index++;
				}
			}
			if (minutes < 10) {
				drawNumber(matrix, 2, minutes);
			} else {
			    String one = ""+String.valueOf(minutes).charAt(0);
				String two = ""+String.valueOf(minutes).charAt(1);
				drawNumber(matrix, 0, Integer.valueOf(one));
				drawNumber(matrix, 4, Integer.valueOf(two));
			}
			return matrix;
		}

		private Color getColorByTime(int minutes) {
			Color result = Color.black;
			Color[] colors = { Color.red, Color.orange, Color.yellow, Color.green, Color.blue };
			float part = (float) this.minutes / (float) 5; // 5 parts from total
			for (int i = 0; i < 5; i++) {
				if (minutes >= (part * i) && minutes <= ((part * i) + part)) {
					return colors[i];
				}
			}
			return result;
		}

		private void drawNumber(Color[][] matrix, int startY, int number) {
			switch (number) {
			case 0: {
				zero(matrix, startY);
				break;
			}
			case 1: {
				one(matrix, startY);
				break;
			}
			case 2: {
				two(matrix, startY);
				break;
			}
			case 3: {
				three(matrix, startY);
				break;
			}
			case 4: {
				four(matrix, startY);
				break;
			}
			case 5: {
				five(matrix, startY);
				break;
			}
			case 6: {
				six(matrix, startY);
				break;
			}
			case 7: {
				seven(matrix, startY);
				break;
			}
			case 8: {
				eight(matrix, startY);
				break;
			}
			case 9: {
				nine(matrix, startY);
				break;
			}
			}
		}

		private void zero(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 0] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 0] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 0] = Color.red;
			matrix[7][startY + 1] = Color.red;
			matrix[7][startY + 2] = Color.red;

		}

		private void one(Color[][] matrix, int startY) {
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void two(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 0] = Color.red;
			matrix[7][startY + 0] = Color.red;
			matrix[7][startY + 1] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void three(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 0] = Color.red;
			matrix[7][startY + 1] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void four(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 0] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void five(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 0] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 0] = Color.red;
			matrix[7][startY + 1] = Color.red;
			matrix[7][startY + 2] = Color.red;

		}

		private void six(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 0] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 0] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 0] = Color.red;
			matrix[7][startY + 1] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void seven(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void nine(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 0] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

		private void eight(Color[][] matrix, int startY) {
			matrix[3][startY + 0] = Color.red;
			matrix[3][startY + 1] = Color.red;
			matrix[3][startY + 2] = Color.red;
			matrix[4][startY + 0] = Color.red;
			matrix[4][startY + 2] = Color.red;
			matrix[5][startY + 0] = Color.red;
			matrix[5][startY + 1] = Color.red;
			matrix[5][startY + 2] = Color.red;
			matrix[6][startY + 0] = Color.red;
			matrix[6][startY + 2] = Color.red;
			matrix[7][startY + 0] = Color.red;
			matrix[7][startY + 1] = Color.red;
			matrix[7][startY + 2] = Color.red;
		}

	}

}

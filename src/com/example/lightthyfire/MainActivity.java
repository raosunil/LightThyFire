package com.example.lightthyfire;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * @author Sunil
 *
 */
public class MainActivity extends Activity {
	private static final int REFRESH = 1;
	private Handler guiRefresher;
	private ComplexGameView gameView;
	private String initVel, angleDeg = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gameView = (ComplexGameView) findViewById(R.id.gameView);
		
		guiRefresher = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what == REFRESH){
					gameView.invalidate();
				}
				super.handleMessage(msg);
			}
		};

		gameView.setCallbackHandler(guiRefresher);


		Thread t = new Thread(gameView);
		t.setDaemon(true);
		t.start();
	}

	/**Called when clicked Enter button
	 * @param view
	 */
	public void getFieldValues(View view){
		initVel =  ((EditText)findViewById(R.id.initVeloText)).getText().toString();
		angleDeg = ((EditText) findViewById(R.id.angleDegreeText)).getText().toString();
	}


	/**Returns the input args from the User back to the view
	 * @return
	 */
	public Float[] getInputArguments() {
		Float[] inputArr = null;
		try{						
			if(initVel!= null && angleDeg!=null && initVel.trim().length()>0 && angleDeg.trim().length()>0 ){
				if (Float.valueOf(initVel)<=0 || Float.valueOf(angleDeg)<=0)
					throw new Exception("Input values are less than 0");
				inputArr = new Float[2];
				inputArr[0] = Float.valueOf(initVel);
				inputArr[1] = Float.valueOf(angleDeg);
			}		
		}catch (Exception e){
			Log.d("MainActivity getInputArguments", "Exception is : "+e.getLocalizedMessage());
		}
		return inputArr;        
	}

	/**For handling click event of Quit button
	 * @param v
	 */
	public void moveBackground(View v){   	
		System.runFinalization();
		System.exit(0);

	}

	/**Called when the game needs to restart after winning
	 * @param view
	 */
	public void resetGameView(View view){
		gameView.setCallOnce(false);
	}
}

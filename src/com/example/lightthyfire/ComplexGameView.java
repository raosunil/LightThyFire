package com.example.lightthyfire;

import java.util.Random;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.PiecesManager;
import com.example.hud.Scoreboard;
import com.example.math.Vector2D;
import com.example.pieces.Ball;
import com.example.pieces.Pad;
import com.example.pieces.Pad.Axis;
import com.example.pieces.Wall;


/**
 * @author Sunil
 *
 */
public class ComplexGameView extends View implements Runnable {
	//initial values only - gets real values after drawn
	private int width = 400, height = 700;
	private Paint background;
	private Handler handler;

	private PiecesManager manager;
	private Context ctx;	
	private Pad targetPad;

	//used during createBall and also used during reset
	private boolean callOnce = false;


	public ComplexGameView(Context context) {
		super(context);
		this.ctx = context;
		init();
	}

	public ComplexGameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		init();
	}

	public ComplexGameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.ctx = context;
		init();
	}

	private void init() {
		/*background = new Paint();
		background.setColor(Color.WHITE);*/
		manager = new PiecesManager(ctx);
		manager.addPiece(new Wall(new Rect(0, 0, 10, height),
				new Vector2D(1, 0)));
		manager.addPiece(new Wall(new Rect(width - 10, 0, width, height),
				new Vector2D(-1, 0)));
		Wall north = new Wall(new Rect(0, 0, width, 10), new Vector2D(0, -1));
		manager.addPiece(north);
		Wall south = new Wall(new Rect(0, height - 10, width, height),
				new Vector2D(0, 1));
		manager.addPiece(south);
		targetPad = new Pad(new Vector2D(0, -1), Axis.Y_AXIS, 400,BitmapFactory.decodeResource(getResources(), R.drawable.pirate_ship));
		manager.addPiece(targetPad);


		manager.addPiece(new Scoreboard(targetPad));
	}

	public void onDraw(Canvas canvas) {		
		/*the below code snippet gets called after having got 
		the real width and height. Accordingly the canon ball is drawn after this step
		*/ 
		if (!callOnce){
			width = getWidth();
			height = getHeight();
			init();
			createBall();
			callOnce = true;
		}
		canvas.save();
		//canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		Drawable d = getResources().getDrawable(R.drawable.waves);
		d.setBounds(0, 0, getWidth(), getHeight());		
		d.draw(canvas);
		manager.draw(canvas);
		canvas.restore();
	}

	private void createBall() {
		Random rdm = new Random();
		Vector2D vec = new Vector2D(getWidth(), getHeight());
		Ball ball = new Ball(new Vector2D(50,height - 120 ), vec,
				0.005f, manager, Color.rgb(50 + rdm.nextInt(206),
						50 + rdm.nextInt(206), 50 + rdm.nextInt(206)),
						BitmapFactory.decodeResource(getResources(), R.drawable.fire_ball));
		manager.addPiece(ball);
	}



	@Override
	public void run() {		
		while (true) {
			try {			
				manager.processAI();
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
				Thread.sleep(10);
			} catch (Exception e) {
				Log.d("ComplexGameView run", e.getLocalizedMessage());
			}
		}
	}

	public void setCallbackHandler(Handler guiRefresher) {
		this.handler = guiRefresher;
	}

	public boolean onTouchEvent(MotionEvent evt) {		
		for (int i = 0; i < evt.getPointerCount(); i++) {
			targetPad.notifyMotionEvent(evt.getX(i), evt.getY(i));
		}
		return true;
	}


	public boolean isCallOnce() {
		return callOnce;
	}

	public void setCallOnce(boolean callOnce) {
		this.callOnce = callOnce;
	}

}

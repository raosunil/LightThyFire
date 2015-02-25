package com.example.pieces;

import com.example.math.Vector2D;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class Pad extends BlockableEntity {
	private Paint wallPaint;

	private int defaultDistanceFromAxis;
	private HitListener listener;
	private Bitmap image;
	
	float posX,posY;
	private boolean clickedOnce = false;

	

	

	private Axis thisAxis;

	public Pad(Vector2D normal, Axis axis, int defaultDistance, Bitmap image) {
		super(new Rect(), normal);
		wallPaint = new Paint();
		wallPaint.setColor(Color.GRAY);
		this.image = image;

		thisAxis = axis;
		this.defaultDistanceFromAxis = defaultDistance;

	}

	public void notifyMotionEvent(float x, float y) {
		this.posX = x;
		this.posY = y;
		thisAxis.atualize(this, x, y);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		if(isClickedOnce())
			canvas.drawBitmap(image, posX,posY, wallPaint);
		//canvas.drawRect(getBounds(), wallPaint);
		canvas.restore();
	}

	@Override
	public void processAI() {
		// Do nothing, position is handled by events
	}
	
	public void addHitListener(HitListener hitListener) {
		this.listener = (HitListener) hitListener;
	}	
	
	public void notifyHit() {
		if (listener != null)
			listener.notifyHited();
	}
	
	public enum Axis {
		X_AXIS {
			@Override
			void atualize(Pad pad, float x, float y) {
				pad.setBounds(new Rect((int) x - 120,
						(int) y - 180, (int) x + 120,
						(int) y + 120));
				pad.posX = x;
				pad.posY = y;
				pad.setClickedOnce(true);
			}
		},
		Y_AXIS {
			@Override
			void atualize(Pad pad, float x, float y) {
				pad.setBounds(new Rect((int) x + 45,
						(int) y - 5, (int) x + 145,
						(int) y + 190));
				pad.posX = x;
				pad.posY = y;
				pad.setClickedOnce(true);
			}

		};
		abstract void atualize(Pad pad, float x, float y);
	}
	
	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
	public boolean isClickedOnce() {
		return clickedOnce;
	}

	public void setClickedOnce(boolean clickedOnce) {
		this.clickedOnce = clickedOnce;
	}


}

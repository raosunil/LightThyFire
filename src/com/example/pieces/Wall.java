package com.example.pieces;

import com.example.math.Vector2D;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class Wall extends BlockableEntity {
	private Paint wallPaint;
	private HitListener listener;

	public Wall(Rect bounds, Vector2D normal) {
		super(bounds, normal);
		wallPaint = new Paint();
		wallPaint.setColor(Color.GRAY);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.drawRect(getBounds(), wallPaint);
		canvas.restore();
	}

	@Override
	public void processAI() {
		// Do nothing, walls don't think
	}

	public void addHitListener(HitListener listener) {
		this.listener = listener;
	}

	
	public void notifyHit() {
		if (listener != null)
			listener.notifyHited();
	}

}

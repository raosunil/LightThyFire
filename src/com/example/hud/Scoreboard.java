package com.example.hud;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import com.example.pieces.HitListener;
import com.example.pieces.Pad;


public class Scoreboard extends com.example.pieces.Entity {
	private int targetHits;


	private Paint letter;

	public Scoreboard(Pad target) {
		super(null);

		targetHits = 0;
		letter = new Paint();
		letter.setColor(Color.BLACK);
		letter.setStyle(Style.FILL_AND_STROKE);
		letter.setTextSize(12);

		target.addHitListener(new HitListener() {
			public void notifyHited() {
				targetHits++;
			}
		});


	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.drawText("Hit Status" + " : " + (targetHits>0?"true":"false"), 350, 250, letter);
		canvas.restore();
	}

	@Override
	public void processAI() {
		// TODO Auto-generated method stub

	}
	public int getTargetHits() {
		return targetHits;
	}

	public void setTargetHits(int targetHits) {
		this.targetHits = targetHits;
	}

}

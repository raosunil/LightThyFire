package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.pieces.Entity;

import android.content.Context;
import android.graphics.Canvas;


public class PiecesManager {
	private CopyOnWriteArrayList<Entity> pieces;
	private Context context;

	private Collection<Entity> tmpList;

	public PiecesManager(Context ctx) {
		context = ctx;
		pieces = new CopyOnWriteArrayList<Entity>();
		tmpList = Collections.synchronizedCollection(new ArrayList<Entity>());
	}

	public void addPiece(Entity ent) {
		tmpList.add(ent);
		//		pieces.add(ent);
	}

	public void draw(Canvas canvas) {
		pieces.addAll(tmpList);
		tmpList.clear();
		for (Entity ent : pieces)
			ent.draw(canvas);
	}

	public List<Entity> getPieces() {
		return pieces;
	}

	/**Remove canon ball after strike
	 * @param ent
	 */
	public synchronized void remove(Entity ent){
		pieces.remove(ent);
		tmpList.remove(ent);
	}

	/**
	 * The main method which calculates the position logic of each of its pieces
	 * Walls are exempted as they remain in the same place
	 */
	public void processAI() {
		pieces.addAll(tmpList);
		tmpList.clear();
		for (Entity ent : pieces)
			ent.processAI();
	}
	public Context getContext() {
		return context;
	}
}

package com.example.pieces;

import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.example.PiecesManager;
import com.example.hud.Scoreboard;
import com.example.lightthyfire.MainActivity;
import com.example.lightthyfire.R;
import com.example.math.MathUtil;
import com.example.math.Vector2D;



/**
 * @author Sunil
 *
 */
public class Ball extends Entity {
	private Paint paint;

	private Vector2D pos;
	private Vector2D dir;
	private float speed;

	private PiecesManager pieces;

	private Bitmap image;

	private Entry<Float,Float> start;


	private Entry<Float,Float> mid1;
	private Entry<Float,Float> mid2;
	private Entry<Float,Float> end;	

	private Rect collisionBox;


	private float t = 0;


	public Ball(Vector2D pos, Vector2D dir, float speed) {
		super(new Rect());

		this.pos = pos;
		this.dir = dir;
		this.speed = speed;
		paint = new Paint();
		paint.setColor(Color.RED);
	}

	public Ball(Vector2D pos, Vector2D dir, float speed, PiecesManager manager,
			int color,Bitmap image) {
		super(new Rect());

		this.pos = pos;
		this.dir = dir;
		this.speed = speed;		

		paint = new Paint();
		paint.setColor(color);

		pieces = manager;
		this.image = image;
	}

	public void setPiecesManager(PiecesManager manager) {
		pieces = manager;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.drawBitmap(image, pos.getX(),pos.getY(), paint);
		//		canvas.drawCircle(pos.getX(), pos.getY(), 10, paint);
		//canvas.drawRect(getCollisionBox(), paint);
		canvas.restore();
	}

	/*	private void move() {
		pos.plusMe(dir.multiply(speed));
	}
	 */
	/**
	 * Implements Bezier curve by setting the position
	 */
	private void traceParabola(){

		try{

			/*var p0 = {x:60, y:10};
		   var p1 = {x:150, y:350};
		   var p2 = {x:300, y:375};
		   var p3 = {x:400, y:20};*/

			if (start!= null && mid1!=null && mid2!=null && end!=null){
				//control points of Bezier curve for parabola
				Vector2D p0 = new Vector2D(start.getKey(),start.getValue());
				Vector2D p1 = new Vector2D(mid1.getKey(),mid1.getValue());
				Vector2D p2 = new Vector2D(mid2.getKey(),mid2.getValue());
				Vector2D p3 = new Vector2D(end.getKey(),end.getValue());

				/*List<Vector2D> tmpList = new ArrayList<Vector2D>();*/

				/*
				 * (a + b) cube - degree 3 ( 1 more than the required degree)
				 * var cx = 3 * (p1.x - p0.x)
      var bx = 3 * (p2.x - p1.x) - cx;
      var ax = p3.x - p0.x - cx - bx;

      var cy = 3 * (p1.y - p0.y);
      var by = 3 * (p2.y - p1.y) - cy;
      var ay = p3.y - p0.y - cy - by;

      var xt = ax*(t*t*t) + bx*(t*t) + cx*t + p0.x;

      var yt = ay*(t*t*t) + by*(t*t) + cy*t + p0.y;

      player.t += player.speed;
				 * */
				Vector2D c = p1.minus(p0).multiply(3);
				Vector2D b = p2.minus(p1).multiply(3).minus(c);
				Vector2D a = p3.minus(p0).minus(c).minus(b);
				Vector2D result = a.multiply(t*t*t).plus(b.multiply(t*t)).plus(c.multiply(t)).plus(p0);
				t = t + speed;
				if (getT()>1){
					setT(1);
					setInputArguments();
					setT(0);
				}


				pos.set(result);
			}else{
				setInputArguments();
			}
		}catch (Exception e){
			Log.d("Ball traceParabola", "Exception is : "+e.getLocalizedMessage());
		}


	}

	private TreeMap<Float,Float> getParabolaPoints(Float velocity, Float degree) {
		// TODO Auto-generated method stub

		double radian_angle_temp = degree * (Math.PI / 180); 
		TreeMap<Float,Float> vals = new TreeMap<Float,Float>();
		int inital_x_val = 60;		
		for (int i = inital_x_val; i< 10000; i = i + 20){

			double temp_time = i/(velocity * Math.cos(radian_angle_temp));
			double temp_height = (velocity * temp_time * Math.sin(radian_angle_temp)) 
			- ((32.17 * temp_time * temp_time)/2);

			if ((temp_height/2)<0 || (temp_height/2)> (dir.getY() - 20) || (i/2)>(dir.getX() - 20)){				 
				break;
			}		
			vals.put(Float.valueOf(i/2),Float.valueOf((float) (dir.getY() - temp_height/2)));
		}

		return vals;
	}

	@Override
	public void processAI() {
		try{
			//bounds created to suit the image 
			Rect bounds = new Rect((int) pos.getX(), (int) pos.getY() + 35,
					(int) pos.getX() + 45, (int) pos.getY() + 65);
			setCollisionBox(bounds);
			boolean alreadyHit = false;
			for (Entity ent : pieces.getPieces()) {
				if (ent == this)
					continue;
				if(ent instanceof Scoreboard){
					if (((Scoreboard) ent).getTargetHits()>0)
						alreadyHit = true;
				}
				if (ent instanceof BlockableEntity)
					if (bounds.intersect(ent.getBounds())) {
						
						/*//start logic of bounce
					Vector2D n = ((BlockableEntity) ent).getNormal();
					// r = v-2 * v.dot( n ) * n
					pos.plusMe(dir.minus(n.multiply(2).multiply(dir.dot(n))));
					//end logic of bounce
						 */					
						
						if(ent instanceof Pad){
							 ((Pad)ent).notifyHit();
							 if (!alreadyHit)
								 ((Pad)ent).setImage(BitmapFactory.decodeResource(pieces.getContext().getResources(), R.drawable.shipwreck));
							 pieces.remove(this);	
						 }
						 break;
					}
			}
			//move();
			traceParabola();
		}catch (Exception e){
			Log.d("Ball processAI", "Exception is : "+e.getLocalizedMessage());
		}
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}

	public Entry<Float, Float> getStart() {
		return start;
	}

	public void setStart(Entry<Float, Float> start) {
		this.start = start;
	}

	public Entry<Float, Float> getMid1() {
		return mid1;
	}

	public void setMid1(Entry<Float, Float> mid1) {
		this.mid1 = mid1;
	}

	public Entry<Float, Float> getMid2() {
		return mid2;
	}

	public void setMid2(Entry<Float, Float> mid2) {
		this.mid2 = mid2;
	}

	public Entry<Float, Float> getEnd() {
		return end;
	}

	public void setEnd(Entry<Float, Float> end) {
		this.end = end;
	}

	/**
	 * Responsible for getting input arguments from Activity 
	 * and setting the control points for Bezier curve
	 */
	private void setInputArguments(){
		Context ctx = pieces.getContext();
		setStart(null);
		setMid1(null);
		setMid2(null);
		setEnd(null);


		if (ctx!=null && (ctx instanceof MainActivity)){
			MainActivity activity = (MainActivity)ctx;
			Float[] inputArgs = activity.getInputArguments();
			if (inputArgs!= null){
				TreeMap<Float,Float> parabolaPoints = getParabolaPoints(inputArgs[0],inputArgs[1]);
				Entry<Float,Float> first = parabolaPoints.firstEntry();
				Entry<Float,Float> last = parabolaPoints.lastEntry();				
				setStart(first);
				setEnd(last);

				//to determine next 2 points
				float distance = last.getKey() - first.getKey();
				float xMid1 = (int)distance /3 ;
				float remainder = xMid1%10;
				if (remainder != 0){
					if (remainder <5){
						xMid1 = xMid1 - remainder;
					}else{
						xMid1 = xMid1 + (10 - remainder);
					}
				}else {
					//do nothing
				}
				xMid1 = xMid1 + first.getKey();
				Float yMid1 = parabolaPoints.get(Float.valueOf(xMid1));

				float xMid2 = ((int)distance * 2 )/3 ;
				remainder = xMid2%10;
				if (remainder != 0){
					if (remainder <5){
						xMid2 = xMid2 - remainder;
					}else{
						xMid2 = xMid2 + (10 - remainder);
					}
				}else {
					//do nothing
				}
				xMid2 = xMid2 + first.getKey();
				Float yMid2 = parabolaPoints.get(Float.valueOf(xMid2));
				setMid1(new java.util.AbstractMap.SimpleEntry<Float,Float>((float) xMid1,yMid1));
				setMid2(new java.util.AbstractMap.SimpleEntry<Float,Float>((float) xMid2,yMid2));
			}
		}


	}


	public Rect getCollisionBox() {
		return collisionBox;
	}

	public void setCollisionBox(Rect collisionBox) {
		this.collisionBox = collisionBox;
	}


}

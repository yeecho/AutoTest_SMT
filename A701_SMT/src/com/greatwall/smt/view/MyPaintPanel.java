package com.greatwall.smt.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyPaintPanel extends View{
	

//	private TouchTestActivity2 mActivity;
	
	Paint paint = new Paint();
	Paint paint1 = new Paint();
	Paint paint2 = new Paint();
	
	class Pos{
		float x;
		float y;
	}
	
	private ArrayList<Pos> list = new ArrayList<MyPaintPanel.Pos>();
	
	public MyPaintPanel(Context context) {
		this(context, null);
	}

	public MyPaintPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
//		this.mActivity = (TouchTestActivity2) context;
		paint.setColor(Color.RED);
		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint1.setStrokeWidth(2);
		paint1.setColor(Color.BLUE);
		paint2.setColor(Color.BLUE);
		paint2.setAntiAlias(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			list.clear();
			Pos a = new Pos();
			a.x = event.getX();
			a.y = event.getY();
			list.add(a);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Pos a1 = new Pos();
			a1.x = event.getX();
			a1.y = event.getY();
			list.add(a1);
			invalidate();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
			
		for (int i = 1; i < list.size(); i++) {
			canvas.drawLine(list.get(i-1).x, list.get(i-1).y, list.get(i).x, list.get(i).y, paint);
		}
		if (list.size()>0) {
			canvas.drawCircle(list.get(list.size()-1).x, list.get(list.size()-1).y, 10, paint2);
			canvas.drawLine(list.get(list.size()-1).x-1280, list.get(list.size()-1).y, list.get(list.size()-1).x+1280, list.get(list.size()-1).y, paint1);
			canvas.drawLine(list.get(list.size()-1).x, list.get(list.size()-1).y-1280, list.get(list.size()-1).x, list.get(list.size()-1).y+1280, paint1);
		}
	}

}

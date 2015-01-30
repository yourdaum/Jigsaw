package com.nathan.jigsaw.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PieceView extends ImageView {
	
	Rect mCorrectRect;
	final int mCorrectRate = 80;  // percentage.
	int mInitLeft, mInitTop;
	boolean mDone = false;
	
	public PieceView(Context context){
		super(context);
	}
	
	public PieceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PieceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setInitPosition(int left, int top){
		this.mInitLeft = left;
		this.mInitTop = top;
	}
	
	public int getInitLeftPosition(){
		return this.mInitLeft;
	}
	
	public int getInitTopPosition(){
		return this.mInitTop;
	}
	
	public void setCorrectRect(Rect rect){
		this.mCorrectRect = rect;
	}
	
	public int getCorrectLeft(){
		return this.mCorrectRect.left;
	}
	
	public int getCorrectTop(){
		return this.mCorrectRect.top;
	}	
	
	public void setIsDone(){
		this.mDone = true;
	}
	
	public boolean isDone(){
		return this.mDone;
	}
	
	public boolean isApproxmatelyCorrect(int left, int top){
		int h = this.mCorrectRect.bottom-this.mCorrectRect.top;
		int w = this.mCorrectRect.right-this.mCorrectRect.left;
		int topAccurate = (int) ((h-Math.abs(getCorrectTop()-top))/(float)h*100);
		int leftAccurate = (int) ((w-Math.abs(getCorrectLeft()-left))/(float)w*100);
		
		return 
				topAccurate>=mCorrectRate && leftAccurate>=mCorrectRate;
	}
}

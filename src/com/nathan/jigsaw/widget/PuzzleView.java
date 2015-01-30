package com.nathan.jigsaw.widget;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nathan.jigsaw.common.Utils;

public class PuzzleView extends ViewGroup {

	private ArrayList<PieceView> mPieces = new ArrayList<PieceView>();
	private ViewDragHelper mDragHelper;	
	private int mCols, mRows;
	private final int mJumbledPiecesMargin = 10; // dp
	private final int mBackgroundAlpha = 80;
	private Rect mDraggingArea;
	
	private Paint mPaintBg;
	private int mBackgroundRes;
	private Bitmap mBitmapBg;
	private boolean mIsCompeleted = false;
	private boolean mIsInitalized = false;
	
	OnCompletedListener mListener;
	
	public interface OnCompletedListener{
		void onComplete();
	}
	
	public PuzzleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
		this.setWillNotDraw(false);
	}
	
	public PuzzleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void addPiece(PieceView piece){
		mPieces.add(piece);
		this.addView(piece);
	}
	
	public void setMatrix(int cols, int rows){
		this.mCols = cols;
		this.mRows = rows;
	}
	
	public void setUnderneathImage(int resId){
		mBackgroundRes = resId;
	}
	
	public void setOnCompletedListener(OnCompletedListener listener){
		this.mListener = listener;
	}
	
	public void init(){
		mPaintBg = new Paint();
		mPaintBg.setStyle(Style.FILL);
		mPaintBg.setAntiAlias(true);
		mPaintBg.setAlpha(mBackgroundAlpha);
	}
	
	@Override
	protected void onFinishInflate() {
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
	    if(mDragHelper.continueSettling(true)) {
	        ViewCompat.postInvalidateOnAnimation(this);
	    }
	}	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean shouldInterceptTouchEvent = mDragHelper.shouldInterceptTouchEvent(ev);
	    return shouldInterceptTouchEvent;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    mDragHelper.processTouchEvent(event);
	    return true;
	}	


	private class DragHelperCallback extends ViewDragHelper.Callback{

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			PieceView piece = (PieceView) changedView;
			if(piece.isApproxmatelyCorrect(piece.getLeft(), piece.getTop())){
				mDragHelper.smoothSlideViewTo(piece, piece.getCorrectLeft(), piece.getCorrectTop());
				ViewCompat.postInvalidateOnAnimation(PuzzleView.this);
				piece.setIsDone();
				checkComplete();
			}
		}
		
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			PieceView piece = (PieceView) releasedChild;

			if(!piece.isApproxmatelyCorrect(piece.getLeft(), piece.getTop())){
				mDragHelper.settleCapturedViewAt(piece.getInitLeftPosition(), piece.getInitTopPosition());
		        invalidate();
			}
		}

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			boolean isNotDone = !((PieceView) arg0).isDone();
			if(isNotDone){
				arg0.bringToFront();
			}
			return isNotDone;
		}
		
		@Override
		public int getViewVerticalDragRange(View child) {
			return PuzzleView.this.getMeasuredHeight()-child.getMeasuredHeight();
		}
		
		@Override
		public int getViewHorizontalDragRange(View child) {
			return PuzzleView.this.getMeasuredWidth()-child.getMeasuredWidth();
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			return left;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			return top;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    measureChildren(widthMeasureSpec, heightMeasureSpec);

	    int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

	    setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
	            resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
	}	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(this.mBackgroundRes>0){
			canvas.drawBitmap(
					mBitmapBg, 
					null, mDraggingArea, mPaintBg);
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(!mIsInitalized){
			if(this.getMeasuredWidth()>this.getMeasuredHeight()){
				mDraggingArea = new Rect(this.getMeasuredWidth()-this.getMeasuredHeight(), 0, this.getMeasuredWidth(), this.getMeasuredHeight());
			}else{
				mDraggingArea = new Rect(this.getMeasuredHeight()-this.getMeasuredWidth(), 0, this.getMeasuredHeight(), this.getMeasuredWidth());
			}
			
			int gridSize = (mDraggingArea.right-mDraggingArea.left)/mRows;  // assume square
			setGrid(gridSize);
			setJumbledPiecesLayout(gridSize);
			
			mBitmapBg = Utils.decodeSampledBitmapFromResource(getResources(), this.mBackgroundRes, mDraggingArea.width(), mDraggingArea.height());
			
			invalidate();
			
			mIsInitalized = true;
		}
	}
	
	private void setJumbledPiecesLayout(int gridSize){
		int margin = Utils.getPixelSize(getContext(), mJumbledPiecesMargin);
		
		Random random = new Random();
		
		for(int i=0;i<mPieces.size();i++){
			int topPos = random.nextInt(this.getHeight()/2 + margin);
			int leftPos = random.nextInt(Utils.getPixelSize(getContext(), 20));  // temporary
			mPieces.get(i).layout(
						leftPos,
			            topPos,
			            gridSize + leftPos,
			            gridSize + topPos);
			
			mPieces.get(i).setInitPosition(mPieces.get(i).getLeft(), mPieces.get(i).getTop());
		}		
	}
	
	private void setGrid(int gridSize){
		int seq = 0;
		for(int j=0;j<mCols;j++){
			for(int i=0;i<mRows;i++){
				int left = i*gridSize + mDraggingArea.left;
				int right = left + gridSize;
				
				int top = j*gridSize + mDraggingArea.top;
				int bottom = top + gridSize;
				
				Rect rect = new Rect(left, top, right, bottom);
				mPieces.get(seq).setCorrectRect(rect);
				seq++;
			}
		}		
	}
	
	private void checkComplete(){
		for(int i=0;i<mPieces.size();i++){
			if(!mPieces.get(i).isDone()) return;
		}
		
		if(!mIsCompeleted){
			mIsCompeleted = true;
			if(mListener!=null) mListener.onComplete();
		}
	}
}

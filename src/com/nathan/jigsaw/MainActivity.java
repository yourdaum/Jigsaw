package com.nathan.jigsaw;

import android.app.Activity;
import android.os.Bundle;

import com.ageoflearning.nathan.puzzle.R;
import com.nathan.jigsaw.common.Utils;
import com.nathan.jigsaw.widget.PieceView;
import com.nathan.jigsaw.widget.PuzzleView;
import com.nathan.jigsaw.widget.PuzzleView.OnCompletedListener;

public class MainActivity extends Activity {
	
	PuzzleView mPuzzleView;
	final int mRows = 4;  // temporary
	final int mCols = 4;  // temporary

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mPuzzleView = (PuzzleView) findViewById(R.id.puzzleView);
		initSetup();
	}
	
	private void initSetup(){
		mPuzzleView.setMatrix(mRows, mCols);
		mPuzzleView.setUnderneathImage(R.drawable.background_puzzle);
		mPuzzleView.init();
		
		for(int i=0;i<(mRows*mCols);i++){
			PieceView piece = new PieceView(this);
			
			String resName = "0"+ (i+1);
			int resId = getResources().getIdentifier("slice"+ resName.substring(resName.length()-2), "drawable", getPackageName());
			
			piece.setImageResource(resId);
			mPuzzleView.addPiece(piece);
		}
		
		mPuzzleView.setOnCompletedListener(new OnCompletedListener() {
			@Override
			public void onComplete() {
				Utils.showAlert(MainActivity.this, 
						getString(R.string.msg_title_completed), 
						getString(R.string.msg_completed), 
						getString(android.R.string.ok));
			}
		});
	}
}

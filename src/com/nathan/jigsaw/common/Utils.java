package com.nathan.jigsaw.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils {
	
	public static void showAlert(Context context, String title, String msg, String button){
 		AlertDialog.Builder builder = new AlertDialog.Builder(context);
 		builder
 			.setTitle(title)
 			.setMessage(msg)       
 			.setCancelable(false)       
 			.setPositiveButton(button, new DialogInterface.OnClickListener() {           
 				public void onClick(DialogInterface dialog, int id) {
 					dialog.cancel();
 				}      
 			}).show();
	}	
	
	public static int getPixelSize(Context context, int dip){
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);		
	}
	
	
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}	
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}		
}

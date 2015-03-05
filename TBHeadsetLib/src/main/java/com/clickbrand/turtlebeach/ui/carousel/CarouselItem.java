package com.clickbrand.turtlebeach.ui.carousel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickbrand.turtlebeach.R;

public class CarouselItem extends FrameLayout 
	implements Comparable<CarouselItem> {
	private static final String TAG = "CarouselItem";
	
	public boolean placeholder = false;
	public boolean first = false;
	public boolean last = false;
	
	ImageView mImage;
	public int index;
	public float currentAngle;
	public float itemX;
	public float itemY;
	public float itemZ;
	boolean drawn;	
	
	// It's needed to find screen coordinates
	Matrix mCIMatrix;
	
	public CarouselItem(Context context) {
		super(context);
		View mRoot = LayoutInflater.from(context).inflate(R.layout.carousel_item, this, true);
		mImage = (ImageView)mRoot.findViewById(R.id.content);
	}	
	
	public int compareTo(CarouselItem another) {
		return (int)(another.itemZ - this.itemZ);
	}

	public void setItemZ(float z) {
		if(z == 0 && placeholder) {
			Log.d(TAG, "z is zero for place holder");
		}
		this.itemZ = z;
	}
	
	public void setImageBitmap(Bitmap bitmap){
		Log.d(TAG, "setting image");
		mImage.setImageBitmap(bitmap);
		
	}
	
	public void setText(String txt){
		((TextView)findViewById(R.id.text)).setText(txt);
	}
	
	public static void copy(CarouselItem item, CarouselItem itemCopy) {
		itemCopy.mImage = item.mImage;
		((TextView)itemCopy.findViewById(R.id.text)).setText(
				((TextView)item.findViewById(R.id.text)).getText());
		itemCopy.setTag(item.getTag());
	}
}

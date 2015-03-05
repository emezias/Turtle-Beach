package com.turtlebeach.gstop.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clickbrand.turtlebeach.model.Headset;
import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/**
 * This activity will show the Special Edition headset catalog
 * It is overly simple because there is only one special edition in this app
 * There might be a problem if Special Edition headsets also appear in the headset catalog 
 * So far, the special edition headsets do not have both a special edition and catalog button image
 */
public class SpecialEdition extends NWBaseActivity{
	//private static final String TAG = "SpecialEdition";
	
	private static final int[][] sTopHeadsetIds = new int[][]
		{
			{
				HeadsetManager.HEADSET_ID_COD_PRESTIGE,
				HeadsetManager.HEADSET_ID_SENTINELTASKFORCEX1,
				HeadsetManager.HEADSET_ID_SENTINELTASKFORCEPS4
			}
		};
	
	private static final int[][] sBottomHeadsetIds = new int[][]
		{
			{HeadsetManager.HEADSET_ID_STARWARS},
			{HeadsetManager.HEADSET_ID_HOTS_HEROESOFTHESTORM},
			{HeadsetManager.HEADSET_ID_MARVEL_DISNEYSUPERHEROES},
			{HeadsetManager.HEADSET_ID_TITANFALL_ATLAS}
		};
	
	private static final int[] sGroupBackgrounds = new int[] {
		R.drawable.bg_specialedition_cod,
		R.drawable.bg_specialedition_starwars,
		R.drawable.bg_specialedition_heroesofthestorm,
		R.drawable.bg_specialedition_disney,
		R.drawable.bg_specialedition_titanfall
	};
	
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_edition_activity);
        
        sScreenName = "SpecialEdition";
        		
        //Load headsets.
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout topRow = (LinearLayout) findViewById(R.id.special_headset_top_row);
        int groupCount = 0;
        for(int i=0; i<sTopHeadsetIds.length; i++) {
        	int[] group = sTopHeadsetIds[i];
        	boolean first = true;
        	RelativeLayout groupContainer = (RelativeLayout) inflater.inflate(R.layout.special_headset_container, topRow, false);
        	LinearLayout container = (LinearLayout) groupContainer.findViewById(R.id.container);
        	TextView title = (TextView) groupContainer.findViewById(R.id.container_title);
    		if(first) {
    			String s = HeadsetManager.sSpecialEditionGroupTitles[groupCount];
    			if(s.endsWith("\u00AE")) {
    				TextView copyright = (TextView) groupContainer.findViewById(R.id.container_title_copyright);
    				copyright.setVisibility(View.VISIBLE);
    				copyright.setText("\u00AE");
    				s = s.substring(0, s.length()-1);
    				title.setPadding(0, 0, 0, 0);
    			}
    			title.setText(s);
    			first = false;
    		}
    		else {
    			title.setText(" ");
    		}
        	for(int j=0; j<group.length; j++) {
        		int id = group[j];
        		Headset headset = HeadsetManager.findHeadsetById(id, this);
        		ImageView headsetView = (ImageView) inflater.inflate(R.layout.special_headset, container, false);
        		headsetView.setImageDrawable(headset.catalogThumb);
        		headsetView.setTag(R.layout.catalog_slide_content, id);
                headsetView.setTag(R.layout.catalog_slide_panel, group);
                headsetView.setTag(R.layout.slider_fragment, sGroupBackgrounds[groupCount]);
                if(group.length > 1 && j != 1) {
                	LayoutParams lp = (LayoutParams) headsetView.getLayoutParams();
                	lp.rightMargin = (int)(getResources().getDisplayMetrics().density * 80f);
                }
        		container.addView(headsetView);
        	}
        	if(group.length > 1) {
        		//Left justify the container title.
        		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) title.getLayoutParams();
        		lp.addRule(RelativeLayout.CENTER_HORIZONTAL,0);
        		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        		lp.leftMargin = (int)(getResources().getDisplayMetrics().density * 5f);
        		lp.rightMargin = 0;
        	}
        	topRow.addView(groupContainer);
        	groupCount++;
        }
        LinearLayout bottomRow = (LinearLayout) findViewById(R.id.special_headset_bottom_row);
        for(int i=0; i<sBottomHeadsetIds.length; i++) {
        	int[] group = sBottomHeadsetIds[i];
        	boolean first = true;
        	RelativeLayout groupContainer = (RelativeLayout) inflater.inflate(R.layout.special_headset_container, bottomRow, false);
        	LinearLayout container = (LinearLayout) groupContainer.findViewById(R.id.container);
        	TextView title = (TextView) groupContainer.findViewById(R.id.container_title);
    		if(first) {
    			String s = HeadsetManager.sSpecialEditionGroupTitles[groupCount];
    			if(s.endsWith("\u00AE")) {
    				TextView copyright = (TextView) groupContainer.findViewById(R.id.container_title_copyright);
    				copyright.setVisibility(View.VISIBLE);
    				copyright.setText("\u00AE");
    				s = s.substring(0, s.length()-1);
    				title.setPadding(0, 0, 0, 0);
    			}
    			title.setText(s);
    			first = false;
    		}
    		else {
    			title.setText(" ");
    		}
        	for(int id : group) {
        		Headset headset = HeadsetManager.findHeadsetById(id, this);
        		ImageView headsetView = (ImageView) inflater.inflate(R.layout.special_headset, container, false);
        		headsetView.setImageDrawable(headset.catalogThumb);
        		headsetView.setTag(R.layout.catalog_slide_content, id);
                headsetView.setTag(R.layout.catalog_slide_panel, group);
                headsetView.setTag(R.layout.slider_fragment, sGroupBackgrounds[groupCount]);
        		container.addView(headsetView);
        	}
        	if(group.length > 1) {
        		//Left justify the container title.
        		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) title.getLayoutParams();
        		lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        		lp.leftMargin = (int)(getResources().getDisplayMetrics().density * 5f);
        	}
        	bottomRow.addView(groupContainer);
        	groupCount++;
        }
    }

    /**
     * This is a click listener set in xml, only one headset/button
     * @param headsetImgBtn
     */
    public void showSlider(View view) {
    	//This is where we open up the slider panel
    	final Intent tnt = new Intent(this, SpecialSlider.class);
		tnt.putExtra(SpecialSlider.TAG, (Integer) view.getTag(R.layout.catalog_slide_content));
		tnt.putExtra(SpecialSlider.GROUP, (int[]) view.getTag(R.layout.catalog_slide_panel));
		tnt.putExtra(SpecialSlider.BACKGROUND, (Integer) view.getTag(R.layout.slider_fragment));
		startActivityForResult(tnt, 99);
    }
    
    public void mMenu(View btn) {
    	//set in XML
        finish();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This is to support the back button alongside the menu button
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			finish();
			//return to the main menu from the slide panel
		}
	}
    
    
  
}
    
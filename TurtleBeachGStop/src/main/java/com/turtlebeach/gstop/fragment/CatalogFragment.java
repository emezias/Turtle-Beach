package com.turtlebeach.gstop.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.clickbrand.turtlebeach.model.Headset;
import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.clickbrand.turtlebeach.ui.TrueTypeTextView;
import com.turtlebeach.gstop.R;

public class CatalogFragment extends Fragment {
	private static final String TAG = "CatalogFragment";
	/**
	 * This class is inflated and attached/detached to the container view group in the CatalogSliderPanel
	 * Fragments are added, detached and attached
	 * It is simply a view in the activity, the FragmentManager is caching these views to make it fast
	 */

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.catalog_slide_content, container, false);
        final int headsetID = (Integer) getActivity().findViewById(R.id.catMenu).getTag();
        Log.d(TAG, "onCreateView, headset id is: " + headsetID);
        final Headset h = HeadsetManager.findHeadsetById(headsetID, getActivity());
        //this text view will be a scratch to set the bullets as well
        TrueTypeTextView tv = (TrueTypeTextView) fragmentView.findViewById(R.id.catDescription);
        tv.setFont("dharma.otf");
        tv.setText(h.sliderText);
        //need to set the designed for text every time the fragment is displayed - using a tag to hold the string
        tv.setTag(h.sliderDesign);
        //this tag tells the learn more button to open the right headset
        fragmentView.findViewById(R.id.catLearn).setTag(headsetID);
        
        ((ImageView)fragmentView.findViewById(R.id.catLogo)).setImageBitmap(HeadsetManager.getSlidePanel(headsetID, true));
        ((ImageView)fragmentView.findViewById(R.id.catHeadset)).setImageBitmap(HeadsetManager.getSlidePanel(headsetID, false));
        
        
        final String[] bullets = getActivity().getResources().getStringArray(h.getArrayResource(false));
        final LinearLayout bull = (LinearLayout) fragmentView.findViewById(R.id.catBullets);
        
        for(String s: bullets) {
        	//inflate a text view for each bullet and add it to the linear layout in the xml
        	tv = (TrueTypeTextView) inflater.inflate(R.layout.bullet_text, null);
        	tv.setFont("gothamMedium.otf");
        	tv.setText(s);
        	
        	bull.addView(tv);
        }
        
        //The Headset class is tracking a few items...
        if(Headset.sSliderLogos[h.deviceID].length > 0) {
        	LinearLayout icons = (LinearLayout) inflater.inflate(R.layout.slider_icons, null);
        	
        	for(int drawableId : Headset.sSliderLogos[h.deviceID]) {
        		if(drawableId > 0) {
        			ImageView icon = (ImageView) inflater.inflate(R.layout.slider_icons_item, icons, false);
            		icon.setImageResource(drawableId);
            		icons.addView(icon);
        		}
        		
        	} //end for loop, all icons added to linear layout
        	bull.addView(icons);
        }
        
        ((TrueTypeTextView)getActivity().findViewById(R.id.catDesignText)).setText(h.sliderDesign);
        //Later on, this text field is getting set in the Activity from the tag on catDescription
        return fragmentView;
    }
}

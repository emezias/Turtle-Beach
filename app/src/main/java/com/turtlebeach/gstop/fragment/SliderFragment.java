package com.turtlebeach.gstop.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.model.Headset;
import com.turtlebeach.gstop.headsets.model.HeadsetManager;
import com.turtlebeach.gstop.headsets.ui.TrueTypeTextView;

public class SliderFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View fragmentView = inflater.inflate(R.layout.slider_fragment, container, false);
        final int headsetID = (Integer) getActivity().findViewById(R.id.rootView).getTag();
        final Headset h = HeadsetManager.findHeadsetById(headsetID, getActivity());
        final TrueTypeTextView tv = (TrueTypeTextView) fragmentView.findViewById(R.id.slideDescription);
        tv.setFont("dharma.otf");
        tv.setText(h.sliderText);
        fragmentView.findViewById(R.id.slideLearn).setTag(headsetID);
        ((ImageView)fragmentView.findViewById(R.id.slideLogo)).setImageBitmap(HeadsetManager.getSlidePanel(h.deviceID, true));
        ((ImageView)fragmentView.findViewById(R.id.slideHeadset)).setImageBitmap(HeadsetManager.getSlidePanel(h.deviceID, false));
        ((TextView)fragmentView.findViewById(R.id.slideDesignText)).setText(h.sliderDesign);
        String[] bullets = getActivity().getResources().getStringArray(h.getArrayResource(false));
        LinearLayout bull = (LinearLayout) fragmentView.findViewById(R.id.slideBullets);
        TrueTypeTextView bulletTV;
        for(String s: bullets) {
        	bulletTV = (TrueTypeTextView) inflater.inflate(R.layout.bullet_text, null);
        	bulletTV.setFont("gothamMedium.otf");
        	bulletTV.setText(s);
        	
        	bull.addView(bulletTV);
        }
        
        //The Headset class is tracking a few items...
        if(Headset.sSliderLogos[h.deviceID].length > 0) {
        	//TODO change out the view group, no headset has more than one
        	LinearLayout icons = (LinearLayout) inflater.inflate(R.layout.slider_icons, null);
        	for(int drawableId : Headset.sSliderLogos[h.deviceID]) {
        		ImageView icon = (ImageView) inflater.inflate(R.layout.slider_icons_item, icons, false);
        		icon.setImageResource(drawableId);
        		icons.addView(icon);
        	}
        	bull.addView(icons);
        }
        
        return fragmentView;
    }

}

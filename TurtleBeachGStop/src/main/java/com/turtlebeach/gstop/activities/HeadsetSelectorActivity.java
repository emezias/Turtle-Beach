package com.turtlebeach.gstop.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickbrand.turtlebeach.model.Headset;
import com.clickbrand.turtlebeach.model.HeadsetManager;
import com.clickbrand.turtlebeach.model.HeadsetManager.Trait;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;

/*
 * This is just a place holder so the main menu can be completed...
 */
public class HeadsetSelectorActivity extends NWBaseActivity {
	
	/**
	 * Length of the first dimension should match that of sProgressImages. 
	 * Each entry in the first dimension represents a set of possible answers 
	 * to one question.
	 */
	private static final Trait[][] sQuestionTraits = new Trait[][]{
		{
			Trait.XBOX_360, Trait.PS3, Trait.XBOX_ONE,
			Trait.PS4, Trait.MULTI_PLATFORM, Trait.PC
		},
		{
			Trait.WIRED, Trait.WIRELESS
		},
		{
			Trait.SURROUND_SOUND, Trait.STEREO_SOUND
		}
	};
	
	/**
	 * Length of this should match the length of the first dimension of sQuestionTraits.
	 */
	private static final int[] sProgressImages = new int[] {
		R.drawable.img_selector_progressbar_step1,
		R.drawable.img_selector_progressbar_step2,
		R.drawable.img_selector_progressbar_step3
	};
	
	private String[] mQuestions;
	private int mIndex;
	private List<Trait> mTraitFilters;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.headset_selector);
		
		mTraitFilters = new ArrayList<Trait>();
		mIndex = 0;
		
		mQuestions = getResources().getStringArray(R.array.hs_question);
		TextView questionView = (TextView) findViewById(R.id.hs_question);
		questionView.setText(mQuestions[mIndex]);
		
		List<String> gridData = new ArrayList<String>();
		for(Trait trait : sQuestionTraits[mIndex]) {
			//get the trait index to find the String
			gridData.add(getResources().getString(HeadsetManager.sTrtLabels[trait.ordinal()]));
		}
		GridView gridView = (GridView) findViewById(R.id.hs_answers_container);
		if(gridData.size() % 3 != 0) {
			gridView.setNumColumns(2);
		}
		else {
			gridView.setNumColumns(3);
		}
		gridView.setAdapter(new ArrayAdapter<String>(this, R.layout.headset_selector_answer, gridData));
		
		sScreenName = "HeadsetSelector";
	}
	
	public void gotoMainMenu(View view) {
		finish();
	}
	
	public void answerQuestion(View view) {
		if(mIndex >= sQuestionTraits.length) {
			return;
		}
		//Which trait did the user just pick?  Add to trait filters.
		String text = ((TextView)view).getText().toString();
		for(Trait trait : sQuestionTraits[mIndex]) {
			if(getResources().getString(HeadsetManager.sTrtLabels[trait.ordinal()]).equals(text)) {
				mTraitFilters.add(trait);
			}
		}
		
		mIndex++;
		
		//If there are more questions and more headsets available with in the the current set
		//that have one of the traits that the next question's answers would also have,
		//change the question, the answers, and the progress indicator image.
		List<Headset> filteredHeadsets = HeadsetManager.getFilteredHeadsets(mTraitFilters);
		if(mIndex < sQuestionTraits.length && mIndex < mQuestions.length
				&& filteredHeadsets.size() > 0) {
			//If there are more filters, but the next question won't filter them further, skip to the next question.
			boolean hasTrait = true;
			for(Trait trait : sQuestionTraits[mIndex]) {
				boolean found = false;
				for(Headset headset : filteredHeadsets) {
					if(headset != null && headset.traits != null) {
						for(Trait headsetTrait : headset.traits) {
							if(headsetTrait == trait) {
								found = true;
								break;
							}
						}
					}
				}
				hasTrait = found && hasTrait;
				if(!hasTrait) {
					answerQuestion(view);
					return;
				}
			}
			//Display back button if we were on the first question and are not at the results
			if(mIndex > 0) {
				findViewById(R.id.hs_back).setVisibility(View.VISIBLE);
			}
			TextView questionView = (TextView) findViewById(R.id.hs_question);
			questionView.setText(mQuestions[mIndex]);
			
			List<String> gridData = new ArrayList<String>();
			for(Trait trait : sQuestionTraits[mIndex]) {
				gridData.add(getResources().getString(HeadsetManager.sTrtLabels[trait.ordinal()]));
			}
			GridView gridView = (GridView) findViewById(R.id.hs_answers_container);
			if(gridData.size() % 3 != 0) {
				gridView.setNumColumns(2);
			}
			else {
				gridView.setNumColumns(3);
			}
			gridView.setAdapter(new ArrayAdapter<String>(this, R.layout.headset_selector_answer, gridData));
			
			ImageView progress = (ImageView) findViewById(R.id.hs_progress);
			progress.setImageResource(sProgressImages[mIndex]);
		}
		else {
			//Otherwise display the results.
			findViewById(R.id.hs_progress).setVisibility(View.GONE);
			findViewById(R.id.hs_band).setVisibility(View.GONE);
			findViewById(R.id.hs_silhouette).setVisibility(View.GONE);
			findViewById(R.id.hc_sub_title).setVisibility(View.VISIBLE);
			findViewById(R.id.back).setVisibility(View.VISIBLE);
			findViewById(R.id.hs_results_container).setVisibility(View.VISIBLE);
			
			LinearLayout resultsContainer = (LinearLayout) findViewById(R.id.hs_results_container);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v1 = null;
			View v2 = null;
			View v3 = null;
			View v4 = null;
			switch(filteredHeadsets.size()) {
			case 1:
				v1 = inflater.inflate(R.layout.headset_selector_one_result, resultsContainer, false);
				bindView(filteredHeadsets.get(0), v1);
				resultsContainer.addView(v1);
				break;
			case 2:
				v1 = inflater.inflate(R.layout.headset_selector_two_result, resultsContainer, false);
				bindView(filteredHeadsets.get(0), v1);
				resultsContainer.addView(v1);
				v2 = inflater.inflate(R.layout.headset_selector_two_result, resultsContainer, false);
				bindView(filteredHeadsets.get(1), v2);
				resultsContainer.addView(v2);
				break;
			case 3:
				v1 = inflater.inflate(R.layout.headset_selector_three_result, resultsContainer, false);
				bindView(filteredHeadsets.get(0), v1);
				resultsContainer.addView(v1);
				v2 = inflater.inflate(R.layout.headset_selector_three_result, resultsContainer, false);
				bindView(filteredHeadsets.get(1), v2);
				resultsContainer.addView(v2);
				v3 = inflater.inflate(R.layout.headset_selector_three_result, resultsContainer, false);
				bindView(filteredHeadsets.get(2), v3);
				resultsContainer.addView(v3);
				break;
			case 4:
				v1 = inflater.inflate(R.layout.headset_selector_four_result, resultsContainer, false);
				bindView(filteredHeadsets.get(0), v1);
				resultsContainer.addView(v1);
				v2 = inflater.inflate(R.layout.headset_selector_four_result, resultsContainer, false);
				bindView(filteredHeadsets.get(1), v2);
				resultsContainer.addView(v2);
				v3 = inflater.inflate(R.layout.headset_selector_four_result, resultsContainer, false);
				bindView(filteredHeadsets.get(2), v3);
				resultsContainer.addView(v3);
				v4 = inflater.inflate(R.layout.headset_selector_four_result, resultsContainer, false);
				bindView(filteredHeadsets.get(3), v4);
				resultsContainer.addView(v4);
				break;
			}
		}
	}
	
	private void bindView(Headset headset, View view) {
		((ImageView)view.findViewById(R.id.logo)).setImageBitmap(HeadsetManager.getSelectorLogo(headset.deviceID));
        ((ImageView)view.findViewById(R.id.headset)).setImageBitmap(HeadsetManager.getSlidePanel(headset.deviceID, false));
        
        view.findViewById(R.id.logo).setTag(headset.deviceID);
        view.findViewById(R.id.headset).setTag(headset.deviceID);
        view.findViewById(R.id.learn).setTag(headset.deviceID);
	}
	
	public void back(View view) {
		if(mTraitFilters.size() == 0 || mIndex == 0) {
			//We can't go back.
			findViewById(R.id.hs_back).setVisibility(View.GONE);
			return;
		}
		mTraitFilters.remove(mTraitFilters.size()-1);
		
		if(findViewById(R.id.hs_progress).getVisibility() == View.GONE) {
			findViewById(R.id.hs_progress).setVisibility(View.VISIBLE);
			findViewById(R.id.hs_band).setVisibility(View.VISIBLE);
			findViewById(R.id.hs_silhouette).setVisibility(View.VISIBLE);
			findViewById(R.id.hc_sub_title).setVisibility(View.GONE);
			findViewById(R.id.back).setVisibility(View.GONE);
			LinearLayout resultsContainer = (LinearLayout) findViewById(R.id.hs_results_container);
			resultsContainer.setVisibility(View.GONE);
			resultsContainer.removeAllViews();
		}
		
		while(mIndex != mTraitFilters.size()) {
			mIndex--;
		}
		if(mTraitFilters.size() == 0) {
			mIndex = 0;
		}
		if(mIndex == 0) {
			findViewById(R.id.hs_back).setVisibility(View.GONE);
			findViewById(R.id.back).setVisibility(View.GONE);
		}
				
		//If there are more questions and more headsets available with in the the current set
		//that have one of the traits that the next question's answers would also have,
		//change the question, the answers, and the progress indicator image.
		List<Headset> filteredHeadsets = HeadsetManager.getFilteredHeadsets(mTraitFilters);
		//TODO change from a collection to an array
		//If there are more filters, but the next question won't filter them further, skip to the next question.
		boolean hasTrait = true;
		for(Trait trait : sQuestionTraits[mIndex]) {
			boolean found = false;
			for(Headset headset : filteredHeadsets) {
				if(headset != null && headset.traits != null) {
					for(Trait headsetTrait : headset.traits) {
						if(headsetTrait == trait) {
							found = true;
							break;
						}
					}
				}
			}
			hasTrait = found && hasTrait;
			if(!hasTrait) {
				answerQuestion(view);
				return;
			}
		}
		//Display back button if we were on the first question and are not at the results
		if(mIndex > 0) {
			findViewById(R.id.hs_back).setVisibility(View.VISIBLE);
		}
		TextView questionView = (TextView) findViewById(R.id.hs_question);
		questionView.setText(mQuestions[mIndex]);
		
		List<String> gridData = new ArrayList<String>();
		for(Trait trait : sQuestionTraits[mIndex]) {
			gridData.add(getResources().getString(HeadsetManager.sTrtLabels[trait.ordinal()]));
		}
		GridView gridView = (GridView) findViewById(R.id.hs_answers_container);
		if(gridData.size() % 3 != 0) {
			gridView.setNumColumns(2);
		}
		else {
			gridView.setNumColumns(3);
		}
		gridView.setAdapter(new ArrayAdapter<String>(this, R.layout.headset_selector_answer, gridData));
			
		ImageView progress = (ImageView) findViewById(R.id.hs_progress);
		progress.setImageResource(sProgressImages[mIndex]);
	}

	public void learnMore(View view) {
		Integer id = (Integer) view.getTag();
		final Intent tnt = new Intent(this, LearnMore.class);
		//tnt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); - nothing to clear, app does not maintain back stack
		tnt.putExtra(LearnMore.TAG, id);
		startActivity(tnt);
	}
}

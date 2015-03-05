package com.turtlebeach.gstop.activities;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.clickbrand.turtlebeach.Constants;
import com.clickbrand.turtlebeach.model.VideoManager;
import com.clickbrand.turtlebeach.ui.carousel.Carousel;
import com.clickbrand.turtlebeach.ui.carousel.CarouselAdapter;
import com.clickbrand.turtlebeach.ui.carousel.CarouselAdapter.OnItemClickListener;
import com.clickbrand.turtlebeach.ui.carousel.CarouselItem;
import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.internet.NWBaseActivity;
/**
 * This is where featured headset, game trailer, sound experiences and turtle beach promo videos play
 * The activity is using tags on the video view, seekbar and mControls view group to track state
 */
public class VideoPlaybackActivity extends NWBaseActivity implements AnimationListener, OnSeekBarChangeListener { 
	private static final String TAG = "VideoPlaybackActivity";
	
	/**
	 * Extras = Video file name, title and demo specific boolean trigger
	 * Also, the type of list to display, populate the carousel as needed, essentially the from screen
	 * List type is set with sound experiences and game trailer playback
	 */
	public static final String EXTRA_VIDEO_FILE = "videoFileName";
	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_DEMO_FLAG = "demo";
	public static final String EXTRA_LIST_TYPE = "listType";
	public static final String EXTRA_SCREEN_TAG = "screenTag";
	/**
	 * Time in milliseconds to wait before hiding the controls. Currently 5 seconds.
	 */
	private static final long HIDE_CONTROLS_DELAY = 5000; 
	/**
	 * Used to hide controls after 5 seconds of activity.
	 */
	private Timer mTimer;
	
	/**
	 * Onscreen controls, views
	 */
	RelativeLayout mTitleBar, mControls;
	//private ImageButton mVideoControlBtn; //pause/play
	private VideoView mVideoView; //viewer
	private TextView mPlaybackTimeProgress;
	private SeekBar mSeekBar;
	//The Carousel showing the list of videos upon completion
	private Carousel mCarousel;
	//Carousel position
	private int mSelected;
	//videos displayed in carousel, sent to carousel adapter
	private String[] mFiles;
	/**
	 * Async task to show timer in the video control bar
	 */
	private VideoProgressUpdater mVideoProgressUpdater;
	//the video that is playing, the current mSelected file from the Carousel
	private String mCurrentItem; 

	/**
	 * Animations to move the top and bottom bars onto and off the screen.
	 */
	private static Animation mSlideOutFromBottomAnim, mSlideInFromBottomAnim, 
		mSlideOutFromTopAnim, mSlideInFromTopAnim, mFadeOut;
	
	//scratch pad for the video duration textview
	final StringBuilder sSB = new StringBuilder();
	static int sVolumeSaver = -1;
	public static boolean sMuted = false;
	//This value is hold for only one run, really, at app launch
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_playback);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		final Bundle extras = getIntent().getExtras();
		if(extras == null) { 
			Log.e(TAG, "Video playback expects extras");
			//TODO error toast
			finish();
		}
		
		// If set, set the screen name for localytics
		if (extras.containsKey(EXTRA_SCREEN_TAG)) {
			sScreenName = extras.getString(EXTRA_SCREEN_TAG);
		}
		else {
			//Do not report this screen.
			sScreenName = null;
		}
		//init views for title and playback control bar
		//This activity always begins by playing a video
		mTitleBar = (RelativeLayout) findViewById(R.id.video_titleRL);
		mControls = (RelativeLayout) findViewById(R.id.video_controlRL);
		mCarousel = (Carousel) findViewById(R.id.videoCarousel);
		mCarousel.setGravity(Gravity.CENTER);
		mPlaybackTimeProgress = (TextView) findViewById(R.id.videoTime);
		mVideoView = (VideoView) findViewById(R.id.videoView);
		setupVideoView();
		mVideoView.setTag(true);
		//This tag shows onResume we came from onCreate, not returning from a dialog or Carousel
		mSeekBar = (SeekBar) findViewById(R.id.video_volControl);
		
		//Initialize animations used on the title and control bar
		mFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		mFadeOut.setAnimationListener(this);
		mSlideOutFromBottomAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_bottom);
		mSlideOutFromBottomAnim.setAnimationListener(this);
		mSlideOutFromTopAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_from_top);
		mSlideOutFromTopAnim.setAnimationListener(this);
		//Slide in animations do not need any listeners
		mSlideInFromTopAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top); 
		mSlideInFromBottomAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom);
		
		if(extras.containsKey(EXTRA_DEMO_FLAG)) {
			mControls.findViewById(R.id.video_moreBtn).setVisibility(View.INVISIBLE);
			//mControls.findViewById(R.id.video_backBtn).setVisibility(View.GONE);
			mCurrentItem = Constants.getDemoDir() + File.separator + "why turtle beach.mp4";
			((TextView)mTitleBar.findViewById(R.id.videoTitle)).setText(
					getString(R.string.mmenu_why));
	        //TODO what title for Why Turtle Beach video?
	        return;
		} else {
			mCurrentItem = extras.getString(EXTRA_VIDEO_FILE);
			if(mCurrentItem != null) {
				Log.i(TAG, "video file passed into playback activity: " + mCurrentItem);
				//All named files sent in should have a title except demo
				((TextView)findViewById(R.id.videoTitle)).setText(getIntent().getExtras().getString(EXTRA_TITLE));
				//Now set up the display for showing the list
				//Setup offscreen Carousel view 
				//initialize the carousel
				if(extras.containsKey(EXTRA_LIST_TYPE)) {
					final int listType = extras.getInt(EXTRA_LIST_TYPE);
					Log.i(TAG, "List type extra is set to " + listType);
					mCarousel.init(listType);
					//Get the string array of filenames for the carousel, initialize it for display when playback completes
					mFiles = VideoManager.getFileArray(this, listType);
					//set the default selection
					mSelected = mFiles.length / 2;
					//set the Carousel to the center or find the video passed in and select it
					for(int i=0; i<mFiles.length; i++) {
						if(mCurrentItem.contains(mFiles[i])) {
							mSelected = i;
							Log.i(TAG, "selected in carousel: " + mFiles[i]);
							//set the specific selection in the carousel
						}
					}					
					
					mControls.findViewById(R.id.video_moreBtn).setVisibility(View.VISIBLE);
					mCarousel.setOnItemClickListener(carouselListener);
				} else {
					//no list type, no more button, playing feature headset video
					mControls.findViewById(R.id.video_moreBtn).setVisibility(View.INVISIBLE);
				}
			} else {
				//Show error to the user and exit, no video filename sent into the activity
				//Error !extras.containsKey(EXTRA_TITLE)
				Toast.makeText(this, "Failed to start video: no file set on Intent",
	                    Toast.LENGTH_LONG).show();
				finish();
			}
		}		
		
	}

	@Override
	protected void onResume() {
		super.onResume();	
		if(getIntent().getExtras().containsKey(EXTRA_LIST_TYPE) && mVideoView.getTag() != null) {
			mCarousel.setSelection(mSelected);
		} 

		if(mVideoView.getTag() != null) {
			mVideoView.setTag(null);
			Log.i(TAG, "starting video? " + mCurrentItem);
			mVideoView.setVideoPath(mCurrentItem);
			mVideoView.setVisibility(View.VISIBLE);
			
			hideControls();
		}
		
		//setupVolumes();
		final AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		//Only the demo video sets max volume, other videos read existing setting
		mSeekBar.setMax(mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		
		//setback, can't read current stream, always returns zero
		//volVal = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		//Log.d(TAG, "getStream volVal=" + volVal);
		if(sVolumeSaver < 0) {
			sVolumeSaver = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		}
		mSeekBar.setOnSeekBarChangeListener(this);
		if(!sMuted) {
			mSeekBar.setProgress(sVolumeSaver);
			Log.d(TAG, "setting saved value");
		} else {
			((ImageButton)findViewById(R.id.video_volumeBtn))
				.setImageResource(R.drawable.btn_speaker_mute);
		}
		//Log.d(TAG, "remove onResume");
		mHandler.removeCallbacks(mFinishRunnable);
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fade_in, 0);
		if(mVideoView != null && mVideoView.isPlaying()) {
			mVideoView.pause();
			mVideoProgressUpdater.cancel(true);
			//Log.d(TAG, "remove on Pause");
			mHandler.removeCallbacks(mFinishRunnable);
		}
		if(mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}

	}
	
	/**
	 * Set the touch listener for the video view
	 * show controls if a video is playing, otherwise start the video
	 */
	private void setupVideoView() {
		mVideoView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				//start simple, hide or show controls with a touch
				if(mControls.getVisibility() != View.VISIBLE) {
					showControls();
					//set the timer on the display of the controls to animate them offscreen
					if(mVideoView.isPlaying()) {
						mHandler.removeCallbacks(mFinishRunnable);
						if(mTimer != null) {
							mTimer.cancel();
							mTimer.purge();
						}
						mTimer = new Timer();
						mTimer.schedule(new HideControlsTask(), HIDE_CONTROLS_DELAY);
					}
				} else {
					//controls are showing, hide them and exit
					hideControls();
				} 
				
				return false;
			}
		});
		
		mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				//This code executes when the video finishes playing
				Log.i(TAG, "video completed " + mCurrentItem);
				//Video stopped, let's set the right play/pause image
				((ImageButton)mControls.findViewById(R.id.videoControlBtn)).
					setImageResource(R.drawable.btn_play_small);
				//just in case controls were up
				if(mTimer != null) {
					mTimer.cancel();
					mTimer.purge();
				}

				//show video carousel		
				if(getIntent().getExtras().containsKey(EXTRA_LIST_TYPE)) {
					//This is where we show the directory in the carousel after completing, similar to "more"
					Log.i(TAG, "list found, showing carousel after playback");
					hideControls();
					mCarousel.setVisibility(View.VISIBLE);
					mCarousel.bringToFront();
					mVideoView.startAnimation(mFadeOut);
					mTitleBar.setVisibility(View.GONE);
					mControls.setVisibility(View.INVISIBLE);
					Log.i(TAG, "post carousel");
					mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
				} else {
					//this must be a featured video, return to featured screen				
					finish();
				}
				
			}
		});
		
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
				if(mControls.getTag() != null) {
					//Tag set by pause button, do not start, resume...
					Log.e(TAG, "prepared listener not starting video, previous pause");
					return;
				}
				//This line stops the demo from kicking in when another video is playing
				mHandler.removeCallbacks(mFinishRunnable);
				//Log.d(TAG, "remove callbacks, on prepared");
				mVideoView.setVisibility(View.VISIBLE);
				mVideoView.start();
				Log.i(TAG, "prepared mVideoView " + mCurrentItem + " now start updater");
				setupProgressUpdater();
				//Got to set play/pause every time!
				((ImageButton)mControls.findViewById(R.id.videoControlBtn))
					.setImageResource(R.drawable.btn_pause_small);
			}
			
		});
		
	}

	/**
	 * This is pulled from the previous releases
	 * set up an Async task to write out the video duration to a timer textview
	 */
	private void setupProgressUpdater() {
		//noinspection unchecked
		if(mVideoProgressUpdater != null && 
				mVideoProgressUpdater.getStatus().equals(AsyncTask.Status.RUNNING)) {
			mVideoProgressUpdater.cancel(true);
		}
		mVideoProgressUpdater = new VideoProgressUpdater(mVideoView.getDuration());
		//noinspection unchecked
		mVideoProgressUpdater.execute();
	}

	/**
	 * top and bottom bars on video that are shown and hidden with a timer
	 * There is an animation listener to set the controls to invisible with onscreen motion
	 */
	private void hideControls() {
		if(mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}
		if(mControls.getVisibility() == View.INVISIBLE) {
			return;
		}
		//Linear Layout holding play/pause/volume
		mControls.startAnimation(mSlideOutFromBottomAnim);
		mTitleBar.startAnimation(mSlideOutFromTopAnim);

	}

	/**
	 * Show the controls after a touch, animate them into view
	 * Set a timer to hide the controls after 5s
	 */
	private void showControls() {
		//don't show the animation if the controls are already visible
		if(mControls.getVisibility() == View.INVISIBLE) {
			mControls.startAnimation(mSlideInFromBottomAnim);
			mControls.setVisibility(View.VISIBLE);
			mTitleBar.startAnimation(mSlideInFromTopAnim);
			if(!getIntent().hasExtra(EXTRA_DEMO_FLAG)) {
				//Demo has no title
				mTitleBar.setVisibility(View.VISIBLE);
			}
		}
		
		//added new function for video carousel in completion listener
		mTimer = new Timer();
		mTimer.schedule(new HideControlsTask(), HIDE_CONTROLS_DELAY);

	}


	private void updatePlaybackTime(Integer aCurrent, Integer aDuration) {
		/* V4.1 shows a leading zero in durations, v 4.2 gives a more exact string with no padding */
		if(Build.VERSION.SDK_INT <= 16) {
			sSB.append(DateUtils.formatElapsedTime(aCurrent / 1000).substring(1));
			sSB.append(" / ");
			sSB.append(DateUtils.formatElapsedTime(aDuration / 1000).substring(1));
		} else {
			sSB.append(DateUtils.formatElapsedTime(aCurrent / 1000));
			sSB.append(" / ");
			sSB.append(DateUtils.formatElapsedTime(aDuration / 1000));
		}
		
		if (mPlaybackTimeProgress != null) {
			mPlaybackTimeProgress.setText(sSB.toString());
		}
		sSB.setLength(0); //save some GC time
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation == mSlideOutFromBottomAnim) {
			mControls.setVisibility(View.INVISIBLE);
		}
		else if(animation == mSlideOutFromTopAnim && mTitleBar.getVisibility() == View.VISIBLE) {
			mTitleBar.setVisibility(View.GONE);
		} else if(animation == mFadeOut) {
			mVideoView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) { }

	@Override
	public void onAnimationStart(Animation animation) {	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
		if(fromUser) {
			//reset the timer on a touch to these controls
			setTimeoutOnControls();
		}
		
		final ImageButton btn = (ImageButton)findViewById(R.id.video_volumeBtn);
		//using a tag to set when the volume button image needs to show mute
		if(value <= 1) {
			//essentially zero or mute with this value, thus, change display on control bar
			value = 0;
			sMuted = true;
			btn.setImageResource(R.drawable.btn_speaker_mute);
		} else {
			//save the value for the volume when it is not muted
			sVolumeSaver = value;
			sMuted = false;
			btn.setImageResource(R.drawable.btn_speaker_on);
		}
		Log.i(TAG, "progress changed, setting progress value " + value);
		//tag will track the volume
		((AudioManager) getSystemService(Context.AUDIO_SERVICE))
			.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
		btn.invalidate();
		//This is where the seekbar is actually setting the volume 
		
	}
	

	/**
	 * This is a button click listener set in the XML layout for the mute button
	 * @param button
	 */
	
	public void clickVolume(View button) {
		//This is the speaker button - set to mute or return to former saved value 
		setTimeoutOnControls();
		if(sMuted) {
			//tablet is muted, restore from sVolumeSaver 
			Log.i(TAG, "setting volume from saved value " + sVolumeSaver);
			mSeekBar.setProgress(sVolumeSaver);
		} else {
			//tablet is noisy, save the current volume then mute and set to zero
			sVolumeSaver = ((AudioManager) getSystemService(Context.AUDIO_SERVICE))
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mSeekBar.setProgress(0);
			sMuted = true;
			Log.i(TAG, "muting volume with click, volume saver = " + sVolumeSaver);
		}
		
	}
	
	/**
	 * This method is to cancel the animation and reset the timer for the controls
	 * need to be called to start the code block on every button click
	 */
	void setTimeoutOnControls() {
		if(mTimer != null) {
			mTimer.cancel();
			mTimer.purge();
		}

		mTimer = new Timer();
		mTimer.schedule(new HideControlsTask(), HIDE_CONTROLS_DELAY);
	}	

	/**
	 * This onClick method is set in xml for the black buttons
	 * @param button -> either main menu, back or more button
	 */
	public void mMenu(View button) {
		setTimeoutOnControls();
		switch(button.getId()) {
		case R.id.video_mMenu:
			//set result code that will finish calling activity and return to the main menu
			setResult(MenuActivity.MENUCODE);
	        finish();
			break;
		case R.id.video_backBtn:
			finish();
			break;
		case R.id.video_moreBtn:
			if(mVideoView.isPlaying()) {
				mVideoView.pause();
				//stopping video, let's make sure play/pause image is right
				((ImageButton)mControls.findViewById(R.id.videoControlBtn)).setImageResource(R.drawable.btn_play_small);
				mVideoView.startAnimation(mFadeOut);
				Log.i(TAG, "post more");
				mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
			}
			mCarousel.setVisibility(View.VISIBLE);
			mCarousel.bringToFront();
			break;
		}
	}
	
	/**
	 * This onClick method is set in xml
	 * it will play or pause the video and set the appropriate appearance
	 * @param button, the play/pause video control button from the bar at the bottom of the screen
	 */
	public void playOrPause(View button) {
		Log.d(TAG, "play or Pause");
		//setTimeoutOnControls();
		hideControls();
		if(mVideoView.isPlaying()) {
			//video is on, switch to play button and pause the playback
			mControls.setTag(mVideoView.getCurrentPosition());
			mVideoView.pause();
			mVideoProgressUpdater.cancel(true);
			Log.i(TAG, "post play or pause");
			mHandler.postDelayed(mFinishRunnable, mIdleAfterTime);
			((ImageButton)button).setImageResource(R.drawable.btn_play_small);
		} else {
			//start the video and switch to the pause image
			Log.i(TAG, "remove, play or pause");
			mHandler.removeCallbacks(mFinishRunnable);
			((ImageButton)button).setImageResource(R.drawable.btn_pause_small);
			if(mControls.getTag() != null) {
				//video is paused, resume
				
				final int seekto = (Integer) mControls.getTag();
				mControls.setTag(null);
				mVideoView.seekTo(seekto);
				//mVideoView.seekTo(mVideoView.getDuration() - seekto);
				setupProgressUpdater();
				mVideoView.start();
				//not sure why, but removeCallbacks has to be called twice in this block
				mHandler.removeCallbacks(mFinishRunnable);
				Log.i(TAG, "remove, play or pause");
				//Got to set play/pause every time!
				((ImageButton)mControls.findViewById(R.id.videoControlBtn)).setImageResource(R.drawable.btn_pause_small);
			} else {
				//start playing a new video
				mVideoView.setVideoPath(mCurrentItem);
				//starts playing once prepared
			}
			
		}
	}
	

	/**
	 * This listener is attached to carousel items in onCreate
	 */
	OnItemClickListener carouselListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(CarouselAdapter<?> parent, View view, int position, long id) {
			// Carousel displays the list of videos (thumbnails)
			//This is the action when a user touches/selects one
			if(mSelected != position) {
				//clear any pause...
				mControls.setTag(null);
			}
			//position = position%mFiles.length;
			CarouselItem item = mCarousel.mImages[position];
			mSelected = position;			
			mCarousel.setVisibility(View.GONE);
			if(position != mCarousel.getSelectedItemPosition()) {
				mCarousel.setSelection(position);
			}
			mVideoView.setVisibility(View.VISIBLE);
			
			mCurrentItem = (String) item.getTag();
			mVideoView.setVideoPath(mCurrentItem);
			//Log.d(TAG, "onClick from carousel "  + position + " file tag is " + mCurrentItem);

			//video will start, once prepared
			hideControls();
			//set title from tag set on the carousel item's textview object
			((TextView)mTitleBar.findViewById(R.id.videoTitle)).setText(
					(CharSequence) ((TextView)item.findViewById(R.id.text)).getTag());
		}
		
	};
	/************** 2 nested classes ***********************/
	/**
	 * Progress updater copied from earlier TB projects
	 * Async task tracks the video time and displays it in the mControls Relative layout
	 */
	private class VideoProgressUpdater extends AsyncTask<Void, Integer, Void> {
		private int mDuration = 0;
		private int mCurrent = 0;
	
		private VideoProgressUpdater(int aDuration) {
			mDuration = aDuration;
			Log.i(TAG, "mDuration " + mDuration);
			
		}
	
		@Override
		protected Void doInBackground(Void... params) {
			do {
				if(mVideoView == null || !mVideoView.isPlaying()) {
					return null;
				}
				mCurrent = mVideoView.getCurrentPosition();
				publishProgress(mCurrent * 100 / mDuration, mCurrent);
				if (mCurrent >= mDuration) {
					break;
				}
				
			} while (mCurrent <= mDuration && !isCancelled());
	
			return null;
		}
	
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			updatePlaybackTime(values[1], mDuration);
		}
	
	}

	/**
	 * Timer task to hide the controls, sent to mTimer to hide them after 5 seconds.
	 */
	private class HideControlsTask extends TimerTask {
		@Override
		public void run() {
			VideoPlaybackActivity.this.runOnUiThread(timerRunnable);
		}
		
		private Runnable timerRunnable = new Runnable() {

			@Override
			public void run() {
				// This needs to run on the UiThread
				hideControls();
			}
			
		};

	}
	
}

package com.turtlebeach.gstop.headsets.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.turtlebeach.gstop.R;
import com.turtlebeach.gstop.headsets.Constants;

import org.apache.commons.io.FilenameUtils;

public class VideoManager {
	private static final String TAG = "VideoManager";
	//Video management functions
	public static final int SOUNDVIDEOS = 9;
	public static final int GAMEVIDEOS = 99;
	public static final int ALLVIDEOS = 999;
	
	
	public static String[] getFileArray(Context ctx, int type) {
		switch(type) {
		case VideoManager.GAMEVIDEOS:
			return ctx.getResources().getStringArray(R.array.trailerVideos);
		case VideoManager.SOUNDVIDEOS:
			return ctx.getResources().getStringArray(R.array.soundVideos);
		}
		return null;
	}
	
	/**
	 * This method will return the string title for any given video file
	 * It runs through the different video 'types' to make a string match and return the correct title
	 * @param ctx
	 * @param f
	 * @return The title to display above the video during playback or below the video in the Carousel
	 */
	public static String getTitleForFile(Context ctx, File f) {
		final String filename = f.getName();
		String[] tmp = ctx.getResources().getStringArray(R.array.featuredVideo);
		int dex;
		for(dex = 0; dex < tmp.length; dex++) {
			if(filename.contains(tmp[dex])) {
				return ctx.getResources().getStringArray(R.array.featuredTitles)[dex];
			}
		} //not a feature video
		
		tmp = ctx.getResources().getStringArray(R.array.soundVideos);
		for(dex = 0; dex < tmp.length; dex++) {
			if(filename.contains(tmp[dex])) {
				Log.d(TAG, "title set by Video Manager: " + ctx.getResources().getStringArray(R.array.soundTitles)[dex]);
				return ctx.getResources().getStringArray(R.array.soundTitles)[dex];
			}
		} //not a sound experience video
		//TODO
		return "";
	}
	
	public static String getFeatureTitle(Context ctx, int position) {
		return ctx.getResources().getStringArray(R.array.featuredTitles)[position];
	}
	
	public static File getFeatureVideoFile(Context ctx, int position) {
		final String filename = ctx.getResources().getStringArray(R.array.featuredVideo)[position];
		Log.i(TAG, "Viewing Featured Video: " + filename);
		return new File(Constants.getFeatureVideo(), filename);
	}
	
	public static void createVideoImages(String[] filenames) {
		final File[] vidFiles = new File[filenames.length];
		int dex = 0;
		for(String name: filenames) {
			vidFiles[dex++] = new File(Constants.getUpdateDir() + File.separator + "video", name);
		}
		createVideoImages(vidFiles);
		
	}
	
	public static void createVideoImages(File[] videoFiles) {
		Bitmap thumbnail;
		File xFile;
		for(File video: videoFiles) {
			thumbnail = ThumbnailUtils.createVideoThumbnail(video.getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
			Log.v(TAG, "creating thumbnail for: " + FilenameUtils.getBaseName(video.getAbsolutePath()));
			if (thumbnail != null) {
				try {
					xFile = new File(Constants.getRootDir() + File.separator 
							+ FilenameUtils.getBaseName(video.getAbsolutePath()) + ".jpg");
					
					OutputStream os = new FileOutputStream(xFile);
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, os);
					os.close();
					xFile.setReadable(true, false);
					xFile.setWritable(true, false);
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			} else {
				Log.e(TAG, "Error creating thumbnail from video file: " + video.getAbsolutePath());
			}
		}
		
		
	}

}

package com.clickbrand.turtlebeach.update;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.clickbrand.turtlebeach.Constants;
import com.clickbrand.turtlebeach.model.VideoManager;

/**
 * This class will pull files from the webserver and copy them to local storage
 */
public class UpdateService extends IntentService{
    static final String LOG_TAG = "UpdateService";

    public static final String ACTION_UPDATE_FINISHED = "update.finished";
    public static final String ACTION_UPDATE_STARTED = "update.started";
    public static final String ACTION_FILE_LOADING_STARTED = "update.file.started";
    public static final String ACTION_UPDATE_CONTENT_FROM_SDCARD = "update.from.sdcard";
    //public static final String ACTION_UPDATE_CONTENT_FROM_NETWORK = "update.from.network";
    public static final String ACTION_STOP_UPDATE = "update.stop";
    
    public static volatile boolean mUpdating = false;
    
    public static final String sXtraTotal = "total";
	public static final String sXtraFileNum = "current";
	public static final String sXtraFileName = "filename";
	public static final String sXtraError = "errorReason";

	public UpdateService(){
        super(LOG_TAG);
    }
    
    @Override
    protected void onHandleIntent(Intent aIntent){
        String action = aIntent.getAction();
        Log.v(LOG_TAG, "onHandleIntent " + action);
        if(ACTION_UPDATE_CONTENT_FROM_SDCARD.equals(action)){
        	updateContentFromSDCard();
            return;
        } //end sd card update

        Log.i(LOG_TAG, action + ": action not supported");
    }
    
    private void updateContentFromSDCard(){
        if(!Constants.isRemoteSDAvailable()){
        	Log.i(LOG_TAG, "no update, SD card unavailable");
        	// Just in case
        	mUpdating = false;
        	notify(ACTION_STOP_UPDATE);
            return;
        }
        //TODO combine
        File localfile = new File(Constants.getRootDir());
        if(localfile.exists()) {
        	Log.i(LOG_TAG, "no update, root directory turtle_beach exists");
        	// Just in case
        	mUpdating = false;
        	notify(ACTION_STOP_UPDATE);
        	return ;
        } 
        Log.v(LOG_TAG, "starting update");
        mUpdating = true;
        notify(ACTION_UPDATE_STARTED);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(ACTION_UPDATE_STARTED, true).commit();
        final File remoteContent = new File(Constants.getUpdateDir());
        final File[] newFiles = remoteContent.listFiles();
        		//an array of files contained in the directory
        int ignoreAbsolute = Constants.getUpdateDir().length() + 1;
        try{

            int counter;
            for(File updateFile : newFiles){
            	//reading the listFiles output and copying directories
                if(updateFile.isDirectory()){
                	//match directory name
                	localfile = new File(Constants.getRootDir() + File.separator
                    		+ updateFile.getAbsolutePath().substring(ignoreAbsolute));
                	
                	
                    if(!localfile.mkdirs()){
                    	//create the local directory and report errors
                        Log.e(LOG_TAG, "Cannot create dir: " + updateFile.getAbsolutePath());
                    } else {
                    	//local directory creation success, use Apache utils to copy the directory
                        Log.v(LOG_TAG, "Directory made, Copying dir: " + updateFile.getAbsolutePath());
                        counter = updateFile.listFiles().length;
                        FileUtils.copyDirectory(updateFile, localfile);
                        //TODO drop extra parameter?
                        notify(ACTION_FILE_LOADING_STARTED, newFiles.length, counter, localfile.getAbsolutePath(),  null);
                    }
                } else {
                	//this is a file, not a directory, ignore it
                    Log.v(LOG_TAG, "found file:" + updateFile.getName());                    
                }

            }
            //now finish up by creating video thumbnails for the carousel
            localfile = new File(Constants.getVideo());
            //TODO game trailers, make it public static from VideoManager class
            //contains both sound and game trailers
            Log.v(LOG_TAG, "creating video thumbnails");
            VideoManager.createVideoImages(localfile.listFiles());
        } catch (IOException e) {
			// This means trouble
        	Log.e(LOG_TAG, "Error in update: " + e.getMessage());
			e.printStackTrace();
		} finally {
            Log.v(LOG_TAG, "updateApp: sendBroadcast ACTION_UPDATE_FINISHED");
            notify(ACTION_UPDATE_FINISHED);
            mUpdating = false;
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove(ACTION_UPDATE_STARTED).commit();
        }
    }
    
    /* This is not needed for Target, nor others
     * uninstalling the old version will remove all existing files
     * 
     * private void removeOrphanFiles(List<UpdateFileDescriptor> aFileInfo, ArrayList<String> updateFiles){

        List<File> dirs = new LinkedList<File>();
        // getting dirs
        File file;
        String fileName;
        for(UpdateFileDescriptor fileDescriptor : aFileInfo){
            fileName = fileDescriptor.getName();
            if(fileName == null) {
            	continue;
            }
            file = new File(Constants.getRootDir(), fileName);
            //build a list of directories here
            if(file.isDirectory()){
                dirs.add(file);
            }
        }
        // scanning dirs and checking existing if local files are in the UpdateFileDescriptor list
        // if not in that list - remove it
        for(File folder : dirs){
            String[] fileList = folder.list();
            for(String name : fileList){
            	//loop through all files in each of the directories                
                if(!updateFiles.contains(name)){
                	File localFile = new File(folder, name);
                    localFile.delete();
                    Log.i(LOG_TAG, "removing outdated local file: " + name);
                    //do another file deletion if this orphan was a video
                    if(localFile.getName().contains("mp4")){
                        File thumbFile = new File(Constants.getRootDir() + File.separator + localFile.getName());
                        if(thumbFile.exists()){
                            thumbFile.delete();
                            Log.i(LOG_TAG, "removing video thumbnail:" + thumbFile.getName());
                        }
                    }
                }
            }
        }

    }*/


    private void notify(String aAction) {
    	Intent intent = new Intent(aAction);
    	sendBroadcast(intent);
    }
    
    private void notify(String aAction, int total, int aCounter, String directoryName,
                        String aErrorDescription){
        Intent intent = new Intent(aAction);
        intent.putExtra(sXtraTotal, total);
        if(aCounter > 0){
            intent.putExtra(sXtraFileNum, aCounter);
            if(!TextUtils.isEmpty(directoryName)){
                intent.putExtra(sXtraFileName, directoryName);
            }
            if(aErrorDescription != null){
                intent.putExtra(sXtraError, aErrorDescription);
            }
        }
        
        sendBroadcast(intent);
    }

}

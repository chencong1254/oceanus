package com.mediatek.tvinput.dtv;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.content.Context;
import android.os.RemoteException;
import android.content.ContentResolver;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.media.tv.TvContract;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.MtkTvRecordBase;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordNotifyMsgType;
import com.mediatek.twoworlds.tv.MtkTvPvrBrowserBase;
import com.mediatek.twoworlds.tv.model.MtkTvPvrBrowserItemBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

class RecordingFilePump {
    public static String recordingFileName;
    private static String TAG = "MtkTvInput(RecordingFilePump)";
    private static long FAKE_CHANNEL_ID = 0xffffffff;
    private static String recordingInputID = ChannelPump.ServiceInputIdDtv;
    private static final String SELECTION_WITH_SVLID = "substr(cast(" +
        TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA + " as varchar),7,5) = ?";
    private static final String SELECTION_WITH_SVLID_CHANNELID = SELECTION_WITH_SVLID +
        " and substr(cast(" + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA +
        " as varchar),19,10) = ?";

    private String[] getSvlIdAndChannelIdSelectionArgs(long channelId) {
        long newId = (channelId & 0xffffffffL);
        Log.d(TAG, "channelId>>>" + channelId + ">>" + newId);
        int svlId = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_BS_SVL_ID);
        String[] selectionArgs = {
            String.format("%05d", svlId), String.format("%010d", newId)
        };
        return selectionArgs;
    }

    private long channelIdToInternalID(ContentResolver contentResolver, long channelId)
    {
        Cursor c = contentResolver.query(TvContract.Channels.CONTENT_URI, null,
            SELECTION_WITH_SVLID_CHANNELID, getSvlIdAndChannelIdSelectionArgs(channelId), null);
        if ((c == null) || (c.getCount() < 1)) {
          Log.d(TAG, "cannot find channel " + channelId);
          return 0;
        }
        c.moveToFirst();
        long id = c.getLong(c.getColumnIndex(TvContract.Channels._ID));
        Log.d(TAG, "channel " + channelId + " to " + id);
        return id;
    }
    
    public void resetSync(ContentResolver contentResolver) {

        ArrayList<ContentProviderOperation> opsdelete = new ArrayList<ContentProviderOperation>();
        opsdelete.add(ContentProviderOperation.newDelete(TvContract.RecordedPrograms.CONTENT_URI).build());

        try {
            contentResolver.applyBatch(TvContract.AUTHORITY, opsdelete);
        } catch (RemoteException e) {
            Log.d(TAG, "catch RemoteException when clean");
        } catch (OperationApplicationException e) {
            Log.d(TAG, "catch OperationApplicationException when clean");
        }
        
        MtkTvPvrBrowserBase record = new MtkTvPvrBrowserBase();
        int listCount = record.getPvrBrowserItemCount();
        if (listCount == 0) {
            Log.d(TAG, "recording file 0, reset failed");
            return;
        }

        for (int i = 0; i < listCount; i++) {
            MtkTvPvrBrowserItemBase a = record.getPvrBrowserItemByIndex(i);
            ContentValues values = new ContentValues();
            values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI, ContentResolver.SCHEME_FILE
                    + "://" + a.mPath);
            values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, recordingInputID);
            long id = channelIdToInternalID(contentResolver, a.mChannelId);
            if(id != 0)
            {
                values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID, id);
            }
            values.put(TvContract.RecordedPrograms.COLUMN_TITLE, a.mChannelName);
            values.put(TvContract.RecordedPrograms.COLUMN_SHORT_DESCRIPTION, a.mProgramName);
            values.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS, a.mStartTime);
            values.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS, a.mEndTime);
            values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, a.mDuration);
            Log.d(TAG, "insert " + ContentResolver.SCHEME_FILE
                    + "://" + a.mPath + ", channel " + a.mChannelId);
            contentResolver.insert(TvContract.RecordedPrograms.CONTENT_URI, values);
        }
    }
    public Cursor readTvproviderByPath(ContentResolver contentResolver, String path) {
      Log.d(TAG, "readTvprovider begin");
    
      String[] projection = {
          TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI,
      };
    
      String selection = TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI + " = ? ";
      String[] selectionArgs = { ContentResolver.SCHEME_FILE + "://" + path };
      String order = null;
    
      Log.d(TAG, "readTvprovider end");
    
      return contentResolver.query(TvContract.RecordedPrograms.CONTENT_URI, projection, selection, selectionArgs, order);
    }
    public void mergeSync(ContentResolver contentResolver) {
        MtkTvPvrBrowserBase browser = new MtkTvPvrBrowserBase();
        int listCount = browser.getPvrBrowserItemCount();
        if (listCount == 0) {
            Log.d(TAG, "recording file 0, merge failed");
            return;
        }

        for (int i = 0; i < listCount; i++) {
            MtkTvPvrBrowserItemBase a = browser.getPvrBrowserItemByIndex(i);
            Cursor mCursor = readTvproviderByPath(contentResolver, a.mPath);
            if (mCursor.getCount() < 1){
                Log.d(TAG, "delete file " + a.mPath);
                browser.deletePvrBrowserFileByIndex(i);
                return;
            }
        }
        Log.d(TAG, "no file delete");
        
    }
    public void insertRecordingFile(ContentResolver contentResolver){
        recordingFileName = MtkTvRecord.getInstance().getRecordingFileName();
        
        MtkTvPvrBrowserBase base = new MtkTvPvrBrowserBase();
        MtkTvPvrBrowserItemBase recordItem = null;
        if (recordingFileName != null) {
            recordItem = base.getPvrBrowserItemByPath(recordingFileName);
            ContentValues values = new ContentValues();
            values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI, ContentResolver.SCHEME_FILE
                    + "://" + recordItem.mPath);
            //values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID, FAKE_CHANNEL_ID);
            values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, recordingInputID);
            values.put(TvContract.RecordedPrograms.COLUMN_TITLE, recordItem.mChannelName);
            values.put(TvContract.RecordedPrograms.COLUMN_SHORT_DESCRIPTION, recordItem.mProgramName);
            values.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS, recordItem.mStartTime);
            values.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS, recordItem.mEndTime);
            values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, recordItem.mDuration);
            contentResolver.insert(TvContract.RecordedPrograms.CONTENT_URI, values);
            Log.d(TAG, "intert recording file:" + recordItem.mPath);
        } else {
            Log.d(TAG, "recording file name null, insert failed");
        }
    }

    public void updateRecordingFile(ContentResolver contentResolver){
        if (recordingFileName == null) {
            Log.d(TAG, "recording file name null, update failed");
            return;
        }
        MtkTvPvrBrowserBase base = new MtkTvPvrBrowserBase();
        MtkTvPvrBrowserItemBase recordItem = null;
        recordItem = base.getPvrBrowserItemByPath(recordingFileName);
        ContentValues values = new ContentValues();
        values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI, ContentResolver.SCHEME_FILE
                    + "://" + recordItem.mPath);
        //values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID, FAKE_CHANNEL_ID);
        values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, recordingInputID);
        values.put(TvContract.RecordedPrograms.COLUMN_TITLE, recordItem.mChannelName);
        values.put(TvContract.RecordedPrograms.COLUMN_SHORT_DESCRIPTION, recordItem.mProgramName);
        values.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS, recordItem.mStartTime);
        values.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS, recordItem.mEndTime);
        values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, recordItem.mDuration);
        
        String where = TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI + " = ?";
        String[] selectionArgs = { ContentResolver.SCHEME_FILE + "://" + recordItem.mPath };
        Log.d(TAG, "update recording file:" + recordItem.mPath);
        contentResolver.update(TvContract.RecordedPrograms.CONTENT_URI, values, where, selectionArgs);
    }

    public void updateRecordingFileDuration(ContentResolver contentResolver, int handle, int duration){
        if (recordingFileName == null) {
            Log.d(TAG, "recording file name null, update failed");
            return;
        }
        MtkTvPvrBrowserBase base = new MtkTvPvrBrowserBase();
        MtkTvPvrBrowserItemBase recordItem = null;
        recordItem = base.getPvrBrowserItemByPath(recordingFileName);
        ContentValues values = new ContentValues();
        values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI, ContentResolver.SCHEME_FILE
                    + "://" + recordItem.mPath);
        //values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID, FAKE_CHANNEL_ID);
        values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, recordingInputID);
        values.put(TvContract.RecordedPrograms.COLUMN_TITLE, recordItem.mChannelName);
        values.put(TvContract.RecordedPrograms.COLUMN_SHORT_DESCRIPTION, recordItem.mProgramName);
        values.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS, recordItem.mStartTime);
        values.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS, recordItem.mStartTime + duration);
        values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, duration);
        
        String where = TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI + " = ?";
        String[] selectionArgs = { ContentResolver.SCHEME_FILE + "://" + recordItem.mPath };
        
        Log.d(TAG, "update recording duration:" + duration);
        contentResolver.update(TvContract.RecordedPrograms.CONTENT_URI, values, where, selectionArgs);
    }
        
}


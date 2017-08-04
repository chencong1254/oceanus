
package com.mediatek.tvinput.dtv;

import android.content.ContentResolver;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import android.os.SystemProperties;

import java.util.List;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.MtkTvEventBase;
import android.content.Intent;
import com.mediatek.twoworlds.tv.common.MtkTvIntentBase;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;

import android.database.Cursor;
import android.content.ContentValues;
import android.media.tv.TvContract;
//import android.media.tv.Programs;
import android.content.Intent;
import android.app.ActivityManagerNative;
import android.os.UserHandle;

import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import android.media.tv.TvContentRating;
import android.text.TextUtils;
import com.mediatek.twoworlds.tv.model.MtkTvEventConverBroadcastGene;

public class ProgramPump {
  private static String TAG = "MtkTvInput(ProgramlPump)";
  private List<Integer> mActiveWindowChannelIdList = new ArrayList<Integer>();

  private static long ac_startTime = 0;
  private static long ac_endTime = 0;
  private static boolean g_b_is_active = false;

  private static MtkTvEventInfoBase g_p_event_obj = null;
  private static MtkTvEventInfoBase g_f_event_obj = null;
  private static int now_channel_id = 0; // svl channel_id
  private TunerInputService service;
  public static final String tvprovidrVer = "00100";

  protected ContentResolver contentResolver;
  private final MtkTvEventBase mEpg = MtkTvEvent.getInstance();
  private final MtkTvEventConverBroadcastGene mBroadCastGene = new MtkTvEventConverBroadcastGene();

  public ProgramPump(ContentResolver contentResolver, TunerInputService service) {
    Log.d(TAG, "program pump constructor");
    this.contentResolver = contentResolver;
    this.service = service;
  }

  public ProgramPump(ContentResolver contentResolver) {
    Log.d(TAG, "program pump constructor");
    this.contentResolver = contentResolver;

  }

  // is_p_f_s.1,current;0,next;2,schule
  private void syncSingleProgram(String IdStr, MtkTvEventInfoBase eventObj,
      MtkTvRatingConvert2Goo ratingMapped, int is_p_f_s) {
    // String IdStr = channelIdTransfer(eventObj.getChannelId());
    /*
     * if(IdStr == null) { Log.d(TAG, "can't find the channel id.refused insert .id"+channelId
     * +"event_id"+eventObj.getEventId()+"c_id"+eventObj.getChannelId()+"from"+is_p_f_s); return; }
     * else { Log.d(TAG,"find channel id"+channelId +"==>"+IdStr+"event id"+eventObj.getEventId());
     * }
     */

    Log.d(TAG, "syncSingleProgram channel _id" + IdStr + ",event id=" + eventObj.getEventId());

    ContentValues values = new ContentValues();
    // values.put(TvContract.Programs.COLUMN_CHANNEL_ID, mChannelId);
    values.put(TvContract.Programs.COLUMN_TITLE, eventObj.getEventTitle());
    values
        .put(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS, ((eventObj.getStartTime()) * 1000));
    values.put(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        ((eventObj.getDuration() + eventObj.getStartTime()) * 1000));
    values.put(TvContract.Programs.COLUMN_SHORT_DESCRIPTION, eventObj.getEventDetail());
    values.put(TvContract.Programs.COLUMN_LONG_DESCRIPTION, eventObj.getEventDetailExtend());
    // values.put(TvContract.Programs.COLUMN_CONTENT_RATING, eventObj.getEventRating());//for
    // rating,here used string always

    // values.put(TvContract.Programs.COLUMN_CONTENT_RATING, eventObj.getEventRating());//for
    // rating,here used string always
    int[] eventCategory = eventObj.getEventCategory();
    int eventCatogoryNum = eventObj.getEventCategoryNum();
    if (eventCatogoryNum > 0)
    {
      Log.d(
          TAG,
          "syncSingleProgram catogry num :" + eventCatogoryNum + "catogry value"
              + Integer.toHexString(eventCategory[0]) + "catogry value org:" + eventCategory[0]);

      String marketregion = SystemProperties.get("ro.mtk.system.marketregion");

      Log.d(TAG, "syncSingleProgram  marketregion==" + marketregion);

      String BroadCastGene;
      if (marketregion != null && marketregion.equals("eu"))
      {
        BroadCastGene = mBroadCastGene.getEUBroadcastGene(Integer.toHexString(eventCategory[0]));
      }
      else {
        BroadCastGene = mBroadCastGene.getSABroadcastGene(Integer.toHexString(eventCategory[0]));
      }

      if (!TextUtils.isEmpty(BroadCastGene)) {
        values.put(TvContract.Programs.COLUMN_BROADCAST_GENRE, BroadCastGene);
        Log.d(TAG, "syncSingleProgram catogry  :" + BroadCastGene);
      }
    }

    if (!TextUtils.isEmpty(ratingMapped.getDomain())
        && !TextUtils.isEmpty(ratingMapped.getRatingSystem())
        && !TextUtils.isEmpty(ratingMapped.getRating())) {
      TvContentRating contentRating = TvContentRating.createRating(ratingMapped.getDomain(),
          ratingMapped.getRatingSystem(),
          ratingMapped.getRating(),
          ratingMapped.getSubRating());

      values.put(TvContract.Programs.COLUMN_CONTENT_RATING, contentRating.flattenToString());
    }//

    // values.put(TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA,
    // Integer.toString(mEvent.getEventId()));//save event id here.
    values.put(
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA,
        tvprovidrVer + "," + String.format("%05d", eventObj.getEventId()) + ","
            + String.format("%01d", is_p_f_s));
    values.put(TvContract.Programs.COLUMN_CHANNEL_ID, IdStr);

    contentResolver.insert(TvContract.Programs.CONTENT_URI, values);
  }

  // is_p_f_s.1,current;0,next;2,schule
  private void updateSingleProgram(String IdStr, MtkTvEventInfoBase eventObj,
      MtkTvRatingConvert2Goo ratingMapped, int is_p_f_s) {
    /*
     * String IdStr = channelIdTransfer(eventObj.getChannelId()); if(IdStr == null) { Log.d(TAG,
     * "can't find the channel id.refused insert .id"+channelId
     * +"event_id"+eventObj.getEventId()+"c_id"+eventObj.getChannelId()+"from"+is_p_f_s); return; }
     * else { Log.d(TAG,"find channel id"+channelId +"==>"+IdStr+"event id"+eventObj.getEventId());
     * }
     */
    Log.d(TAG, "updateSingleProgram channel _id" + IdStr + ",event id=" + eventObj.getEventId()
        + " ,tag= " + is_p_f_s);

    // first delete orgin record.
    ContentValues values = new ContentValues();
    // values.put(TvContract.Programs.COLUMN_CHANNEL_ID, mChannelId);
    values.put(TvContract.Programs.COLUMN_TITLE, eventObj.getEventTitle());
    values
        .put(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS, ((eventObj.getStartTime()) * 1000));
    values.put(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        ((eventObj.getDuration() + eventObj.getStartTime()) * 1000));
    values.put(TvContract.Programs.COLUMN_SHORT_DESCRIPTION, eventObj.getEventDetail());
    values.put(TvContract.Programs.COLUMN_LONG_DESCRIPTION, eventObj.getEventDetailExtend());
    // values.put(TvContract.Programs.COLUMN_CONTENT_RATING, eventObj.getEventRating());//for
    // rating,here used string always
    int[] eventCategory = eventObj.getEventCategory();
    int eventCatogoryNum = eventObj.getEventCategoryNum();
    if (eventCatogoryNum > 0)
    {
      Log.d(
          TAG,
          "updateSingleProgram catogry num :" + eventCatogoryNum + "catogry value"
              + Integer.toHexString(eventCategory[0]) + "catogry value org:" + eventCategory[0]);

      String marketregion = SystemProperties.get("ro.mtk.system.marketregion");

      Log.d(TAG, "updateSingleProgram  marketregion==" + marketregion);

      String BroadCastGene;
      if (marketregion != null && marketregion.equals("eu"))
      {
        BroadCastGene = mBroadCastGene.getEUBroadcastGene(Integer.toHexString(eventCategory[0]));
      }
      else {
        BroadCastGene = mBroadCastGene.getSABroadcastGene(Integer.toHexString(eventCategory[0]));
      }

      if (!TextUtils.isEmpty(BroadCastGene)) {
        values.put(TvContract.Programs.COLUMN_BROADCAST_GENRE, BroadCastGene);
        Log.d(TAG, "updateSingleProgram catogry  :" + BroadCastGene);
      }
    }

    // values.put(TvContract.Programs.COLUMN_BROADCAST_GENRE, eventObj.getEventCategory()));//for
    // rating,here used string always
    if (!TextUtils.isEmpty(ratingMapped.getDomain())
        && !TextUtils.isEmpty(ratingMapped.getRatingSystem())
        && !TextUtils.isEmpty(ratingMapped.getRating())) {
      TvContentRating contentRating = TvContentRating.createRating(ratingMapped.getDomain(),
          ratingMapped.getRatingSystem(),
          ratingMapped.getRating(),
          ratingMapped.getSubRating());

      values.put(TvContract.Programs.COLUMN_CONTENT_RATING, contentRating.flattenToString());
    }//

    // values.put(TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA,
    // Integer.toString(mEvent.getEventId()));//save event id here.
    values.put(
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA,
        tvprovidrVer + "," + String.format("%05d", eventObj.getEventId()) + ","
            + String.format("%01d", is_p_f_s));
    values.put(TvContract.Programs.COLUMN_CHANNEL_ID, IdStr);

    String selection = "substr(" + TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
        + ",7,5)=? AND " + TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";
    // now,just used svl channel id to instead channel id in provider.need to replace
    // then>>>>>>>>>>>>>>>>>>>>>
    String[] selectionArgs = {
        String.format("%05d", eventObj.getEventId()), IdStr
    };

    contentResolver.update(TvContract.Programs.CONTENT_URI, values, selection, selectionArgs);
  }

  private void deleteSingleProgram(String IdStr, int eventId) {
    /*
     * String IdStr = channelIdTransfer(channelId); if(IdStr == null) { Log.d(TAG,
     * "can't find the channel id.refused insert .id"+channelId +"event_id"+eventId); return; } else
     * { Log.d(TAG,"find channel id"+channelId +"==>"+IdStr+"event id"+eventId); }
     */
    Log.d(TAG, "deleteSingleProgram channel _id=" + IdStr + " ,event id=" + eventId);
    // now,just used svl channel id to instead channel id in provider.need to replace
    // then>>>>>>>>>>>>>>>>>>>>>

    String selection = "substr(" + TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
        + ",7,5)=? AND " + TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";
    // now,just used svl channel id to instead channel id in provider.need to replace
    // then>>>>>>>>>>>>>>>>>>>>>
    String[] selectionArgs = {
        String.format("%05d", eventId), IdStr
    };

    contentResolver.delete(TvContract.Programs.CONTENT_URI, selection, selectionArgs);
  }

  private List<Integer> queryEventsByChannelyId(String IdStr) {
    Log.d(TAG, "queryEventsBChannelyId channel _id=" + IdStr);
    List<Integer> eventIDs = new ArrayList<Integer>();
    String[] projection = {
        TvContract.Programs._ID,
        TvContract.Programs.COLUMN_TITLE,
        TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_SHORT_DESCRIPTION,
        TvContract.Programs.COLUMN_LONG_DESCRIPTION,
        TvContract.Programs.COLUMN_CONTENT_RATING,
        TvContract.Programs.COLUMN_BROADCAST_GENRE,
        TvContract.Programs.COLUMN_CANONICAL_GENRE,
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
    };

    String selection = TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";
    // now,just used svl channel id to instead channel id in provider.need to replace
    // then>>>>>>>>>>>>>>>>>>>>>
    String[] selectionArgs = {
        IdStr
    };
    Cursor c = contentResolver.query(TvContract.Programs.CONTENT_URI, projection, selection,
        selectionArgs, null);
    while (c.moveToNext()) {
      String data = c
          .getString(c.getColumnIndex(TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA));
      if (data == null) {
        continue;
      }
      String value[] = data.split(",");
      if (value.length < 3) {
        continue;
      }
      int eventId = Integer.parseInt(value[1]);
      eventIDs.add(eventId);
    }
		c.close();
    return eventIDs;
  }

  private Cursor queryEventById(String IdStr, int eventId) {
    /*
     * String IdStr = channelIdTransfer(channelId); if(IdStr == null) { Log.d(TAG,
     * "can't find the channel id.refused insert .id"+channelId +"event_id"+eventId); return null; }
     * else { Log.d(TAG,"find channel id"+channelId +"==>"+IdStr+"event id"+eventId); }
     */
    Log.d(TAG, "queryEventById channel _id=" + IdStr + " ,event id=" + eventId);

    String[] projection = {
        TvContract.Programs._ID,
        TvContract.Programs.COLUMN_TITLE,
        TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_SHORT_DESCRIPTION,
        TvContract.Programs.COLUMN_LONG_DESCRIPTION,
        TvContract.Programs.COLUMN_CONTENT_RATING,
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
    };

    String selection = "substr(" + TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
        + ",7,5)=? AND " + TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";
    // now,just used svl channel id to instead channel id in provider.need to replace
    // then>>>>>>>>>>>>>>>>>>>>>
    String[] selectionArgs = {
        String.format("%05d", eventId), IdStr
    };
    return contentResolver.query(TvContract.Programs.CONTENT_URI, projection, selection,
        selectionArgs, null);

  }

  // note:input is channel id which is from svl
  public void syncPFProgramByChannel(int channelId, int is_current) {
    // first get pf info from mw
    Log.d(TAG, "syncPFProgramByChannel" + channelId);
    // now_channel_id = channelId;
    MtkTvEventInfoBase mEvent;//
    // String current_event_str = null;
    MtkTvEventInfoBase p_event_obj = null;// orgin event obj;
    if (is_current == 1) {
      p_event_obj = g_p_event_obj;
      mEvent = mEpg.getPFEventInfoByChannel(channelId, true);
      if (mEvent == null)
      {
        Log.d(TAG, "current event is not exist");
      }
      else
      {
        // current_event_str = mEvent.toString();
        // Log.d(TAG, "current event:"+current_event_str);
      }
    }
    else {
      mEvent = mEpg.getPFEventInfoByChannel(channelId, false);
      p_event_obj = g_f_event_obj;
      if (mEvent != null)
      {
        // current_event_str = mEvent.toString();
        // Log.d(TAG, "next event:"+current_event_str);

      }
      else
      {
        Log.d(TAG, "next event is not exist");
      }
    }

    // if now not set acitve window,will write it to the pool
    if (g_b_is_active == false)
    {
      Log.d(TAG, "active window is not set,just write to the pool");
      // just query and do update,not care what happen before
      if (mEvent != null)
      {
        MtkTvRatingConvert2Goo ratingMapped = new MtkTvRatingConvert2Goo();
        mEpg.getEventRatingMapById(channelId, mEvent.getEventId(), ratingMapped);
        String IdStr = channelIdTransfer(channelId);
        if (IdStr == null)
        {
          Log.d(TAG, "can't find the channel id.refused insert .id=" + channelId + "event_id="
              + mEvent.getEventId());

        }
        else
        {
          Cursor mCursor = queryEventById(IdStr, mEvent.getEventId());

          if (mCursor == null)
          {
            Log.d(TAG, "cursor not loaded ");
            throw new IllegalStateException("Cursor not loaded");

          }
          else
          {
            Log.d(TAG,
                "find channel id" + channelId + "==>" + IdStr + ",event id=" + mEvent.getEventId());
            if (mCursor.getCount() < 1)
            {
              // just sync to pool
              Log.d(TAG, "cursor " + mCursor.getCount() + "insert this program");

              syncSingleProgram(IdStr, mEvent, ratingMapped, is_current);// 1,current;0,next
            }
            else if (mCursor.getCount() == 1)
            {
              Log.d(TAG, "cursor " + mCursor.getCount() + "update this program");
              updateSingleProgram(IdStr, mEvent, ratingMapped, is_current);// update event
                                                                           // 1,current;0,next
            }
            else if (mCursor.getCount() > 1)
            {
              Log.d(TAG, "cursor " + mCursor.getCount() + "delete and insert this program");
              // just for test,delete all the record,and add new one:
              deleteSingleProgram(IdStr, mEvent.getEventId());

              syncSingleProgram(IdStr, mEvent, ratingMapped, is_current);// s event
            }
            mCursor.close();
            broadcastIntentOut(is_current, channelId);
          }
        }
      }

    }

    // deleteChannelProgram(1);
    /*
     * //sync to the TVprovider if(mEvent != null) { //first query.if p event is already exist,maybe
     * the last channel,maybe the current channel if(p_event_obj != null){ if(channelId ==
     * p_event_obj.getChannelId()){ Log.d(TAG, is_current+" event maybe update,will do check");
     * String event_str_org = p_event_obj.toString(); boolean b_is_same =
     * event_str_org.equals(current_event_str); if(!b_is_same){ //for event id is not the same,check
     * if confilct if(mEvent.getEventId()!= p_event_obj.getEventId()){ //check conflict boolean
     * b_is_conflict = false; if((mEvent.getStartTime()>=
     * (p_event_obj.getStartTime()+p_event_obj.getDuration()))
     * ||((mEvent.getStartTime()+mEvent.getDuration())<= p_event_obj.getStartTime())) { Log.d(TAG,
     * "current event is not conflict with last current event"); b_is_conflict= false; } else {
     * b_is_conflict = true; Log.d(TAG, "current event is  conflict with last current event"); }
     * //for event conflict with orgin one.should delete the orgin one if(b_is_conflict) {
     * deleteSingleProgram(channelId,p_event_obj.getEventId()); p_event_obj = null; }
     * syncSingleProgram(mEvent.getChannelId(),mEvent,is_current); } else{ //just update record
     * updateSingleProgram(channelId,mEvent,is_current); } //update orgin event obj;
     * if(is_current==1) { g_p_event_obj = mEvent; } else { g_f_event_obj = mEvent; } //send intent
     * } else{ Log.d(TAG, "this time event is the same one,will not update TV provider"); return; }
     * } else{ Log.d(TAG, "current channel changed,will insert new present event");
     * syncSingleProgram(mEvent.getChannelId(),mEvent,is_current); //update orgin event obj;
     * if(is_current==1) { g_p_event_obj = mEvent; } else { g_f_event_obj = mEvent; } } } }
     */
  }

  public void syncActiveWindowProgram(int b_active_set) {
    Log.d(TAG, "syncActiveWindowProgram " + b_active_set);

    if (b_active_set == 1)
    {
      g_b_is_active = true;
      int[] acChannelList = mEpg.getCurrentActiveWinChannelList();
      Log.d(TAG, "already get channel list in active window: " + mActiveWindowChannelIdList.size());
      if (mActiveWindowChannelIdList == null) {
        mActiveWindowChannelIdList = new ArrayList<Integer>();
      } else {
        mActiveWindowChannelIdList.clear();
      }
      if (acChannelList != null) {
        for (int i = 0; i < acChannelList.length; i++)
        {
          Log.d(TAG, "syncActiveWindowProgram>>index" + i + "id:" + acChannelList[i]);
          mActiveWindowChannelIdList.add(acChannelList[i]);
        }
      }

      long startTime = mEpg.getCurrentActiveWinStartTime();
      Log.d(TAG, "already get start time in active window " + mActiveWindowChannelIdList.size());
      long endTime = mEpg.getCurrentActiveWinEndTime();
      Log.d(TAG, "notifyEvent is ative window changed ,start time " + startTime + "endTime "
          + endTime + "orgin start" + ac_startTime + "end" + ac_endTime);

      ac_startTime = startTime;
      ac_endTime = endTime;

      if (acChannelList != null) {
        Log.d(TAG, "syncActiveWindowProgram begin to sync active window by channel");
        for (int i = 0; i < acChannelList.length; i++)
        {
          Log.d(TAG, "index" + i + "id:" + acChannelList[i]);
          syncActiveWindowProgramByChannel(acChannelList[i]);
        }
        Log.d(TAG, "syncActiveWindowProgram end to sync active window by channel");
      }
      else {
        Log.d(TAG, "syncActiveWindowProgram  fail,channel list is null");
      }
    }
    else
    {
      if (mActiveWindowChannelIdList != null) {
        mActiveWindowChannelIdList.clear();
      }
      g_b_is_active = false;
      ac_startTime = 0;
      ac_endTime = 0;
    }
  }

  public void syncActiveWindowProgramByChannel(int channelId) {
    Log.d(TAG, "syncActiveWindowProgramByChannel " + channelId);
    if ((ac_startTime == 0) || (ac_endTime == 0) || (ac_startTime > ac_endTime))
    {
      Log.d(TAG, "syncActiveWindowProgramByChannel fail because active window time wrong (start"
          + ac_startTime + ",end" + ac_endTime + ")");
      return;
    }

    try
    {
      String IdStr = channelIdTransfer(channelId);
      if (IdStr == null)
      {
        Log.d(TAG, "can't find the channel id.refused insert .id" + channelId);

      }
      else
      {
        if (mActiveWindowChannelIdList == null || !mActiveWindowChannelIdList.contains(channelId)) {
          Log.d(TAG,
              "syncActiveWindowProgram"
                  + " can't find the channel id.refused insert .id and delete it's event:"
                  + channelId);
          deleteSpecifyChannelProgram_by_ID_str(IdStr);// delete event program for this channel
          return;
        }
        // deleteSpecifyChannelProgram_by_ID_str(IdStr);//delete event program for this channel
        List<MtkTvEventInfoBase> event_list = mEpg.getEventListByChannelId(channelId, ac_startTime,
            (ac_endTime - ac_startTime));

        if (event_list != null) {
          List<Integer> eventIds = queryEventsByChannelyId(IdStr);
          int event_num = event_list.size();
          Log.d(TAG, "get event list ,event number " + event_num);
          for (int i = 0; i < event_num; i++)
          {
            Log.d(TAG, "get event ,index  " + i);
            MtkTvEventInfoBase event_tmp = event_list.get(i);

            Log.d(TAG, "find event in channel :" + channelId + "==>" + IdStr + ",event id"
                + event_tmp.getEventId());

            MtkTvRatingConvert2Goo ratingMapped = new MtkTvRatingConvert2Goo();
            mEpg.getEventRatingMapById(channelId, event_tmp.getEventId(), ratingMapped);

            Cursor mCursor = queryEventById(IdStr, event_tmp.getEventId());
            if (mCursor == null)
            {
              Log.d(TAG, "cursor not loaded ");
              throw new IllegalStateException("Cursor not loaded");

            }
            else
            {
              if (mCursor.getCount() < 1)
              {
                // just sync to pool
                Log.d(TAG, "cursor " + mCursor.getCount() + "insert this program");

                syncSingleProgram(IdStr, event_tmp, ratingMapped, 2);// s event
              }
              else if (mCursor.getCount() == 1)
              {
                Log.d(TAG, "cursor " + mCursor.getCount() + "update this program");
                updateSingleProgram(IdStr, event_tmp, ratingMapped, 2);// update event
                if (eventIds.contains(event_tmp.getEventId())) {
                  eventIds.remove((Integer) event_tmp.getEventId());
                }
              }
              else if (mCursor.getCount() > 1)
              {
                // just for test,delete all the record,and add new one:
                deleteSingleProgram(IdStr, event_tmp.getEventId());
                syncSingleProgram(IdStr, event_tmp, ratingMapped, 2);// s event
                if (eventIds.contains(event_tmp.getEventId())) {
                  eventIds.remove((Integer) event_tmp.getEventId());
                }
              }
              mCursor.close();
            }
            // Log.d(TAG,event_tmp.toString());

          }//
          int size = eventIds.size();
          Log.i(TAG, "syncActiveWindowProgramByChannel size>>" + size);
          for (int i = 0; i < size; i++) {
            deleteSingleProgram(IdStr, eventIds.get(i));
          }
          // if(event_num >= 0 )
          {
            broadcastIntentOut(2, channelId);
          }
        }
        else
        {
          Log.d(TAG, "get event list fail. ");
        }
      }
    } catch (MtkTvExceptionBase e)
    {
      e.printStackTrace();
    }
  }

  public void syncProgramByEventId(long channel_id, int event_id) {
  }

  public void deleteSpecifyChannelProgram_by_ID_str(String channel_str) {
    Log.d(TAG, "delete Specify Channel Program _id= " + channel_str);
    String selection = TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";
    String[] selectionArgs =
        new String[] {
            channel_str
        };
    contentResolver.delete(TvContract.Programs.CONTENT_URI, selection, selectionArgs);
  }

  public void deleteSpecifyChannelProgram(long channel_id) {
    Log.d(TAG, "delete Specify Channel Program _id= " + channel_id);
    String selection = TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";
    String[] selectionArgs =
        new String[] {
            Long.toString(channel_id)
        };
    contentResolver.delete(TvContract.Programs.CONTENT_URI, selection, selectionArgs);
  }

  public void deleteChannelProgram(int svl_id) {
    Log.d(TAG, "delete  Channel Program by select args svl =" + svl_id);
    // String selection = TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";

    // query before test
    String[] projection_query = {
        TvContract.Programs._ID,
        TvContract.Programs.COLUMN_TITLE,
        TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_SHORT_DESCRIPTION,
        TvContract.Programs.COLUMN_LONG_DESCRIPTION,
        TvContract.Programs.COLUMN_CONTENT_RATING,
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
    };

    Cursor mCursor_before = contentResolver.query(TvContract.Programs.CONTENT_URI,
        projection_query, null, null, null);

    if (mCursor_before == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");

    }
    else
    {
      Log.d(TAG, "before delete,total count now is : " + mCursor_before.getCount());
    }

    mCursor_before.close();

    String selection = "channel_id in (select _id from channels where substr(cast("
        + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA + " as varchar),7,5)=?)";
    String[] selectionArgs =
        new String[] {
            String.format("%05d", svl_id)
        };

    contentResolver.delete(TvContract.Programs.CONTENT_URI, selection, selectionArgs);
    Log.d(TAG, "delete  Channel Program by select args finished " + svl_id);

    // query afters test
    Cursor mCursor = contentResolver.query(TvContract.Programs.CONTENT_URI, projection_query, null,
        null, null);

    if (mCursor == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");

    }
    else
    {
      Log.d(TAG, "after delete,total count now is : " + mCursor.getCount());
    }

    mCursor.close();
  }

  public void deleteChannelProgramBySvlRecId(int svl_id, int svl_rec_id) {
    Log.d(TAG, "delete  Channel Program by select args svl id=" + svl_id + ",svl_rec_id="
        + svl_rec_id);
    // String selection = TvContract.Programs.COLUMN_CHANNEL_ID + " = ?";

    // query before test
    String[] projection_query = {
        TvContract.Programs._ID,
        TvContract.Programs.COLUMN_TITLE,
        TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_SHORT_DESCRIPTION,
        TvContract.Programs.COLUMN_LONG_DESCRIPTION,
        TvContract.Programs.COLUMN_CONTENT_RATING,
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
    };

    Cursor mCursor_before = contentResolver.query(TvContract.Programs.CONTENT_URI,
        projection_query, null, null, null);

    if (mCursor_before == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");

    }
    else
    {
      Log.d(TAG, "before delete,total count now is : " + mCursor_before.getCount());
    }

    mCursor_before.close();

    String selection = "channel_id in (select _id from channels where substr(cast("
        + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA + " as varchar),7,11)=?)";
    String[] selectionArgs =
        new String[] {
            String.format("%05d", svl_id) + "," + String.format("%05d", svl_rec_id)
        };

    contentResolver.delete(TvContract.Programs.CONTENT_URI, selection, selectionArgs);
    Log.d(TAG, "delete  Channel Program by select args finished " + svl_id + svl_rec_id);

    // query afters test
    Cursor mCursor = contentResolver.query(TvContract.Programs.CONTENT_URI, projection_query, null,
        null, null);

    if (mCursor == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");

    }
    else
    {
      Log.d(TAG, "after delete,total count now is : " + mCursor.getCount());
    }

    mCursor.close();
  }

  public void deleteChannelProgrambytime(long time) {
    Log.d(TAG, "delete  Channel Program by time" + time);

    // query before test
    String[] projection_query = {
        TvContract.Programs._ID,
        TvContract.Programs.COLUMN_TITLE,
        TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
        TvContract.Programs.COLUMN_SHORT_DESCRIPTION,
        TvContract.Programs.COLUMN_LONG_DESCRIPTION,
        TvContract.Programs.COLUMN_CONTENT_RATING,
        TvContract.Programs.COLUMN_INTERNAL_PROVIDER_DATA
    };

    Cursor mCursor_before = contentResolver.query(TvContract.Programs.CONTENT_URI,
        projection_query, null, null, null);

    if (mCursor_before == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");

    }
    else
    {
      Log.d(TAG, "before delete,total count now is : " + mCursor_before.getCount());
    }

    mCursor_before.close();

    String selection = TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS + "<?";//

    String[] selectionArgs = {
        Long.toString(time)
    };
    contentResolver.delete(TvContract.Programs.CONTENT_URI, selection, selectionArgs);
    Log.d(TAG, "delete  Channel Program by time " + time);

    // query afters test
    Cursor mCursor = contentResolver.query(TvContract.Programs.CONTENT_URI, projection_query, null,
        null, null);

    if (mCursor == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");

    }
    else
    {
      Log.d(TAG, "after delete,total count now is : " + mCursor.getCount());
    }

    mCursor.close();
  }

  private void broadcastIntentOut(int notifyType, int channel_id) {
    Log.d(TAG, "broadcastIntentOut " + notifyType + "channel_id" + channel_id);
    try {
      synchronized (ProgramPump.class)
      {
        if (service != null)
        {
          Intent intent = new Intent();
          // 1,curent;0,next;2,s event
          if (notifyType == 0)// next
          {
            intent.setAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF);

          } else if (notifyType == 1)// current
          {
            intent.setAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_PF);
          }
          else
          {
            intent.setAction(MtkTvIntentBase.MTK_INTENT_EVENT_UPDATE_ACTIVE_WIN);
          }

          Log.d(TAG, "broadcastIntentOut==>do broadcast ");
          intent.putExtra("channel_id", channel_id);

          // ActivityManagerNative.broadcastStickyIntent(intent, null, UserHandle.USER_ALL);
          service.sendBroadcast(intent);
        }
      }
    } catch (Exception e) {
      Log.d(TAG, "RemoteException:" + e.getMessage());
    }
  }

  // from svl channel id ==>tv api channel id
  private String channelIdTransfer(int channel_id) {
    long newId = (channel_id & 0xffffffffL);
    String[] projection = {
        TvContract.Channels._ID,
    };
    // String selection = TvContract.Channels.COLUMN_DISPLAY_NUMBER + " = ?";
    String selection = "substr(cast(" + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA
        + " as varchar),19,10)=?";

    String[] selectionArgs = {
        String.format("%010d", newId)
    };

    Cursor mCursor = contentResolver.query(TvContract.Channels.CONTENT_URI,
        projection, selection, selectionArgs, null);

    if (mCursor == null)
    {
      Log.d(TAG, "cursor not loaded ");
      throw new IllegalStateException("Cursor not loaded");
    }

    else
    {

      if (mCursor.getCount() < 1)
      {
        // just sync to pool
        Log.d(TAG, "cursor " + mCursor.getCount()
            + "can't find channel id in the table,a this event will be omited");
        mCursor.close();
        return null;
      }
      else
      {
        // long channelId = mCursor.getLong(0);
        mCursor.moveToFirst();
        int idIndex = mCursor.getColumnIndex(TvContract.Channels._ID);
        String idStr = mCursor.getString(idIndex);
        if (mCursor.getCount() == 1)
        {
          Log.d(TAG, "cursor " + mCursor.getCount() + "found one channel" + idIndex + "" + idStr);
        } else
        {
          Log.d(TAG, "cursor " + mCursor.getCount() + "found more channel" + idIndex + "" + idStr);
        }

        mCursor.close();
        return idStr;

      }

    }

  }

}

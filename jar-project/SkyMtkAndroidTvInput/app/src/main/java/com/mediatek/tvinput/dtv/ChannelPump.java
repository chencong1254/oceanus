package com.mediatek.tvinput.dtv;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;

import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import com.mediatek.twoworlds.tv.model.TvProviderChannelEventBase;
import com.mediatek.twoworlds.tv.model.TvProviderChannelInfoBase;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvInputSource;


import com.mediatek.tvinput.dtv.ProgramPump;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;

import android.media.tv.TvContract;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvStreamConfig;

import com.mediatek.tvinput.dtv.TunerInputService;
import com.mediatek.tvinput.Channel;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class ChannelPump {
    public  static      final String tvprovidrVer = "00100";
    public  static      final Map<Integer, TvProviderChannelInfoBase> mTvproviderMap = new HashMap<Integer, TvProviderChannelInfoBase>();
    public  static      final String TAG = "MtkTvInput[ChannelPump]";
    public  static      final String ServiceInputIdDtv = "com.mediatek.tvinput/.dtv.TunerInputService/HW0";
    public  static      final String ServiceInputIdAtv = "com.mediatek.tvinput/.dtv.TunerInputService/HW1";
    public  static      String ServiceInputIdDtv_ex = "com.mediatek.tvinput/.dtv.TunerInputService/HW0";
    public  static      String ServiceInputIdAtv_ex = "com.mediatek.tvinput/.dtv.TunerInputService/HW";
    private final String      marketRegion;
    public TunerInputService service;
    public ProgramPump pp;
    public  ContentResolver contentResolver;
  private final MtkTvInputSource mTvInputSource;

    public static int currentSourceIdx  = 0;    /* 0 is only one tv, 1 is dtv/atv */
    public static boolean isUsRegion    = false;  /* Indicate whether the region is us.*/
    public static int svlIdInProvider   = 1;     /* Indicate the svlId of channels in tvprovider. -1:empty, 0:antenna & cable, 1:antenna, 2:cable.*/

    public static final int SERVICE_TYPE_UNKNOWN = 0;    /* Reference the define of serviceType in SVL.*/
    public static final int SERVICE_TYPE_TV      = 1;
    public static final int SERVICE_TYPE_RADIO   = 2;
    public static final int SERVICE_TYPE_ISDB_DIGITAL_TV = 4;
    public static final int SERVICE_TYPE_ISDB_DIGITAL_AUDIO = 5;
    public static final int SERVICE_TYPE_ISDB_SPECIAL_VIDEO = 7;
    public static final int SERVICE_TYPE_ISDB_SPECIAL_AUDIO = 8;

    public static final int SVL_ID_ANTENNA  = 1;
    public static final int SVL_ID_CABLE    = 2;

    public ChannelPump(TunerInputService service, ProgramPump pp, ContentResolver contentResolver){
        this.service = service;
        this.pp = pp;
        this.contentResolver = contentResolver;
        marketRegion = SystemProperties.get("ro.mtk.system.marketregion");
    if ((marketRegion != null)
        && ((marketRegion.equals("cn")) || (marketRegion.equals("eu"))))
        {
            currentSourceIdx = 1;
            Log.d(TAG,"set currentSourceIdx = 1,marketRegion is " + marketRegion);
        }
        else
        {
            currentSourceIdx = 0;
            Log.d(TAG,"set currentSourceIdx = 0,marketRegion is " + marketRegion);
        }


        if ((marketRegion != null) && (marketRegion.equals("us")))
        {
            isUsRegion = true;
            Log.d(TAG, "Region is us, set isUsRegion = true");
        }
        else
        {
            isUsRegion = false;
            Log.d(TAG, "Region is not us, set isUsRegion = false");
        }

        mTvInputSource = MtkTvInputSource.getInstance();
        int sourceTotalCount = 0;
        sourceTotalCount = mTvInputSource.getInputSourceTotalNumber();
        if((ServiceInputIdAtv_ex.charAt(ServiceInputIdAtv_ex.length()-1) >= '0')
          &&(ServiceInputIdAtv_ex.charAt(ServiceInputIdAtv_ex.length()-1) <= '9'))
        {
            //do nothings
        }
        else
        {
            ServiceInputIdAtv_ex = ServiceInputIdAtv_ex + sourceTotalCount;
        }
    }

    public String getInputId(TunerInputService service, int mBrdcstType)
    {
        if (currentSourceIdx == 0)
        {
            if (mBrdcstType == 1)   /* anology channel*/
            {
                return ServiceInputIdAtv_ex;
            }
            else
            {
                return ServiceInputIdDtv_ex;
            }
        }
        else
        {
            if (mBrdcstType == 1)   /* anology channel*/
            {
                return ServiceInputIdAtv;
            }
            else
            {
                return ServiceInputIdDtv;
            }
        }
    }

    public String serviceTypeToString(int mServiceType)
    {
        if ((mServiceType == SERVICE_TYPE_TV)||(mServiceType == SERVICE_TYPE_ISDB_DIGITAL_TV)
            ||(mServiceType == SERVICE_TYPE_ISDB_SPECIAL_VIDEO))    /* SVL_SERVICE_TYPE_TV */
        {
            return TvContract.Channels.SERVICE_TYPE_AUDIO_VIDEO;
        }
        if ((mServiceType == SERVICE_TYPE_RADIO)||(mServiceType == SERVICE_TYPE_ISDB_DIGITAL_AUDIO)
            ||(mServiceType == SERVICE_TYPE_ISDB_SPECIAL_AUDIO))    /* SVL_SERVICE_TYPE_RADIO */
        {
            return TvContract.Channels.SERVICE_TYPE_AUDIO;
        }
        return TvContract.Channels.SERVICE_TYPE_OTHER;
    }

    /* stored this type information in tv.db based on Google defined.*/
    public String GoogleBrdcstTypeToString(int mGoogleBrdcstType)
    {
        switch(mGoogleBrdcstType)
        {
            case 0:
                return TvContract.Channels.TYPE_OTHER;

            /* Analog channel.*/
            case 1:
                return TvContract.Channels.TYPE_NTSC;
            case 2:
                return TvContract.Channels.TYPE_PAL;
            case 3:
                return TvContract.Channels.TYPE_SECAM;

            /* DVB channel.*/
            case 4:
                return TvContract.Channels.TYPE_DVB_T;
            case 5:
                return TvContract.Channels.TYPE_DVB_T2;
            case 6:
                return TvContract.Channels.TYPE_DVB_S;
            case 7:
                return TvContract.Channels.TYPE_DVB_S2;
            case 8:
                return TvContract.Channels.TYPE_DVB_C;
            case 9:
                return TvContract.Channels.TYPE_DVB_C2;
            case 10:
                return TvContract.Channels.TYPE_DVB_H;
            case 11:
                return TvContract.Channels.TYPE_DVB_SH;

            /* ATSC channel.*/
            case 12:
                return TvContract.Channels.TYPE_ATSC_T;
            case 13:
                return TvContract.Channels.TYPE_ATSC_C;
            case 14:
                return TvContract.Channels.TYPE_ATSC_M_H;

            /* ISDB channel.*/
            case 15:
                return TvContract.Channels.TYPE_ISDB_T;
            case 16:
                return TvContract.Channels.TYPE_ISDB_TB;
            case 17:
                return TvContract.Channels.TYPE_ISDB_S;
            case 18:
                return TvContract.Channels.TYPE_ISDB_C;

            case 19:
                return TvContract.Channels.TYPE_1SEG;

            /* DTMB channel.*/
            case 20:
                return TvContract.Channels.TYPE_DTMB;

            case 21:
                return TvContract.Channels.TYPE_CMMB;
            case 22:
                return TvContract.Channels.TYPE_T_DMB;
            case 23:
                return TvContract.Channels.TYPE_S_DMB;

            default:
                return TvContract.Channels.TYPE_OTHER;
        }
    }

    public Cursor readTvprovider(ContentResolver contentResolver)
    {
        Log.d(TAG,"readTvprovider Begin:");

        String[] projection = {
            TvContract.Channels._ID,
            TvContract.Channels.COLUMN_INPUT_ID,
            TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
            TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
            TvContract.Channels.COLUMN_SERVICE_ID,
            TvContract.Channels.COLUMN_DISPLAY_NUMBER,
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
            TvContract.Channels.COLUMN_SERVICE_TYPE};

        String selection = TvContract.Channels.COLUMN_INPUT_ID + " = ? OR " + TvContract.Channels.COLUMN_INPUT_ID + " = ? OR "
                         + TvContract.Channels.COLUMN_INPUT_ID + " = ? OR " + TvContract.Channels.COLUMN_INPUT_ID + " = ?";
        String[] selectionArgs = { ServiceInputIdDtv, ServiceInputIdAtv, ServiceInputIdAtv_ex, ServiceInputIdDtv_ex };
        String order = null;

        Log.d(TAG,"readTvprovider End");
        return contentResolver.query(TvContract.Channels.CONTENT_URI, projection, selection, selectionArgs, order);
    }

    public void insertTvprovider(Map<Integer, TvProviderChannelInfoBase> mChannelMap,ContentResolver contentResolver)
    {
        Log.d(TAG,"insertTvprovider begin");

        Iterator it = mChannelMap.keySet().iterator();
        while(it.hasNext())
        {
            int key = (Integer)it.next();
            TvProviderChannelInfoBase t = new TvProviderChannelInfoBase();
            t = mChannelMap.get(key);

            /* Fake channel on us is: browsable==0 && brdcst_type==1(NTSC) .Fake channel on us don't sync.*/
            if ((isUsRegion == false)||(t.getBrowsableMask() != 0)||(t.getGoogBrdcstType() != 1))
            {
                Channel c = new Channel.Builder()
                    .setType(GoogleBrdcstTypeToString(t.getGoogBrdcstType()))
                    .setInputId(getInputId(service, t.getbroadcastType()))
                    .setTransportStreamId(t.getTransportStreamId())
                    .setProgramNumber(t.getProgramNumber())
                    .setOriginalNetworkId(t.getOriginalNetworkId())
                    .setDisplayNumber(t.getChannelNumber())
                    .setDisplayName(t.getDisplayName())
                    .setBrowsable(t.getBrowsableMask() == 1)
                    .setLocked(t.getLockedMask() == 1)
                    .setServiceType(serviceTypeToString(t.getServiceType()))
                    .setData(
                        ( tvprovidrVer + ","
                        + String.format("%05d", t.getSvlId()) + ","
                        + String.format("%05d", t.getSvlRecId())+ ","
                        + String.format("%010d", (0xFFFFFFFFL & t.getChannelId())) + ","
                        + String.format("%010d", (0xFFFFFFFFL & t.getHashcode()))).getBytes()
                        ).build();
                contentResolver.insert(TvContract.Channels.CONTENT_URI, c.toContentValues());
                Log.d(TAG, "ServiceName " + getInputId(service, t.getbroadcastType())
                    + " TYPE " + t.getbroadcastType()
                    + " GoogleTYPE " + GoogleBrdcstTypeToString(t.getGoogBrdcstType())
                    + " ORIGINAL_NETWORK_ID " + t.getOriginalNetworkId()
                    + " TRANSPORT_STREAM_ID " + t.getTransportStreamId()
                    + " PROGRAM_NUMBER " + t.getProgramNumber()
                    + " DISPLAY_NUMBER " + t.getChannelNumber()
                    + " DISPLAY_NAME " + t.getDisplayName()
                    + " BrowsableMask  " + t.getBrowsableMask()
                    + " LockedMask  " + t.getLockedMask()
                    + " SERVICE_TYPE " + serviceTypeToString(t.getServiceType())
                    + " isUsRegion " + isUsRegion);
                Log.d(TAG,  tvprovidrVer + ","
                    + String.format("%05d", t.getSvlId()) + ","
                    + String.format("%05d", t.getSvlRecId()) + ","
                    + String.format("%010d", (0xFFFFFFFFL & t.getChannelId()))+ ","
                    + String.format("%010d", (0xFFFFFFFFL & t.getHashcode())));
            }
        }
        Log.d(TAG,"insertTvprovider end");
    }

    public Map<Integer, TvProviderChannelInfoBase> createHashMap(List<TvProviderChannelInfoBase> t)
    {
        Log.d(TAG,"createHashMap begin : ");

        mTvproviderMap.clear();     /* clear first */
        if (t != null)
        {
            Log.d(TAG,"createHashMap: t.size = " + t.size());
            for(int i = 0;i<t.size();i++)
            {
                Log.d(TAG,"t.get("+i+").getSvlId()"+t.get(i).getSvlId()
                    +"t.get("+i+").getSvlRecId()"+t.get(i).getSvlRecId()
                    +"t.get("+i+").getbroadcastType()"+t.get(i).getbroadcastType()
                    +"GoogleType: "+GoogleBrdcstTypeToString(t.get(i).getGoogBrdcstType())
                    +"t.get("+i+").getOriginalNetworkId()"+t.get(i).getOriginalNetworkId()
                    +"t.get("+i+").getTransportStreamId()"+t.get(i).getTransportStreamId()
                    +"t.get("+i+").getProgramNumber()"+t.get(i).getProgramNumber()
                    +"t.get("+i+").getChannelNumber()"+t.get(i).getChannelNumber()
                    +"t.get("+i+").getDisplayName()"+t.get(i).getDisplayName()
                    +"t.get("+i+").getBrowsableMask()"+t.get(i).getBrowsableMask()
                    +"t.get("+i+").getLockedMask()"+t.get(i).getLockedMask()
                    +"t.get("+i+").getServiceType()"+t.get(i).getServiceType());
                mTvproviderMap.put((t.get(i).getSvlId()<<16)+t.get(i).getSvlRecId(), t.get(i));
                Log.d(TAG,"key ="+((t.get(i).getSvlId()<<16)+t.get(i).getSvlRecId()));
            }
        }
        Log.d(TAG," createHashMap end .");
        return mTvproviderMap;
    }

    void mergeSyncHandle(Map<Integer, TvProviderChannelInfoBase> mChannelMap, ContentResolver contentResolver, int svlId)
    {
        /* mChannelMap: channels from svl.*/
        Cursor mCursor = readTvprovider(contentResolver);   /* get channel from tvprovider.*/
        if (mCursor == null)
        {
            Log.d(TAG,"Error: Cursor(tvprovider) not loaded.");
            throw new IllegalStateException("Cursor not loaded");
        }
        else
        {
            int oldPosition = mCursor.getPosition();
            mCursor.moveToFirst();
            do
            {
                if (mCursor.getCount() < 1)
                {
                    Log.d(TAG,"Break: cursor(tvprovider).getCount()=" + mCursor.getCount());
                    break;
                }
                byte[] mData = mCursor.getBlob(mCursor.getColumnIndex(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA));
                String mBlob = new String(mData);
                String value[] = mBlob.split(",");
                if (value.length != 5)      /* now there are 5 elements in COLUMN_INTERNAL_PROVIDER_DATA.*/
                {
                    break ;
                }

                int mSvlId = Integer.parseInt(value[1]);
                int mSvlRecId = Integer.parseInt(value[2]);
                long mHashcode = Long.parseLong(value[4]);
                int mKey = (mSvlId<<16) + mSvlRecId;

                TvProviderChannelInfoBase t = new TvProviderChannelInfoBase();
                t = mChannelMap.get(mKey);
                if(t != null)
                {
                    /* The channel exsits in svl and tvprovider.*/
                    if( (long)(0xFFFFFFFFL & t.getHashcode()) == mHashcode)
                    {
                        mChannelMap.remove(mKey);
                    }
                    else
                    {
                        /* Fake channel on us is: browsable==0 && brdcst_type==1(NTSC) .Fake channel on us don't sync.*/
                        if ((isUsRegion == false)||(t.getBrowsableMask() != 0)||(t.getGoogBrdcstType() != 1))
                        {
                            ContentValues values = new ContentValues();
                            values.put(TvContract.Channels.COLUMN_INPUT_ID,getInputId(service, t.getbroadcastType()));
                            values.put(TvContract.Channels.COLUMN_TYPE,GoogleBrdcstTypeToString(t.getGoogBrdcstType()));
                            values.put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, t.getOriginalNetworkId());
                            values.put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, t.getTransportStreamId());
                            values.put(TvContract.Channels.COLUMN_SERVICE_ID, t.getProgramNumber());
                            values.put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, t.getChannelNumber());
                            values.put(TvContract.Channels.COLUMN_DISPLAY_NAME, t.getDisplayName());
                            values.put(TvContract.Channels.COLUMN_BROWSABLE, t.getBrowsableMask());
                            values.put(TvContract.Channels.COLUMN_LOCKED, t.getLockedMask());
                            values.put(TvContract.Channels.COLUMN_SERVICE_TYPE,serviceTypeToString(t.getServiceType()));
                            values.put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
                                (tvprovidrVer + ","
                                + String.format("%05d", mSvlId) + ","
                                + String.format("%05d", mSvlRecId) + ","
                                + String.format("%010d", (0xFFFFFFFFL & t.getChannelId())) + ","
                                + String.format("%010d", (0xFFFFFFFFL & t.getHashcode()))).getBytes());

                            String where = TvContract.Channels._ID + " = ?";
                            String[] selectionArgs = { mCursor.getString(mCursor.getColumnIndex(TvContract.Channels._ID)) };

                            contentResolver.update(TvContract.Channels.CONTENT_URI, values, where, selectionArgs);
                            Log.d(TAG, "ServiceName(InputId) " + getInputId(service, t.getbroadcastType())
                                + " TYPE " + Integer.toString(t.getbroadcastType())
                                + " GoogleTYPE " + GoogleBrdcstTypeToString(t.getGoogBrdcstType())
                                + " ORIGINAL_NETWORK_ID " + t.getOriginalNetworkId()
                                + " TRANSPORT_STREAM_ID " + t.getTransportStreamId()
                                + " PROGRAM_NUMBER " + t.getProgramNumber()
                                + " DISPLAY_NUMBER " + t.getChannelNumber()
                                + " DISPLAY_NAME " + t.getDisplayName()
                                + " BrowsableMask " + t.getBrowsableMask()
                                + " LockedMask " + t.getLockedMask()
                                + " SERVICE_TYPE " + t.getServiceType()
                                + " isUsRegion " + isUsRegion);
                            Log.d(TAG,tvprovidrVer + ","
                                + String.format("%05d", mSvlId) + ","
                                + String.format("%05d", mSvlRecId) + ","
                                + String.format("%010d", (0xFFFFFFFFL & t.getChannelId()))+ ","
                                + String.format("%010d", (0xFFFFFFFFL & t.getHashcode())));
                        }
                        else
                        {
                            /* This is fake channel in us region, don't sync to tvprovider.*/
                            Log.d(TAG, "Don't sync this channel. mSvlId:" + mSvlId
                                + ", mSvlRecId:" + mSvlRecId
                                + ", ChannelId:" + t.getChannelId()
                                + ", isUsRegion:" + isUsRegion
                                + ", BrowsableMask:" + t.getBrowsableMask()
                                + ", GoogBrdcstType" + t.getGoogBrdcstType());
                        }
                        mChannelMap.remove(mKey);   /* had been handled, remove from channelMap */
                    }
                }
                else
                {
                    /* The channel exsits in tvprovider, but not in svl. delete it from tvprovider.*/
                    String selection = TvContract.Channels._ID + " = ?";
                    String[] selectionArgs = { mCursor.getString(mCursor.getColumnIndex(TvContract.Channels._ID))};
                    contentResolver.delete(TvContract.Channels.CONTENT_URI,selection, selectionArgs);
                    pp.deleteSpecifyChannelProgram(mCursor.getColumnIndex(TvContract.Channels._ID));
                }
            } while (mCursor.moveToNext());

            mCursor.moveToPosition(oldPosition);
            mCursor.close();
            insertTvprovider(mChannelMap , contentResolver);
            setSvlIdInTvprovider(svlId);    /* Record the svlId in tvprovider.*/
        }
    }

    public void clearTvprovider()
    {
        ArrayList<ContentProviderOperation> opsdelete = new ArrayList<ContentProviderOperation>();

        long mTime1,mTime2,mTime3,mTime4,aa,bb,cc,dd;
        mTime1 = System.currentTimeMillis();
        Log.d(TAG,"clearTvprovider Begin :[svlIdInProvider:"+svlIdInProvider+" ] [time:"+mTime1+" ]");

        if (svlIdInProvider == 0)
        {
            pp.deleteChannelProgram(SVL_ID_ANTENNA);
            pp.deleteChannelProgram(SVL_ID_CABLE);
        }
        else
        {
            pp.deleteChannelProgram(svlIdInProvider);
        }

        mTime2 = System.currentTimeMillis();
        aa = mTime2 - mTime1;
        Log.d(TAG,"pp deleteChannelProgram svlId:" + svlIdInProvider+" [time:"+mTime2+" ]"+ " [used time:"+aa+" ]");

        opsdelete.add(ContentProviderOperation.newDelete(TvContract.Channels.CONTENT_URI)
            .withSelection(TvContract.Channels.COLUMN_INPUT_ID + " = ? OR " + TvContract.Channels.COLUMN_INPUT_ID + " = ? OR "
            + TvContract.Channels.COLUMN_INPUT_ID + " = ? OR " + TvContract.Channels.COLUMN_INPUT_ID + " = ? "
            ,new String[]{ServiceInputIdDtv, ServiceInputIdAtv,ServiceInputIdAtv_ex,ServiceInputIdDtv_ex})
            .build());

        mTime3 = System.currentTimeMillis();
        bb = mTime3 - mTime2;
        Log.d(TAG,"add newdelete svlId:" + svlIdInProvider+" [time:"+mTime3+" ]"+ " [used time:"+bb+" ]");

        try{
            contentResolver.applyBatch(TvContract.AUTHORITY,opsdelete);
        }catch(RemoteException e){

        }catch(OperationApplicationException e){

        }

        mTime4 = System.currentTimeMillis();
        cc = mTime4 - mTime3;
        dd = mTime4 - mTime1;
        Log.d(TAG,"clearTvprovider End :applyBatch delete svlId:" + svlIdInProvider+" [time:"+mTime4+" ]"+ " [used time:"+cc+" ] [Total Used Time:"+dd+" ]");
    }

    public void applyListToTvprovider(List<TvProviderChannelInfoBase> channelInfos)
    {
        /* applybatch the channel List to tvprovider.*/
        int i = 0;
        long mTime1,mTime2,mTime3,aa,bb,cc;
        if (channelInfos == null)
        {
            Log.d(TAG," ERROR applyListToTvprovider: channelInfos is null");
            return ;
        }

        mTime1 = System.currentTimeMillis();
        Log.d(TAG," applyListToTvprovider Begin :start [time:"+mTime1+" ] [count:" + channelInfos.size() + " ].");

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        Log.d(TAG,"#### applyBatch add, isUsRegion: " + isUsRegion);
        for(i = 0;i < channelInfos.size();i++)
        {
            TvProviderChannelInfoBase a = channelInfos.get(i);
            /* Fake channel on us is: browsable==0 && brdcst_type==1(NTSC) .Fake channel on us don't sync.*/
            if ((isUsRegion == false)||(a.getBrowsableMask() != 0)||(a.getGoogBrdcstType() != 1))
            {
                ops.add(ContentProviderOperation.newInsert(TvContract.Channels.CONTENT_URI)
                    .withValue(TvContract.Channels.COLUMN_TYPE,GoogleBrdcstTypeToString(a.getGoogBrdcstType()))
                    .withValue(TvContract.Channels.COLUMN_INPUT_ID, getInputId(service, a.getbroadcastType()))
                    .withValue(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, a.getTransportStreamId())
                    .withValue(TvContract.Channels.COLUMN_SERVICE_ID, a.getProgramNumber())
                    .withValue(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, a.getOriginalNetworkId())
                    .withValue(TvContract.Channels.COLUMN_DISPLAY_NUMBER, a.getChannelNumber())
                    .withValue(TvContract.Channels.COLUMN_DISPLAY_NAME, a.getDisplayName())
                    .withValue(TvContract.Channels.COLUMN_BROWSABLE, a.getBrowsableMask())
                    .withValue(TvContract.Channels.COLUMN_LOCKED, a.getLockedMask())
                    .withValue(TvContract.Channels.COLUMN_SERVICE_TYPE,serviceTypeToString(a.getServiceType()))
                    .withValue(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,
                        (tvprovidrVer + "," + String.format("%05d", a.getSvlId()) + ","
                        + String.format("%05d", a.getSvlRecId()) + ","
                        + String.format("%010d", (0xFFFFFFFFL & a.getChannelId())) + ","
                        + String.format("%010d", (0xFFFFFFFFL & a.getHashcode()))).getBytes()).build());

                /* The max number of applyBatch(insert) is about 1250.
                   if the number is greater than 1250, the channels can't be insert to tvprovider.
                   But the max number of applyBatch(delete) is greater than 5000.
                */
                if ((i != 0)&&(i%1000 == 0))
                {
                    Log.d(TAG, "###### applyBatch. i = " + i);
                    try {
                        contentResolver.applyBatch(TvContract.AUTHORITY, ops);
                    } catch (RemoteException e) {

                    } catch (OperationApplicationException e) {

                    }
                    ops.clear();    /* clear */
                }
            }
        }
        mTime2 = System.currentTimeMillis();
        aa=mTime2-mTime1;
        Log.d(TAG,"applyBatch channelList to tvprovider, [time:"+mTime2+" ]"+" [used time:"+aa+" ]");
        try{
            contentResolver.applyBatch(TvContract.AUTHORITY,ops);
        }catch(RemoteException e){

        }catch(OperationApplicationException e){

        }

        mTime3 = System.currentTimeMillis();
        bb=mTime3-mTime2;
        cc=mTime3-mTime1;
        Log.d(TAG," applyListToTvprovider(i="+i+") End, [End time:"+mTime3+" ], [used time:"+bb+" ], [ALL USED TIME:"+cc+" ]");
    }

    /*
      Note:the value of svlId.
        >= 1: the data in tvprovider is based on svlId.
         = 0: the data in tvprovider are antenna and cable.
        = -1: the tvprovider is empty.
    */
    public static void setSvlIdInTvprovider(int svlId)
    {
        svlIdInProvider = svlId;
        Log.d(TAG," setSvlIdTvprovider: [ Record svlIdInProvider=" + svlIdInProvider + " ].");
    }

    public void mergeSync(ContentResolver contentResolver , int svlId)
    {
        Log.d(TAG," mergeSync: svlId=" + svlId + ", do nothing...");
    }

    public void resetSync(int svlId , ContentResolver contentResolver)
    {
        Log.d(TAG," resetSync: svlId=" + svlId + ", do nothing...");
    }

    public void active()
    {
        Log.d(TAG," active: do nothing...");
    }

    public void deactive()
    {
        Log.d(TAG," deactive: do nothing...");
    }

}



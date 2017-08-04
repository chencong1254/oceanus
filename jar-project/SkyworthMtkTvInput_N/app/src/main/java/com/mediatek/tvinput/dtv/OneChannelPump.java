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

import com.mediatek.tvinput.dtv.ProgramPump;
import com.mediatek.tvinput.dtv.ChannelPump;


class OneChannelPump extends ChannelPump{
    public static final String TAG = "MtkTvInput[OneChannelPump]";

    public OneChannelPump(TunerInputService service, ProgramPump pp, ContentResolver contentResolver){
        super(service, pp, contentResolver);
    }
    
    @Override
    public void mergeSync(ContentResolver contentResolver , int svlId) 
    {
        Log.d(TAG," mergeSync begin [ svlId=" + svlId + " ] [current svlIdInProvider=" + svlIdInProvider + " ] :");
        
        List<TvProviderChannelInfoBase> t = MtkTvChannelList.getInstance().getTvproviderOcl();
        Map<Integer, TvProviderChannelInfoBase> mChannelMap = createHashMap(t);   /* Get channels from svl.*/
        mergeSyncHandle(mChannelMap, contentResolver, 0);       /* 0 : Antenna and Cable.*/
        
        Log.d(TAG," mergeSync end: [ svlId=" + svlId + " ] [current svlIdInProvider=" + svlIdInProvider + " ].");
    }
    
    @Override
    public void resetSync(int svlId , ContentResolver contentResolver) 
    {
        long time1,time2,time3,aa,bb,cc;
        time1 = System.currentTimeMillis();
        Log.d(TAG," resetSync Begin :[svlId:"+svlId+" ] [time:"+time1+" ]");
        
        clearTvprovider();
        List<TvProviderChannelInfoBase> t = MtkTvChannelList.getInstance().getTvproviderOcl();
        
        time2 = System.currentTimeMillis();
        aa=time2-time1;
        Log.d(TAG,"channel sync from linux: [svlId: " + svlId+"] [time: "+time2+"]"+"[used time: "+aa+"] [count: "+t.size()+"] ");

        applyListToTvprovider(t);
        setSvlIdInTvprovider(0);    /* record the svlId in tvprovider.*/
        
        time3 = System.currentTimeMillis();
        bb=time3-time2;
        cc=time3-time1;
        Log.d(TAG," resetSync End : [svlId: " + svlId+"] [time: "+time3+"]"+"[used time: "+bb+"] [All used time: "+cc+"] ");
    }
    
    public void active()
    {
        Log.d(TAG,"active start: svlIdInProvider = " + svlIdInProvider);
        
        List<TvProviderChannelInfoBase> t;
        t = MtkTvChannelList.getInstance().getTvproviderOcl();
        applyListToTvprovider(t);
        setSvlIdInTvprovider(0);  /* record the svlId in tvprovider:antenna and cable.*/
        
        Log.d(TAG,"active end: svlIdInProvider = " + svlIdInProvider);
    }

    public void deactive()
    {
        Log.d(TAG,"deactive start: svlIdInProvider = " + svlIdInProvider);
        
        clearTvprovider();
        setSvlIdInTvprovider(-1);    /* record the svlId in tvprovider. -1 is empty.*/
        
        Log.d(TAG,"deactive end: svlIdInProvider = " + svlIdInProvider);
    }
}



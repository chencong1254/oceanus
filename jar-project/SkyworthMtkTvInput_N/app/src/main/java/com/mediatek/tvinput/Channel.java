
package com.mediatek.tvinput;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A convenience class to create and insert channel entries into the database.
 */
public final class Channel {
  private static final String TAG = "Channel";

  public static final long INVALID_ID = -1;

  /** ID of this channel. Matches to BaseColumns._ID. */
  private long mId;

  private String mInputId;
  private String mType;
  private int mOriginalNetworkId;
  private int mTransportStreamId;
  private int mProgramNumber;
  private String mDisplayNumber;
  private String mDisplayName;
  private String mDescription;
  private boolean mIsBrowsable;
  private boolean mIsLocked;
  private byte[] mData;
  private String mServiceType;

  private boolean mIsLogoLoaded;
  private LoadLogoTask mLoadLogoTask;
  private Bitmap mLogo;

  public interface LoadLogoCallback {
    void onLoadLogoFinished(Channel channel, Bitmap logo);
  }

  private final List<LoadLogoCallback> mPendingLoadLogoCallbacks =
      new ArrayList<LoadLogoCallback>();

  public static Channel fromCursor(Cursor cursor) {
    Channel channel = new Channel();
    int index = cursor.getColumnIndex(TvContract.Channels._ID);
    if (index >= 0) {
      channel.mId = cursor.getLong(index);
    } else {
      channel.mId = INVALID_ID;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_INPUT_ID);
    if (index >= 0) {
      channel.mInputId = cursor.getString(index);
    } else {
      channel.mInputId = "inputId";
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_TYPE);
    if (index >= 0) {
      channel.mType = cursor.getString(index);
    } else {
      channel.mType = "unknow";
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID);
    if (index >= 0) {
      channel.mTransportStreamId = cursor.getInt(index);
    } else {
      channel.mTransportStreamId = 0;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_SERVICE_ID);
    if (index >= 0) {
      channel.mProgramNumber = cursor.getInt(index);
    } else {
      channel.mProgramNumber = 0;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID);
    if (index >= 0) {
      channel.mOriginalNetworkId = cursor.getInt(index);
    } else {
      channel.mOriginalNetworkId = 0;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NUMBER);
    if (index >= 0) {
      channel.mDisplayNumber = cursor.getString(index);
    } else {
      channel.mDisplayNumber = "0";
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NAME);
    if (index >= 0) {
      channel.mDisplayName = cursor.getString(index);
    } else {
      channel.mDisplayName = "name";
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_DESCRIPTION);
    if (index >= 0) {
      channel.mDescription = cursor.getString(index);
    } else {
      channel.mDescription = "description";
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_BROWSABLE);
    if (index >= 0) {
      channel.mIsBrowsable = cursor.getInt(index) == 1;
    } else {
      channel.mIsBrowsable = true;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_LOCKED);
    if (index >= 0) {
      channel.mIsLocked = cursor.getInt(index) == 1;
    } else {
      channel.mIsLocked = false;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA);
    if (index >= 0) {
      channel.mData = cursor.getBlob(index);
    } else {
      channel.mData = null;
    }

    index = cursor.getColumnIndex(TvContract.Channels.COLUMN_SERVICE_TYPE);
    if (index >= 0) {
      channel.mServiceType = cursor.getString(index);
    } else {
      channel.mServiceType = TvContract.Channels.SERVICE_TYPE_OTHER;
    }

    return channel;
  }

  private Channel() {
    // Do nothing.
  }

  public long getId() {
    return mId;
  }

  public String getInputId() {
    return mInputId;
  }

  public String getType() {
    return mType;
  }

  public int getOriginalNetworkId() {
    return mOriginalNetworkId;
  }

  public int getTransportStreamId() {
    return mTransportStreamId;
  }

  public int getProgramNumber() {
    return mProgramNumber;
  }

  public String getDisplayNumber() {
    return mDisplayNumber;
  }

  public String getDisplayName() {
    return mDisplayName;
  }

  public String getDescription() {
    return mDescription;
  }

  public boolean isBrowsable() {
    return mIsBrowsable;
  }

  public boolean isLocked() {
    return mIsLocked;
  }

  public void setDescription(String description) {
    mDescription = description;
  }

  public void setBrowsable(boolean browsable) {
    mIsBrowsable = browsable;
  }

  public void setLocked(boolean lock) {
    mIsLocked = lock;
  }

  public byte[] getData() {
    return mData;
  }

  public String getServiceName() {
    return mServiceType;
  }

  public ContentValues toContentValues() {
    ContentValues values = new ContentValues();
    values.put(TvContract.Channels.COLUMN_INPUT_ID, mInputId);
    values.put(TvContract.Channels.COLUMN_TYPE, mType);
    values.put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, mOriginalNetworkId);
    values.put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, mTransportStreamId);
    values.put(TvContract.Channels.COLUMN_SERVICE_ID, mProgramNumber);
    values.put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, mDisplayNumber);
    values.put(TvContract.Channels.COLUMN_DISPLAY_NAME, mDisplayName);
    values.put(TvContract.Channels.COLUMN_DESCRIPTION, mDescription);
    values.put(TvContract.Channels.COLUMN_BROWSABLE, mIsBrowsable ? 1 : 0);
    values.put(TvContract.Channels.COLUMN_LOCKED, mIsLocked ? 1 : 0);
    values.put(TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA, mData);
    values.put(TvContract.Channels.COLUMN_SERVICE_TYPE, mServiceType);
    return values;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Channel{")
        .append("id=").append(mId)
        .append(", inputId=").append(mInputId)
        .append(", type=").append(mType)
        .append(", originalNetworkId=").append(mOriginalNetworkId)
        .append(", transportStreamId=").append(mTransportStreamId)
        .append(", programNumber=").append(mProgramNumber)
        .append(", displayNumber=").append(mDisplayNumber)
        .append(", displayName=").append(mDisplayName)
        .append(", description=").append(mDescription)
        .append(", browsable=").append(mIsBrowsable)
        .append(", lock=").append(mIsLocked)
        .append(", data=").append(mData)
        .append("}")
        .append(", serviceName=").append(mServiceType)
        .toString();
  }

  public void copyFrom(Channel other) {
    if (this == other) {
      return;
    }
    mId = other.mId;
    mInputId = other.mInputId;
    mType = other.mType;
    mTransportStreamId = other.mTransportStreamId;
    mProgramNumber = other.mProgramNumber;
    mOriginalNetworkId = other.mOriginalNetworkId;
    mDisplayNumber = other.mDisplayNumber;
    mDisplayName = other.mDisplayName;
    mDescription = other.mDescription;
    mIsBrowsable = other.mIsBrowsable;
    mIsLocked = other.mIsLocked;
    mData = other.mData;
    mServiceType = other.mServiceType;
  }

  public static final class Builder {
    private final Channel mChannel;

    public Builder() {
      mChannel = new Channel();
      // Fill initial data.
      mChannel.mId = INVALID_ID;
      mChannel.mInputId = "inputId";
      mChannel.mType = "unknow";
      mChannel.mTransportStreamId = 0;
      mChannel.mProgramNumber = 0;
      mChannel.mOriginalNetworkId = 0;
      mChannel.mDisplayNumber = "0";
      mChannel.mDisplayName = "name";
      mChannel.mDescription = "description";
      mChannel.mIsBrowsable = true;
      mChannel.mIsLocked = false;
      mChannel.mData = null;
      mChannel.mServiceType = TvContract.Channels.SERVICE_TYPE_OTHER;
    }

    public Builder(Channel other) {
      mChannel = new Channel();
      mChannel.copyFrom(other);
    }

    public Builder setId(long id) {
      mChannel.mId = id;
      return this;
    }

    public Builder setInputId(String inputId) {
      mChannel.mInputId = inputId;
      return this;
    }

    public Builder setType(String type) {
      mChannel.mType = type;
      return this;
    }

    public Builder setTransportStreamId(int transportStreamId) {
      mChannel.mTransportStreamId = transportStreamId;
      return this;
    }

    public Builder setProgramNumber(int programNumber) {
      mChannel.mProgramNumber = programNumber;
      return this;
    }

    public Builder setOriginalNetworkId(int originalNetworkId) {
      mChannel.mOriginalNetworkId = originalNetworkId;
      return this;
    }

    public Builder setDisplayNumber(String displayNumber) {
      mChannel.mDisplayNumber = displayNumber;
      return this;
    }

    public Builder setDisplayName(String displayName) {
      mChannel.mDisplayName = displayName;
      return this;
    }

    public Builder setDescription(String description) {
      mChannel.mDescription = description;
      return this;
    }

    public Builder setBrowsable(boolean browsable) {
      mChannel.mIsBrowsable = browsable;
      return this;
    }

    public Builder setLocked(boolean lock) {
      mChannel.mIsLocked = lock;
      return this;
    }

    public Builder setData(byte[] data) {
      mChannel.mData = data;
      return this;
    }

    public Builder setServiceType(String serviceType) {
      mChannel.mServiceType = serviceType;
      return this;
    }

    public Channel build() {
      return mChannel;
    }
  }

  public boolean isLogoLoaded() {
    return mIsLogoLoaded;
  }

  public boolean isLogoLoading() {
    return mLoadLogoTask != null;
  }

  public Bitmap getLogo() {
    return mLogo;
  }

  // Assumes call from UI thread.
  public void loadLogo(Context context, LoadLogoCallback callback) {
    if (isLogoLoaded()) {
      callback.onLoadLogoFinished(this, mLogo);
    } else {
      mPendingLoadLogoCallbacks.add(callback);
      if (!isLogoLoading()) {
        mLoadLogoTask = new LoadLogoTask(context);
        mLoadLogoTask.execute();
      }
    }
  }

  // Assumes call from UI thread.
  private void setLogo(Bitmap logo) {
    mIsLogoLoaded = true;
    if (isLogoLoading()) {
      mLoadLogoTask.cancel(true);
      mLoadLogoTask = null;
    }
    mLogo = logo;

    for (LoadLogoCallback callback : mPendingLoadLogoCallbacks) {
      callback.onLoadLogoFinished(this, logo);
    }
    mPendingLoadLogoCallbacks.clear();
  }

  private class LoadLogoTask extends AsyncTask<Void, Void, Bitmap> {
    private final Context mContext;

    LoadLogoTask(Context context) {
      mContext = context;
    }

    @Override
    public Bitmap doInBackground(Void... params) {
      InputStream is = null;
      try {
        is = mContext.getContentResolver().openInputStream(
            TvContract.buildChannelLogoUri(mId));
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        if (bitmap == null) {
          Log.e(TAG, "Failed to decode logo image for " + Channel.this);
        }
        return bitmap;
      } catch (FileNotFoundException e) {
        // Logo may not exist.
        Log.i(TAG, "Logo not found for " + Channel.this);
        return null;
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
            // Does nothing.
          }
        }
      }
    }

    @Override
    public void onPostExecute(Bitmap logo) {
      if (isCancelled()) {
        Log.w(TAG, "Load logo canceled for " + Channel.this);
        return;
      }
      Log.i(TAG, "Loaded logo for " + Channel.this + ": " + logo);
      mLoadLogoTask = null;
      setLogo(logo);
    }
  }
}

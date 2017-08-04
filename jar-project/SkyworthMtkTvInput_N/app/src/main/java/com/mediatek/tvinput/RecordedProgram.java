/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediatek.tvinput;

import android.content.ContentValues;
import android.media.tv.TvContentRating;
import android.media.tv.TvContract;

/**
 * A convenience class to create and insert RecordedProgram information entries into the database.
 */
public final class RecordedProgram {
  private static final String TAG = "RecordedProgram";
  public static final long INVALID_ID = -1;

  private String mInputId;
  private long mChannelId;
  private String mTitle;
  private long mStartTimeUtcMillis;
  private long mEndTimeUtcMillis;
  private long mDurationTimeMillis;
  private String mPath;

  private RecordedProgram() {
    // Do nothing.
  }

  public String getInputId() {
    return mInputId;
  }

  public void setInputId(String inputId) {
    mInputId = inputId;
  }

  public long getChannelId() {
    return mChannelId;
  }

  public void setChannelId(long channelId) {
    mChannelId = channelId;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }

  public long getStartTimeUtcMillis() {
    return mStartTimeUtcMillis;
  }

  public void setStartTimeUtcMillis(long startTimeUtcMillis) {
    mStartTimeUtcMillis = startTimeUtcMillis;
  }

  public long getEndTimeUtcMillis() {
    return mEndTimeUtcMillis;
  }

  public void setEndTimeUtcMillis(long endTimeUtcMillis) {
    mEndTimeUtcMillis = endTimeUtcMillis;
  }

  public long getDurationTimeMillis() {
    return mDurationTimeMillis;
  }

  public void setDurationTimeMillis(long durationTimeMillis) {
    mDurationTimeMillis = durationTimeMillis;
  }

  public String getPath() {
    return mPath;
  }

  public void setPath(String path) {
    mPath = path;
  }

  public ContentValues toContentValues() {
    ContentValues values = new ContentValues();
    values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, mInputId);
    values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID, mChannelId);
    values.put(TvContract.RecordedPrograms.COLUMN_TITLE, mTitle);
    values.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS, mStartTimeUtcMillis);
    values.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS, mEndTimeUtcMillis);
    values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, mDurationTimeMillis);
    values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI, mPath);
    return values;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RecordedProgram{")
        .append(", inputId=").append(mInputId)
        .append(", channelId=").append(mChannelId)
        .append(", title=").append(mTitle)
        .append(", startTimeUtcSec=").append(mStartTimeUtcMillis)
        .append(", endTimeUtcSec=").append(mEndTimeUtcMillis)
        .append(", durationTime=").append(mDurationTimeMillis)
        .append(", Path=").append(mPath)
        .append("}")
        .toString();
  }

  public void copyFrom(RecordedProgram other) {
    if (this == other) {
      return;
    }

    mInputId = other.mInputId;
    mChannelId = other.mChannelId;
    mTitle = other.mTitle;
    mStartTimeUtcMillis = other.mStartTimeUtcMillis;
    mEndTimeUtcMillis = other.mEndTimeUtcMillis;
    mDurationTimeMillis = other.mDurationTimeMillis;
    mPath = other.mPath;
  }

  public static final class Builder {
    private final RecordedProgram mRecordedProgram;

    public Builder() {
      mRecordedProgram = new RecordedProgram();
      // Fill initial data.
      mRecordedProgram.mInputId = "inputId";
      mRecordedProgram.mChannelId = RecordedProgram.INVALID_ID;
      mRecordedProgram.mTitle = "title";
      mRecordedProgram.mStartTimeUtcMillis = -1;
      mRecordedProgram.mEndTimeUtcMillis = -1;
      mRecordedProgram.mDurationTimeMillis = -1;
      mRecordedProgram.mPath = "file//";
    }

    public Builder(RecordedProgram other) {
      mRecordedProgram = new RecordedProgram();
      mRecordedProgram.copyFrom(other);
    }

    public Builder setInputId(String inputId) {
      mRecordedProgram.mInputId = inputId;
      return this;
    }

    public Builder setChannelId(long channelId) {
      mRecordedProgram.mChannelId = channelId;
      return this;
    }

    public Builder setTitle(String title) {
      mRecordedProgram.mTitle = title;
      return this;
    }

    public Builder setStartTimeUtcMillis(long startTimeUtcMillis) {
      mRecordedProgram.mStartTimeUtcMillis = startTimeUtcMillis;
      return this;
    }

    public Builder setEndTimeUtcMillis(long endTimeUtcMillis) {
      mRecordedProgram.mEndTimeUtcMillis = endTimeUtcMillis;
      return this;
    }

    public Builder setDurationTimeMillis(long durationTimeMillis) {
      mRecordedProgram.mDurationTimeMillis = durationTimeMillis;
      return this;
    }

    public Builder setPath(String path) {
      mRecordedProgram.mPath = path;
      return this;
    }

    public RecordedProgram build() {
      return mRecordedProgram;
    }
  }
}

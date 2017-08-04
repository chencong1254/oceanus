package skyworth.platformsupport.componentSupport;

import android.content.Context;
import android.media.tv.TvInputService;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.BaseTvInputService;
import com.google.android.media.tv.companionlibrary.TvPlayer;
import com.google.android.media.tv.companionlibrary.model.Advertisement;
import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.Program;
import com.google.android.media.tv.companionlibrary.model.RecordedProgram;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by yangxiong on 2017/5/9.
 */

public class AndroidTvSupportTvinputService extends BaseTvInputService {
    public class AndroidTvInputSession extends BaseTvInputService.Session
    {
        public AndroidTvInputSession(Context context, String inputId) {
            super(context, inputId);
            Log.d(DEBUG_TAG,"AndroidTvInputSession------------>Init");
        }

        @Override
        public TvPlayer getTvPlayer() {
            Log.d(DEBUG_TAG,"AndroidTvInputSession------------>getTvPlayer");
            return null;
        }

        @Override
        public boolean onPlayProgram(Program program, long startPosMs) {
            Log.d(DEBUG_TAG,"AndroidTvInputSession------------>onPlayProgram");
            return false;
        }

        @Override
        public boolean onPlayRecordedProgram(RecordedProgram recordedProgram) {
            Log.d(DEBUG_TAG,"AndroidTvInputSession------------>onPlayRecordedProgram");
            return false;
        }

        @Override
        public void onPlayChannel(Channel channel) {
            super.onPlayChannel(channel);
        }

        @Override
        public void onPlayAdvertisement(Advertisement advertisement) {
            Log.d(DEBUG_TAG,"onPlayAdvertisement: " + advertisement.toString());
            super.onPlayAdvertisement(advertisement);
        }

        @Override
        public void onSetCaptionEnabled(boolean enabled) {
            Log.d(DEBUG_TAG,"AndroidTvInputSession------------>onSetCaptionEnabled");
        }
    }
    @Nullable
    @Override
    public TvInputService.Session onCreateSession(String inputId) {
        AndroidTvInputSession session = new AndroidTvInputSession(this, inputId);
        session.setOverlayViewEnabled(true);
        return super.sessionCreated(session);
    }
}

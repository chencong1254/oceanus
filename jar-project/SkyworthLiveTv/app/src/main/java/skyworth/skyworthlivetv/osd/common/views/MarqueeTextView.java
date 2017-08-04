package skyworth.skyworthlivetv.osd.common.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by sky057509 on 2017/5/23.
 */

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {
    public MarqueeTextView(Context context) {
        super(context);
        setMarqueeRepeatLimit(-1);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
    }
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMarqueeRepeatLimit(-1);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMarqueeRepeatLimit(-1);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
    }
    @Override
    public boolean isFocused() {
        return true;
    }
}

package skyworth.skyworthlivetv.osd.common;
import java.io.InputStream;
import java.math.BigDecimal;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @author yellowlgx
 * @version TODO (write something)
 * @ClassName SkyScreenParams
 * @Description <p>
 * 为单例工具类，其中提供了获取当前分辨率下UI的实际大小,获取当前分辨率下文字的实际大小等方法 具体详见个方法注释
 * </p>
 * @date 2015-2-9
 */

public class SkyScreenParams {
    private float resolutionDiv;
    private float dipDiv;

    private static SkyScreenParams screenParams;

    // 单例初始化
    public static SkyScreenParams getInstence(Context context)
    {
        if (screenParams == null)
        {
            screenParams = new SkyScreenParams(context);
        }
        return screenParams;
    }

    public SkyScreenParams(Context context)
    {
        setDpiDiv_Resolution(context);
    }

    /**
     * 获取当前屏幕div和分辨率参数 需要首先调用,一个工程只需调用一次
     *
     * @param context
     * @return
     */
    private void setDpiDiv_Resolution(Context context)
    {
        int width = getDisplayWidth(context);
        switch (width)
        {
            case 3840:
                resolutionDiv = 0.5f;
                break;
            case 1920:
                resolutionDiv = 1;
                break;
            case 1366:
                resolutionDiv = 1.4f;
                break;
            case 1280:
                resolutionDiv = 1.5f;
                break;
            case 800:
                resolutionDiv = 2.4f;
                break;
            default:
                resolutionDiv = 1;
                break;
        }
        dipDiv = resolutionDiv * getDisplayDensity(context);
    }

    /**
     * 概述：得到当前density<br/>
     *
     * @param context
     * @return float
     * @date 2013-12-20
     */
    public float getDisplayDensity(Context context)
    {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }

    /**
     * 概述：得到屏幕的宽度<br/>
     *
     * @param context
     * @return int
     * @date 2013-10-22
     */
    public int getDisplayWidth(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        if (display == null)
        {
            return 1920;
        }
        return display.getWidth();
    }

    /**
     * 概述：得到屏幕的高度<br/>
     *
     * @param context
     * @return int
     * @date 2013-10-22
     */
    public int getDisplayHeight(Context context)
    {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        return display.getHeight();
    }

    public ObjectAnimator nope_X(View view)
    {
        int delta = getResolutionValue(6);
        Keyframe kf0 = Keyframe.ofFloat(0f, 0);
        Keyframe kf1 = Keyframe.ofFloat(.1f, -delta);
        Keyframe kf2 = Keyframe.ofFloat(.2f, 0);
        Keyframe kf3 = Keyframe.ofFloat(.3f, delta);
        Keyframe kf4 = Keyframe.ofFloat(.4f, 0);
        Keyframe kf5 = Keyframe.ofFloat(.5f, -delta);
        Keyframe kf6 = Keyframe.ofFloat(.6f, 0);
        Keyframe kf7 = Keyframe.ofFloat(.7f, delta);
        Keyframe kf8 = Keyframe.ofFloat(.8f, 0);
        Keyframe kf9 = Keyframe.ofFloat(.9f, -delta);
        Keyframe kf10 = Keyframe.ofFloat(1f, 0);

        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                kf0, kf1, kf2, kf3, kf4, kf5, kf6, kf7, kf8, kf9, kf10);

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).setDuration(500);
    }

    public ObjectAnimator nope_Y(View view)
    {
        int delta = getResolutionValue(6);

        Keyframe kf0 = Keyframe.ofFloat(0f, 0);
        Keyframe kf1 = Keyframe.ofFloat(.1f, -delta);
        Keyframe kf2 = Keyframe.ofFloat(.2f, 0);
        Keyframe kf3 = Keyframe.ofFloat(.3f, delta);
        Keyframe kf4 = Keyframe.ofFloat(.4f, 0);
        Keyframe kf5 = Keyframe.ofFloat(.5f, -delta);
        Keyframe kf6 = Keyframe.ofFloat(.6f, 0);
        Keyframe kf7 = Keyframe.ofFloat(.7f, delta);
        Keyframe kf8 = Keyframe.ofFloat(.8f, 0);
        Keyframe kf9 = Keyframe.ofFloat(.9f, -delta);
        Keyframe kf10 = Keyframe.ofFloat(1f, 0);

        PropertyValuesHolder pvhTranslateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                kf0, kf1, kf2, kf3, kf4, kf5, kf6, kf7, kf8, kf9, kf10);

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateY).setDuration(500);
    }

    /**
     * 获取当前分辨率UI实际需要的大小 根据当前分辨率适配UI
     *
     * @param context
     * @return
     */
    public int getResolutionValue(int value)
    {
        if (value < 0)
            return 0;
        String rv = String.valueOf((float) (value / resolutionDiv));
        BigDecimal bd = new BigDecimal(rv).setScale(0, BigDecimal.ROUND_HALF_UP);
        int r_value = bd.intValue();
        return r_value;
    }

    /**
     * 获取当前分辨率文字实际需要的大小 根据DPI值适配当前文字
     *
     * @param context
     * @return
     */
    public int getTextDpiValue(int value)
    {
        if (value < 0)
            return 0;

        int r_value = (int) (value / dipDiv);
        return r_value;
    }

    /**
     * 设置文字透明度
     *
     * @param view
     * @param alpha
     * @param color
     * @return TextView
     * @date 2015-2-13
     */
    public TextView settextAlpha(TextView view, int alpha, String color)
    {
        String r = color.substring(0, 2);
        String g = color.substring(2, 4);
        String b = color.substring(4, 6);
        view.setTextColor(Color.argb(alpha, Integer.valueOf(r, 16), Integer.valueOf(g, 16),
                Integer.valueOf(b, 16)));
        return view;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取当前屏幕截图
     *
     * @param activity 可由Context强制转换
     * @return
     */
    public Bitmap getScreenShot(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public Bitmap readBitMap(Context context, int resId)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);

        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public Drawable readDrawable(Context context, int resId)
    {
        Drawable drawable = new BitmapDrawable(context.getResources(), readBitMap(context, resId));
        return drawable;
    }


}

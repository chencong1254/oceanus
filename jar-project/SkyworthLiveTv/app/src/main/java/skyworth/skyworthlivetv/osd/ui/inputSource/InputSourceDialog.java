package skyworth.skyworthlivetv.osd.ui.inputSource;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.platform.IPlatformSourceManager;
import com.platform.ui.IPlatformTvActivity;

import java.lang.ref.WeakReference;

import Oceanus.Tv.Service.SourceManager.Source;
import skyworth.platformsupport.PlatformSourceManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.global.GlobalApplication;
import skyworth.skyworthlivetv.osd.ui.inputSource.Adapter.InputSourceAdapter;
import skyworth.skyworthlivetv.osd.ui.mainActivity.LiveTvScreenActivity;

/**
 * Created by yangxiong on 2017/5/3.
 */

public class InputSourceDialog extends AlertDialog implements InputSourceAdapter.OnSourceItemClickListener ,DialogInterface.OnShowListener{

    private String TAG = "InputSourceDialog";
    private IPlatformSourceManager mSourceManager;
    private WeakReference<IPlatformTvActivity> wp_MainActivity = null;
    private RecyclerView rvInputSource;
    private static InputSourceDialog m_pThis = null;
    private InputSourceAdapter mSourceAdapter;
    public static InputSourceDialog getInstance(Context context, WindowManager windowManager)
    {
        if(m_pThis==null)
        {
            m_pThis = new InputSourceDialog(context,windowManager);
        }
        return m_pThis;
    }
    private InputSourceDialog(Context context, WindowManager windowManager) {
        super(context);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = dm.widthPixels;
        lp.height = (int)(dm.heightPixels * 0.3);
        getWindow().setAttributes(lp);
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        setOnShowListener(this);
    }
    public void BindMainActivity(IPlatformTvActivity activity)
    {
        wp_MainActivity = new WeakReference<>(activity);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tv_input_source_main);
        initData();
        initView();
    }

    private void initData() {
        mSourceManager = PlatformSourceManager.getInstance( );
        mSourceAdapter = new InputSourceAdapter(getContext());
    }

    private void initView() {
        rvInputSource = (RecyclerView) findViewById(R.id.rv_input_source);
        setListener();//set all Listeners
        rvInputSource.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInputSource.setAdapter(mSourceAdapter);

    }

    private void setListener() {
        mSourceAdapter.setSourceOnItemClick(this);
    }

    @Override
    public void onItemClick(View view, int position, Source source) {
        if(wp_MainActivity!=null)
        {
            if(wp_MainActivity.get().IsStop())
            {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName componentName = new ComponentName(GlobalApplication.getAppInstance().getPackageName(),LiveTvScreenActivity.class.getName());
                intent.setComponent(componentName);
                GlobalApplication.getAppInstance().startActivity(intent);
            }
        }
        mSourceManager.SetSource(source);
        this.dismiss();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mSourceAdapter.RefreshSourceList();
        mSourceAdapter.notifyDataSetChanged();
    }
}

package skyworth.platformsupport.androidTvOsd;

import android.app.Activity;
import android.content.Intent;
import android.media.tv.TvInputInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import skyworth.androidtvsupport.R;
import skyworth.platformsupport.PlatformManager;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/6.
 */

public class SkyworthLiveTvSetupActivity extends Activity implements View.OnClickListener{
    private Button setupButton = null;
    private String inputId = null;
    private TextView packageName = null;
    private ImageView appIcon = null;
    private TvInputInfo setupInfo = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livetvsetup);
        setupButton = (Button)findViewById(R.id.setupButton);
        packageName = (TextView) findViewById(R.id.packageName);
        appIcon = (ImageView)findViewById(R.id.appIcon);
        setupButton.setOnClickListener(this);
        Intent intent = getIntent();
        Bundle idBundle =intent.getExtras();
        inputId = idBundle.getString("inputId");
        setupInfo = PlatformManager.getInstance().GetTvInputManager().getTvInputInfo(inputId);
        if(setupInfo!=null)
        {
            packageName.setText(setupInfo.getServiceInfo().packageName);
            appIcon.setImageDrawable(setupInfo.loadIcon(this));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.setupButton) {
            Log.d(DEBUG_TAG,"Press button set up!" + inputId);
            if(setupInfo!=null)
            {
                Intent setupIntent = setupInfo.createSetupIntent();
                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(setupIntent);
            }
        }
    }
}

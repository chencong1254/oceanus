package skyworth.skyworthlivetv.osd.ui.menu.channel;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skyworth.skyworthlivetv.osd.common.BaseDialog;
import skyworth.skyworthlivetv.osd.ui.menu.channel.Listener.DlgScanCancelListener;
import skyworth.skyworthlivetv.R;

/**
 * @author yangjianjun
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class ScanCancelDlg extends BaseDialog {
    @BindView(R.id.canceltxt)
    TextView canceltxt;
    @BindView(R.id.okbuttontxt)
    TextView okbuttontxt;
    @BindView(R.id.okbutton)
    LinearLayout okbutton;
    @BindView(R.id.cancelbuttontxt)
    TextView cancelbuttontxt;
    @BindView(R.id.cancelbutton)
    LinearLayout cancelbutton;
    private Context mContext;
    private DlgScanCancelListener myListener;

    public ScanCancelDlg(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ScanCancelDlg(Context context, int theme) {
        super(context, theme);
    }

    public ScanCancelDlg(Context context, DlgScanCancelListener listener) {
        super(context);
        this.mContext = context;
        myListener = listener;
        View v = getLayoutInflater().inflate(R.layout.dlg_cancelscan_layout, null);
        setContentView(v);
        ButterKnife.bind(this,v);
    }

    @OnClick({R.id.okbutton, R.id.cancelbutton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.okbutton:
                this.dismiss();
                if(myListener != null){
                    myListener.cancelScan();
                }
                break;
            case R.id.cancelbutton:
                this.dismiss();
                break;
        }
    }
}

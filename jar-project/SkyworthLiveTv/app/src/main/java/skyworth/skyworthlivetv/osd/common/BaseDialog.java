/**
 * Copyright (C) 2013 AVIT, All rights reserved
 *
 * @fileName: com.avit.ott.common.base.BaseDialog.java
 *
 * @author: daishulun@avit.com.cn
 *
 * Modification History:
 * Date         Author      Version     Description
 * -----------------------------------------------------------------
 * 2013-9-25     daishulun      v1.0.0        create
 *
 */
package skyworth.skyworthlivetv.osd.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import skyworth.skyworthlivetv.R;

/**
 * @Description: BaseDialog
 */
public class BaseDialog extends Dialog {

	protected String LOG_TAG = "BaseDialog";

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, R.style.DialogStyle);
		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		initDialog();
		LOG_TAG = this.getClass().getSimpleName();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param context
	 * @param theme
	 */
	public BaseDialog(Context context, int theme) {
		super(context, R.style.DialogStyle);
		initDialog();
		LOG_TAG = this.getClass().getSimpleName();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 *
	 * @param context
	 */
	public BaseDialog(Context context) {
		super(context, R.style.DialogStyle);
		initDialog();
		LOG_TAG = this.getClass().getSimpleName();
	}

	private void initDialog(){
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		//getWindow().setWindowAnimations(R.style.DlgAnimStyle);
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param savedInstanceState
	 * @see Dialog#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @see Dialog#onSaveInstanceState()
	 */
	@Override
	public Bundle onSaveInstanceState() {
		return super.onSaveInstanceState();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#onAttachedToWindow()
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#onDetachedFromWindow()
	 */
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#dismiss()
	 */
	@Override
	public void dismiss() {
		super.dismiss();
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param hasFocus
	 * @see Dialog#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @see Dialog#cancel()
	 */
	@Override
	public void cancel() {
		super.cancel();
	}

}

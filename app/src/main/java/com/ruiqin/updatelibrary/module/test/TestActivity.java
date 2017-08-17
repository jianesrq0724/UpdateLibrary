package com.ruiqin.updatelibrary.module.test;

import android.app.AlertDialog;
import android.os.Bundle;

import com.ruiqin.updatelibrary.R;
import com.ruiqin.updatelibrary.base.BaseActivity;
import com.ruiqin.updatelibrary.commonality.view.PermissionTipDialog;
import com.ruiqin.updatelibrary.commonality.view.ShareDialog;
import com.ruiqin.updatelibrary.commonality.view.TipCustomDialog;

import butterknife.OnClick;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }


    @OnClick(R.id.button)
    public void onViewClicked() {
        showShareDialog();
//        showPermissionDialog();
    }

    ShareDialog mShareDialog;

    private void showShareDialog() {
        if (mShareDialog == null) {
            mShareDialog = new ShareDialog(mContext, "http://www.baidu.com");
        }
        mShareDialog.show();

        AlertDialog mAlertDialog = new AlertDialog.Builder(mContext).show();
        mAlertDialog.show();
    }

    PermissionTipDialog mPermissionTipDialog;

    private void showPermissionDialog() {
        if (mPermissionTipDialog == null) {
            mPermissionTipDialog = new PermissionTipDialog(mContext);
        }
        mPermissionTipDialog.show();
    }

    private TipCustomDialog mTipCustomDialog;

    private void showTipDialog() {
        if (mTipCustomDialog == null) {
            mTipCustomDialog = new TipCustomDialog(mContext);
        }
        mTipCustomDialog.show();
    }

}

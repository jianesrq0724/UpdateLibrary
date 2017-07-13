package com.ruiqin.updatetest.module.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ruiqin.downloadlibrary.UpdateDialog;
import com.ruiqin.updatetest.R;
import com.ruiqin.updatetest.base.BaseActivity;
import com.ruiqin.updatetest.util.ToastUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainPresenter, MainModel> implements MainContract.View {

    @Override
    protected int getFragmentId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void testSuccess() {
        ToastUtils.showShort("大于0");
    }

    @Override
    public void textFail() {
        ToastUtils.showShort("小于0");
    }

    @Override
    public boolean canBack() {
        mToolbarTitle.setText("test");
        return true;
    }

    private long lastClickTime;

    @Override
    public void onBackPressed() {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 2000) {
            ToastUtils.showShort("再按一次退出");
            lastClickTime = currentClickTime;
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        showUpdateDialog();
    }

    /**
     * 展示dialog
     */
    UpdateDialog mUpdateDialog;

    public void showUpdateDialog() {
        if (mUpdateDialog == null) {
            mUpdateDialog = new UpdateDialog(mContext);
            String url = "http://www.baidu.com";
            String version = "1.0";
            String desc = "test";
            boolean force = true;
            mUpdateDialog.setValue(url, version, desc, force);
        }
        mUpdateDialog.show();
    }
}

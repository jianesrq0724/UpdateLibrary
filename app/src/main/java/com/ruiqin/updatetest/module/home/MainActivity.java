package com.ruiqin.updatetest.module.home;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.ruiqin.downloadlibrary.UpdateDialog;
import com.ruiqin.updatetest.R;
import com.ruiqin.updatetest.base.BaseActivity;
import com.ruiqin.updatetest.util.ToastUtils;

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


    private String requestPermission = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final int DOWNLOAD_REQUESTCODE = 1;

    @OnClick(R.id.button)
    public void onViewClicked() {
        onClickUpdate();
    }

    private void onClickUpdate() {
        if (ActivityCompat.checkSelfPermission(mContext, requestPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{requestPermission}, DOWNLOAD_REQUESTCODE);
        } else {
            showUpdateDialog();
        }
    }

    boolean mShowRequestPermission = true;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case DOWNLOAD_REQUESTCODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permissions[0]);
                    if (!showRequestPermission) {
                        mShowRequestPermission = false;
                    }

                    if (!mShowRequestPermission) {
                        ToastUtils.showShort("请手动授予权限");
                        return;
                    }
                }
                onClickUpdate();
                break;
        }
    }

    /**
     * 展示dialog
     */
    UpdateDialog mUpdateDialog;

    public void showUpdateDialog() {
        if (mUpdateDialog == null) {
            mUpdateDialog = new UpdateDialog(mContext);
            String url = "http://imtt.dd.qq.com/16891/789C83C3D3B6DC67BEDA10C5FE776D8F.apk?fsname=cn.baidaibao_1.5.0_10.apk&csr=1bbd";
            String version = "1.0";
            String desc = "test";
            boolean force = true;
            mUpdateDialog.setValue(url, version, desc, force);
        }
        mUpdateDialog.show();
    }
}

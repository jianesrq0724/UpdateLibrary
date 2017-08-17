package com.ruiqin.updatelibrary.module.home;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ruiqin.downloadlibrary.UpdateDialog;
import com.ruiqin.updatelibrary.R;
import com.ruiqin.updatelibrary.base.BaseActivity;
import com.ruiqin.updatelibrary.commonality.view.PermissionTipDialog;
import com.ruiqin.updatelibrary.module.home.adapter.MainRecyclerAdapter;
import com.ruiqin.updatelibrary.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity<MainPresenter, MainModel> implements MainContract.View {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected int getFragmentContentId() {
        return R.id.fragment;
    }

    @OnClick(R.id.btn_update)
    public void onBtnUpdate() {
        String url = "http://imtt.dd.qq.com/16891/789C83C3D3B6DC67BEDA10C5FE776D8F.apk?fsname=cn.baidaibao_1.5.0_10.apk&csr=1bbd";
        String version = "1.0";
        String desc = "test";
        boolean force = false;
        showUpdateDialog(url, version, desc, force);
    }

    private static final int PERMISSION_WRITE_STORAGE = 1;

    UpdateDialog updateDialog;

    /**
     * 更新Dialog
     *
     * @param url
     * @param version
     * @param desc
     * @param force
     */
    private void showUpdateDialog(String url, String version, String desc, boolean force) {
        if (updateDialog == null) {
            updateDialog = new UpdateDialog(mContext);
            updateDialog.setValue(url, version, desc, force);
            updateDialog.setOnClickUpdateListener(() -> {
                updateDialog.cancel();
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
                } else {
                    updateDialog.onClickUpdate();
                }
            });
        }
        updateDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {
            showPermissionDialog();
            return;
        }
        switch (requestCode) {
            case PERMISSION_WRITE_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showPermissionDialog();
                } else {
                    if (updateDialog != null) {
                        updateDialog.onClickUpdate();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    PermissionTipDialog permissionTipDialog;

    private void showPermissionDialog() {
        if (permissionTipDialog == null) {
            permissionTipDialog = new PermissionTipDialog(mContext);
        }
        permissionTipDialog.show();
        permissionTipDialog.setMesage("存储");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        addFragment(new BlankFragment());
//        mPresenter.setAdapter();
    }

    @Override
    public boolean canBack() {
        mToolbarTitle.setText("BaseProject");
        return false;
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

    @Override
    public void setRecyclerAdapterSuccess(MainRecyclerAdapter mainRecyclerAdapter) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(mainRecyclerAdapter);
    }
}

package com.ruiqin.downloadlibrary;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ruiqin.downloadlibrary.bean.UpdateInfo;
import com.ruiqin.downloadlibrary.util.DownloadToast;
import com.ruiqin.downloadlibrary.util.DownloadUtils;
import com.ruiqin.downloadlibrary.view.PermissionTipDialog;

import java.io.File;

public class UpdateActivity extends AppCompatActivity {

    private Button mBbtnUpdate;
    private TextView mTvUpdateDesc;
    UpdateInfo updateInfo;

    private static final String EXTRA_UPDATE_INFO = "updateInfo";

    public static Intent newIntent(Context context, UpdateInfo updateInfo) {
        Intent intent = new Intent(context.getApplicationContext(), UpdateActivity.class);
        intent.putExtra(EXTRA_UPDATE_INFO, updateInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        registerReceiver();//注册下载完成广播
        sharedPreferences = getSharedPreferences("download-library", Context.MODE_PRIVATE);
        getIntentData();
    }

    /**
     * 从Intent中获取数据
     */
    private void getIntentData() {
        Parcelable tempUpdateInfo = getIntent().getParcelableExtra(EXTRA_UPDATE_INFO);
        if (tempUpdateInfo != null) {
            updateInfo = (UpdateInfo) tempUpdateInfo;
            initView();
            initDate();
        }
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        if (!TextUtils.isEmpty(updateInfo.getTips())) {
            mTvUpdateDesc.setText(updateInfo.getTips());
        }
        fileName = "baidaibao-v" + updateInfo.getVersion() + ".apk";
        if (fileName != null) {
            apkFile = new File(Environment.getExternalStorageDirectory().getPath() + DownloadUtils.FILE_PATH + File.separator + fileName);
        }
        mDownloadId = getDownloadIdFromSp();//从SP中获取downloadId
    }

    private long mDownloadId;
    private static final int PERMISSION_WRITE_STORAGE = 1;

    /**
     * 初始化
     */
    private void initView() {
        mTvUpdateDesc = (TextView) findViewById(R.id.tv_update_desc);
        mBbtnUpdate = (Button) findViewById(R.id.btn_update);
        mBbtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_STORAGE);
                } else {
                    onClickUpdate();
                }
            }
        });
    }

    /**
     * 点击下载
     */
    public void onClickUpdate() {
        if (mDownloadId != 0) {//downloadID不为默认值，表示存在下载任务
            int status = DownloadUtils.queryDownloadStatus(this, mDownloadId);
            Log.e("TAG", status + "");
            switch (status) {
                case DownloadManager.STATUS_RUNNING://下载中
                    DownloadToast.showShort(this, "正在下载，请稍后");
                    break;
                case DownloadManager.STATUS_FAILED://下载失败
                    startDownApk();//重新开始下载
                    break;
                case DownloadManager.STATUS_SUCCESSFUL://下载成功
                    installApk();
                    break;
                default:
                    break;
            }
        } else {//无下载任务，开始下载
            startDownApk();
        }
    }

    /**
     * 权限判断
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) {//部分机型，拒绝权限，grantResult的长度为0
            showPermissionDialog();
            return;
        }
        switch (requestCode) {
            case PERMISSION_WRITE_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showPermissionDialog();
                } else {
                    onClickUpdate();//开始下载
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
            permissionTipDialog = new PermissionTipDialog(this);
        }
        permissionTipDialog.show();
        permissionTipDialog.setMesage("存储");
    }

    /**
     * 开始下载APK
     */
    private void startDownApk() {
        if (updateInfo.getUrl() != null && fileName != null) {
            try {
                long downloadId = DownloadUtils.downLoadFile(this, updateInfo.getUrl(), fileName);//开始下载
                DownloadToast.showShort(this, "开始下载");
                mDownloadId = downloadId;
                saveDownloadId2Sp(mDownloadId);//保存值
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    SharedPreferences sharedPreferences;
    private String SP_DOWNLOADID = "downloadId";//SP参数

    /**
     * 将downloadId保存到SP中
     */
    private void saveDownloadId2Sp(long value) {
        sharedPreferences.edit().putLong(SP_DOWNLOADID, value).apply();
    }

    /**
     * 从SP中获取downLoadId
     *
     * @return
     */
    private long getDownloadIdFromSp() {
        return sharedPreferences.getLong(SP_DOWNLOADID, 0);
    }

    @Override
    public void onBackPressed() {
        if (!updateInfo.isForce()) {
            super.onBackPressed();
        }
    }

    private String fileName;
    File apkFile;

    DownloadCompleteReceiver downloadCompleteReceiver;//下载完成广播

    /**
     * 注册下载完成广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//下载完成的动作
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        registerReceiver(downloadCompleteReceiver, intentFilter);
    }

    /**
     * 下载完成广播
     */
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(downloadCompleteReceiver);//接受广播后，取消注册
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                installApk();
            }
        }
    }

    /**
     * 安装软件
     */
    private void installApk() {
        if (apkFile == null || !apkFile.exists()) {
            startDownApk();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data;
            String type = "application/vnd.android.package-archive";

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                data = Uri.fromFile(apkFile);
            } else {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                data = FileProvider.getUriForFile(this, "com.ruiiqn.update.fileprovider", apkFile);
            }
            intent.setDataAndType(data, type);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}

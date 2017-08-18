package com.ruiqin.downloadlibrary;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
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
        if (fileName != null) {
            apkFile = new File(Environment.getExternalStorageDirectory().getPath() + DownloadUtils.FILE_PATH + File.separator + fileName);
        }

        mDownloadId = getDownloadIdFromSp();//从SP中获取downloadId
    }

    /**
     * 设置值
     */
    public void setValue(String url, String version, String desc, boolean force) {
        mUpdateUrl = url;
        fileName = "baidaibao-v" + version + ".apk";
        mUpdateDesc = desc;
        mUpdateForce = force;
    }

    private long mDownloadId;

    /**
     * 初始化
     */
    private void initView() {
        mTvUpdateDesc = (TextView) findViewById(R.id.tv_update_desc);
        mBbtnUpdate = (Button) findViewById(R.id.btn_update);
        mBbtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdate();
            }
        });
    }

    /**
     * 点击下载
     */
    public void onClickUpdate() {
        if (mDownloadId != 0) {//downloadID不为默认值，表示存在下载任务
            int status = DownloadUtils.queryDownloadStatus(mContext, mDownloadId);
            Log.e("TAG", status + "");
            switch (status) {
                case DownloadManager.STATUS_RUNNING://下载中
                    DownloadToast.showShort(mContext, "正在下载，请稍后");
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
     * 开始下载APK
     */
    private void startDownApk() {
        if (mUpdateUrl != null && fileName != null) {
            try {
                long downloadId = DownloadUtils.downLoadFile(mContext, mUpdateUrl, fileName);//开始下载
                DownloadToast.showShort(mContext, "开始下载");
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
        if (!mUpdateForce) {
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
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//下载完成的动作
        mContext.registerReceiver(downloadCompleteReceiver, intentFilter);
    }

    /**
     * 下载完成广播
     */
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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

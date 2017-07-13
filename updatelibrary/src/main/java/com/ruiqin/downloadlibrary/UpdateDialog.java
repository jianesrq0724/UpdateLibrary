package com.ruiqin.downloadlibrary;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ruiqin.downloadlibrary.util.DownloadToast;
import com.ruiqin.downloadlibrary.util.DownloadUtils;

import java.io.File;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class UpdateDialog extends Dialog {
    private Button mBbtnUpdate;
    private TextView mTvUpdateDesc;
    private String mUpdateUrl;
    private String mUpdateDesc;
    private Context mContext;
    private boolean mUpdateForce;

    public UpdateDialog(@NonNull Context context) {
        this(context, R.style.CustomDialogTheme);
    }

    public UpdateDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected UpdateDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        setCanceledOnTouchOutside(false);//点击周围不会消失
        sharedPreferences = mContext.getSharedPreferences("download-library", Context.MODE_PRIVATE);
        registerReceiver();//注册下载完成广播
        initView();
        initDate();
    }

    /**
     * 初始化数据
     */
    private void initDate() {
        if (!TextUtils.isEmpty(mUpdateDesc)) {
            mTvUpdateDesc.setText(mUpdateDesc);
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
        });
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
            Intent mIntent = new Intent(Intent.ACTION_VIEW);
            mIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);
        }
    }

}

package com.ruiqin.downloadlibrary.util;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * 描述：
 * 作者：baicaibang
 * 时间：2016/11/4 10:29
 */
public class DownloadUtils {

    public static final String FILE_PATH = "/com.baidaibao/app";//相对路径

    /**
     * 下载文件
     */
    public static long downLoadFile(Context mContext, String url, String fileName) {
        long downloadId = -1;
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))//路径
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)//默认，下载过程中显示，下载完成后自动消失
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)//
                .setDestinationInExternalPublicDir(FILE_PATH, fileName);//要使用相对路径
        downloadId = downloadManager.enqueue(request);//执行下载，返回的downloadId用来查询下载进度
        return downloadId;
    }

    /**
     * 查询下载状态
     */
    public static int queryDownloadStatus(Context mContext, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query downloadQuery = new DownloadManager.Query();
        downloadQuery.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(downloadQuery);
        int status = 0;
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        return status;
    }


}

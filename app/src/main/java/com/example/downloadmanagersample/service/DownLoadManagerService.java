package com.example.downloadmanagersample.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DownLoadManagerService extends IntentService {

    private static String DOWNLOAD_PATH;// = "https://screeningtest.vdocipher.com/download/";
    private static String DESTINATION_PATH;// = "com.spartons.androiddownloadmanager_DownloadSongService_Destination_path";
    private long lastDownload=-1L;
    ResultReceiver myResultReceiver;

    public DownLoadManagerService() {
        super("DownloadSongService");
    }
    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath) {
        return new Intent(callingClassContext, DownLoadManagerService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        Log.d("sx1", "sx1 inside Service "+downloadPath);
        Log.d("sx1", "sx1 inside Service"+destinationPath);
        if(intent!=null){
            myResultReceiver =  intent.getParcelableExtra("result");
        }
        startDownload(downloadPath, destinationPath);
    }
    private void startDownload(String downloadPath, String destinationPath) {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        //request.setDestinationInExternalPublicDir(destinationPath, uri.getLastPathSegment());  // Storage directory path
        request.setDestinationInExternalFilesDir(this, Environment.getExternalStorageDirectory().getPath() + "MyExternalStorageAppPath","test");
        lastDownload = ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        Bundle bundle = new Bundle();
        bundle.putLong("lastDownLoad",lastDownload);
        myResultReceiver.send(100,bundle);

    }
}

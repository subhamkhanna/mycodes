package com.example.downloadmanagersample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloadmanagersample.data.FileContent;
import com.example.downloadmanagersample.receiver.MyResultReceiver;
import com.example.downloadmanagersample.service.DownLoadManagerService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements MyResultReceiver.GetResultInterface {

    private DownloadManager mgr=null;
    private long lastDownload=-1L;
    ArrayList<String> fileContent = new ArrayList<>();
    MyResultReceiver myResultReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

        if(isNetworkAvailable()) {
            fetchMainFile();
        } else {
            findViewById(R.id.progress_text).setVisibility(View.GONE);
            findViewById(R.id.progress).setVisibility(View.GONE);
            findViewById(R.id.enable_network).setVisibility(View.VISIBLE);
            findViewById(R.id.enable_network).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()){
                        fetchMainFile();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enable internet first",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
        unregisterReceiver(onNotificationClick);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void fetchMainFile(){
        findViewById(R.id.progress_text).setVisibility(View.VISIBLE);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.enable_network).setVisibility(GONE);
        Environment.getExternalStorageDirectory()
                .mkdirs();

        Intent intent = DownLoadManagerService.getDownloadService(this,
                "https://screeningtest.vdocipher.com/download/files.txt");
        intent.putExtra("result",myResultReceiver);

        startService(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Log.d("sx1", "onComplete");
            findViewById(R.id.launch_fragment).setEnabled(true);
            findViewById(R.id.progress).setVisibility(GONE);
            ((TextView)findViewById(R.id.progress_text)).setText(getString(R.string.main_file_downloaded));

        }
    };

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
        }
    };

    public void launchFragment(View v){

        Cursor c=mgr.query(new DownloadManager.Query().setFilterById(lastDownload));
        c.moveToFirst();

        String downloadFilePath = null;
        String downloadFileLocalUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                .replace("file://","");
        Log.d("sx1", "file "+downloadFileLocalUri);
        if (downloadFileLocalUri != null) {
            File mFile = new File(Uri.parse(downloadFileLocalUri).getPath());
            downloadFilePath = mFile.getAbsolutePath();
            Log.d("sx1", "file "+downloadFilePath);
            try (BufferedReader br = new BufferedReader(new FileReader(mFile))) {
                String line;
                fileContent.clear();
                while ((line = br.readLine()) != null) {
                    // process the line.
                    Log.d("sx1 File Content", "line "+line);
                    fileContent.add(line);
                }
                FileContent.getInstance().setFilesList(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("sx1", " error "+e.getMessage());
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DownloadContents())
                .addToBackStack(null).commit();
    }

    @Override
    public void getResult(int resultCode, Bundle resultData) {

        if(resultData!=null){
            switch (resultCode){
                case 100:
                    lastDownload = resultData.getLong("lastDownLoad");
                    Log.d("sx1 ", " Last Download Received "+lastDownload);
                    break;
            }
        }
    }
}
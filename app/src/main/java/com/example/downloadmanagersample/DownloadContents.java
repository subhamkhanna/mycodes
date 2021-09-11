package com.example.downloadmanagersample;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.downloadmanagersample.adapter.MyRecyclerViewAdapter;
import com.example.downloadmanagersample.data.FileContent;
import com.example.downloadmanagersample.receiver.MyResultReceiver;
import com.example.downloadmanagersample.service.DownLoadManagerService;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadContents extends Fragment implements MyResultReceiver.GetResultInterface{

    ArrayList<String> filesContent = new ArrayList<>();

    RecyclerView recyclerView;
    View view;

    private DownloadManager mgr=null;
    private long lastDownload=-1L;
    MyResultReceiver myResultReceiver;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    int selectedPosition;


    public DownloadContents() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mgr=(DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);
        getActivity().registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        getActivity().registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));

        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_download_contents, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        filesContent = FileContent.getInstance().getFilesList();
        recyclerView = view.findViewById(R.id.files_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(getActivity(),
                FileContent.getInstance().getDownloadedContentMap(),
                filesContent,
                new MyOnClickListener(){

            @Override
            public void onitemPosition(int position, String text) {
                Log.d("sx1", " position "+position + text);
                myRecyclerViewAdapter.notifyDataSetChanged();
                selectedPosition = position;
                Uri uri = Uri.parse("https://screeningtest.vdocipher.com/download/"+text);

                Log.d("sx1", "directory "+ Environment.getExternalStorageDirectory());

                Environment.getExternalStorageDirectory()
                        .mkdirs();

                Intent intent = DownLoadManagerService.getDownloadService(getActivity(),
                        "https://screeningtest.vdocipher.com/download/"+text);
                intent.putExtra("result",myResultReceiver);


                getActivity().startService(intent);
            }
        });
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }

    @Override
    public void getResult(int resultCode, Bundle resultData) {

    }

    public interface MyOnClickListener{
        void onitemPosition(int position, String text);
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Log.d("sx1", "onComplete");
            makePositionDownloaded(selectedPosition);
        }
    };

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Ummmm...hi!", Toast.LENGTH_LONG).show();
        }
    };

    private void makePositionDownloaded(int position){
        FileContent.getInstance().setPositionInMap(position);
        myRecyclerViewAdapter.makePositionDownloadComplete(FileContent.getInstance().
                getDownloadedContentMap());
        myRecyclerViewAdapter.notifyDataSetChanged();
    }


}
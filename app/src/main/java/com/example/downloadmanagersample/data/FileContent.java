package com.example.downloadmanagersample.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class FileContent {

    public static FileContent fileContent = null;
    ArrayList<String> filesList = new ArrayList<>();
    HashMap<Integer, Boolean> downloadedContentMap = new HashMap<>();

    public static FileContent getInstance(){
        if(fileContent == null) {
            fileContent = new FileContent();
        }
        return fileContent;
    }

    public void setFilesList(ArrayList<String> list){
        filesList = list;
        Log.d("sx1", " file list "+filesList.size());
    }

    public ArrayList<String> getFilesList(){
        return filesList;
    }

    public HashMap<Integer, Boolean> getDownloadedContentMap(){
        return downloadedContentMap;
    }

    public void setPositionInMap(int position){
        downloadedContentMap.put(position, true);
    }
}

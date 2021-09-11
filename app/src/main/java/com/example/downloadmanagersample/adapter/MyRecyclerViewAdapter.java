package com.example.downloadmanagersample.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloadmanagersample.DownloadContents;
import com.example.downloadmanagersample.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    ArrayList<String> fileContents;
    DownloadContents.MyOnClickListener myOnClickListener;
    int downloadCompletePosition = -1;
    Context context;
    HashMap<Integer, Boolean> downloadComplete = new HashMap<>();
    boolean fileDownloading = false;
    int downloadingFilePosition = -1;

    public MyRecyclerViewAdapter(Context context, HashMap<Integer, Boolean> map,
                                 ArrayList<String> fileContents, DownloadContents.MyOnClickListener onClickListener){
        this.context = context;
        downloadComplete = map;
        this.fileContents = fileContents;
        this.myOnClickListener = onClickListener;
    }

    public void makePositionDownloadComplete(HashMap<Integer, Boolean> map){
        downloadComplete = map;
        fileDownloading = false;
        downloadingFilePosition = -1;
    }

    public void setFileDownloading(int position){
        fileDownloading = true;
        downloadingFilePosition = position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recyclerview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(fileContents.get(position));
        if(fileDownloading) {
            Log.d("sx1 1", "pos1 "+position + "pos2 "+ downloadingFilePosition);
            if(position != downloadingFilePosition){
                if (downloadComplete.containsKey(position) && downloadComplete.get(position)) {
                    ((ImageView) holder.downloadBtn).setImageResource(R.mipmap.done);
                } else {
                    ((ImageView) holder.downloadBtn).setImageResource(R.mipmap.disabled);
                    ((ImageView) holder.downloadBtn).setEnabled(false);
                }
            } else {
                ((ImageView) holder.downloadBtn).setImageResource(R.mipmap.downloading);
            }
        } else {
            Log.d("sx1 2", "pos1 "+position + "pos2 "+ downloadingFilePosition);
            holder.downloadBtn.setEnabled(true);
            if (downloadComplete.containsKey(position) && downloadComplete.get(position)) {
                ((ImageView) holder.downloadBtn).setImageResource(R.mipmap.done);
            } else {
                ((ImageView) holder.downloadBtn).setImageResource(R.mipmap.downloadbtn);
            }
        }

        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fileDownloading) {
                    Toast.makeText(context, "The file is getting downloaded",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (downloadComplete.containsKey(position) && downloadComplete.get(position)) {
                        Toast.makeText(context, "The file is already downloaded",
                                Toast.LENGTH_SHORT).show();
                    } else {

                        ((ImageView) holder.downloadBtn).setImageResource(R.mipmap.downloading);
                        myOnClickListener.onitemPosition(position, fileContents.get(position));
                        setFileDownloading(position);
                        //notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("sx1", "file size "+fileContents.size());
        return fileContents.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView downloadBtn;

        MyViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            downloadBtn = view.findViewById(R.id.download_btn);
        }
    }
}

package com.example.soundrecorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private TimeAgo timeAgo;

    public Adapter(File[] files, Adapter.onItemListCLick onItemListCLick) {
        this.files = files;
        this.onItemListCLick = onItemListCLick;
    }

    private File[] files;
    private onItemListCLick onItemListCLick;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.list_title.setText(files[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(files[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView list_title;
        private TextView list_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date= itemView.findViewById(R.id.list_date);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemListCLick.onCLickListener(files[getAdapterPosition()], getAdapterPosition());
        }
    }

    public interface onItemListCLick{
        void onCLickListener(File file, int position);
    }
}

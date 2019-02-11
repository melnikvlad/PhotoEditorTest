package com.example.photoeditor.main.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.photoeditor.R;
import com.example.photoeditor.data.ImageItem;
import com.example.photoeditor.utils.BitmapDecoder;
import com.example.photoeditor.utils.TimeRandomizer;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    public static final int START = 0;
    private static final int PAUSE_FROM = 3;
    private static final int PAUSE_TO = 15;

    private final Context mContext;
    private final OnImageItemActionListener mCallback;
    private final List<ImageItem> mList;
    private boolean withProgress = true;

    public ImageAdapter(Context context, OnImageItemActionListener callback) {
        mContext = context;
        mCallback = callback;
        mList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.holder_processed_image, viewGroup, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int pos) {
        holder.bind(holder.getAdapterPosition());
        holder.parent.setOnClickListener((v) -> mCallback.onItemClick(
                holder.getAdapterPosition(),
                mList.get(holder.getAdapterPosition()))
        );
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void add(final ImageItem item) {
        withProgress = true;
        mList.add(START, item);
        notifyItemInserted(START);
    }

    public void addWithProgress(ImageItem item) {
        withProgress = false;
        mList.add(START, item);
        notifyItemInserted(START);
    }

    public void addAll(final List<ImageItem> item) {
        withProgress = false;
        mList.clear();
        mList.addAll(START, item);
        notifyDataSetChanged();
    }

    public void remove(final int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(START, pos + 1);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private View parent;
        private ProgressBar progressBar;
        private ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            progressBar = itemView.findViewById(R.id.progress);
            imageView = itemView.findViewById(R.id.img);
        }

        void bind(int pos) {

            ImageItem item = mList.get(pos);

            if (!withProgress) {
                if (item.getProgress() < 100) {
                    bindWithProgress(item);
                } else {
                    bindDefault(item);
                }
            } else {
                bindWithProgress(item);
            }

            if ((mList.size() - pos) % 2 == 0) {
                parent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.rv_item_color_1));
            } else {
                parent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.rv_item_color_2));
            }
        }

        private void bindDefault(final ImageItem item) {

            imageView.setImageBitmap(BitmapDecoder.stringToBitmap(item.getEncodedBitmapString()));

            parent.setClickable(true);
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        private void bindWithProgress(final ImageItem item) {

            Handler handler = new Handler();
            int pause = TimeRandomizer.getRandomNumberInRange(PAUSE_FROM, PAUSE_TO);
            int pauseInMillis = pause * 10;

            parent.setClickable(false);
            imageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);

            new Thread(() -> {

                int progressStatus = item.getProgress();

                while (progressStatus < 100) {
                    progressStatus++;

                    try {
                        Thread.sleep(pauseInMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int finalProgressStatus = progressStatus;
                    handler.post(() -> {
                        mCallback.onItemChangedProgress(item.getUuid(), finalProgressStatus);
                        progressBar.setProgress(finalProgressStatus);
                        item.setProgress(finalProgressStatus);
                    });
                }

                handler.post(() -> {
                    mCallback.onItemLoaded(item.getUuid());
                    parent.setClickable(true);
                    imageView.setImageBitmap(BitmapDecoder.stringToBitmap(item.getEncodedBitmapString()));
                    imageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });
            }
            ).start();
        }
    }

    public interface OnImageItemActionListener {

        void onItemClick(int pos, ImageItem item);

        void onItemLoaded(String uuid);

        void onItemChangedProgress(String uuid, int progress);

    }
}

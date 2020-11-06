package com.example.a202sgi_assignment.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>  {
    private List<String> _imageList;
    private Context _context;
    private OnActionListener _listener;

    public ImageAdapter(List<String> _imageList, Context _context) {
        this._imageList = _imageList;
        this._context = _context;

        if (_context instanceof OnActionListener){
            _listener = (OnActionListener)_context;
        } else{
            throw new RuntimeException(_context.toString() + " must implement OnActionListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_image,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String image=_imageList.get(position);
        Uri uri=Uri.parse(image);
        holder._imageView.setImageURI(uri);
        holder._imageView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final Dialog dialog = new Dialog(_context);
                dialog.setTitle("Image Option");
                dialog.setContentView(R.layout.dialog_photo_option);
                LinearLayout _cancelBtn = dialog.findViewById(R.id.cancel_button);
                LinearLayout _removeBtn = dialog.findViewById(R.id.remove_button);

                _cancelBtn.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        dialog.dismiss();
                    }
                });

                _removeBtn.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {

                        _listener.removeImage(position);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return _imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView _imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _imageView=itemView.findViewById(R.id.image);
        }
    }
    public interface OnActionListener{
        void removeImage(int position);
    }
}

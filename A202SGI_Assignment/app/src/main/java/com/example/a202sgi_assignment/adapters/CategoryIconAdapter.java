package com.example.a202sgi_assignment.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.utilities.CircleImageView;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryIconAdapter extends RecyclerView.Adapter<CategoryIconAdapter.ViewHolder> {
    private Context _context;
    private List<String> _iconUrlList;
    private OnActionListener _listener;

    public CategoryIconAdapter(Context _context, List<String> _iconUrlList) {
        this._context = _context;
        this._iconUrlList = _iconUrlList;

        if (_context instanceof OnActionListener){
            _listener = (OnActionListener)_context;
        } else{
            throw new RuntimeException(_context.toString() + " must implement OnActionListener");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_category_icon,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String _iconUrl=_iconUrlList.get(position);
        if(_iconUrl.startsWith("https"))
        {
            Picasso.get().load(_iconUrl).placeholder(R.drawable.question).into(holder._iconView);
        }
        else{
            Uri uri=Uri.parse(_iconUrl);
            holder._iconView.setImageURI(uri);
        }

        holder._iconView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.returnIcon(_iconUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _iconUrlList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView _iconView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _iconView=itemView.findViewById(R.id.icon);
        }
    }
    public interface OnActionListener
    {
        void returnIcon(String url);

    }

}

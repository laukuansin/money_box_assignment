package com.example.a202sgi_assignment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.domains.Category;
import com.example.a202sgi_assignment.utilities.CircleImageView;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectCategoryAdapter extends RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder> {
    private List<Category> _categoryList;
    private Context _context;
    private OnActionListener _listener;


    public SelectCategoryAdapter(List<Category> _categoryList, Context _context) {
        this._categoryList = _categoryList;
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
        View v= LayoutInflater.from(_context).inflate(R.layout.item_category,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Category category=_categoryList.get(position);

        holder._categoryName.setText(category.getCategoryName());
        Picasso.get().load(category.getCategoryImage()).placeholder(R.drawable.question).into(holder._iconView);
        holder._itemBlock.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.returnCategory(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView _iconView;
        private TextView _categoryName;
        private LinearLayout _itemBlock;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _iconView=itemView.findViewById(R.id.icon_view);
            _categoryName=itemView.findViewById(R.id.category_name);
            _itemBlock=itemView.findViewById(R.id.item_block);
        }
    }

    public interface OnActionListener
    {
        void returnCategory(Category category);

    }

}

package com.example.a202sgi_assignment.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.CategoryDetailActivity;
import com.example.a202sgi_assignment.domains.Category;
import com.example.a202sgi_assignment.utilities.CircleImageView;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    public Context _context;
    public List<Category> _categoryList;

    public CategoryAdapter(Context _context, List<Category> _categoryList) {
        this._context = _context;
        this._categoryList = _categoryList;
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
        if(category.isDefault())
        {
            holder._categoryName.setText(category.getCategoryName());
            holder._arrow.setVisibility(View.GONE);
        }
        else{
            holder._arrow.setVisibility(View.VISIBLE);
            holder._categoryName.setText(String.format("%1$s (Custom)",category.getCategoryName()));
            holder._itemBlock.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent=new Intent(_context, CategoryDetailActivity.class);
                    intent.putExtra(CategoryDetailActivity.CATEGORY_NAME,category.getCategoryName());
                    intent.putExtra(CategoryDetailActivity.CATEGORY_TYPE,category.getCategoryType());
                    _context.startActivity(intent);
                }
            });
        }
        Picasso.get().load(category.getCategoryImage()).placeholder(R.drawable.question).into(holder._iconView);
    }

    @Override
    public int getItemCount() {
        return _categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView _iconView;
        public LinearLayout _itemBlock;
        public TextView _categoryName;
        public ImageView _arrow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _itemBlock=itemView.findViewById(R.id.item_block);
            _arrow=itemView.findViewById(R.id.arrow);
            _iconView=itemView.findViewById(R.id.icon_view);
            _categoryName=itemView.findViewById(R.id.category_name);
        }
    }
}

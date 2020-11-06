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
import com.example.a202sgi_assignment.activities.TransactionDetailActivity;
import com.example.a202sgi_assignment.domains.Transaction;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Context _context;
    private List<Transaction> _transactionList;
    private SimpleDateFormat _fullFormat;
    private SimpleDateFormat _dateFormat;
    private String date;

    public TransactionAdapter(Context _context, List<Transaction> _transactionList) {
        this._context = _context;
        this._transactionList = _transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(_context).inflate(R.layout.item_transaction,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Transaction transaction=_transactionList.get(position);

        _dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        _fullFormat= new SimpleDateFormat("EEEE, dd MMM yyyy");

        try {
            Date _tempDate=_dateFormat.parse(transaction.getDate());
            date=_fullFormat.format(_tempDate);
            holder._date.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder._amount.setText(String.format("%2$s: %.2f",transaction.getAmount(),transaction.getCategoryType()));
        holder._categoryName.setText(transaction.getCategoryName());
        if(!transaction.getDescription().isEmpty())
        {
            holder._description.setText(transaction.getDescription());
        }
        Picasso.get().load(transaction.getCategoryImage()).placeholder(R.drawable.question).into(holder._icon);

        holder._itemBlock.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent=new Intent(_context, TransactionDetailActivity.class);
                intent.putExtra(TransactionDetailActivity.TRANSACTION_ID,transaction.getTransactionID());
                _context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return _transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout _itemBlock;
        private TextView _date;
        private TextView _amount;
        private ImageView _icon;
        private TextView _categoryName;
        private TextView _description;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _itemBlock=itemView.findViewById(R.id.item_block);
            _date=itemView.findViewById(R.id.date);
            _amount=itemView.findViewById(R.id.amount);
            _icon=itemView.findViewById(R.id.icon);
            _categoryName=itemView.findViewById(R.id.category_name);
            _description=itemView.findViewById(R.id.description);
        }
    }
}

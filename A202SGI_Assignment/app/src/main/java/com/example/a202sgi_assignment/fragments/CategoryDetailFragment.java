package com.example.a202sgi_assignment.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.CategoryDetailActivity;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Category;
import com.example.a202sgi_assignment.domains.Transaction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CategoryDetailFragment extends BaseFragment {
    private String _name,_type,_imageUrl,childID;
    private TextView name;
    private RadioButton type;
    private ImageView iconView;
    private DatabaseReference _databaseRef;
    private DatabaseReference _databaseRef_2;

    public CategoryDetailFragment() {
    }
    public static CategoryDetailFragment newInstance(String name,String type) {
        CategoryDetailFragment fragment = new CategoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(CategoryDetailActivity.CATEGORY_NAME,name);
        args.putString(CategoryDetailActivity.CATEGORY_TYPE,type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _name = getArguments().getString(CategoryDetailActivity.CATEGORY_NAME);
            _type=getArguments().getString(CategoryDetailActivity.CATEGORY_TYPE);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_category_detail, container, false);

        name=view.findViewById(R.id.category_name);
        type=view.findViewById(R.id.category_type);
        iconView=view.findViewById(R.id.category_image);
        _databaseRef=FirebaseDatabase.getInstance().getReference("category");
        _databaseRef_2=FirebaseDatabase.getInstance().getReference("transaction");
        new RetrieveData().execute();
        return view;

    }

    public void removeCategory()
    {
        DatabaseReference _delRef=FirebaseDatabase.getInstance().getReference("category").child(childID);
        _delRef.removeValue();

        _databaseRef_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Transaction transaction=dataSnapshot.getValue(Transaction.class);

                    if(transaction.getCategoryName().equals(_name)&&transaction.getUserID().equals(getSessionHandler().getFacebookIdentity()))
                    {
                        dataSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        Toast.makeText(getContext(), "Remove the category and the related transaction", Toast.LENGTH_SHORT).show();
    }

    private class RetrieveData extends AsyncTask<Void,Void,Void> {
        private ProgressDialog _progressDialog= new ProgressDialog(getContext());


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.setMessage(getResources().getString(R.string.progress_loading));
            _progressDialog.setIndeterminate(false);
            _progressDialog.setCancelable(false);
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            _databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Category category=dataSnapshot.getValue(Category.class);

                        if(category.getCategoryName().equals(_name)&&category.getUserID().equals(getSessionHandler().getFacebookIdentity())&&
                        category.getCategoryType().equals(_type))
                        {
                            _imageUrl=category.getCategoryImage();
                            childID=dataSnapshot.getKey();
                        }
                    }

                    name.setText(_name);
                    type.setText(_type);
                    Picasso.get().load(_imageUrl).placeholder(R.drawable.question).into(iconView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        };
    }
}

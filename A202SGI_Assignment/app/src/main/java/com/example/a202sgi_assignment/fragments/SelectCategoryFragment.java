package com.example.a202sgi_assignment.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.SelectCategoryActivity;
import com.example.a202sgi_assignment.adapters.SelectCategoryAdapter;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectCategoryFragment extends BaseFragment {
    private RecyclerView _recyclerView;
    private SelectCategoryAdapter _adapter;
    private String categoryType ="";
    private List<Category> _categoryList;
    private DatabaseReference _databaseRef;
    private RetrieveData _getData=null;

    public SelectCategoryFragment() {
    }
    public static SelectCategoryFragment newInstance(String category) {
        SelectCategoryFragment fragment = new SelectCategoryFragment();
        Bundle args = new Bundle();
        args.putString(SelectCategoryActivity.CATEGORY_TYPE,category);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryType = getArguments().getString(SelectCategoryActivity.CATEGORY_TYPE);
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_select_category, container, false);
        _recyclerView=view.findViewById(R.id.recyclerView);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        _recyclerView.setNestedScrollingEnabled(false);
        _categoryList=new ArrayList<>();
        _databaseRef= FirebaseDatabase.getInstance().getReference("category");




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(categoryType);
    }

    public void loadData(String type)
    {
        if(_getData==null)
        {
            _getData=new RetrieveData(type);
            _getData.execute();
        }
    }
    public class RetrieveData extends AsyncTask<Void,Void,Void>
    {
        private String type;

        public RetrieveData(String type) {
            this.type = type;
        }
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
                    for(DataSnapshot postSnapshot:snapshot.getChildren())
                    {
                        Category category=postSnapshot.getValue(Category.class);

                        if(category.isDefault())
                        {
                            if(category.getCategoryType().equals(type))
                            {
                                _categoryList.add(category);
                            }
                        }
                        else{
                            if(category.getCategoryType().equals(type)&&category.getUserID().equals(getSessionHandler().getFacebookIdentity()))
                            {
                                _categoryList.add(category);
                            }
                        }
                    }
                    _adapter=new SelectCategoryAdapter(_categoryList,getContext());
                    _recyclerView.setAdapter(_adapter);
                    _getData=null;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    _getData=null;
                }
            });
            return null;
        }
    }
}

package com.example.a202sgi_assignment.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.adapters.CategoryAdapter;
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

public class CategoryFragment extends BaseFragment {
    public static final String CATEGORY_TYPE="category-type";
    private String categoryType="";
    private RecyclerView _recyclerView;
    private List<Category> _categoryList;
    private DatabaseReference _databaseRef;
    private CategoryAdapter _adapterDef;
    private RetrieveCategory _getCategory=null;


    public CategoryFragment() {
    }
    public static CategoryFragment newInstance(String categoryType) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY_TYPE, categoryType);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryType = getArguments().getString(CATEGORY_TYPE);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_category, container, false);
        _recyclerView=view.findViewById(R.id.recyclerView);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        _categoryList=new ArrayList<>();
        _databaseRef= FirebaseDatabase.getInstance().getReference("category");
        _databaseRef.keepSynced(true);



        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        loadCategory(categoryType);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategory(categoryType);
    }

    public void loadCategory(String type)
    {
        if(_getCategory==null)
        {
            _getCategory=new RetrieveCategory(type);
            _getCategory.execute();
        }
    }


    public class RetrieveCategory extends AsyncTask<Void,Void,Void>
    {
        private String type;
        public RetrieveCategory(String type) {
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
            _categoryList.clear();
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
                        if(type.equals(category.getCategoryType()))
                        {
                            if(category.isDefault())
                            {
                                _categoryList.add(category);
                            }else if(category.getUserID().equals(getSessionHandler().getFacebookIdentity()))
                            {
                                _categoryList.add(category);
                            }
                        }
                    }
                    _adapterDef=new CategoryAdapter(getContext(),_categoryList);
                    _recyclerView.setAdapter(_adapterDef);
                    _getCategory=null;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    _getCategory=null;
                }
            });
            return null;
        }
    }
}

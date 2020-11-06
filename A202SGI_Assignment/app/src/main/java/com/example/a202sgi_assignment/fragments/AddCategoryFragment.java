package com.example.a202sgi_assignment.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.CategoryImageActivity;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Category;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;

public class AddCategoryFragment extends BaseFragment {
    public static final int SELECT_ICON_CODE=201;
    private RadioGroup _radioGroup;
    private String categoryName="";
    private String categoryType="Expense";
    private String categoryImage="";
    private ImageView _categoryView;
    private TextInputEditText _categoryName;
    private DatabaseReference _databaseRef;
    private ProgressDialog _progressDialog;
    public AddCategoryFragment() {
    }
    public static AddCategoryFragment newInstance() {
        AddCategoryFragment fragment = new AddCategoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_category, container, false);
        _radioGroup=view.findViewById(R.id.radio_group);
        _progressDialog= new ProgressDialog(getContext());
        _categoryView=view.findViewById(R.id.category_image);
        _databaseRef= FirebaseDatabase.getInstance().getReference("category");
        _categoryName=view.findViewById(R.id.category_name);
        _categoryView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent=new Intent(getContext(), CategoryImageActivity.class);
                startActivityForResult(intent,SELECT_ICON_CODE);
            }
        });
        return view;

    }

    public void saveNewCategory()
    {
        boolean check=true;
        int checkId=_radioGroup.getCheckedRadioButtonId();
        categoryType=findRadioButton(checkId);
        categoryName=_categoryName.getText().toString().trim();
        if(categoryName.isEmpty())
        {
            Toast.makeText(getContext(), "Please enter the category name", Toast.LENGTH_SHORT).show();
            check=false;
        }
        else if(categoryType.isEmpty())
        {
            Toast.makeText(getContext(), "Please select the category type", Toast.LENGTH_SHORT).show();
            check=false;
        }
        else if(categoryImage.isEmpty())
        {
            Toast.makeText(getContext(), "Please select the category image", Toast.LENGTH_SHORT).show();
            check=false;
        }

        if(check)
        {

            Category category=new Category();
            category.setCategoryName(categoryName);
            category.setCategoryType(categoryType);
            category.setCategoryImage(categoryImage);
            category.setDefault(false);
            category.setUserID(getSessionHandler().getFacebookIdentity());

            checkDatabase(category);
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
        }
    }

    private void checkDatabase(final Category category) {
        _progressDialog.setMessage(getResources().getString(R.string.progress_saving));
        _progressDialog.setIndeterminate(false);
        _progressDialog.setCancelable(false);
        _progressDialog.show();
        _databaseRef.keepSynced(true);
        _databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean check=true;
                for(DataSnapshot postSnapshot:snapshot.getChildren())
                {
                    Category categoryTemp=postSnapshot.getValue(Category.class);

                    if(categoryTemp.isDefault())
                    {
                        if(TextUtils.equals(categoryTemp.getCategoryName().toLowerCase(),category.getCategoryName().toLowerCase())&&TextUtils.equals(categoryTemp.getCategoryType(),category.getCategoryType()))
                        {
                            check=false;
                        }
                    }
                    else{
                        if(TextUtils.equals(categoryTemp.getCategoryName().toLowerCase(),category.getCategoryName().toLowerCase())&&TextUtils.equals(categoryTemp.getCategoryType(),category.getCategoryType())
                                &&TextUtils.equals(categoryTemp.getUserID(),category.getUserID()))
                        {
                            check=false;
                        }
                    }

                }
                if(check)
                {
                    saveToDatabase(category);
                    Toast.makeText(getContext(), "Add Category Success", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                else{
                    ErrorAlert("The category already exist.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                        }
                    }).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void saveToDatabase(Category category) {
        String id=_databaseRef.push().getKey();
        _databaseRef.child(id).setValue(category);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_ICON_CODE)
        {
            if(resultCode== RESULT_OK)
            {
                categoryImage=data.getStringExtra(CategoryImageActivity.ICON_URL);
                if(categoryImage.startsWith("https"))
                {
                    Picasso.get().load(categoryImage).placeholder(R.drawable.question).into(_categoryView);

                }
                else{
                    Uri uri=Uri.parse(categoryImage);
                    _categoryView.setImageURI(uri);
                }
            }
        }
    }

    private String findRadioButton(int id) {
        String type="";
        switch(id){
            case R.id.income:
                type= "Income";
                    break;
            case R.id.expense:
                type= "Expense";
        }
        return type;
    }
}

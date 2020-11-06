package com.example.a202sgi_assignment.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.adapters.CategoryIconAdapter;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class CategoryImageFragment extends BaseFragment{
    private RecyclerView _recyclerView;
    private CategoryIconAdapter _adapter;
    private List<String> _iconUrlList;
    private DatabaseReference _databaseRef;
    private RetrieveImage _getImage=null;
    public static final int SELECT_IMAGE_CODE=10;
    public static final int SELECT_CAMERA_CODE=11;
    public CategoryImageFragment() {
    }
    public static CategoryImageFragment newInstance() {
        CategoryImageFragment fragment = new CategoryImageFragment();

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
        View view=inflater.inflate(R.layout.fragment_category_image, container, false);

        _recyclerView=view.findViewById(R.id.recyclerView);
        _recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        _recyclerView.setNestedScrollingEnabled(false);
        _iconUrlList=new ArrayList<>();

        _databaseRef=FirebaseDatabase.getInstance().getReference("uploads");



        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData()
    {
        if(_getImage==null)
        {
            _getImage=new RetrieveImage();
            _getImage.execute();
        }
    }

    public class RetrieveImage extends AsyncTask<Void,Void,Void>
   {
       private ProgressDialog _progressDialog= new ProgressDialog(getContext());


       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           _progressDialog.setMessage(getResources().getString(R.string.progress_loading));
           _progressDialog.setIndeterminate(false);
           _progressDialog.setCancelable(false);
           _progressDialog.show();
           _iconUrlList.clear();
       }

       @Override
       protected Void doInBackground(Void... voids) {

           _databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {

                   for(DataSnapshot postSnapshot: snapshot.getChildren())
                   {
                       String imageUrl=postSnapshot.getValue(String.class);
                       _iconUrlList.add(imageUrl);
                   }
                   if (_progressDialog.isShowing())
                       _progressDialog.dismiss();
                   _adapter=new CategoryIconAdapter(getContext(),_iconUrlList);
                   _recyclerView.setAdapter(_adapter);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   if (_progressDialog.isShowing())
                       _progressDialog.dismiss();
                   Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
               }
           });
            _getImage=null;
           return null;
       }
   }

   public void addIcon()
   {
       final Dialog dialog = new Dialog(getContext());
       dialog.setTitle("Option");
       dialog.setContentView(R.layout.dialog_add_logo);
       LinearLayout _uploadImage = dialog.findViewById(R.id.uploadImageButton);
       LinearLayout _takePhoto = dialog.findViewById(R.id.camera_button);

       _uploadImage.setOnClickListener(new OnSingleClickListener() {
           @Override
           public void onSingleClick(View v) {
               uploadImage();
               dialog.dismiss();
           }
       });

       _takePhoto.setOnClickListener(new OnSingleClickListener() {
           @Override
           public void onSingleClick(View v) {
                takePhoto();
               dialog.dismiss();
           }
       });
       dialog.show();
   }
    private void uploadImage()
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,SELECT_IMAGE_CODE);
    }
    private void takePhoto()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, SELECT_CAMERA_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_IMAGE_CODE)
        {
            if(resultCode==RESULT_OK&&data!=null)
            {
                Uri uri=data.getData();
                String uriImage=uri.toString();
                String id=_databaseRef.push().getKey();
                _databaseRef.child(id).setValue(uriImage);
            }
        }
        if(requestCode==SELECT_CAMERA_CODE)
        {
            if(resultCode==RESULT_OK&&data!=null)
            {
                Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                String uriImage=saveImage(bitmap);
                String id=_databaseRef.push().getKey();
                _databaseRef.child(id).setValue(uriImage);
            }
        }
    }

    private String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/image");
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


}

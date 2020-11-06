package com.example.a202sgi_assignment.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;
import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.SelectCategoryActivity;
import com.example.a202sgi_assignment.adapters.ImageAdapter;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Transaction;
import com.example.a202sgi_assignment.utilities.DigitsInputFilter;
import com.example.a202sgi_assignment.utilities.NumberTextWatcher;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class AddTransactionFragment extends BaseFragment{
    public static final int SELECT_ICON_CODE=202;
    public static final int SELECT_IMAGE_CODE=10;
    public static final int SELECT_CAMERA_CODE=11;

    private LinearLayout _categoryLayout,_dateLayout,_imageLayout,_remindLayout;
    private Button _uploadImageButton,_takePictureButton;
    private RecyclerView _recyclerViewImage;
    private TextView _category,_date;
    private TextView _alarmDate;
    private ImageView _removeAlarm;
    private TextInputLayout _amountLayout;
    private TextInputEditText _amount,_description;
    private ImageAdapter _adapterImage;
    private String date,day,alarmDate;
    private ImageView _iconView;
    private double amount;
    private int _month,_year;
    private String categoryName="",categoryType="",categoryImage="",description="";
    private SimpleDateFormat dateFormat,dayFormat,dateTimeFormat;
    private Calendar calendar;
    private Date templateDate;
    private List<String> _imageList;
    private DatabaseReference _databaseRef;
    private ProgressDialog _progressDialog;

    public AddTransactionFragment() {
    }
    public static AddTransactionFragment newInstance() {
        AddTransactionFragment fragment = new AddTransactionFragment();

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
        View view=inflater.inflate(R.layout.fragment_add_transaction, container, false);
        _categoryLayout=view.findViewById(R.id.category_layout);
        _dateLayout=view.findViewById(R.id.date_layout);
        _category=view.findViewById(R.id.category);
        _date=view.findViewById(R.id.date);
        _amountLayout=view.findViewById(R.id.amount_layout);
        _description=view.findViewById(R.id.description);
        _amount=view.findViewById(R.id.amount);
        _remindLayout=view.findViewById(R.id.remind_layout);
        _iconView=view.findViewById(R.id.category_image);
        _databaseRef= FirebaseDatabase.getInstance().getReference("transaction");
        _progressDialog= new ProgressDialog(getContext());
        _imageLayout=view.findViewById(R.id.imageLayout);
        _alarmDate=view.findViewById(R.id.alarm);
        _removeAlarm=view.findViewById(R.id.remove_alarm_button);
        _imageList=new ArrayList<>();
        _uploadImageButton=view.findViewById(R.id.uploadImageButton);
        _takePictureButton=view.findViewById(R.id.takePictureButton);
        _recyclerViewImage=view.findViewById(R.id.recyclerView);
        _recyclerViewImage.setHasFixedSize(true);
        _recyclerViewImage.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        calendar=Calendar.getInstance();
        alarmDate="";
        dateTimeFormat=new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        dayFormat=new SimpleDateFormat("EEEE");
        date=dateFormat.format(calendar.getTime());
        day=dayFormat.format(calendar.getTime());
        String[] dateValue = date.split("/");
        _month=Integer.parseInt(dateValue[1]);
        _year=Integer.parseInt(dateValue[2]);
        _date.setText(String.format("%1$s ,%2$s",day,date));
        _amount.addTextChangedListener(new NumberTextWatcher(_amount));
        _amount.setFilters(new InputFilter[]{new DigitsInputFilter(Integer.MAX_VALUE,Integer.MAX_VALUE,99999999.99)});
        _removeAlarm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _alarmDate.setText("");
                alarmDate="";
                _removeAlarm.setVisibility(View.GONE);
            }
        });
        _dateLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle args = new Bundle();
                String[] dateValue = date.split("/");
                args.putInt("year", Integer.parseInt(dateValue[2]));
                args.putInt("month", Integer.parseInt(dateValue[1]) - 1);
                args.putInt("day", Integer.parseInt(dateValue[0]));


                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.setArguments(args);
                dateDialogFragment.setCallBack(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date=String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + String.valueOf(year);
                        try {
                            Date tempDate =dateFormat.parse(date);
                            day=dayFormat.format(tempDate);
                            _year=year;
                            _month=monthOfYear+1;
                            _date.setText(String.format("%1$s ,%2$s",day,date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });
                dateDialogFragment.show(getActivity().getSupportFragmentManager(),"Date");
            }
        });
        _remindLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle args = new Bundle();
                String[] dateValue = date.split("/");
                args.putInt("year", Integer.parseInt(dateValue[2]));
                args.putInt("month", Integer.parseInt(dateValue[1]) - 1);
                args.putInt("day", Integer.parseInt(dateValue[0]));


                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.setArguments(args);
                dateDialogFragment.setCallBack(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        alarmDate=String.format("%02d", dayOfMonth) + "/" + String.format("%02d", monthOfYear + 1) + "/" + String.valueOf(year);
                        _alarmDate.setText(alarmDate);
                        _removeAlarm.setVisibility(View.VISIBLE);
                    }
                });
                dateDialogFragment.show(getActivity().getSupportFragmentManager(),"Date");
            }
        });
        _categoryLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent=new Intent(getContext(), SelectCategoryActivity.class);
                startActivityForResult(intent,SELECT_ICON_CODE);
            }
        });
        _uploadImageButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_IMAGE_CODE);
            }
        });

        _takePictureButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, SELECT_CAMERA_CODE);
            }
        });

        return view;

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_ICON_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                categoryType=data.getStringExtra(SelectCategoryActivity.CATEGORY_TYPE);
                categoryImage=data.getStringExtra(SelectCategoryActivity.ICON_URL);
                categoryName=data.getStringExtra(SelectCategoryActivity.CATEGORY_NAME);
                Picasso.get().load(categoryImage).placeholder(R.drawable.question).into(_iconView);
                _category.setText(categoryName);


            }
        }
        if(requestCode==SELECT_IMAGE_CODE)
        {
            if(resultCode==RESULT_OK&&data!=null)
            {
                Uri uri=data.getData();
                String uriImage=uri.toString();
                _imageLayout.setVisibility(View.VISIBLE);
                _imageList.add(uriImage);
                _adapterImage=new ImageAdapter(_imageList,getContext());
                _recyclerViewImage.setAdapter(_adapterImage);
            }
        }
        if(requestCode==SELECT_CAMERA_CODE)
        {
            if(resultCode==RESULT_OK&&data!=null)
            {
                Bitmap bitmap=(Bitmap)data.getExtras().get("data");

                String uriImage=saveImage(bitmap);
                _imageLayout.setVisibility(View.VISIBLE);
                _imageList.add(uriImage);
                _adapterImage=new ImageAdapter(_imageList,getContext());
                _recyclerViewImage.setAdapter(_adapterImage);
            }
        }
    }

    public void saveTransaction()
    {
        boolean check=true;


        description=_description.getText().toString();

        if(_amount.getText().toString().isEmpty())
        {
            Toast.makeText(getContext(), "The amount cannot be empty", Toast.LENGTH_SHORT).show();
            _amountLayout.setError("Please enter the amount");
            check=false;
        }
        else{
            if(_amount.getText().toString().equals("."))
            {
                Toast.makeText(getContext(), "The amount cannot be '.'", Toast.LENGTH_SHORT).show();
                _amountLayout.setError("Please enter the amount");
                check=false;
            }
            else{
                amount=Double.valueOf(_amount.getText().toString());
                if(amount==0)
                {
                    Toast.makeText(getContext(), "The amount cannot be 0", Toast.LENGTH_SHORT).show();
                    _amountLayout.setError("Please enter the amount");
                    check=false;
                }
            }

        }
        if(categoryName.isEmpty()&&categoryType.isEmpty()&&categoryImage.isEmpty())
        {
            Toast.makeText(getContext(), "The category cannot be empty", Toast.LENGTH_SHORT).show();
            check=false;
        }



        if (check)
        {



            Transaction transaction=new Transaction();
            transaction.setAmount(amount);
            transaction.setCategoryImage(categoryImage);
            transaction.setCategoryName(categoryName);
            transaction.setCategoryType(categoryType);
            transaction.setDate(date);
            transaction.setDescription(description);
            transaction.setMonth(_month);
            transaction.setYear(_year);
            transaction.setUserID(getSessionHandler().getFacebookIdentity());
            transaction.setImageList(_imageList);
            transaction.setAlarmDate(alarmDate);

            _progressDialog.setMessage(getResources().getString(R.string.progress_saving));
            _progressDialog.setIndeterminate(false);
            _progressDialog.setCancelable(false);
            _progressDialog.show();
            _databaseRef.keepSynced(true);

            String id=_databaseRef.push().getKey();
            _databaseRef.child(id).setValue(transaction);

            if(!alarmDate.isEmpty())
            {
                try {
                    templateDate= dateFormat.parse(alarmDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendarAlarm = Calendar.getInstance();
                calendarAlarm.setTime(templateDate);
                NotifyMe notifyMe = new NotifyMe.Builder(getContext())
                        .title("Notice")
                        .small_icon(R.mipmap.logo_no_til)
                        .content(String.format("Transaction %2$s: RM %.2f.",amount,categoryName))
                        .time(calendarAlarm)
                        .key(id)
                        .build();
            }

            Toast.makeText(getContext(), "Add transaction success!!", Toast.LENGTH_SHORT).show();
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
            getActivity().finish();
        }
    }

   public void removeImage(int position)
   {
       _imageList.remove(position);
       if(_imageList.size()==0)
       {
           _imageLayout.setVisibility(View.GONE);
       }
       _adapterImage=new ImageAdapter(_imageList,getContext());
       _recyclerViewImage.setAdapter(_adapterImage);
   }
}

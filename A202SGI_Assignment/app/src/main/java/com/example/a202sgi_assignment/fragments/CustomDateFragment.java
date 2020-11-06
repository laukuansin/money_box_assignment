package com.example.a202sgi_assignment.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class CustomDateFragment extends DialogFragment {
    private static final String START_DATE_BUNDLE = "start-date";
    private static final String END_DATE_BUNDLE = "end-date";
    private static final String TYPE_BUNDLE = "type";


    private String startDate,endDate,type;
    private Fragment _parentFragment;
    private SimpleDateFormat _dateFormat;
    private Calendar _calendar;
    private TextView _startDateView,_endDateView;
    public CustomDateFragment() {
    }
    public void setParentFragment(Fragment fragment){
        _parentFragment = fragment;
    }

    public static CustomDateFragment newInstance(String startDate, String endDate,String type, Fragment _parentFragment) {
        CustomDateFragment fragment=new CustomDateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(START_DATE_BUNDLE, startDate);
        bundle.putString(END_DATE_BUNDLE, endDate);
        bundle.putString(TYPE_BUNDLE, type);

        fragment.setArguments(bundle);
        fragment.setParentFragment(_parentFragment);
        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        startDate = args.getString(START_DATE_BUNDLE);
        endDate = args.getString(END_DATE_BUNDLE);
        type = args.getString(TYPE_BUNDLE);

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(START_DATE_BUNDLE, startDate);
        outState.putString(END_DATE_BUNDLE, endDate);
        outState.putString(TYPE_BUNDLE, type);

    }
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null){
            startDate = savedInstanceState.getString(START_DATE_BUNDLE);
            endDate = savedInstanceState.getString(END_DATE_BUNDLE);
            type = savedInstanceState.getString(TYPE_BUNDLE);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_date, container);

        _startDateView=(TextView)view.findViewById(R.id.start_date);
        _endDateView=(TextView)view.findViewById(R.id.end_date);
        _dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        getDialog().setTitle("Select Date");
        _startDateView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle args = new Bundle();

                String[] dateValue = startDate.split("/");
                args.putInt("day",  Integer.parseInt(dateValue[0]));
                args.putInt("month", Integer.parseInt(dateValue[1]) - 1);
                args.putInt("year", Integer.parseInt(dateValue[2]));

                FragmentManager fragmentManager = getFragmentManager();
                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.setArguments(args);
                dateDialogFragment.setCallBack(new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDate=String.format("%02d", dayOfMonth)+ "/" + String.format("%02d", monthOfYear + 1) + "/" +  String.valueOf(year) ;
                        _startDateView.setText(startDate);
                    }
                });

                dateDialogFragment.show(fragmentManager, "Start Date");
            }
        });
        _endDateView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle args = new Bundle();
                final String[] endDateValue = endDate.split("/");
                String[] startDateValue = startDate.split("/");

                args.putInt("day",  Integer.parseInt(endDateValue[0]));
                args.putInt("month", Integer.parseInt(endDateValue[1]) - 1);
                args.putInt("year", Integer.parseInt(endDateValue[2]));
                args.putInt("day1",  Integer.parseInt(startDateValue[0]));
                args.putInt("month1", Integer.parseInt(startDateValue[1]) - 1);
                args.putInt("year1", Integer.parseInt(startDateValue[2]));
                FragmentManager fragmentManager = getFragmentManager();
                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.setMinimumDate(args.getInt("year1"), args.getInt("month1"), args.getInt("day1"));
                dateDialogFragment.setArguments(args);
                dateDialogFragment.setCallBack(new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate=String.format("%02d", dayOfMonth)+ "/" + String.format("%02d", monthOfYear + 1) + "/" +  String.valueOf(year);
                        _endDateView.setText(endDate);
                    }
                });

                dateDialogFragment.show(fragmentManager,"End Date");
            }
        });
        Button editButton = (Button)view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                OnCustomDateDialogListener activity = (OnCustomDateDialogListener)_parentFragment;
                activity.onReturnCustomDate(startDate,endDate);
                CustomDateFragment.this.dismiss();
            }
        });

        Button cancelButton = (Button)view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new OnSingleClickListener(){
            @Override
            public void onSingleClick(View v){
                CustomDateFragment.this.dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    public void LoadData(){
        if(TextUtils.equals(type,"All"))
        {
            _calendar = Calendar.getInstance();
            _calendar.add(Calendar.MONTH, 0);
            _calendar.set(Calendar.DATE, _calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            startDate=_dateFormat.format(_calendar.getTime());

            _calendar.set(Calendar.DATE, _calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            endDate=_dateFormat.format(_calendar.getTime());
        }

        _startDateView.setText(startDate);
        _endDateView.setText(endDate);
    }
    @Override
    public void onStart() {
        super.onStart();

        LoadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public interface OnCustomDateDialogListener{
        void onReturnCustomDate(String startDate,String endDate);
    }
}

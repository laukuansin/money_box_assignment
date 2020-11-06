package com.example.a202sgi_assignment.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class DateDialogFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener _onDateSet;
    private boolean _hasMinimumDate = false;
    private Calendar _minimumDate;

    public DateDialogFragment() {
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        _onDateSet = ondate;
    }

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    public void setMinimumDate(int year, int month, int day){
        _hasMinimumDate = true;

        _minimumDate = Calendar.getInstance();
        _minimumDate.set(year, month, day);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), _onDateSet, year, month, day);

        if (_hasMinimumDate){
            dialog.getDatePicker().setMinDate(_minimumDate.getTimeInMillis());
        }

        return dialog;
    }
}
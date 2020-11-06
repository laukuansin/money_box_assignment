package com.example.a202sgi_assignment.utilities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.example.a202sgi_assignment.R;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class YearPickerDialog extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.dialog_year_picker, null);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(1000);
        yearPicker.setMaxValue(3000);
        yearPicker.setValue(year);
        builder.setView(dialog).setPositiveButton((getResources().getString(R.string.alert_ok)), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                listener.onDateSet(null, yearPicker.getValue(), 0, 0);
            }
        }).setNegativeButton((getResources().getString(R.string.alert_close)), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                YearPickerDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
}

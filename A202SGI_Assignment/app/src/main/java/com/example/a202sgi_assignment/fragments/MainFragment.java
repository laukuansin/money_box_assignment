package com.example.a202sgi_assignment.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.MainActivity;
import com.example.a202sgi_assignment.adapters.TransactionAdapter;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Transaction;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFragment extends BaseFragment {
    private LinearLayoutManager layoutManager;
    private TextView _dateView, _incomeView, _expenseView, _balanceView;
    private ImageButton _leftClick;
    private ImageButton _rightClick;
    private SimpleDateFormat _dateFormat, monthYearFormat;
    private String currentDate;
    private Calendar _calendar;
    private double income = 0, expense = 0, balance = 0;
    private RecyclerView _recyclerView;
    private TransactionAdapter _adapter;
    private LinearLayout _emptyMsgLayout;
    private List<Transaction> _transactionList;
    private String[] months;
    private DatabaseReference _databaseRef;
    private RetrieveTransaction _getTransaction = null;
    private int monthIndex, _year;
    private int monthInt, yearInt, dayInt;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();

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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        _dateView = view.findViewById(R.id.date);
        _leftClick = view.findViewById(R.id.leftClick);
        _rightClick = view.findViewById(R.id.rightClick);
        _emptyMsgLayout = view.findViewById(R.id.emptyMessage_layout);
        layoutManager = new LinearLayoutManager(getContext());

        _recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        _recyclerView.setLayoutManager(layoutManager);
        _transactionList = new ArrayList<>();
        _incomeView = view.findViewById(R.id.income);
        _balanceView = view.findViewById(R.id.balance);
        _expenseView = view.findViewById(R.id.expense);
        months = getResources().getStringArray(R.array.months);
        _calendar = Calendar.getInstance();
        monthYearFormat = new SimpleDateFormat("MMMM yyyy");
        _dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = _dateFormat.format(_calendar.getTime());
        String[] dateValue = currentDate.split("/");
        monthIndex = Integer.parseInt(dateValue[1]) - 1;
        _year = Integer.parseInt(dateValue[2]);
        _databaseRef = FirebaseDatabase.getInstance().getReference("transaction");
        setAndCheckDate();
        _leftClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthIndex -= 1;
                setAndCheckDate();
            }
        });

        _rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthIndex += 1;
                setAndCheckDate();
            }
        });
        _dateView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                datePicker();
            }
        });


        return view;

    }

    private void datePicker() {

        DatePickerDialog monthDatePickerDialog = new DatePickerDialog(getContext(),
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                monthIndex = month;
                _year = year;
                setAndCheckDate();


            }
        }, _year, monthIndex, 1) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            }
        };
        monthDatePickerDialog.setTitle("Select month and year");
        monthDatePickerDialog.show();


    }

    private void loadData(int month, int year) {
        if (_getTransaction == null) {
            _getTransaction = new RetrieveTransaction(month, year);
            _getTransaction.execute();
        }
    }


    private void setAndCheckDate() {
        if (monthIndex > 11) {
            monthIndex = 0;
            _year += 1;
        }
        if (monthIndex < 0) {
            monthIndex = 11;
            _year -= 1;
        }
        _transactionList.clear();

        loadData(monthIndex + 1, _year);
        String month = months[monthIndex];
        _dateView.setText(String.format("%1$s %2$d", month, _year));

    }

    @Override
    public void onResume() {
        super.onResume();
        setAndCheckDate();

    }

    private class RetrieveTransaction extends AsyncTask<Void, Void, Void> {
        private ProgressDialog _progressDialog = new ProgressDialog(getContext());
        private int month, year;

        public RetrieveTransaction(int month, int year) {
            this.month = month;
            this.year = year;
        }

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
            Query query = _databaseRef.orderByChild("date");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
                        if (transaction.getMonth() == month && transaction.getYear() == year && transaction.getUserID().equals(getSessionHandler().getFacebookIdentity())) {
                            transaction.setTransactionID(dataSnapshot.getRef().getKey());
                            _transactionList.add(transaction);
                        }
                    }
                    income = 0;
                    expense = 0;
                    balance = 0;
                    if (_transactionList.isEmpty()) {
                        _recyclerView.setVisibility(View.GONE);
                        _emptyMsgLayout.setVisibility(View.VISIBLE);


                        _incomeView.setText(String.valueOf(income));
                        _expenseView.setText(String.valueOf(expense));
                        _balanceView.setText(String.valueOf(balance));
                    } else {
                        _emptyMsgLayout.setVisibility(View.GONE);
                        _recyclerView.setVisibility(View.VISIBLE);
                        for (Transaction transaction : _transactionList) {
                            if (transaction.getCategoryType().equals("Income")) {
                                income += transaction.getAmount();
                            } else {
                                expense += transaction.getAmount();
                            }
                        }
                        balance = income - expense;
                        _incomeView.setText(String.format("%.2f", income));
                        _expenseView.setText(String.format("%.2f", expense));
                        _balanceView.setText(String.format("%.2f", balance));
                        _adapter = new TransactionAdapter(getContext(), _transactionList);
                        _recyclerView.setAdapter(_adapter);


                    }
                    _getTransaction = null;


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    _getTransaction = null;

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();

        }
    }

    public void ExportData() {
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.bottom_dialog_export);

        String month = monthYearFormat.format(_calendar.getTime());
        String monthFull = _dateFormat.format(_calendar.getTime());

        String[] dateValue = monthFull.split("/");
        monthInt = Integer.parseInt(dateValue[1]);
        yearInt = Integer.parseInt(dateValue[2]);
        dayInt = Integer.parseInt(dateValue[0]);

        Button _exportBtn = dialog.findViewById(R.id.export_btn);
        ImageView _closeBtn = dialog.findViewById(R.id.close_button);
        final TextView _month = dialog.findViewById(R.id.month);
        _month.setText(month);
        dialog.show();

        _closeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();
            }
        });
        _exportBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                exportData(monthInt, yearInt);
                dialog.dismiss();
            }
        });
        _month.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                DatePickerDialog monthDatePickerDialog = new DatePickerDialog(getContext(),
                        AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month + 1) + "/" + String.valueOf(year);
                        try {
                            Date tempDate = _dateFormat.parse(date);
                            String tempSelectMonth = monthYearFormat.format(tempDate);
                            _month.setText(tempSelectMonth);
                            monthInt = month + 1;
                            yearInt = year;
                            dayInt = dayOfMonth;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, yearInt, monthInt - 1, dayInt) {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                    }
                };
                monthDatePickerDialog.setTitle("Select month and year");
                monthDatePickerDialog.show();
            }
        });
    }

    private void exportData(final int month, final int year) {
        final List<Transaction> _exportList = new ArrayList<>();
        final StringBuilder data = new StringBuilder();
        data.append("Date,Category Name,Category Type,Description,Amount(RM)");
        Query query = _databaseRef.orderByChild("date");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);

                    if (transaction.getMonth() == month && transaction.getYear() == year) {
                        _exportList.add(transaction);
                    }
                }
                for (Transaction transaction : _exportList) {
                    data.append("\n" + transaction.getDate() + "," + transaction.getCategoryName() + ","
                            + transaction.getCategoryType() + ","
                            + transaction.getDescription() + "," + String.valueOf(transaction.getAmount()));
                }
                try {
                    FileOutputStream out = getActivity().openFileOutput("data.csv", Context.MODE_PRIVATE);
                    out.write((data.toString()).getBytes());
                    out.close();

                    File fileLocation = new File(getActivity().getFilesDir(), "data.csv");
                    Uri path = FileProvider.getUriForFile(getContext(), "com.myApp.fileprovider", fileLocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(fileIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

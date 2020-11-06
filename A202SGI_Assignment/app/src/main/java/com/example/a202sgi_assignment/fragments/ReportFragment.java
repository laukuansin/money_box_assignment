package com.example.a202sgi_assignment.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Category;
import com.example.a202sgi_assignment.domains.Transaction;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.example.a202sgi_assignment.utilities.YearPickerDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class ReportFragment extends BaseFragment implements CustomDateFragment.OnCustomDateDialogListener{
    private TextView _dateView;
    public String startDate = "", endDate = "";
    private String currentDate;
    private String customDateType="currentMonth";
    private Calendar _calendar;
    private SimpleDateFormat _dateFormat,_monthYearFormat;
    private List<Transaction> _transactionList;
    private ArrayList<PieEntry> _pieDataExpense,_pieDataIncome;
    private List<Category> _expenseList, _incomeList;
    private List<String> _expenseNameList, _incomeNameList;
    private DatabaseReference _databaseRef;
    private PieChart _pieChartExpense,_pieChartIncome;
    private RetrieveData _getData = null;
    private int dayInt,monthInt,yearInt;
    public ReportFragment() {
    }

    public static ReportFragment newInstance() {
        ReportFragment fragment = new ReportFragment();

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
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        _dateView = view.findViewById(R.id.date);

        _calendar = Calendar.getInstance();
        _dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        _monthYearFormat= new SimpleDateFormat("MMMM yyyy");
        currentDate = _dateFormat.format(_calendar.getTime());
        _transactionList = new ArrayList<>();
        _expenseList = new ArrayList<>();
        _incomeList = new ArrayList<>();
        _expenseNameList = new ArrayList<>();
        _incomeNameList = new ArrayList<>();
        _pieChartExpense = view.findViewById(R.id.pie_chart_expense);
        _pieDataExpense = new ArrayList<>();
        _pieChartIncome=view.findViewById(R.id.pie_chart_income);
        _pieDataIncome=new ArrayList<>();
        _calendar.add(Calendar.MONTH, 0);
        _calendar.set(Calendar.DATE, _calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        startDate=_dateFormat.format(_calendar.getTime());

        _calendar.set(Calendar.DATE, _calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate=_dateFormat.format(_calendar.getTime());

        _dateView.setText(_monthYearFormat.format(_calendar.getTime()));

        _databaseRef = FirebaseDatabase.getInstance().getReference("transaction");




        return view;

    }

    public void loadData(String startDate,String endDate) {
        if (_getData == null) {
            _getData = new RetrieveData(startDate, endDate);
            _getData.execute();
        }
    }
    public void dateRangeDialog()
    {
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.bottom_dialog_date_range);

        LinearLayout _allLayout=dialog.findViewById(R.id.all_layout);
        LinearLayout _yearLayout=dialog.findViewById(R.id.year_layout);
        LinearLayout _customLayout=dialog.findViewById(R.id.custom_layout);
        LinearLayout _monthYearPicker=dialog.findViewById(R.id.month_year_layout);
        dialog.show();

        _yearLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                YearPickerDialog yearPicker=new YearPickerDialog();
                yearPicker.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        startDate=String.format("%02d", 1)+ "/" + String.format("%02d", 1) + "/" +  String.valueOf(year) ;
                        endDate=String.format("%02d", 31)+ "/" + String.format("%02d", 12) + "/" +  String.valueOf(year) ;
                        _dateView.setText(String.valueOf(year));
                        setAndCheckDate();
                    }
                });
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                yearPicker.show(fragmentManager,"Select Year");
                dialog.dismiss();
            }
        });
        _monthYearPicker.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String monthFull = _dateFormat.format(_calendar.getTime());
                String[] dateValue = monthFull.split("/");
                monthInt = Integer.parseInt(dateValue[1]);
                yearInt = Integer.parseInt(dateValue[2]);
                dayInt = Integer.parseInt(dateValue[0]);

                DatePickerDialog monthDatePickerDialog = new DatePickerDialog(getContext(),
                        AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            startDate = String.format("%02d", 1) + "/" + String.format("%02d", month + 1) + "/" + String.valueOf(year);
                            Date tempDate = _dateFormat.parse(startDate);
                            Calendar current=Calendar.getInstance();
                            current.setTime(tempDate);
                            current.set(Calendar.DATE, current.getActualMaximum(Calendar.DAY_OF_MONTH));

                            endDate=_dateFormat.format(current.getTime());

                            String tempSelectMonth = _monthYearFormat.format(tempDate);
                            _dateView.setText(tempSelectMonth);
                            setAndCheckDate();

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
                dialog.dismiss();

            }
        });

        _allLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                startDate="01/01/1000";
                endDate="01/01/3000";
                customDateType="All";
                setAndCheckDate();
                _dateView.setText("All Transaction");

                dialog.dismiss();
            }
        });

        _customLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                CustomDateFragment fragment = CustomDateFragment.newInstance( startDate,endDate,customDateType,ReportFragment.this);
                fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                fragment.show(fragmentManager, "Custom Date");
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setAndCheckDate();
    }

    private void setAndCheckDate() {

        _expenseNameList.clear();
        _incomeNameList.clear();
        _incomeList.clear();
        _expenseList.clear();
        _pieDataExpense.clear();
        _pieDataIncome.clear();
        _transactionList.clear();

        loadData(startDate,endDate);
       // _dateView.setText(String.format("%1$s %2$d", month, year));
    }

    @Override
    public void onReturnCustomDate(String startDate, String endDate) {
        this.startDate=startDate;
        this.endDate=endDate;
        customDateType="custom";
        _dateView.setText(String.format("%1$s - %2$s",startDate,endDate));
        setAndCheckDate();
    }


    private class RetrieveData extends AsyncTask<Void, Void, Void> {
        private ProgressDialog _progressDialog = new ProgressDialog(getContext());
        private String start,end;

        public RetrieveData(String start, String end) {
            this.start = start;
            this.end = end;
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


           _databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();


                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transaction transaction = dataSnapshot.getValue(Transaction.class);
                        if (transaction != null) {

                            if (transaction.getUserID().equals(getSessionHandler().getFacebookIdentity())) {
                                try{
                                    Date startTime=_dateFormat.parse(start);
                                    Date endTime=_dateFormat.parse(end);
                                    Date currentTime=_dateFormat.parse(transaction.getDate());

                                    if(!startTime.after(currentTime)&&!endTime.before(currentTime))
                                    {
                                        if (transaction.getCategoryType().equals("Income")) {
                                            _incomeNameList.add(transaction.getCategoryName());
                                        } else {
                                            _expenseNameList.add(transaction.getCategoryName());
                                        }

                                        _transactionList.add(transaction);
                                    }
                                }catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }


                            }
                        }

                    }
                    if (_transactionList.isEmpty()) {


                    } else {
                        //remove duplicate value
                        Set<String> setIncome = new HashSet<>(_incomeNameList);
                        Set<String> setExpense = new HashSet<>(_expenseNameList);
                        _incomeNameList.clear();
                        _incomeNameList.addAll(setIncome);
                        _expenseNameList.clear();
                        _expenseNameList.addAll(setExpense);

                        for (String name : _incomeNameList) {
                                Category tempCategory = new Category();
                                tempCategory.setAmount(0);
                            for (Transaction transaction : _transactionList) {

                                if (transaction.getCategoryName().equals(name)) {
                                    tempCategory.setCategoryName(transaction.getCategoryName());
                                    tempCategory.setCategoryType(transaction.getCategoryType());
                                    tempCategory.setCategoryImage(transaction.getCategoryImage());
                                    double tempAmount = transaction.getAmount() + tempCategory.getAmount();
                                    tempCategory.setAmount(tempAmount);

                                }
                            } _incomeList.add(tempCategory);
                        }
                        for (String name : _expenseNameList) {
                            Category tempCategory = new Category();
                            tempCategory.setAmount(0);
                            for (Transaction transaction : _transactionList) {

                                if (transaction.getCategoryName().equals(name)) {
                                    tempCategory.setCategoryName(transaction.getCategoryName());
                                    tempCategory.setCategoryType(transaction.getCategoryType());
                                    tempCategory.setCategoryImage(transaction.getCategoryImage());
                                    double tempAmount = transaction.getAmount() + tempCategory.getAmount();
                                    tempCategory.setAmount(tempAmount);

                                }
                            }
                            _expenseList.add(tempCategory);
                        }


                    }
                    setUpPieChartExpense();
                    setUpPieChartIncome();
                    _getData = null;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    _getData = null;
                }
            });
            return null;
        }
    }
    private void setUpPieChartExpense()
    {
        for (Category category : _expenseList) {

            _pieDataExpense.add(new PieEntry((float)(category.getAmount()), category.getCategoryName()));
        }
        PieDataSet pieDataSetExpense = new PieDataSet(_pieDataExpense, "");
        pieDataSetExpense.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSetExpense.setValueTextColor(Color.BLACK);
        pieDataSetExpense.setValueLineColor(Color.BLACK);
        pieDataSetExpense.setValueTextSize(15f);
        pieDataSetExpense.setSliceSpace(5f);
        pieDataSetExpense.setSelectionShift(5f);

        PieData pieDataExpense = new PieData(pieDataSetExpense);
        float total=pieDataExpense.getYValueSum();


        _pieChartExpense.setData(pieDataExpense);
        _pieChartExpense.getDescription().setEnabled(false);
        _pieChartExpense.setCenterText(String.format("Expense\n%.2f",total));
        _pieChartExpense.setCenterTextColor(R.color.black_1000);
        _pieChartExpense.setCenterTextSize(20f);
        _pieChartExpense.setHoleColor(R.color.white_1000);
        _pieChartExpense.setDrawHoleEnabled(true);
        _pieChartExpense.setTransparentCircleRadius(61f);
        _pieChartExpense.setDragDecelerationFrictionCoef(0.99f);
        _pieChartExpense.setExtraOffsets(5,10,5,5);
        _pieChartExpense.animateY(1000, Easing.EaseInOutCubic);
        _pieChartExpense.invalidate();
    }
    private void setUpPieChartIncome()
    {
        for (Category category : _incomeList) {

            _pieDataIncome.add(new PieEntry((float)(category.getAmount()), category.getCategoryName()));
        }
        PieDataSet pieDataSetIncome = new PieDataSet(_pieDataIncome, "");
        pieDataSetIncome.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSetIncome.setValueTextColor(Color.BLACK);
        pieDataSetIncome.setValueLineColor(Color.BLACK);
        pieDataSetIncome.setValueTextSize(15f);
        pieDataSetIncome.setSliceSpace(5f);
        pieDataSetIncome.setSelectionShift(5f);

        PieData pieDataIncome = new PieData(pieDataSetIncome);
        float total=pieDataIncome.getYValueSum();


        _pieChartIncome.setData(pieDataIncome);
        _pieChartIncome.getDescription().setEnabled(false);
            _pieChartIncome.setCenterText(String.format("Income\n%.2f",total));
        _pieChartIncome.setCenterTextColor(R.color.black_1000);
        _pieChartIncome.setCenterTextSize(20f);
        _pieChartIncome.setHoleColor(R.color.white_1000);
        _pieChartIncome.setDrawHoleEnabled(true);
        _pieChartIncome.setTransparentCircleRadius(61f);
        _pieChartIncome.setDragDecelerationFrictionCoef(0.99f);
        _pieChartIncome.setExtraOffsets(5,10,5,5);
        _pieChartIncome.animateY(1000, Easing.EaseInOutCubic);
        _pieChartIncome.invalidate();
    }


}

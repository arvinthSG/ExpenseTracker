package io.sunhacks.com.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.sunhacks.com.expensetracker.Model.IndividualExpenseModel;
import io.sunhacks.com.expensetracker.Model.SpendingModel;

public class ChartingActivity extends Fragment {

    public static String TAG = "EXPENSE_TRACKER";
    public ArrayList<SpendingModel> parsedMessageList = null;
    public Float[] yData = null;
    public String[] xData = null;
    private boolean calculationDone = false;
    private Map<String, Float> eachSpendingModel = null;
    private Map<String, Float> individualDataMap = null;
    PieChart pieChart;
    Realm realm;

    public static ChartingActivity newInstance(String date) {
        Bundle bundle = new Bundle();
        SimpleDateFormat olddateFormat = new SimpleDateFormat("MMM-yyyy");
        Date d = null;
        try {
            d = olddateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("MM-YYYY", Locale.ENGLISH);
        String monthYear = dateFormat.format(d);
        bundle.putSerializable("date", monthYear);
        ChartingActivity chartingActivity = new ChartingActivity();
        chartingActivity.setArguments(bundle);
        return chartingActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_charting, null);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void update(String date) {
        SimpleDateFormat olddateFormat = new SimpleDateFormat("MMM-yyyy", Locale.US);
        Date d = null;
        try {
            d = olddateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("MM-YYYY", Locale.US);
        String monthYear = dateFormat.format(d);

        RealmResults<SpendingModel> realmResults = realm.where(SpendingModel.class)
                .sort("_monthYear")
                .equalTo("_monthYear", monthYear)
                .findAll();

        if (parsedMessageList != null) {
            parsedMessageList.clear();
        } else {
            parsedMessageList = new ArrayList<>();
        }

        for (SpendingModel spendingModel : realmResults) {
            parsedMessageList.add(realm.copyFromRealm(spendingModel));
        }

        eachSpendingModel = null;
        eachSpendingModel = new HashMap<>();
        for (SpendingModel spm : parsedMessageList) {
            if (spm.isDebit()) {
                eachSpendingModel.put(spm.getCategory(), eachSpendingModel.getOrDefault(spm.getCategory(), 0.0f) + spm.getAmount());
                }
                calculationDone = true;
            }


        List<String> tempList = new ArrayList<>(eachSpendingModel.keySet());
        xData = tempList.toArray(new String[0]);
        yData = eachSpendingModel.values().toArray(new Float[0]);
        addDataSet();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Creating the Pie chart");
        eachSpendingModel = new HashMap<>();
        parsedMessageList = new ArrayList<>();
        String monthYear = getArguments().getString("date");
        Log.i(TAG, "update() " + parsedMessageList.size());
        RealmResults<SpendingModel> realmResults = realm.where(SpendingModel.class)
                .sort("_monthYear")
                .equalTo("_monthYear", monthYear)
                .findAll();

        for (SpendingModel spendingModel : realmResults) {
            parsedMessageList.add(realm.copyFromRealm(spendingModel));
        }

        eachSpendingModel = null;
        eachSpendingModel = new HashMap<>();
        for (SpendingModel spm : parsedMessageList) {
            if (spm.isDebit()) {
                eachSpendingModel.put(spm.getCategory(), eachSpendingModel.getOrDefault(spm.getCategory(), 0.0f) + spm.getAmount());
            }
            calculationDone = true;
        }


        List<String> tempList = new ArrayList<>(eachSpendingModel.keySet());
        xData = tempList.toArray(new String[0]);
        yData = eachSpendingModel.values().toArray(new Float[0]);

        pieChart = view.findViewById(R.id.pc_mainpie);
        Description description = new Description();
        description.setText("Expenses");
        pieChart.setDescription(description);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(2f);
        pieChart.setTransparentCircleAlpha(1);
        pieChart.setDrawEntryLabels(true);

        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ArrayList<IndividualExpenseModel> individualData = new ArrayList<>();
                int pos1 = 0;
                float sales = h.getY();
                PieEntry pe = (PieEntry) e;
                individualDataMap = new HashMap<>();

                for (int i = 0; i < yData.length; i++) {
                    if (yData[i] == sales) {
                        pos1 = i;
                        break;
                    }
                }
                String data = xData[pos1];
                for (SpendingModel spendingModel : parsedMessageList) {
                    if (spendingModel.getCategory().equals(data)) {
                        individualDataMap.put(spendingModel.getMerchant(), individualDataMap.getOrDefault(spendingModel.getMerchant(), 0.0f) + spendingModel.getAmount());
                    }
                }

                for (Map.Entry<String, Float> each : individualDataMap.entrySet()) {
                    IndividualExpenseModel iem = new IndividualExpenseModel(each.getKey(), getProgress(each.getValue(), sales));
                    iem.addAmount(each.getValue());
                    individualData.add(iem);
                }

                Intent intent = new Intent(getContext(), IndividualExpense.class);
                intent.putExtra("individual_expense_list", individualData);
                intent.putExtra("total_expense", String.format(Locale.ENGLISH, "%.2f", sales));
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started " + xData.length + " " + yData.length);
        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], xData[i]));
            Log.i(TAG, "Pie Entry " + yData[i] + " " + xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Expense");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(15);
        pieDataSet.setValueTextColor(Color.WHITE);


        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private Integer getProgress(float amount, float sales) {
        return (int) ((amount / sales) * 100);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

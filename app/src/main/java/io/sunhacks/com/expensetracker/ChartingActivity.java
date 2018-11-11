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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public static ChartingActivity newInstance(ArrayList<SpendingModel> parsedMessageList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PARSED_LIST", parsedMessageList);
        ChartingActivity chartingActivity = new ChartingActivity();
        chartingActivity.setArguments(bundle);
        return chartingActivity;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_charting, null);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void update(ArrayList<SpendingModel> parsedMessageList) {
        Log.i(TAG, "update() " + parsedMessageList.size());
        if (!calculationDone) {
            for (SpendingModel spendingModel : parsedMessageList) {
                if (spendingModel.isDebit()) {
                    eachSpendingModel.put(spendingModel.getCategory(), eachSpendingModel.getOrDefault(spendingModel.getCategory(), 0.0f) + spendingModel.getAmount());
                }
                calculationDone = true;
            }
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
        parsedMessageList = (ArrayList<SpendingModel>) getArguments().getSerializable("PARSED_LIST");
        if (!calculationDone) {
            for (SpendingModel spendingModel : parsedMessageList) {
                if (spendingModel.isDebit()) {
                    eachSpendingModel.put(spendingModel.getCategory(), eachSpendingModel.getOrDefault(spendingModel.getCategory(), 0.0f) + spendingModel.getAmount());
                }
                calculationDone = true;
            }
        }

        List<String> tempList = new ArrayList<>(eachSpendingModel.keySet());
        xData = tempList.toArray(new String[0]);
        yData = eachSpendingModel.values().toArray(new Float[0]);

        pieChart = view.findViewById(R.id.pc_mainpie);
        Description description = new Description();
        description.setText("Expenses by percentage");
        description.setTextSize(20);
        pieChart.setDescription(description);
        pieChart.setRotationEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(0f);
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

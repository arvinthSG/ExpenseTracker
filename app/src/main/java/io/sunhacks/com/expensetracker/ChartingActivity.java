package io.sunhacks.com.expensetracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import java.util.Map;

import io.sunhacks.com.expensetracker.Model.IndividualExpenseModel;
import io.sunhacks.com.expensetracker.Model.SpendingModel;

public class ChartingActivity extends AppCompatActivity {

    public static String TAG = "ChartingActivity";
    public ArrayList<SpendingModel> parsedMessageList = null;
    public Float[] yData = null;
    public String[] xData = null;
    private Map<String, Float> individualDataMap = null;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charting);
        Log.d(TAG, "onCreate: Creating the Pie chart");
        Map<String, Float> eachSpendingModel = new HashMap<>();
        parsedMessageList = (ArrayList<SpendingModel>) getIntent().getSerializableExtra("parsed_list");

        for (SpendingModel spendingModel: parsedMessageList) {
            if(spendingModel.isDebit()) {
                eachSpendingModel.put(spendingModel.getCategory(), eachSpendingModel.getOrDefault(spendingModel.getCategory(), 0.0f) + spendingModel.getAmount());
            }
        }

        List<String> tempList = new ArrayList<>(eachSpendingModel.keySet());
        xData = tempList.toArray(new String[0]);
        yData = eachSpendingModel.values().toArray( new Float[0]);

        pieChart = findViewById(R.id.piechart);
        Description description = new Description();
        description.setText("Expenses by percentage");
        description.setTextSize(20);
        pieChart.setDescription(description);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleAlpha(1);
        pieChart.setDrawEntryLabels(true);

        addDataSet();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ArrayList<IndividualExpenseModel> individualData = new ArrayList<>();
                int pos1 = 0;
                float sales = h.getY();
                individualDataMap = new HashMap<>();

                for (int i = 0; i < yData.length; i++) {
                    if (yData[i] == sales) {
                        pos1 = i;
                        break;
                    }
                }
                String data = xData[pos1];

                for (SpendingModel spendingModel: parsedMessageList) {
                    if (spendingModel.getCategory().equals(data)) {
                        individualDataMap.put(spendingModel.getMerchant(), individualDataMap.getOrDefault(spendingModel.getMerchant(), 0.0f) + spendingModel.getAmount());
                    }
                }

                for (Map.Entry<String, Float> each : individualDataMap.entrySet()) {
                    IndividualExpenseModel iem = new IndividualExpenseModel(each.getKey(), getProgress(each.getValue(), sales));
                    iem.addAmount(each.getValue());
                    individualData.add(iem);
                }

                Intent intent = new Intent(ChartingActivity.this, IndividualExpense.class);
                intent.putExtra("individual_expense_list", individualData);
                intent.putExtra("total_expense", String.valueOf(sales));
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private Integer getProgress(float amount, float sales) {
        return  (int)((amount / sales) * 100);
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], xData[i]));
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
}

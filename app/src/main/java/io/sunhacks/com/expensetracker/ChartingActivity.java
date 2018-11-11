package io.sunhacks.com.expensetracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class ChartingActivity extends AppCompatActivity {

    public static String TAG = "ChartingActivity";
    public ArrayList<SpendingModel> parsedMessageList = null;
    public Float[] yData = null;
    public String[] xData = null;

    public String[] TransportCategories = {"Uber", "Lyft", "Lime", "Bird", "Razer"};
    public float[] TransportData = {100.32f, 50.9f, 120.3f, 30.3f, 20.6f};

    private Map<String, Float> eachSpendingModel = null;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charting);
        Log.d(TAG, "onCreate: Creating the Pie chart");
        eachSpendingModel = new HashMap<>();
        parsedMessageList = (ArrayList<SpendingModel>) getIntent().getSerializableExtra("parsed_list");
        for (SpendingModel spendingModel: parsedMessageList) {
            eachSpendingModel.put(spendingModel.getCategory(), eachSpendingModel.getOrDefault(spendingModel.getCategory(), 0.0f) + spendingModel.getAmount());
        }
        List<String> tempList = new ArrayList<>(eachSpendingModel.keySet());
        xData = tempList.toArray(new String[0]);
        yData = eachSpendingModel.values().toArray( new Float[0]);

        pieChart = (PieChart) findViewById(R.id.piechart);
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
                Log.d(TAG, "onValueSelected: Value select from chart.");
                Log.d(TAG, "onValueSelected: " + e.toString());
                Log.d(TAG, "onValueSelected: " + h.toString());

                int pos1 = 0;
                float sales = h.getY();

                for (int i = 0; i < yData.length; i++) {
                    if (yData[i] == sales) {
                        pos1 = i;
                        break;
                    }
                }
                String data = xData[pos1];


            }

            @Override
            public void onNothingSelected() {

            }
        });

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

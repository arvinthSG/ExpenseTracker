package io.sunhacks.com.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;

public class ChartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charting);
        PieChart chart = (PieChart) findViewById(R.id.chart);

        chart.setUsePercentValues(this);

    }
}

package io.sunhacks.com.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    private EditText etMonthlyLimit;
    private EditText etSuperMarketLimit;
    private EditText etShoppingLimit;
    private EditText etTransportationLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        etMonthlyLimit = findViewById(R.id.et_budget);
        etSuperMarketLimit = findViewById(R.id.et_supermarket);
        etShoppingLimit = findViewById(R.id.et_apparel);
        etTransportationLimit = findViewById(R.id.et_transport);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

package io.sunhacks.com.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SettingsActivity extends AppCompatActivity {

    private EditText etMonthlyLimit;
    private EditText etSuperMarketLimit;
    private EditText etShoppingLimit;
    private EditText etTransportationLimit;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        etMonthlyLimit = findViewById(R.id.et_budget);
        etSuperMarketLimit = findViewById(R.id.et_supermarket);
        etShoppingLimit = findViewById(R.id.et_apparel);
        etTransportationLimit = findViewById(R.id.et_transport);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(config);
        realm = Realm.getInstance(config);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        etMonthlyLimit.getText();
        super.onBackPressed();

    }

}

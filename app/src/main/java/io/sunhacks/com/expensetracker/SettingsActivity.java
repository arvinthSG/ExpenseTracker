package io.sunhacks.com.expensetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.sunhacks.com.expensetracker.Model.BudgetModel;

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
        realm = Realm.getInstance(config);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BudgetModel superMarketModel = realm.where(BudgetModel.class).equalTo("key", "SuperMarket").findFirst();
                if (superMarketModel != null) {
                    etSuperMarketLimit.setText(String.format(Locale.US, "%.2f", superMarketModel.value));
                }

                BudgetModel monthlyModel = realm.where(BudgetModel.class).equalTo("key", "Monthly").findFirst();
                if (monthlyModel != null) {
                    etMonthlyLimit.setText(String.format(Locale.US, "%.2f", monthlyModel.value));
                }

                BudgetModel transportationModel = realm.where(BudgetModel.class).equalTo("key", "Transport").findFirst();
                if (transportationModel != null) {
                    etTransportationLimit.setText(String.format(Locale.US, "%.2f", transportationModel.value));
                }

                BudgetModel shoppingModel = realm.where(BudgetModel.class).equalTo("key", "Shopping").findFirst();
                if (shoppingModel != null) {
                    etShoppingLimit.setText(String.format(Locale.US, "%.2f", shoppingModel.value));
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Float shoppingLimit = 0.0f, monthlyLimit = 0.0f, transportLimit = 0.0f, superMarketLimit = 0.0f;

                if (!etShoppingLimit.getText().toString().equals("")) {
                    shoppingLimit = Float.parseFloat(etShoppingLimit.getText().toString());
                }
                if (!etMonthlyLimit.getText().toString().equals("")) {
                    monthlyLimit = Float.parseFloat(etMonthlyLimit.getText().toString());
                }
                if (!etTransportationLimit.getText().toString().equals("")) {
                    transportLimit = Float.parseFloat(etTransportationLimit.getText().toString());
                }
                if (!etSuperMarketLimit.getText().toString().equals("")) {
                    superMarketLimit = Float.parseFloat(etSuperMarketLimit.getText().toString());
                }

                realm.copyToRealmOrUpdate(new BudgetModel("Shopping", shoppingLimit));
                realm.copyToRealmOrUpdate(new BudgetModel("Monthly", monthlyLimit));
                realm.copyToRealmOrUpdate(new BudgetModel("Transport", transportLimit));
                realm.copyToRealmOrUpdate(new BudgetModel("SuperMarket", superMarketLimit));
            }
        });

        super.onBackPressed();
    }

}

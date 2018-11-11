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
                if (superMarketModel == null) {
                    realm.copyToRealm(new BudgetModel("SuperMarket", Float.parseFloat(etShoppingLimit.getText().toString())));
                } else {
                    etSuperMarketLimit.setText(String.format(Locale.US, "%.2f", superMarketModel.value));
                }

                BudgetModel monthlyModel = realm.where(BudgetModel.class).equalTo("key", "Monthly").findFirst();
                if (monthlyModel == null) {
                    realm.copyToRealm(new BudgetModel("Monthly", Float.parseFloat(etMonthlyLimit.getText().toString())));
                } else {
                    etMonthlyLimit.setText(String.format(Locale.US, "%.2f", monthlyModel.value));
                }

                BudgetModel transportationModel = realm.where(BudgetModel.class).equalTo("key", "Transportation").findFirst();
                if (transportationModel == null) {
                    realm.copyToRealm(new BudgetModel("Transportation", Float.parseFloat(etTransportationLimit.getText().toString())));
                } else {
                    etTransportationLimit.setText(String.format(Locale.US, "%.2f", transportationModel.value));
                }

                BudgetModel shoppingModel = realm.where(BudgetModel.class).equalTo("key", "Shopping").findFirst();
                if (shoppingModel == null) {
                    realm.copyToRealm(new BudgetModel("Shopping", Float.parseFloat(etShoppingLimit.getText().toString())));
                } else {
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
        Float shoppingLimit = Float.parseFloat(etShoppingLimit.getText().toString());
        Float monthlyLimit = Float.parseFloat(etMonthlyLimit.getText().toString());
        Float transportLimit = Float.parseFloat(etTransportationLimit.getText().toString());
        Float superMarketLimit = Float.parseFloat(etSuperMarketLimit.getText().toString());

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(new BudgetModel("Shopping", shoppingLimit));
                realm.copyToRealm(new BudgetModel("Monthly", monthlyLimit));
                realm.copyToRealm(new BudgetModel("Transport", transportLimit));
                realm.copyToRealm(new BudgetModel("SuperMarket", superMarketLimit));
            }
        });

        super.onBackPressed();
    }

}

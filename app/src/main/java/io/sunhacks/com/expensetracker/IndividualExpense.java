package io.sunhacks.com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.sunhacks.com.expensetracker.Adapter.IndividualExpenseAdapter;
import io.sunhacks.com.expensetracker.Model.IndividualExpenseModel;

public class IndividualExpense extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_expense);
        Intent intent = getIntent();
        List<IndividualExpenseModel> individualExpenseList = (List<IndividualExpenseModel>) intent.getSerializableExtra("individual_expense_list");

        Collections.sort(individualExpenseList, new Comparator<IndividualExpenseModel>() {
            @Override
            public int compare(IndividualExpenseModel a, IndividualExpenseModel b) {
                return b.getProgress() - a.getProgress();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView textTotalExpense = findViewById(R.id.totalExpenseDollars);
        textTotalExpense.setText("$ " + intent.getStringExtra("total_expense"));

        IndividualExpenseAdapter individualExpenseAdapter = new IndividualExpenseAdapter(individualExpenseList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(individualExpenseAdapter);
        individualExpenseAdapter.notifyDataSetChanged();

    }
}
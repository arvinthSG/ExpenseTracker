package io.sunhacks.com.expensetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.sunhacks.com.expensetracker.Adapter.IndividualExpenseAdapter;
import io.sunhacks.com.expensetracker.Model.IndividualExpenseModel;

public class IndividualExpense extends AppCompatActivity {

    private List<IndividualExpenseModel> individualExpenseList = new ArrayList<>();
    private RecyclerView recyclerView;
    private IndividualExpenseAdapter individualExpenseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_expense);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        TextView textTotalExpense = (TextView) findViewById(R.id.totalExpenseDollars);
        textTotalExpense.setText("$ 400");

        individualExpenseAdapter = new IndividualExpenseAdapter(individualExpenseList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(individualExpenseAdapter);
        prepareIndividualExpensesData();

    }
    private void prepareIndividualExpensesData() {
        IndividualExpenseModel expense = new IndividualExpenseModel("Mad Max: Fury Road", 25);
        individualExpenseList.add(expense);

        expense = new IndividualExpenseModel("Inside Out", 45);
        individualExpenseList.add(expense);

        expense = new IndividualExpenseModel("Star Wars: Episode VII - The Force Awakens",20);
        individualExpenseList.add(expense);

        expense = new IndividualExpenseModel("Shaun the Sheep", 15);
        individualExpenseList.add(expense);

        expense = new IndividualExpenseModel("The Martian", 10);
        individualExpenseList.add(expense);

        Collections.sort(individualExpenseList, new SortbyProgress());

        individualExpenseAdapter.notifyDataSetChanged();
    }
}

class SortbyProgress implements Comparator<IndividualExpenseModel>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(IndividualExpenseModel a, IndividualExpenseModel b)
    {
        return b.getProgress() - a.getProgress();
    }
}

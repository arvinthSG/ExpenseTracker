package io.sunhacks.com.expensetracker.Adapter;

/**
 * Created by avinash on 10/11/18.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import io.sunhacks.com.expensetracker.Model.IndividualExpenseModel;
import io.sunhacks.com.expensetracker.R;

public class IndividualExpenseAdapter extends RecyclerView.Adapter<IndividualExpenseAdapter.MyViewHolder> {

    private List<IndividualExpenseModel> individualExpensesListModel;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
        }
    }


    public IndividualExpenseAdapter(List<IndividualExpenseModel> individualExpensesListModel) {
        this.individualExpensesListModel = individualExpensesListModel;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_expence, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        IndividualExpenseModel individualExpenseModel = individualExpensesListModel.get(position);
        holder.title.setText(individualExpenseModel.getTitle());
        holder.progressBar.setProgress(individualExpenseModel.getProgress());
    }

    @Override
    public int getItemCount() {
        return individualExpensesListModel.size();
    }
}
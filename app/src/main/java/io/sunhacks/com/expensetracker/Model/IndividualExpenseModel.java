package io.sunhacks.com.expensetracker.Model;

import java.io.Serializable;

/**
 * Created by avinash on 10/11/18.
 */

public class IndividualExpenseModel implements Serializable{
    private String title;
    private Integer progress;
    private Float amount = 0.0f;

    public IndividualExpenseModel() {
    }

    public IndividualExpenseModel(String title, Integer progress) {
        this.title = title;
        this.progress = progress;
    }

    public Float getAmount() {
        return this.amount;
    }

    public void addAmount(Float f) {
        this.amount += f;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}

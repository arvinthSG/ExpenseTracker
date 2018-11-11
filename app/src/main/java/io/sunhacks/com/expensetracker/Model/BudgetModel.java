package io.sunhacks.com.expensetracker.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BudgetModel extends RealmObject {
    @PrimaryKey
    public String key;
    public Float value;

    public BudgetModel() {

    }

    public BudgetModel(String key, Float object) {
        this.key = key;
        this.value = object;
    }
}

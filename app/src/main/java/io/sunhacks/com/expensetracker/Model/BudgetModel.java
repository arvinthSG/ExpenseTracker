package io.sunhacks.com.expensetracker.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BudgetModel extends RealmObject {
    @PrimaryKey
    private String key;
    private float value;

    public BudgetModel() {

    }

    public void BudgetModel(String key, float object) {
        this.key = key;
        this.value = object;
    }
}

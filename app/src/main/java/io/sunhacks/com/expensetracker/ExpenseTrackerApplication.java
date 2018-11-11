package io.sunhacks.com.expensetracker;

import android.app.Application;

import io.realm.Realm;

public class ExpenseTrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}


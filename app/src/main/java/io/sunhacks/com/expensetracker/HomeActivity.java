package io.sunhacks.com.expensetracker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.sunhacks.com.expensetracker.Model.Sms;
import io.sunhacks.com.expensetracker.Model.SpendingModel;

public class HomeActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_WRITE_STORAGE = 2;
    private RecyclerView rvMessagesList = null;
    private MessageAdapter rvAdapter = null;
    private Button btnExport = null;
    private DonutProgress dnProgress = null;
    private FrameLayout flCharts = null;
    private ChartingActivity chartingActivity = null;

    private double dNetSpending = 0;
    private CSVWriter csvWriter = null;
    private List messages = null;
    private ArrayList<SpendingModel> parsedList = null;
    private Map<String, String> numberAccountMap = null;
    private static final String EXPORT_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "message_data1.csv";
    private static final String CSV_HEADER = "Amount,Merchant,Category,Account,Time";
    public Map<String, List<String>> merchantCategoryMap = null;
    private static final String LOG_TAG = "EXPENSE_TRACKER";
    private Realm realm;
    public boolean parsed = false;

    String minMonth = "12-2222";
    String maxMonth = "01-1970";
    float netValue = 0.0f;
    public void getDataForMonth(String month) {
        if (parsedList != null)
            parsedList.clear();
        // Horrible hack to conver MMM to MM.
        String strdate = "22-" + month + "-1970";
        SimpleDateFormat olddateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date d = null;
        try {
            d = olddateFormat.parse(strdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.ENGLISH);
        String m = dateFormat.format(d);
        String monthYear = m + "-2018";
        Log.d("MonthYear", monthYear);
        RealmResults<SpendingModel> realmResults = realm.where(SpendingModel.class)
                .sort("_monthYear")
                .equalTo("_monthYear", monthYear)
                .findAll();
        for (SpendingModel spendingModel : realmResults) {
            parsedList.add(realm.copyFromRealm(spendingModel));
            if (spendingModel.isDebit()) {
                netValue -= spendingModel.getAmount();
            } else {
                netValue += spendingModel.getAmount();
            }
        }
        Log.e("NetValue", String.format("%.2f", netValue));
    }

    public void initMerchantCategoryMap() {
        merchantCategoryMap = new HashMap<>();
        List<String> transportBusinesses = Arrays.asList(getResources().getStringArray(R.array.transportation));
        List<String> foodBusinesses = Arrays.asList(getResources().getStringArray(R.array.food));
        List<String> apparelBusinesses = Arrays.asList(getResources().getStringArray(R.array.apparel));
        List<String> onlineBusinesses = Arrays.asList(getResources().getStringArray(R.array.online));
        List<String> superMarkets = Arrays.asList(getResources().getStringArray(R.array.supermarket));
        merchantCategoryMap.put("Transport", transportBusinesses);
        merchantCategoryMap.put("Food", foodBusinesses);
        merchantCategoryMap.put("Apparel", apparelBusinesses);
        merchantCategoryMap.put("Online", onlineBusinesses);
        merchantCategoryMap.put("SuperMarket", superMarkets);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rvMessagesList = findViewById(R.id.rv_lists);
        numberAccountMap = initializeMap();
        btnExport = findViewById(R.id.btn_export);
        initMerchantCategoryMap();

        dnProgress = findViewById(R.id.donut_progress);
        flCharts = findViewById(R.id.fl_charts);


        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.deleteRealm(config);
        realm = Realm.getInstance(config);
    }

    public boolean checkIsDebit(String message, String account) {
        if (account.equals(Constants.DISCOVER)) {
            return true;
        } else return !account.equals(Constants.CHASE);
    }

    public static Map<String, String> initializeMap() {
        Map<String, String> numberAccountMap = new HashMap<>();
        //numberAccountMap.put("20736", "MidFirst");
        numberAccountMap.put(Constants.DISCOVER_PHONE_NO, Constants.DISCOVER);
        numberAccountMap.put(Constants.CHASE_PHONE_NO, Constants.CHASE);
        return numberAccountMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.i(LOG_TAG, "permission not granted");
            getPermission();
        } else {
            if (!parsed) {
                getANdFilterAllMsgs();
            } else {
                showChart();
            }
        }
        messages = new ArrayList();
        rvAdapter = new MessageAdapter(parsedList, this);
        rvMessagesList.setAdapter(rvAdapter);
        rvMessagesList.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter.notifyDataSetChanged();
        if (parsedList != null && parsedList.size() > 0) {
            showChart();
        }
    }

    private void showChart() {
        if (chartingActivity == null) {
            chartingActivity = ChartingActivity.newInstance(parsedList);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_charts, chartingActivity, "CHARTING_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public String getCategory(String needle) {
        // Iterate through the haystack map, and then get the key
        String retval = "Other";
        boolean entryFound = false;
//        Log.d(LOG_TAG, "getCategory");
        for (Map.Entry<String, List<String>> eachCategory : merchantCategoryMap.entrySet()) {
            // Now, go through the entire list and see if we can recognize anything
            String key = eachCategory.getKey();
            for (String comp : eachCategory.getValue()) {
                if (needle.toLowerCase().contains(comp.toLowerCase())) {
                    // YAAY Match.
                    retval = key;
                    entryFound = true;
                    break;
                }
            }
            if (entryFound) break;
        }
        return retval;
    }

    public List<Sms> getAllMessages() {
        List<Sms> lstSms = new ArrayList<>();
        Log.i(LOG_TAG, "getAllMessages");
        Sms objSms;
        try {
            Uri message = Uri.parse("content://sms/");
            ContentResolver cr = getContentResolver();

            Cursor c = cr.query(message, null, null, null, null);
            startManagingCursor(c);
            assert c != null;
            int totalSMS = c.getCount();

            if (c.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new Sms();
                    objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                    objSms.setAddress(c.getString(c
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSms.setReadState(c.getString(c.getColumnIndex("read")));
                    objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objSms.setFolderName("inbox");
                    } else {
                        objSms.setFolderName("sent");
                    }

                    lstSms.add(objSms);
                    c.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstSms;
    }

    public List<Sms> filterSms(List<Sms> messages) {
        List<Sms> filteredList = new ArrayList<>();
        for (Sms message : messages) {
            if (numberAccountMap.containsKey(message.getAddress())) {
                filteredList.add(message);
            }
        }
        return filteredList;
    }

    public String parseMerchant(String smsString, String account) {
        String merchant = null;
        Pattern pattern;
        switch (account) {
            case Constants.DISCOVER:
                pattern = Pattern.compile("at (.*?) was", Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(smsString);
                while (matcher.find()) {
                    merchant = matcher.group(1);
                }
                if (merchant.toLowerCase().contains("lyft")) {
                    merchant = "LYFT";
                } else if (merchant.toLowerCase().contains("lime")) {
                    merchant = "LIME";
                }
                break;
            case Constants.CHASE:
                merchant = "Credit";
                break;
            default:
                merchant = "NA";
                break;
        }


        return merchant;
    }

    public float parseAmount(String smsString, String account) {
        float d = 0.0f;
        Pattern pattern;
        switch (account) {
            case Constants.DISCOVER:
                // Discover messages are of format
                // Discover Card: Transaction of <Amount> at <Merchant> was made on <date>
                pattern = Pattern.compile("Transaction of \\$(.*?) at", Pattern.MULTILINE);
                break;
            case Constants.CHASE:
                pattern = Pattern.compile("sent you \\$(.*?)\\,", Pattern.MULTILINE);
                break;
            default:
//                Log.d(LOG_TAG, smsString);
                pattern = Pattern.compile("sent you \\$(.*?)\\,", Pattern.MULTILINE);
                break;
        }

        Matcher matcher = pattern.matcher(smsString);

        while (matcher.find()) {
            d = Float.parseFloat(matcher.group(1));
//            Log.d(LOG_TAG, matcher.group(1));
        }
        return d;
    }

    public void parseSms(List<Sms> messages) {
        float totalAmount = 0;
        parsedList = new ArrayList<>();
        for (Sms message : messages) {
            SpendingModel spendingModel = new SpendingModel();
            spendingModel.setAccount(numberAccountMap.get(message.getAddress()));
            String strMsg = message.getMsg();
            float amount = parseAmount(strMsg, spendingModel.getAccount());
            if (amount == 0) {
                // Mostly a auth message or something. skip it!.
                continue;
            }
            spendingModel.setRawMessage(message);
            spendingModel.setMerchant(parseMerchant(strMsg, spendingModel.getAccount()));
            Date d = new Date((long) Double.parseDouble(message.getTime()));
            DateFormat dateFormat = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
            spendingModel.setMonthYear(dateFormat.format(d));
            Log.d("MonthYear", spendingModel.getMonthYear());
            spendingModel.setSmsTime(d);
            spendingModel.setDebit(checkIsDebit(strMsg, spendingModel.getAccount()));
            if (spendingModel.isDebit()) {
                amount = amount;
                spendingModel.setCategory(getCategory(spendingModel.getMerchant()));
            } else {
                spendingModel.setCategory("Income");
            }
            totalAmount += amount;
            spendingModel.setAmount(amount);
            Date currentTime = Calendar.getInstance().getTime();
            String currentDate = dateFormat.format(currentTime);
          /*
            if (currentDate.equalsIgnoreCase(spendingModel.getMonthYear())) {
                parsedList.add(spendingModel);
            }
           */
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(spendingModel);
                }
            });

            if (minMonth.compareToIgnoreCase(spendingModel.getMonthYear()) > 0) {
                minMonth = spendingModel.getMonthYear();
            }

            if (maxMonth.compareToIgnoreCase(spendingModel.getMonthYear()) < 0) {
                maxMonth = spendingModel.getMonthYear();
            }

        }
        dNetSpending = totalAmount;
        Log.d("Years", "Max:" + maxMonth + " Min:" + minMonth);
        dnProgress.setText(String.format("$%.2f", dNetSpending));
        getDataForMonth("NOV");
    }

    public void getPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermisionResults");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!parsed) {
                        getANdFilterAllMsgs();
                    } else {
                        showChart();
                    }

                    rvAdapter.notifyDataSetChanged();
                } else {
                    //SHow message to User
                }
            }
        }

    }

    public void getANdFilterAllMsgs() {
        List<Sms> allMessages;
        allMessages = getAllMessages();
        messages = filterSms(allMessages);
        parseSms(messages);
        if (chartingActivity != null) {
            chartingActivity.update(parsedList);
        }
        parsed = true;
        Log.i(LOG_TAG, "getAndFilterAllMsgs() End " + parsedList.size());
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private List<SpendingModel> messageList;
        private Context mContext;

        public MessageAdapter(List<SpendingModel> messages, Context context) {
            messageList = messages;
            mContext = context;
        }

        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.transaction_list_items, null, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
            if (holder != null) {
                SpendingModel newSms = parsedList.get(position);
                holder.tvMessageMerchant.setText(newSms.getMerchant());
                String amount = String.valueOf(newSms.getAmount());
                if (newSms.isDebit()) {
                    holder.tvMessageAmount.setTextColor(ContextCompat.getColor(mContext, R.color.amountDebitColor));
                } else {
                    holder.tvMessageAmount.setTextColor(ContextCompat.getColor(mContext, R.color.amountCreditCOlor));
                }
                holder.tvMessageAmount.setText(amount);
                holder.tvMessageAccount.setText(newSms.getAccount());
                Date smsDate = newSms.getSmsTime();
                smsDate.getDay();
                String date = (String) android.text.format.DateFormat.format("MMM-dd", smsDate);
                holder.tvMessageDate.setText(date);
                Drawable image;
                Log.i(LOG_TAG, "category " + newSms.getCategory());
                image = ContextCompat.getDrawable(mContext, R.drawable.defaultimg);
                if (newSms.getCategory().equalsIgnoreCase(Constants.Category.SUPERMARKET)) {
                    image = ContextCompat.getDrawable(mContext, R.drawable.supermarket);
                } else if (newSms.getCategory().equalsIgnoreCase(Constants.Category.APPAREL)) {
                    image = ContextCompat.getDrawable(mContext, R.drawable.apparel);
                } else if (newSms.getCategory().equalsIgnoreCase(Constants.Category.FOOD)) {
                    image = ContextCompat.getDrawable(mContext, R.drawable.food);
                } else if (newSms.getCategory().equalsIgnoreCase(Constants.Category.TRANSPORTATION)) {
                    image = ContextCompat.getDrawable(mContext, R.drawable.transportation);
                } else if (newSms.getCategory().equalsIgnoreCase(Constants.Category.ONLINE)) {
                    image = ContextCompat.getDrawable(mContext, R.drawable.apparel);
                } else if (newSms.getCategory().equalsIgnoreCase("others")) {
                    image = ContextCompat.getDrawable(mContext, R.drawable.defaultimg);
                }
                holder.ivCategoryIcon.setBackground(image);
            }
        }

        @Override
        public int getItemCount() {
            if (parsedList != null) {
                return parsedList.size();
            } else {
                return 0;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvMessageMerchant;
            private TextView tvMessageAmount;
            private TextView tvMessageAccount;
            private TextView tvMessageDate;
            private ImageView ivCategoryIcon;

            ViewHolder(View itemView) {
                super(itemView);
                tvMessageMerchant = itemView.findViewById(R.id.tv_msg_merchant);
                tvMessageAmount = itemView.findViewById(R.id.tv_msg_amount);
                tvMessageAccount = itemView.findViewById(R.id.tv_msg_account_type);
                tvMessageDate = itemView.findViewById(R.id.tv_msg_date);
                ivCategoryIcon = itemView.findViewById(R.id.iv_category);
            }
        }
    }
}

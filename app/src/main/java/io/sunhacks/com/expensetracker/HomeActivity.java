package io.sunhacks.com.expensetracker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private RecyclerView rvMessagesList = null;
    private MessageAdapter rvAdapter = null;
    private List messages = null;
    private Map<String, String> numberAccountMap = null;

    private static final String LOG_TAG = "EXPENSE_TRACKER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rvMessagesList = findViewById(R.id.rv_lists);
        numberAccountMap = initializeMap();
    }

    public static Map<String, String> initializeMap() {
        Map<String, String> numberAccountMap = new HashMap<>();
        numberAccountMap.put("20736", "MidFirst");
        numberAccountMap.put("347268", "Discover");
        numberAccountMap.put("24273", "Chase");
        return numberAccountMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
        List<Sms> allMessages;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            getPermission();
        } else {
            allMessages = getAllMessages();
            messages = filterSms(allMessages);
            parseSms(messages);
        }
        Log.i(LOG_TAG, "aterPermissionCheck()");
        rvAdapter = new MessageAdapter(messages);
        rvMessagesList.setAdapter(rvAdapter);
        rvMessagesList.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter.notifyDataSetChanged();
        Log.i(LOG_TAG, "onResume() End");
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
        Log.i(LOG_TAG, "getAllMessages end");
        return lstSms;
    }

    public List<Sms> filterSms(List<Sms> messages) {
        List<Sms> filteredList = new ArrayList<>();
        for (Sms message : messages) {
            if (numberAccountMap.containsKey(message.getAddress())) {
                filteredList.add(message);

                //   Log.d("message", message.toString());
            }
        }
        return filteredList;
    }

    public List<SpendingModel> parseSms(List<Sms> messages) {
        List<SpendingModel> parsedList = new ArrayList<>();
        for (Sms message : messages) {
            SpendingModel spendingModel = new SpendingModel();
            spendingModel.setAccount(numberAccountMap.get(message.getAddress()));
            String strMsg = message.getMsg();
            if (spendingModel.getAccount().equals("Discover")) {
                // Discover messages are of format
                // Discover Card: Transaction of <Amount> at <Merchant> was made on <date>
                Pattern pattern = Pattern.compile("Transaction of $(.*?) at");
                Matcher matcher = pattern.matcher(strMsg);
                while (matcher.find()) {
                    Log.d("Amount is", matcher.group(1));
                }
            }
            spendingModel.setRawMessage(message);
//            spendingModel.setSmsTime(new Date(message.getTime()));
        }
        return parsedList;
    }

    public void getPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
                    messages = getAllMessages();
                    rvAdapter.notifyDataSetChanged();
                } else {
                    //SHow message to User
                }
            }
        }

    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        private List messageList;

        MessageAdapter(List<String> messages) {
            messageList = messages;
        }

        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview, null, false);
            view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
            if (holder != null) {
                Sms newSms = (Sms) messageList.get(position);
                holder.tvMessageBody.setText(newSms.getMsg());
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvMessageBody;

            ViewHolder(View itemView) {
                super(itemView);
                tvMessageBody = itemView.findViewById(R.id.tv_msg_body);
            }
        }
    }
}

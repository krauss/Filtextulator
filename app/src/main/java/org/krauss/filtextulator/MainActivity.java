package org.krauss.filtextulator;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.krauss.filtextulator_new.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FilterAdapter filterAdapter;
    private Button bt_load;
    private ImageView iv_conn;
    private TextView tv_ipaddr;
    private RecyclerView list_filters;
    private TextView tv_text;
    private TextView tv_processedtext;
    private EditText ev;
    private Button bt_process;
    private ImageView iv_random;
    private boolean isRandom = false;
    private LinearLayout linearl_1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_ipaddr = (TextView) findViewById(R.id.server_ip);
        bt_load = (Button) findViewById(R.id.bt_load);
        tv_text = (TextView) findViewById(R.id.text);
        tv_processedtext = (TextView) findViewById(R.id.tv_processedtext);
        iv_random = (ImageView) findViewById(R.id.iv_random);
        iv_conn = (ImageView) findViewById(R.id.conn_ok);
        list_filters = (RecyclerView) findViewById(R.id.list_filters);
        bt_process = (Button) findViewById(R.id.bt_process);
        linearl_1 = (LinearLayout) findViewById(R.id.linearl_1);

        filterAdapter = new FilterAdapter((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        list_filters.setAdapter(filterAdapter);
        list_filters.setLayoutManager(new LinearLayoutManager(this));
        list_filters.setHasFixedSize(true);

        ItemTouchHelper ith = new ItemTouchHelper(new FilterTouchHelper(filterAdapter));
        ith.attachToRecyclerView(list_filters);

        //Create the IP adress validator
        ev = (EditText) findViewById(R.id.server_ip);
        ev.addTextChangedListener(new TextWatcher() {

            String ipadressRegex = "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (ev.getText().toString().matches(ipadressRegex)) {
                    iv_conn.setImageResource(R.drawable.ic_done_black_18dp);
                } else {
                    iv_conn.setImageResource(android.R.color.transparent);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /**
     * Creates an AsyncTask to check the HTTP connection with the server
     *
     * @param view  Connect button
     */
    public void runConnectionChecker(View view) {

        new ConnectionChecker(tv_ipaddr.getText().toString()).execute();

    }

    /**
     * Creates an AsyncTask to load the filters from the server
     *
     * @param view  Get Filters button
     */
    public void runFilterLoader(View view) {

        new FilterLoader(tv_ipaddr.getText().toString(), isRandom).execute();

    }

    /**
     * Creates an AsynTask to apply all the filters selected on the text typed.
     *
     * @param view  Apply button
     */
    public void runFilterProcessor(View view) {

        List<String> filterlist = new ArrayList<String>();


        for (int i = 0; i < list_filters.getChildCount(); i++) {

            LinearLayout v = (LinearLayout) list_filters.getChildAt(i);

            if (((Switch) v.getChildAt(2)).isChecked()) {
                String f = ((TextView) v.getChildAt(1)).getText().toString();
                filterlist.add(f);
            }

        }
        if (filterlist.size() > 0 & (tv_text.getText().toString().length() > 0)) {

            new FilterProcessor(tv_ipaddr.getText().toString(), filterlist, tv_text.getText().toString()).execute();

        } else {
            Toast.makeText(this, "Select at least one filter and type some words!", Toast.LENGTH_SHORT).show();
        }

    }


    private class ConnectionChecker extends AsyncTask<Void, Void, Void> {

        private String ip_server;
        private String result;

        private ConnectionChecker(String s) {

            this.ip_server = s;
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpConnection conn = new HttpConnection(ip_server);
            this.result = conn.checkConnection();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (this.result.equalsIgnoreCase("OK")) {

                iv_conn.setImageResource(R.drawable.ic_done_all_black_18dp);
                bt_load.setEnabled(true);
                iv_random.setVisibility(View.VISIBLE);

                //It hides the IP address text view and the button to make more room for the filters
                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        linearl_1.animate().translationY(-200.0f).alpha(1.0f);
                    }
                }.start();
            }
        }
    }

    private class FilterLoader extends AsyncTask<Void, Void, Void> {

        private String ip_server;
        private InputStream result;
        private boolean random;

        private FilterLoader(String s, boolean random) {

            this.ip_server = s;
            this.random = random;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (filterAdapter.getItemCount() > 0) {
                filterAdapter.removeAllListItems();
            }

            if(bt_process.getVisibility() == View.INVISIBLE){
                bt_process.setVisibility(View.VISIBLE);
            }
            if(tv_text.getVisibility() == View.INVISIBLE){
                tv_text.setVisibility(View.VISIBLE);
            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpConnection conn = new HttpConnection(ip_server);
            this.result = conn.getFilterList(random);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                JSONArray filterjson = (new JSONObject(myInputReader(this.result))).getJSONArray("filters");


                for (int i = 0; i < filterjson.length(); i++) {

                    JSONObject item = filterjson.getJSONObject(i);
                    String name = item.getString("desc");

                    filterAdapter.addListItem(name);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class FilterProcessor extends AsyncTask<Void, Void, Void> {

        private String text;
        private String ip_server;
        private InputStream result;
        private List<String> filterlist;

        private FilterProcessor(String server_addr, List<String> filterlist, String text) {
            this.ip_server = server_addr;
            this.filterlist = filterlist;
            this.text = text;
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpConnection conn = new HttpConnection(ip_server);
            this.result = conn.processFilterList(filterlist, text);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String r = "";

            try {
                JSONArray filterjson = (new JSONObject(myInputReader(this.result))).getJSONArray("processed_filters");


                for (int i = 0; i < filterjson.length(); i++) {

                    JSONObject item = filterjson.getJSONObject(i);
                    r += "[" + item.getString("filt") + "]\t\t" + item.getString("text") + "\n";

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            tv_processedtext.setVisibility(View.VISIBLE);
            tv_processedtext.setText(r);

        }
    }


    public void setRandomLoad(View v){


        if(!isRandom) {
            iv_random.getDrawable().setTint(Color.parseColor("#46bddc"));
            Toast.makeText(this, "Random: ON", Toast.LENGTH_SHORT).show();
            isRandom = true;
        } else {
            iv_random.getDrawable().setTint(Color.parseColor("#000000"));
            Toast.makeText(this, "Random: OFF", Toast.LENGTH_SHORT).show();
            isRandom = false;
        }

    }

    protected String myInputReader(InputStream content) {

        String inputStr = "";
        String output = "";

        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();


            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            output = responseStrBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

}

package com.biy.reviewvia;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {



    private static final String TAG = "MainActivity";
    private ImageView image_scan;
    private ImageView image_enter;
    private ArrayList<String> mReviews = new ArrayList<>();
    String barcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_scan = findViewById(R.id.image_scan);
        final Activity activity = this;
        image_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
        image_enter = findViewById(R.id.image_enter);
        image_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_enter_barcode,null);
                final EditText mEnter = (EditText) mView.findViewById(R.id.editText_enter);
                Button mGobutton = (Button) mView.findViewById(R.id.btn_go);

                mGobutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mEnter.getText().toString().isEmpty()){
                            //st.makeText(MainActivity.this,"Enter successful",Toast.LENGTH_SHORT).show();
                            barcode = mEnter.getText().toString();
                            new GetProduct().execute();

                        }else{
                            Toast.makeText(MainActivity.this,"Please enter the barcode ",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });


        //for test only start
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReviewsActivity.class));
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_history:
                showHistory();
                return true;
            case R.id.item_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showHistory(){

    }

    void showAbout(){

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Log.d("MainActivity","cancel scanned");
                Toast.makeText(this,"cancelled",Toast.LENGTH_LONG).show();
            }else{
                Log.d("MainActivity","scanned");
                //Toast.makeText(this,"scanned:" + result.getContents(),Toast.LENGTH_LONG).show();
                barcode = result.getContents();
                new GetProduct().execute();
                ((TextView)findViewById(R.id.tv_prompt)).setText(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Pasted
    private String TAGapp = MainActivity.class.getSimpleName();
    private ListView lv;

    String productName="";


    private class GetProduct extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.upcitemdb.com/prod/trial/lookup?upc="+ barcode;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAGapp, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray products = jsonObj.getJSONArray("items");

                    int i = 0;
                    JSONObject c = products.getJSONObject(i);
                    String title = c.getString("title");
                    productName = title;


                } catch (final JSONException e) {
                    Log.e(TAGapp, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAGapp, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            productName,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}


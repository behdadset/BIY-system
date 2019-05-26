package com.biy.reviewvia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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
                            new GetProduct().execute(barcode);

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
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            case R.id.item_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                new GetProduct().execute(barcode);
                //((TextView)findViewById(R.id.tv_prompt)).setText(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toReviewer(String productName) {
        if (productName == null) {
            Toast.makeText(this, "No matching data", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(MainActivity.this, ReviewsActivity.class);
            intent.putExtra("product_name", productName);
            intent.putExtra("barcode", barcode);
            startActivity(intent);
        }

    }

    private class GetProduct extends AsyncTask<String, Void, String> {

        private static final String GET_PRODUCT_TAG = "tag_get_product";

        @Override
        protected String doInBackground(String... codes) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://api.upcitemdb.com/prod/trial/lookup?upc="+ codes[0];
            String jsonStr = sh.makeServiceCall(url);
            String result = null;

            Log.i(GET_PRODUCT_TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray products = jsonObj.getJSONArray("items");

                    int i = 0;
                    JSONObject c = products.getJSONObject(i);
                    result = c.getString("title");

                } catch (final JSONException e) {
                    Log.i(GET_PRODUCT_TAG, "Json parsing error: " + e.getMessage());

                }

            } else {
                Log.i(GET_PRODUCT_TAG, "Couldn't get json from server.");
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(final String result) {
            toReviewer(result);
        }



    }

    private static long back_pressed;

    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}


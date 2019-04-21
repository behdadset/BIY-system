package com.biy.reviewvia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView image_scan;
    private ImageView image_enter;
    private ArrayList<String> mReviews = new ArrayList<>();
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
                            Toast.makeText(MainActivity.this,"Enter successful",Toast.LENGTH_SHORT).show();
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
        //for test only end

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
                Toast.makeText(this,"scanned:" + result.getContents(),Toast.LENGTH_LONG).show();

                ((TextView)findViewById(R.id.tv_prompt)).setText(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

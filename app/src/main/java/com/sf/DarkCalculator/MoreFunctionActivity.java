package com.sf.DarkCalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MoreFunctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_function);
    }
    public void goToExchange(View view){
        Intent intent = new Intent(this,exchangeActivity.class);
        startActivity(intent);
    }
    public void goTointerest(View view){
        Intent intent = new Intent(this,interestActivity.class);
        startActivity(intent);
    }
    public void goToreduced(View view){
        Intent intent = new Intent(this,ReducedActivity.class);
        startActivity(intent);
    }

}

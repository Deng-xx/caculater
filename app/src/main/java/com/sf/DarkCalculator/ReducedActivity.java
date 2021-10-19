package com.sf.DarkCalculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReducedActivity extends AppCompatActivity {
    private Button category1;
    private Button category2;
    private Button category3;
    private Spinner spinner1;
    private Spinner spinner2;
    private TextView up;
    private TextView down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reduced);
        initlayout();
    }
    private void initlayout(){
        category1=findViewById(R.id.button4);
        category2=findViewById(R.id.button5);
        category3=findViewById(R.id.button6);
        spinner1=findViewById(R.id.spinner4);
        spinner2=findViewById(R.id.spinner3);
        up = findViewById(R.id.editTextNumber3);
        down = findViewById(R.id.editTextNumber4);
        category1.setText("长度");
        category2.setText("面积");
        category3.setText("时间");
        up.setText("请在此填写数字");
        down.setText("此处显示结果");
    }
}

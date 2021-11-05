package com.sf.DarkCalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReducedActivity  extends AppCompatActivity {
    private String[] typearray;
    public HashMap<String,Double> length = new HashMap<String,Double>();
    public HashMap<String,Double> area = new HashMap<String,Double>();
    public HashMap<String,Double> weight = new HashMap<String,Double>();
    public HashMap<String,Double> current_page = new HashMap<String,Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        length.put("厘米",100.0);
        length.put("分米",10.0);
        length.put("毫米",1000.0);
        length.put("米",1.0);
        length.put("公里/千米",0.001);
        length.put("微米",1000000.0);
        length.put("纳米",1000000000.0);
        length.put("英里",0.000621);
        length.put("英尺",3.281);
        length.put("丈",0.3);
        length.put("尺",3.0);
        length.put("海里",0.00054);
        length.put("码",1.09);

        area.put("平方厘米",10000.0);
        area.put("平方毫米",1000000.0);
        area.put("平方分米",100.0);
        area.put("平方米",1.0);
        area.put("平方千米",0.000001);
        area.put("平方码",1.19599);
        area.put("平方英尺",10.76391);
        area.put("英亩",0.00024710538);
        area.put("公顷",0.0001);

        weight.put("吨",0.001);
        weight.put("公斤",1.0);
        weight.put("克",1000.0);
        weight.put("毫克",1000000.0);
        weight.put("市斤",2.0);
        weight.put("担",0.02);
        weight.put("两",20.0);
        weight.put("钱",200.0);
        weight.put("磅",2.2046226);
        weight.put("盎司",35.273962);

        setContentView(R.layout.activity_reduced);
        typearray = getResources().getStringArray(R.array.length);
        current_page = length;
        initlayout();
    }
    private void initlayout(){
        Button category1 = findViewById(R.id.button4);
        Button category2 = findViewById(R.id.button5);
        Button category3 = findViewById(R.id.button6);
        final Spinner spinner1 = findViewById(R.id.spinner4);
        final Spinner spinner2 = findViewById(R.id.spinner3);
        final TextView up = findViewById(R.id.editTextNumber3);
        final TextView down = findViewById(R.id.editTextNumber4);
        category1.setText("长度");
        category2.setText("面积");
        category3.setText("重量");
        up.setText(null);
        down.setText(null);
        //设置监听器
        up.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(up.getText().length()==0){
                    down.setText("");
                    return;}
                String arg1 = spinner1.getSelectedItem().toString();
                String arg2 = spinner2.getSelectedItem().toString();
                Double argument =current_page.get(arg2)/ current_page.get(arg1);
                String str_tmp = up.getText().toString();
                Double up_num = Double.valueOf(str_tmp);
                Double result = up_num*argument;
                down.setText(result.toString());
            }
        });

        //spinner
        ArrayAdapter<String> typeadapter = new ArrayAdapter<String>(this, R.layout.item_select, typearray);
        spinner1.setAdapter(typeadapter);
        spinner2.setAdapter(typeadapter);
        spinner1.setSelection(0);
        spinner2.setSelection(0);
        //Button
        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typearray = getResources().getStringArray(R.array.length);
                current_page = length;
                initlayout();
                spinner2.setOnItemSelectedListener(new LengthSelectedListener());
                spinner1.setOnItemSelectedListener(new LengthSelectedListener());
            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typearray = getResources().getStringArray(R.array.area);
                current_page = area;
                initlayout();
                spinner2.setOnItemSelectedListener(new AreaSelectedListener());
                spinner1.setOnItemSelectedListener(new AreaSelectedListener());
            }
        });
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typearray = getResources().getStringArray(R.array.weight);
                current_page = weight;
                initlayout();
                spinner2.setOnItemSelectedListener(new TimeSelectedListener());
                spinner1.setOnItemSelectedListener(new TimeSelectedListener());
            }
        });
    }
    class LengthSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final Spinner spinner1 = findViewById(R.id.spinner4);
            final Spinner spinner2 = findViewById(R.id.spinner3);
            final TextView up = findViewById(R.id.editTextNumber3);
            final TextView down = findViewById(R.id.editTextNumber4);
            if(up.getText().length()==0){
                down.setText("");
                return;}
            String arg1 = spinner1.getSelectedItem().toString();
            String arg2 = spinner2.getSelectedItem().toString();
            Double argument =length.get(arg2)/ length.get(arg1);
            String str_tmp = up.getText().toString();
            Double up_num = Double.valueOf(str_tmp);
            Double result = up_num*argument;
            down.setText(result.toString());

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    class AreaSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final Spinner spinner1 = findViewById(R.id.spinner4);
            final Spinner spinner2 = findViewById(R.id.spinner3);
            final TextView up = findViewById(R.id.editTextNumber3);
            final TextView down = findViewById(R.id.editTextNumber4);
            if(up.getText().length()==0){
                down.setText("");
                return;}
            String arg1 = spinner1.getSelectedItem().toString();
            String arg2 = spinner2.getSelectedItem().toString();
            Double argument =area.get(arg2)/ area.get(arg1);
            String str_tmp = up.getText().toString();
            Double up_num = Double.valueOf(str_tmp);
            Double result = up_num*argument;
            down.setText(result.toString());

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    class TimeSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final Spinner spinner1 = findViewById(R.id.spinner4);
            final Spinner spinner2 = findViewById(R.id.spinner3);
            final TextView up = findViewById(R.id.editTextNumber3);
            final TextView down = findViewById(R.id.editTextNumber4);
            if(up.getText().length()==0){
                down.setText("");
                return;}
            String arg1 = spinner1.getSelectedItem().toString();
            String arg2 = spinner2.getSelectedItem().toString();
            Double argument =weight.get(arg2)/ weight.get(arg1);
            String str_tmp = up.getText().toString();
            Double up_num = Double.valueOf(str_tmp);
            Double result = up_num*argument;
            down.setText(result.toString());

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }}

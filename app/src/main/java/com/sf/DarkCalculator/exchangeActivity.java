package com.sf.DarkCalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class exchangeActivity extends AppCompatActivity {
    private TextView Text1;
    private TextView Text2;
    private String[] starArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        initSpinner();
    }

    public void reset(View view){
        Text1= (TextView) findViewById(R.id.input_1);
        Text2= (TextView) findViewById(R.id.input_2);
        Text1.setText(null);
        Text2.setText(null);
    }
    private void initSpinner() {
        starArray=getResources().getStringArray(R.array.money_type);
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(this, R.layout.item_select, starArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        Spinner sp = findViewById(R.id.spinnerIn);
        Spinner sp2 = findViewById(R.id.spinnerOut);
        //设置下拉框的标题，不设置就没有难看的标题了
        sp.setPrompt("请选择币种");
        sp2.setPrompt("请选择币种");
        //设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        sp2.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        sp.setSelection(0);
        sp2.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener());
        sp2.setOnItemSelectedListener(new MySelectedListener());
    }

    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(exchangeActivity.this, "您选择的是：" + starArray[i], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
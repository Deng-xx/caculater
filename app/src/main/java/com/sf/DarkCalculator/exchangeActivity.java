package com.sf.DarkCalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sf.ExpressionHandler.Complex;

public class exchangeActivity extends AppCompatActivity {
    private TextView Text1;
    private TextView Text2;
    private String[] starArray;
    private String[] rateToRMB;
    private String[] rateFromRMB;
    private Complex[] ToRMB = new Complex[50];
    private Complex[] fromRMB= new Complex[50];

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

    public void exchange(View view){
        Text1= (TextView) findViewById(R.id.input_1);
        Text2= (TextView) findViewById(R.id.input_2);
        Spinner spIn = findViewById(R.id.spinnerIn);
        Spinner spOut = findViewById(R.id.spinnerOut);
        String offer= Text1.getText().toString();
        String result= Text2.getText().toString();
        if(offer.length()==0)
        {
            Text2.setText(null);
            return;
        }
        Complex offered = new Complex(offer);
        Complex answer = new Complex(result);//获取到textview的string并转换为complex类以便后续操作
        Complex temp = new Complex();
        rateToRMB=getResources().getStringArray(R.array.rateToRMB_type);
        rateFromRMB=getResources().getStringArray(R.array.rateFromRMB_type);
        for (int i=0;i<rateToRMB.length;i++){
            ToRMB[i] = new Complex(rateToRMB[i]);
            fromRMB[i]=new Complex(rateFromRMB[i]);
        }   //把string数组资源转换为complex数组资源以便后续操作

      int x1=(int) spIn.getSelectedItemId();
      int x2=(int) spOut.getSelectedItemId();
        answer=Complex.mul(offered,ToRMB[x1]);
        answer=Complex.mul(answer,fromRMB[x2]);
        if(x1==x2){
            answer=Complex.mul(offered,new Complex(1));
        }


      Text2.setText(answer.toString());

    }

}
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



public class interestActivity extends AppCompatActivity {
    private TextView Text1;
    private TextView Text2;
    private TextView Text3;
    private TextView Text4;
    private TextView Text5;
    private TextView Text6;
    private TextView Text7;
    private String[] modeArray;
    private String[] dateArray;
    private int[] monthArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        initModeSpinner();
        initDateSpinner();
    }


    public void reset(View view){
        Text1= (TextView) findViewById(R.id.input_Rate);
        Text2= (TextView) findViewById(R.id.paymoney);
        Text3= (TextView) findViewById(R.id.FirstPay);
        Text4= (TextView) findViewById(R.id.month_dec);
        Text5= (TextView) findViewById(R.id.AllInterest);
        Text6= (TextView) findViewById(R.id.AllMoney);
        Text7= (TextView) findViewById(R.id.monthlyPay);
        Text1.setText(null);
        Text2.setText(null);
        Text3.setText(null);
        Text4.setText(null);
        Text5.setText(null);
        Text6.setText(null);
        Text7.setText(null);
    }
    private void initModeSpinner() {
        modeArray=getResources().getStringArray(R.array.payment_type);

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, R.layout.item_select, modeArray);
        Spinner spmode = findViewById(R.id.choice_spinner);
        modeAdapter.setDropDownViewResource(R.layout.item_dropdown);

        //设置下拉框的标题，不设置就没有难看的标题了
        spmode.setPrompt("请选择贷款方式");

        //设置下拉框的数组适配器
        spmode.setAdapter(modeAdapter);

        //设置下拉框默认的显示第一项
        spmode.setSelection(0);

        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        spmode.setOnItemSelectedListener(new interestActivity.MymodeSelectedListener());
    }
    class MymodeSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            Toast.makeText(interestActivity.this, "您选择的是：" + modeArray[i], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    private void initDateSpinner() {
        dateArray=getResources().getStringArray(R.array.Date);

        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(this, R.layout.item_select, dateArray);
        Spinner spdate = findViewById(R.id.paydate_spinner);
        dateAdapter.setDropDownViewResource(R.layout.item_dropdown);

        //设置下拉框的标题，不设置就没有难看的标题了
        spdate.setPrompt("请选择贷款时间");

        //设置下拉框的数组适配器
        spdate.setAdapter(dateAdapter);

        //设置下拉框默认的显示第一项
        spdate.setSelection(0);

        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        spdate.setOnItemSelectedListener(new interestActivity.MydateSelectedListener());
    }
    class MydateSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(interestActivity.this, "您选择的是：" + dateArray[i], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    public void calculate(View view){
        Spinner modeSp = findViewById(R.id.choice_spinner);
        Spinner dateSp = findViewById(R.id.paydate_spinner);
        Text3= (TextView) findViewById(R.id.FirstPay);
        Text4= (TextView) findViewById(R.id.month_dec);
        Text5= (TextView) findViewById(R.id.AllInterest);
        Text6= (TextView) findViewById(R.id.AllMoney);
        Text7= (TextView) findViewById(R.id.monthlyPay);
        int modex=(int) modeSp.getSelectedItemId();
        int datex=(int) dateSp.getSelectedItemId();
        dateArray=getResources().getStringArray(R.array.Date);
        modeArray=getResources().getStringArray(R.array.payment_type);
        monthArray=getResources().getIntArray(R.array.DateNumber);
        Text1= (TextView) findViewById(R.id.input_Rate);
        Text2= (TextView) findViewById(R.id.paymoney);
        String total=Text2.getText().toString();
        String rate=Text1.getText().toString();

       Complex month=new Complex((double)monthArray[datex]);      //拿到总月份数
       Complex allMoney=new Complex(total);                  //拿到总钱数
       Complex payRate=new Complex(rate);                    //拿到年利率
       Complex percent =new Complex(0.01);
       Complex monthRate=Complex.mul(Complex.div(payRate,new Complex(12)),percent);//算出月利率
        Complex firstPay = new Complex();
        Complex allInterest=new Complex();
        Complex monthDec=new Complex(0);
        Complex totalPay=new Complex();
        Complex monthlyPay=new Complex();

        if(modex==0){

             firstPay = Complex.add(Complex.div(allMoney,month),Complex.mul(monthRate,allMoney));
             allInterest=Complex.div(Complex.mul(Complex.mul(Complex.add(month,new Complex("1")),allMoney),monthRate),new Complex("2"));
            monthDec=Complex.mul(monthRate,Complex.div(allMoney,month));
            totalPay=Complex.add(allMoney,allInterest);
            Text7.setText("可按照每月递减计算");

        }//等额本金模式
        else    if(modex==1){
           Complex  rateAdjust= Complex.add(monthRate,new Complex(1));
           Complex temp=Complex.sub(Complex.pow(rateAdjust,month),new Complex(1));
             firstPay=Complex.div(Complex.mul(Complex.mul(Complex.pow(rateAdjust,month),monthRate),allMoney),temp);
             allInterest=Complex.sub(Complex.mul(firstPay,month),allMoney);
             totalPay=Complex.mul(firstPay,month);
             monthDec=new Complex(0);
             monthlyPay=firstPay;
            Text7.setText(monthlyPay.toString());
        }//等额本息模式
        Text3.setText(firstPay.toString());
        Text4.setText(monthDec.toString());
        Text5.setText(allInterest.toString());
        Text6.setText(totalPay.toString());



    }
}
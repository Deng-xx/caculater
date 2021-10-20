package com.sf.DarkCalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//ActionBar返回键
        TextView resultsText = (TextView) findViewById(R.id.text_results);
        resultsText.setText(getIntent().getStringExtra("results"));
    }

    //将.startActivity()及其所需的参数封装起来，这样启动ResultsActivity的参数就清晰了
    public static void actionStart(Context context, String results) {
        Intent intent = new Intent(context, ResultsActivity.class);
        intent.putExtra("results", results);
        context.startActivity(intent);
    }
}

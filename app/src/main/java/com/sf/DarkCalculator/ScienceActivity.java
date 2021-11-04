package com.sf.DarkCalculator;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.sf.ExpressionHandler.Constants;
import com.sf.ExpressionHandler.ExpressionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScienceActivity extends BaseActivity {

    public static ScienceActivity activity;
    private Context context;
    private Toolbar toolbar;
    private EditText inText;
    private TextView outText;

    //按钮显示
    private static final String[] BUTTON = {
            "(", ")", "^", "~", "ans", "e", "π",
            "sqrt", "cbrt", "abs", "C", "÷", "×", "DEL",
            "ln", "exp", "fact", "7", "8", "9", "-",
            "sin", "cos", "tan", "4", "5", "6", "+",
            "asin", "acos", "atan", "1", "2", "3", "=",
            "<", ">", "i", "%", "0", ".", "!"
    };

    //按钮中文显示
    private static final String[] BUTTON_CN = {
            "左括号", "右括号", "指数", "科学计数法", "上次计算答案", "常量e", "常量π",
            "平方根", "立方根", "绝对值", "清除键", "除号", "乘号", "删除键",
            "自然对数", "e底指数", "阶乘", "7就是7", "8就是8", "9就是9", "减号",
            "正弦", "余弦", "正切", "4就是4", "5就是5", "6就是6", "加号",
            "反正弦", "反余弦", "反正切", "1不认识？", "2就是2", "3就是3", "等号",
            "往左移", "往右移", "复数i", "百分号", "00000", "小数点儿", "阶乘"
    };
    //正则表达式
    private static final Pattern FUNCTIONS_KEYWORDS = Pattern.compile(
            "\\b(" + "sqrt|cbrt|abs|lg|ln|exp|fact|" +
                    "sin|cos|tan|asin|acos|atan|Γ" + ")\\b");

    private static final Pattern CONSTANS_KEYWORDS2 = Pattern.compile(
            "\\b(" + "ans|reg|true|false|me|mn|mp" + ")\\b");

    private static final Pattern CONSTANS_KEYWORDS1 = Pattern.compile("[∞°%πe]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        context = this;
        super.onCreate(savedInstanceState);
        //设置全屏（取消状态栏）
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_science);
        initToolBar();
        initInTextView();
        initOutTextView();
        initButton();
    }

    private boolean modified = true;
    private int selection = 0;

    private void modifyInText(String str){
        int index = inText.getSelectionStart();
        int index2 = inText.getSelectionEnd();
        if (index == index2) {
            inText.getText().insert(index, str);
        } else {
            inText.getText().replace(index, index2, str);
        }
    }

    private void moveSelectionInText(String str){
        selection = inText.getSelectionStart();
        int length = inText.getText().length();

        //注意字符头尾不能继续移动光标
        if(selection != 0 && str.equals("<")) {
            selection--;
            inText.setSelection(selection);
        }
        if(selection != length && str.equals(">")) {
            selection++;
            inText.setSelection(selection);
        }
    }

    private void initButton() {
        GridView buttonBar = findViewById(R.id.science_button);
        buttonBar.setNumColumns(7);
        buttonBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String str = BUTTON[i];
                boolean isFunction = (i >= 7 && i <= 9) || (i >= 14 && i <= 16) ||(i >= 21 && i <= 23)||(i >=28 && i <= 30);
                if(isFunction){
                    str = str + "()";
                }
                switch (str){
                    case "=":
                        if(calcThread != null){
                            Snackbar.make(view, "请等待当前运算完成", Snackbar.LENGTH_SHORT)
                                    .setAction("停止运算", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ExpressionHandler.stop();
                                        }
                                    }).show();
                            return;
                        }
                        outText.setTextColor(0xffbdbdbd);
                        calcThread = new Calc(inText.getText().toString());
                        calcThread.start();
                        return;
                    case "÷":
                        str = "/";
                        break;
                    case "×":
                        str = "•";
                        break;
                    case "C":
                        ExpressionHandler.stop();
                        inText.setText(null);
                        return;
                    case "DEL":
                        Editable editable = inText.getText();
                        int index = inText.getSelectionStart();
                        int index2 = inText.getSelectionEnd();
                        if (index == index2) {
                            if (index == 0) return;
                            editable.delete(index - 1, index);
                        } else {
                            editable.delete(index, index2);
                        }
                        break;
                    case "<":
                        moveSelectionInText(str);
                    case ">":
                        moveSelectionInText(str);
                    default:
                }
                if (str.equals("DEL") || str.equals("<") || str.equals(">")) {
                    return;
                }
                modifyInText(str);
            }
        });
        buttonBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = BUTTON_CN[i];
                AlertDialog.Builder dialog = new AlertDialog.Builder(ScienceActivity.this);
                dialog.setTitle(text);
                dialog.setPositiveButton("确定", null);
                dialog.show();
                return true;
            }
        });
        GridViewAdapter adapter = new GridViewAdapter(buttonBar, Arrays.asList(BUTTON), R.layout.button_science, 6);
        buttonBar.setAdapter(adapter);
    }

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.science_toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("帮助").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ScienceActivity.this);
                dialog.setTitle("帮助")
                        .setMessage(R.string.science_help)
                        .setPositiveButton("确定", null)
                        .show();
                return true;
            }
        });
        menu.add("更多功能").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ScienceActivity.this);
                dialog.setTitle("更多功能")
                        .setMessage(R.string.app_more)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent it = new Intent(ScienceActivity.this,MoreFunctionActivity.class);
                                startActivity(it);
                            }
                        })
                        .show();

                return true;
            }
        });
        return true;
    }

    //和MainActivity一样的Thread
    class FastCalc extends Thread implements Runnable {
        private String exp;

        public FastCalc(String exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            final long t = System.currentTimeMillis();
            final String[] value = ExpressionHandler.calculation(exp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    outText.setTextColor(0xffbdbdbd);
                    if (value[0].getBytes().length > 1000) {
                        outText.setText("数值太大");
                    } else
                        outText.setText(value[0]);
                    rootValue = value[0];
                    calcThread = null;
                }
            });
        }
    }

    class Calc extends Thread implements Runnable {
        private String exp;

        public Calc(String exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            final long t = System.currentTimeMillis();
            final String[] value = ExpressionHandler.calculation(exp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (value[1].equals("true")) {
                        outText.setTextColor(0xffff4081);
                        outText.setText(value[0]);
                    } else {
                        Constants.setAns(value[0]);
                        if (value[0].getBytes().length > 1000) {
                            outText.setText("数据太长");
                            //启动结果页面显示结果
                            ResultsActivity.actionStart(context, value[0]);
                        } else
                            outText.setText(value[0]);
                    }
                    rootValue = value[0];
                    calcThread = null;
                }
            });
        }

    }

    private Thread calcThread;
    private String rootValue;

    private void initInTextView() {
        inText = (EditText) findViewById(R.id.science_in);
        AutofitHelper.create(inText).setMinTextSize(18).setMaxLines(1);
        inText.requestFocus();
        //inText.requestFocusFromTouch();
        inText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    outText.setTextColor(0xffbdbdbd);
                    outText.setText(null);
                    rootValue = null;
                    return;
                }

                if (calcThread == null) {
                    calcThread = new FastCalc(s.toString());
                    calcThread.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!modified) return;

                selection = inText.getSelectionStart();
                s.clearSpans();


                for (Matcher m = Pattern.compile("x").matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xfff48fb1), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = CONSTANS_KEYWORDS1.matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xfffff59d), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = CONSTANS_KEYWORDS2.matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xfffff59d), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = FUNCTIONS_KEYWORDS.matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xffa5d6a7), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = Pattern.compile("[()\\-*+.,/!^=√•]").matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xff81d4fa), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                modified = false;
                inText.setText(s);
                modified = true;

                if (selection >= 2 && s.toString().substring(selection - 2, selection).equals("()"))
                    selection--;
                inText.setSelection(selection);
            }
        });
    }

    private void initOutTextView(){
        outText = (TextView) findViewById(R.id.science_out);
        outText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(rootValue);
                Snackbar.make(v, "已复制运算结果", Snackbar.LENGTH_SHORT).show();
            }
        });
        outText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ResultsActivity.actionStart(v.getContext(), rootValue);
                return true;
            }
        });
    }
}
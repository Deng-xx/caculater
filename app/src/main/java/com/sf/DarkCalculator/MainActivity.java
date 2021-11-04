package com.sf.DarkCalculator;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.sf.ExpressionHandler.Constants;
import com.sf.ExpressionHandler.ExpressionHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    public static MainActivity activity;
    private Context context;
    private Toolbar  toolbar;
    private EditText inText;
    private TextView stateText;
    private TextView outText;
//    private ViewPager drawerPager;
//    private DrawerLayout drawer;
//    private ArrayList<View> drawerPageList;
    public FrameLayout delete;


    private static final int[] XX = {1, 3, 1, 3};    //用于gridview设置列数
    private static final int[] YY = {6, 4, 5, 5};    //用于gridViewAdapter传参（行数），行数会与gridview.height一起来计算每行子控件Button的高度

    //初始化操作按钮
    private static final String[] OPERATOR = {"÷", "×", "-", "+"};
    //private static final String[] OPERATOR_VICE = {"√", "^", "!", "()", "°", "∞", "x"};

    //初始化侧边栏函数按钮显示
/*    private static final String[][] BUTTON = {
            {       "sqrt", "cbrt", "abs",
                     "ln", "exp", "fact",
                    "sin", "cos", "tan",
                    "asin", "acos", "atan", //函数
                    },
            {"ans", "π", "e",           //常数
                    }};*/

    //函数中文显示，在科学计算器里弄好了，长按按钮显示
/*    private static final String[][] BUTTON_VICE = {
            {       "平方根", "立方根", "绝对值",
                    "自然对数", "e底指数", "阶乘",
                    "正弦", "余弦", "正切",
                    "反正弦", "反余弦", "反正切", "阶乘",
                    },
            {"上次运算",  "圆周率", "自然底数"}};*/
    //正则表达式
//    private static final Pattern FUNCTIONS_KEYWORDS = Pattern.compile(
//            "\\b(" + "sqrt|cbrt|abs|lg|ln|exp|fact|" +
//                    "sin|cos|tan|asin|acos|atan|Γ" + ")\\b");
//
//    private static final Pattern CONSTANS_KEYWORDS2 = Pattern.compile(
//            "\\b(" + "ans|reg|true|false|me|mn|mp" + ")\\b");
//
//    private static final Pattern CONSTANS_KEYWORDS1 = Pattern.compile("[∞°%πe]");

    //private static final String[] FUNCTION_LIST = {"科学计算", "大数计算", "进制转换", "大写数字"};

    //初始化数字栏按钮显示
    private static final String[] NUMERIC = {
            "7", "8", "9",
            "4", "5", "6",
            "1", "2", "3",
            "·", "0", "=",
            "A", "B", "C",
            "D", "E", "F",
            "⑵", "⑽", "⒃",
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initEditText();
        initTextView();
        //initDrawer();
        //initPages();
        //initTabs();
        initDelete();
        //initSideBar();
        initNumeric();
        initOperator();
        //initFunction();
    }

    //delete键绑定监听器
    private void initDelete() {
        delete = (FrameLayout) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = inText.getText();
                int index = inText.getSelectionStart();
                int index2 = inText.getSelectionEnd();
                if (index == index2) {
                    if (index == 0) return;
                    editable.delete(index - 1, index);
                } else {
                    editable.delete(index, index2);
                }
            }
        });
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ExpressionHandler.stop();
                inText.setText(null);
                return true;
            }
        });
    }

    //初始化结果框
    private void initTextView() {
        stateText = (TextView) findViewById(R.id.text_state);
        stateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpressionHandler.stop();
                stateText.setText(null);
            }
        });
        outText = (TextView) findViewById(R.id.text_out);
        outText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //粘贴板复制结果
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(rootValue);
                Snackbar.make(v, "已复制运算结果", Snackbar.LENGTH_SHORT).show();//toast
            }
        });
        //长按启动ResultsActivity
        outText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ResultsActivity.actionStart(v.getContext(), rootValue);
                return true;
            }
        });
    }

    //初始化抽屉。监听了点击事件，打开右侧抽屉
/*    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        findViewById(R.id.drawer_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });
    }*/

    //初始化抽屉里的页面切换指示器，就设置了一下显示文本和绑定ViewPager
/*    private void initTabs() {
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs_main);
        tabs.setupWithViewPager(drawerPager);//绑定ViewPager让页面可以左右滑动浏览
        tabs.getTabAt(0).setText("函数");//不要用addTab()来添加标签和显示文本，因为setupWithViewPager会removeAllTabs()再添加标签
        tabs.getTabAt(1).setText("常数");//所以只需要设置一下显示文本就行
    }*/

    //初始化页面，主要初始化的是左右两侧的抽屉。这里没有左侧抽屉
/*    private void initPages() {
        drawerPageList = new ArrayList<>();
        //从MainActivity实例化3个gridView送入drawerPageList
        for (int i = 0; i < 2; i++) {
            GridView gridView = new GridView(this);
            drawerPageList.add(gridView);
        }
        drawerPager = (ViewPager) findViewById(R.id.viewPager_drawer);
        MainPagerAdapter drawerPagerAdapter = new MainPagerAdapter(drawerPageList);
        drawerPager.setAdapter(drawerPagerAdapter);
        //重载OnPageChangeListener()必须要重载以下三个方法
        drawerPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {//TODO:已弃用，建议改为addOn...
            //当页面在滑动时至滑动被停止之前，此方法会一直调用
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            //页面跳转完后调用,position是当前选中页面的position
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);//设置右抽屉打开手势滑动
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);//没来及做左抽屉，可以删了
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);//设置右抽屉默认打开且关闭手势滑动
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);//设置左抽屉默认关闭且关闭手势滑动
                }
            }

            //页面状态改变时调用
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }*/

    //TODO:初始化左侧抽屉。完全是空的，没有做完。可以删
/*    private void initSideBar() {
        final GridView sideBar = findViewById(R.id.sideBar);
        sideBar.setNumColumns(XX[0]);
        sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
//                    case 1:
//                        BigDecimalActivity.actionStart(context);
//                        break;
//                    case 2:
//                        BaseConversionActivity.actionStart(context);
//                        break;
//                    case 3:
//                        CapitalMoneyActivity.actionStart(context);
//                        break;
                    default:
                        Snackbar.make(sideBar, "功能还未完善", Snackbar.LENGTH_SHORT).show();
                }
                drawer.closeDrawer(GravityCompat.START);//关闭左抽屉
            }
        });
//        GridViewAdapter sideBarAdapter = new GridViewAdapter(sideBar, Arrays.asList(FUNCTION_LIST),
//                null, R.layout.button_sidebar, YY[0]);
       // sideBar.setAdapter(sideBarAdapter);
    }*/

    //初始化数字按键，按键显示和预先设置的NUMERIC[position]一样。用户点击对应position,将NUMERIC[position]的字符传入str
    private void initNumeric() {
        GridView numericBar = findViewById(R.id.bar_numeric);//获取gridview
        //先设置数字按键网格的列数
        numericBar.setNumColumns(XX[1]);//3
        numericBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = position == 9 ? "." : NUMERIC[position];//NUMERIC[9]为了美观不是小数点，所以这里处理一下
                if (str.equals("=")) {
                    if (calcThread != null) {
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
                    stateText.setText("运算中...");
                    calcThread = new Calc(inText.getText().toString());//实例化Calc对象
                    calcThread.start();//将calcThread可运行
                    return;
                }
                modifyInText(str);
            }
        });
        GridViewAdapter numericAdapter = new GridViewAdapter(numericBar, Arrays.asList(NUMERIC),
                 R.layout.button_numeric, YY[1]);//item是通过这个与gridview绑定的
        numericBar.setAdapter(numericAdapter);
    }

    private void initOperator() {
        GridView operatorBar = (GridView) findViewById(R.id.bar_operator);
        operatorBar.setNumColumns(XX[2]);
        operatorBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = position == 0 ? "/" : OPERATOR[position];
                modifyInText(position == 1 ? "•" : str);
            }
        });
//        operatorBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                modifyInText(OPERATOR_VICE[position]);
//                return true;
//            }
//        });
        GridViewAdapter operatorAdapter = new GridViewAdapter(operatorBar, Arrays.asList(OPERATOR),
                R.layout.button_operator, YY[2]);
              operatorBar.setAdapter(operatorAdapter);
    }

    //初始化函数，搬到科学计数法里并完善了
/*    private void initFunction() {
        int i = 0;
        for (View view : drawerPageList) {
            GridView operatorProBar = (GridView) view;
            operatorProBar.setNumColumns(XX[3]);

            if (i == 0) {
                operatorProBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyInText((BUTTON[0][position].equals("gamma") ? "Γ" : BUTTON[0][position]) + "()");
                    }
                });

                operatorProBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String text = BUTTON[0][position];
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle(text);
                        //dialog.setMessage(HelpUtil.getFunctionHelp(text));
                        dialog.setPositiveButton("确定", null);
                        dialog.show();
                        return true;
                    }
                });
            } else {
                operatorProBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyInText(BUTTON[1][position]);
                    }
                });
            }
            int id = i == 0 ? R.layout.button_function : R.layout.button_constant;
            GridViewAdapter operatorProAdapter = new GridViewAdapter(operatorProBar,
                    Arrays.asList(BUTTON[i++]), id, YY[3]);

            operatorProBar.setAdapter(operatorProAdapter);
        }
    }*/

    //修正输入框字符串
    private void modifyInText(String str) {
        int index = inText.getSelectionStart();
        int index2 = inText.getSelectionEnd();
        if (index == index2) {
            inText.getText().insert(index, str);
        } else {
            inText.getText().replace(index, index2, str);
        }
    }

    //用线程来完成处理表达式并及时更新主线程的UI信息
    class FastCalc extends Thread implements Runnable {
        private String exp;

        public FastCalc(String exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            final long t = System.currentTimeMillis();
            final String[] value = ExpressionHandler.calculation(exp);//调用接口处理表达式
            runOnUiThread(new Runnable() {
                @Override
                public void run() {//在主线程里操作UI,android不能在主线程（UI线程）以外操作UI
                    outText.setTextColor(0xffbdbdbd);//橙色
                    stateText.setText("运算结束，耗时 " + (System.currentTimeMillis() - t) + " 毫秒");
                    if (value[0].getBytes().length > 1000) {
                        outText.setText("数值太大");//数值太大直接显示数值太大
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
                        stateText.setText("运算结束，耗时 " + (System.currentTimeMillis() - t) + " 毫秒");
                        //相比FastCalc多了判断数据是否要用ResultsActivity显示的
                    if (value[1].equals("true")) {
                        outText.setTextColor(0xffff4081);//白色
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
                    calcThread = null;//线程执行完毕要将统一的变量赋Null，以便下次判断线程是否执行完毕
                }
            });
        }

    }

    private Thread calcThread;//一个统一的计算线程变量，用该变量重复引用每次计算创建的线程对象
    private String rootValue;

    private void initEditText() {
        inText = (EditText) findViewById(R.id.editText);
        //设置字数过多自动缩小，在最小size为28时的最大行数为1行。字数再多就直接换行了。
        AutofitHelper.create(inText).setMinTextSize(28).setMaxLines(1);
        inText.requestFocus();//Activity执行后马上获取焦点
        //inText.requestFocusFromTouch();//有点小重复
        inText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {//判断null/""
                    if (calcThread == null)
                        stateText.setText(null);
                    outText.setTextColor(0xffbdbdbd);
                    outText.setText(null);
                    rootValue = null;
                    return;
                }

                if (calcThread == null) {
                    stateText.setText("运算中...");
                    calcThread = new FastCalc(s.toString());
                    calcThread.start();//线程可运行态
                }
            }

            //原本做的是匹配到函数就把光标移到括号中，后面移到科学计算器里了，也是一样的功能
            @Override
            public void afterTextChanged(Editable s) {
/*                if (!modified) return;//没有修改就返回

                //获取光标位置
                selection = inText.getSelectionStart();
                s.clearSpans();

                //匹配字符，并标注不可修改
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
                inText.setSelection(selection);*/
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setSubtitle("简易计算器");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    //创建菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("帮助").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("帮助")
                        .setMessage(R.string.app_help)
                        .setPositiveButton("确定", null)
                        .show();
                return true;
            }
        });
        menu.add("退出").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });
        menu.add("更多功能").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("更多功能")
                        .setMessage(R.string.app_more)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent it = new Intent(MainActivity.this,MoreFunctionActivity.class);
                                startActivity(it);
                            }
                        })
                        .show();

                return true;
            }
        });
        return true;
    }



    //修改返回键操作，在侧边栏弹出时，点击返回时关闭侧边栏。没有就执行正常的
/*    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawerPager.setCurrentItem(0);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
            drawer.closeDrawer(GravityCompat.END);
            return;
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }*/

    //菜单的响应事件，根据ItemId辨别响应事件
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //判断抽屉右侧菜单是否打开
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawerPager.setCurrentItem(0);
                    //设置抽屉锁定模式，这里设置为解锁可以打开右侧菜单
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                    //关闭抽屉
                    drawer.closeDrawer(GravityCompat.END);
                } else if (drawer.isDrawerOpen(GravityCompat.START))//如果左侧菜单打开就直接关闭
                    drawer.closeDrawer(GravityCompat.START);
                else//否则打开左侧菜单，但是没有左侧菜单
                    drawer.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }*/
}

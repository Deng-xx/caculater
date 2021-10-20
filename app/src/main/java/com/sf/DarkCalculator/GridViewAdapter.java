package com.sf.DarkCalculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private List<String> text;//数据集，预设的String[position]
    private List<String> viceText;
    private GridView gridView;//父控件gridView
    private int layoutId;//gridView子控件（各种button）的id
    private int rows;//行数
    private static int height;//存放父控件gridView的高度
    private ViewGroup.LayoutParams deleteParam;//删除按钮特殊布局的参数
    private ViewGroup.LayoutParams equalParam;//等号按钮特殊布局的参数

    public GridViewAdapter(GridView gridView, List<String> text, int layoutId, int rows) {
        this.text = text;//
        //this.viceText = viceText;
        this.gridView = gridView;
        this.layoutId = layoutId;
        this.rows = rows;
    }

    //返回数据集的条目数
    @Override
    public int getCount() {
        return text.size();
    }

    //获取数据集中与指定索引对应的数据项
    @Override
    public Object getItem(int position) {
        return text.get(position);
    }

    //取在列表中与指定索引对应的行id
    @Override
    public long getItemId(int position) {
        return position;
    }

    //静态类暂存view
    private class ViewHolder {
        TextView title;
        TextView vice;//用于中文显示
    }

    //TODO:获取在数据集中指定位置显示数据的视图，优化了，然后可以设置一下科学计算器等号的特殊布局，以后吧
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {//缓存convertView，避免重复实例化view的浪费
            //和findViewById()类似但作用不同，不同点是LayoutInflater()是用来找res/layout/下的xml布局文件，并且实例化
            //在没有载入或动态载入的界面使用LayoutInflater.inflate()来载入
            //对于一个已经载入的界面，可以使用findViewById()方法来获得其中的界面元素。
            //.inflate(需要加载资源的id,资源需要被添加的地方,是否要被添加到root中PS:适配器里不能设置为true)
            if(layoutId == R.layout.button_science)
                convertView = LayoutInflater.from(ScienceActivity.activity).inflate(layoutId, parent, false);
            else
                convertView = LayoutInflater.from(MainActivity.activity).inflate(layoutId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.text_item);//在convertView中查找。convertView就是刚刚载入的那个布局文件
            if (viceText != null)//有几个没有这个组件
                viewHolder.vice = (TextView) convertView.findViewById(R.id.text_vice_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(text.get(position));//显示文本绑定在textview上

        if (viceText != null) {
            viewHolder.vice.setText(viceText.get(position));//副显示文本绑定
        }

        if (height == 0)
            height = gridView.getHeight();

        //设置宽度为MATCH_PARENT，高度为height/row也可以在xml设置
        GridView.LayoutParams param = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height / rows);
        convertView.setLayoutParams(param);

        //载入操作符时还要把特殊布局的删除键载入一下
        if (layoutId == R.layout.button_operator) {
            if (deleteParam == null)
                deleteParam = MainActivity.activity.delete.getLayoutParams();
            if (deleteParam.height != height / rows) {
                deleteParam.height = height / rows;
                MainActivity.activity.delete.setLayoutParams(deleteParam);
            }
        }

//        if (layoutId == R.layout.button_science) {
//            if (deleteParam == null)
//                deleteParam = ScienceActivity.activity.delete.getLayoutParams();
//            if(equalParam.height != 2 * height / rows) {
//                equalParam.height = 2 * height / rows;
//                //TODO:将参数赋给等号，itemPosition=34
//            }
//        }

        return convertView;
    }
}

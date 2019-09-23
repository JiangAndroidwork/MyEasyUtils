package com.jiang.myeasyutils;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jiang.myeasyutils.activity.CalendarActivity;
import com.jiang.myeasyutils.activity.HVViewpagerActivity;
import com.jiang.myeasyutils.activity.SlidingUpActivity;
import com.jiang.myeasyutils.activity.VerticalViewPagerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.list_view)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
        initView();
        initListener();
    }

    private void initListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    SlidingUpActivity.start(view.getContext());
                    break;
                case 1:
                    VerticalViewPagerActivity.start(view.getContext());
                    break;
                case 2:
                    HVViewpagerActivity.start(view.getContext());
                    break;
                case 3:
                    CalendarActivity.start(view.getContext());
                    break;
            }
        });
    }

    private void initView() {
        List<String> titls = new ArrayList<>();
        titls.add("向上划出内容");
        titls.add("竖直viewpager");
        titls.add("横滑嵌套竖滑");
        titls.add("往返时间选择");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titls);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

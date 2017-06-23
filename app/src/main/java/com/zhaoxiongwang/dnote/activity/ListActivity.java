package com.zhaoxiongwang.dnote.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.zhaoxiongwang.dnote.R;
import com.zhaoxiongwang.dnote.adpter.NoteAdapter;
import com.zhaoxiongwang.dnote.global.NoteFactory;
import com.zhaoxiongwang.dnote.model.Note;

import java.util.List;

/**
 * Class ListActivity
 *
 * @author XhinLiang
 */
public class ListActivity extends AppCompatActivity {
    public static final String KEY_EXTRA_NOTE = "note";
    public static final int REQUEST_FOR_EDIT_NOTE = 100;
    private static final int REQUEST_FOR_CREATE_NOTE = 101;
    private NoteAdapter adapter;
    private ListView listView;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NoteFactory noteFactory = NoteFactory.getInstance();
    private NoteAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initData();
        initListView();
        initEvents();
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.listview_content);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        setSupportActionBar(toolbar);
    }

    private void initData() {
        adapter = new NoteAdapter(this, noteFactory.getNotes());
        noteFactory.updatefactory(adapter);
    }

    private void initListView() {
        listView.setNestedScrollingEnabled(true);
        listView.setAdapter(adapter);
        noteFactory.updatefactory(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra(KEY_EXTRA_NOTE, position);
                startActivityForResult(intent, REQUEST_FOR_EDIT_NOTE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(ListActivity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(noteFactory.getNotes().get(position)!=null){
                         noteFactory.deletefactory(noteFactory.getNotes().get(position));
                            noteFactory.getNotes().remove(position);
                            System.out.println("success");
                        }else {
                            System.out.println("failed");
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return true;
            }
        });
        /*滑动删除*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                intent.putExtra(KEY_EXTRA_NOTE, -1);
                startActivityForResult(intent, REQUEST_FOR_CREATE_NOTE);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        // 刷新完毕，关闭下拉刷新的组件
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_EDIT_NOTE) {
            Snackbar.make(fab,"编辑成功", Snackbar.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
        }
        if (requestCode == REQUEST_FOR_CREATE_NOTE) {
            Snackbar.make(fab, "创建成功", Snackbar.LENGTH_LONG)
                    .setAction("撤销", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            noteFactory.deletefactory(noteFactory.getNotes().get( noteFactory.getNotes().size()-1));
                            noteFactory.getNotes().remove(noteFactory.getNotes().size() - 1);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .show();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                AVUser.getCurrentUser().logOut();
                finish();
                Intent intent =new Intent(ListActivity.this,LoginActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

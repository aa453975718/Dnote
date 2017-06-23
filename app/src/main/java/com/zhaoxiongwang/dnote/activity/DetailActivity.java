package com.zhaoxiongwang.dnote.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.zhaoxiongwang.dnote.R;
import com.zhaoxiongwang.dnote.global.NoteFactory;
import com.zhaoxiongwang.dnote.model.Note;


public class DetailActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private TextView tvContent;
    private EditText etContent;
    private EditText etTitle;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NoteFactory noteFactory = NoteFactory.getInstance();
    private Note note;
    private boolean editMode = true;
    private AlertDialog dialog;
    private int noteIndex;
    private String ObejectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // 初始化View相关的内容
        initView();
        // 初始化数据
        initData();
        // 初始化事件
        initEvent();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tvContent = (TextView) findViewById(R.id.tv_content);
        etContent = (EditText) findViewById(R.id.et_content);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etTitle = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        etTitle.setLayoutParams(lp);

        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.input_new_title)
                .setView(etTitle)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note.setTitle(etTitle.getText().toString());
                        collapsingToolbarLayout.setTitle(note.getTitle());
                    }
                })
                .create();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 获得传递过来的Intent（可见ListActivity）
        Intent intent = getIntent();
        // 看看是否带了KEY_EXTRA_NOTE这个参数
        // 如果带了，这个参数就是全局的笔记列表中这条笔记的INDEX，为正整数，表示这一次的操作是浏览一条已经存在的笔记
        // 如果不带，设置为-1，表示这一次的操作是新建一个笔记
         noteIndex = intent.getIntExtra(ListActivity.KEY_EXTRA_NOTE, -1);
        // 不带参数
        if (noteIndex == -1) {
            // 在笔记库中添加一条笔记，笔记标题和内容是默认的
             noteFactory.addNote(new Note(getString(R.string.edit_title), getString(R.string.edit_content)));
            noteIndex = noteFactory.getNotes().size() - 1;
        }
        note=noteFactory.getNotes().get(noteIndex);
        etTitle.setText(note.getTitle());
        tvContent.setText(note.getContent());
        etContent.setText(note.getContent());
        collapsingToolbarLayout.setTitle(note.getTitle());

    }

    private void initEvent() {
        switchEditMode();

        // 点击标题的时间

        collapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode){
                // 给对话框里的EditText设置内容为现在的标题内容
                etTitle.setText(note.getTitle());;
                // 显示对话框（dialog）
                dialog.show();}
            }
        });
    }


    /**
     * 切换 “查看模式” 和 “编辑模式”
     */
    private void switchEditMode() {
        editMode = !editMode;
        etContent.setVisibility(editMode ? View.VISIBLE : View.GONE);
        tvContent.setVisibility(editMode ? View.GONE : View.VISIBLE);
        fab.setImageResource(editMode ? android.R.drawable.ic_menu_save : android.R.drawable.ic_menu_edit);
        // 如果是编辑模式的话，点击按钮先保存笔记的标题和内容再转换模式
        if (editMode) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note.setTitle(etTitle.getText().toString());
                    note.setContent(etContent.getText().toString());
                    if(note.getObjectID()==null){
                        final AVObject mNote = new AVObject("Todo");
                        mNote.put("owner",AVUser.getCurrentUser());
                        mNote.put("title", note.getTitle());
                        mNote.put("content", note.getContent());
                        mNote.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    // 存储成功
                                    note.setObjectID(mNote.getObjectId());
                                } else {
                                    // 失败的话，请检查网络环境以及 SDK 配置是否正确
                                }
                            }
                        });
                    }
                    else  {
                        AVObject mNote = AVObject.createWithoutData("Todo",note.getObjectID() );
                        // 修改 content
                        mNote.put("title", note.getTitle());
                        mNote.put("content", note.getContent());
                        // 保存到云端
                        mNote.saveInBackground();

                    }
                    etTitle.setText(note.getTitle());
                    note.setTitle(note.getTitle());
                   tvContent.setText(note.getContent());
                    Snackbar.make(v, "笔记保存成功", Snackbar.LENGTH_LONG).show();
                    switchEditMode();
                }
            });
            return;
        }
        // 如果是查看模式，点击按钮只切换模式
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchEditMode();
            }
        });
    }

    /**
     * 设置点击菜单的事件
     * @param item 点击的菜单 Item
     * @return 是否消费这个点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

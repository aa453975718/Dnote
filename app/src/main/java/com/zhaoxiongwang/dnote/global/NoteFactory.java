package com.zhaoxiongwang.dnote.global;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.zhaoxiongwang.dnote.adpter.NoteAdapter;
import com.zhaoxiongwang.dnote.model.Note;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by XhinLiang on 2017/5/6.
 *
 * @author XhinLiang
 */

public class NoteFactory {
    private static final NoteFactory ourInstance = new NoteFactory();

    private List<Note> notes;

    public static NoteFactory getInstance() {
        return ourInstance;
    }

    private NoteFactory() {
        notes = new LinkedList<>();
    }

    public void addTestNotes() {
        for (int i = 0; i < 30; ++i) {
            String content = "this is content.";
            for (int j = 0; j < 300; ++j) {
                content += " content " + i;
            }
            notes.add(new Note("title " + i, content));
        }
    }
    public  void  updatefactory(final NoteAdapter adapter){
        AVQuery<AVObject> avQuery = new AVQuery<>("Todo");
        avQuery.orderByDescending("createdAt");
        avQuery.include("owner");
        avQuery.whereEqualTo("owner", AVUser.getCurrentUser());
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    notes.removeAll(notes);
                    for (int i=0;i<list.size();i++){
                        AVObject notefactory1= list.get(i);
                        notes.add(new Note(notefactory1.getString("title"),notefactory1.getString("content"),notefactory1.getObjectId()));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
    public  void  deletefactory(Note note1){
        if(note1.getObjectID()==null){
        }
        else {
            AVObject todo = AVObject.createWithoutData("Todo", note1.getObjectID());
            todo.deleteInBackground();
        }
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public List<Note> getNotes() {
        return notes;
    }
}

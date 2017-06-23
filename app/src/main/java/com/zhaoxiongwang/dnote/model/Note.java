package com.zhaoxiongwang.dnote.model;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.zhaoxiongwang.dnote.global.NoteFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class Note
 *
 * @author XhinLiang
 */
public class Note {

    private AVObject avObject;
    private String title;
    private String content;
    private Calendar calendar;
    private String ObjectID;
    private static final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        calendar = Calendar.getInstance();
    }

    public Note(String title, String content,String objectID) {
        this.title = title;
        this.content = content;
        this.ObjectID= objectID;
        calendar = Calendar.getInstance();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCreatedAt() {
        return dateFormat.format(calendar.getTime());
    }
    public String getObjectID(){return ObjectID;}
    public void setObjectID(String objectID){this.ObjectID=objectID;}
}

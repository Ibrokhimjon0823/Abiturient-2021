package com.jaloliddinabdullaev.abiturient.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaloliddinabdullaev.abiturient.Model.Category;
import com.jaloliddinabdullaev.abiturient.Model.Question;
import com.jaloliddinabdullaev.abiturient.QuestionActivity;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteAssetHelper {
    private static final String DB_NAME="EDMTQuiz2019.db";
    private static final int DB_VER=1;

    private static DBHelper instance;
    public static synchronized DBHelper getInstance(Context context){
        if (instance==null)
            instance=new DBHelper(context);
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null,  DB_VER);

    }

     /*
      get all category from db
     */
    public List<Category> getAllCategories(){
        SQLiteDatabase db =instance.getWritableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM Category;", null);
        List<Category> categories=new ArrayList<>();
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                Category category=new Category(cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("Name")),
                        cursor.getString(cursor.getColumnIndex("Image")));
                categories.add(category);
                cursor.moveToNext();

            }
        }
        cursor.close();
        db.close();
        return categories;
    }
    /*
    Get 30 question from db by category
     */
    public List<Question> getQuestionByCategory(int category){
        SQLiteDatabase db =instance.getWritableDatabase();
        Cursor cursor =db.rawQuery(String.format("SELECT * FROM Question WHERE CategoryID = %d ORDER BY RANDOM() LIMIT 30", category), null);
        List<Question> questions=new ArrayList<>();
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
               Question question=new Question(cursor.getInt(cursor.getColumnIndex("ID")),
                       cursor.getString(cursor.getColumnIndex("QuestionText")),
                       cursor.getString(cursor.getColumnIndex("QuestionImage")),
                       cursor.getString(cursor.getColumnIndex("AnswerA")),
                       cursor.getString(cursor.getColumnIndex("AnswerB")),
                       cursor.getString(cursor.getColumnIndex("AnswerC")),
                       cursor.getString(cursor.getColumnIndex("AnswerD")),
                       cursor.getString(cursor.getColumnIndex("CorrectAnswer")),
                       cursor.getInt(cursor.getColumnIndex("IsImageQuestion"))==0?Boolean.FALSE:Boolean.TRUE,
                       cursor.getInt(cursor.getColumnIndex("CategoryID")));
                questions.add(question);
                cursor.moveToNext();

            }
        }
        cursor.close();
        db.close();
        return questions;
    }


}

package com.jaloliddinabdullaev.abiturient.Common;

import android.content.Intent;
import android.os.CountDownTimer;

import com.jaloliddinabdullaev.abiturient.Model.Category;
import com.jaloliddinabdullaev.abiturient.Model.CurrentQuestion;
import com.jaloliddinabdullaev.abiturient.Model.Question;
import com.jaloliddinabdullaev.abiturient.QuestionFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Common {
    public static final int TOTLA_TIME = 10 * 1000;
    public static final String KEY_GO_TO_QUESTION = "GO_TO_QUESTION";
    public static final String KEY_BACK_FROM_RESULT = "BACK_FROM_RESULT";
    public static List<Question> questionList=new ArrayList<>();
    public static List<CurrentQuestion> answerSheetList=new ArrayList<>();
    public static List<CurrentQuestion> answerSheetListFiltered=new ArrayList<>();

    public static Category selectedCategory=new Category();
    public static CountDownTimer countDownTimer;
    public static ArrayList<QuestionFragment> fragmentList=new ArrayList<>();
    public static TreeSet<String> selected_value=new TreeSet<>();
    public static int right_answer_count=0;
    public static int wrong_answer_count=0;
    public static int timer=0;
    public static int not_answer_count=0;
    public static StringBuilder data_question=new StringBuilder();

    public enum ANSWER_TYPE{
        NO_ANSWER,
        WRONG_ANSWER,
        RIGHT_ANSWER
    }
}

package com.jaloliddinabdullaev.abiturient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.jaloliddinabdullaev.abiturient.Adapter.AnswerSheetAdapter;
import com.jaloliddinabdullaev.abiturient.Adapter.AnswerSheetHelperAdapter;
import com.jaloliddinabdullaev.abiturient.Adapter.QuestionFragmentAdapter;
import com.jaloliddinabdullaev.abiturient.Common.Common;
import com.jaloliddinabdullaev.abiturient.DBHelper.DBHelper;
import com.jaloliddinabdullaev.abiturient.Model.CurrentQuestion;
import com.jaloliddinabdullaev.abiturient.Model.Question;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int CODE_GET_RESULT = 9999;
    int time_lay= Common.TOTLA_TIME;
    boolean isAnswerModeView=false;
    RecyclerView answer_sheet_view;
    AnswerSheetAdapter answerSheetAdapter;
    AnswerSheetHelperAdapter answerSheetHelperAdapter;
    TextView txt_right_answer, txt_wrong_answer, txt_timer;

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onDestroy() {
        if (Common.countDownTimer!=null){
            Common.countDownTimer.cancel();
        }
        super.onDestroy();
    }

    private AppBarConfiguration mAppBarConfiguration;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.selectedCategory.getName());
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        takeQuestion();

        if (Common.questionList.size()>0) {

            txt_right_answer=findViewById(R.id.txt_question_right);
            txt_timer=findViewById(R.id.txt_timer);

            txt_right_answer.setVisibility(View.VISIBLE);
            txt_timer.setVisibility(View.VISIBLE);

            txt_right_answer.setText(new StringBuilder(String.format("%d/%d", Common.right_answer_count, Common.questionList.size())));

            countTimer();




            answer_sheet_view = findViewById(R.id.grid_answer);
            answer_sheet_view.setHasFixedSize(true);
            if (Common.questionList.size() > 5) {
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.questionList.size() / 2));
            }
            answerSheetAdapter = new AnswerSheetAdapter(this, Common.answerSheetList);
            answerSheetHelperAdapter=new AnswerSheetHelperAdapter(this, Common.answerSheetList);
            answer_sheet_view.setAdapter(answerSheetAdapter);




            tabLayout=findViewById(R.id.sliding_tabs);
            viewPager=findViewById(R.id.viewPager);

            genFragmentList();

            QuestionFragmentAdapter questionFragmentAdapter=new QuestionFragmentAdapter(getSupportFragmentManager(),
                    this, Common.fragmentList);
            viewPager.setAdapter(questionFragmentAdapter);
            tabLayout.setupWithViewPager(viewPager);
            //Event
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                int SCROLLING_RIGHT=0;
                int SCROLLING_LEFT=1;
                int SCROLLING_UNDETERMINED=2;

                int currentScrolledDirection=2;
                private void setScrollingDirection(float positionOffset){
                    if ((1-positionOffset)>=0.5){
                        this.currentScrolledDirection=SCROLLING_RIGHT;
                    }else if ((1-positionOffset)<=0.5){
                        this.currentScrolledDirection=SCROLLING_LEFT;
                    }
                }

                private boolean isScrollDirectionUndermined(){
                    return currentScrolledDirection==SCROLLING_UNDETERMINED;
                }

                private boolean isScrollingRight(){
                    return currentScrolledDirection==SCROLLING_RIGHT;
                }
                private boolean isScrollingLeft(){
                    return currentScrolledDirection==SCROLLING_LEFT;
                }


                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (isScrollDirectionUndermined()){
                        setScrollingDirection(positionOffset);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                    QuestionFragment questionFragment;
                    int position1=0;
                    if (position>0){
                        if (isScrollingRight()){
                            //if user scroll to right , getPrevious fragment to calculate result
                            questionFragment=Common.fragmentList.get(position-1);
                            position1=position-1;
                        }else if (isScrollingLeft()){
                            //if user scroll to left , getNext fragment to calculate result
                            questionFragment=Common.fragmentList.get(position+1);
                            position1=position+1;
                        }else {
                            questionFragment=Common.fragmentList.get(position1);
                        }
                    }else {
                        questionFragment=Common.fragmentList.get(0);
                        position1=0;
                    }
                    CurrentQuestion currentQuestion=questionFragment.getSelectedAnswer();
                    Common.answerSheetList.set(position1, currentQuestion);
                    answerSheetAdapter.notifyDataSetChanged();
                    answerSheetHelperAdapter.notifyDataSetChanged();

                    countCorrectAnswer();
                    txt_right_answer.setText(new StringBuilder(String.format("%d", Common.right_answer_count))
                    .append("/")
                    .append(String.format("%d", Common.questionList.size())).toString());
                    txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

                    if (currentQuestion.getType()== Common.ANSWER_TYPE.NO_ANSWER){
                        questionFragment.showCorrectAnswer();
                        questionFragment.disableAnswer();
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state==ViewPager.SCROLL_STATE_IDLE){
                        this.currentScrolledDirection=SCROLLING_UNDETERMINED;
                    }
                }
            });
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void finishGame() {
        int position=viewPager.getCurrentItem();
        QuestionFragment questionFragment=Common.fragmentList.get(position);
        CurrentQuestion currentQuestion=questionFragment.getSelectedAnswer();
        Common.answerSheetList.set(position, currentQuestion);
        answerSheetAdapter.notifyDataSetChanged();

        countCorrectAnswer();
        txt_right_answer.setText(new StringBuilder(String.format("%d", Common.right_answer_count))
                .append("/")
                .append(String.format("%d", Common.questionList.size())).toString());
        txt_wrong_answer.setText(String.valueOf(Common.wrong_answer_count));

        if (currentQuestion.getType()== Common.ANSWER_TYPE.NO_ANSWER){
            questionFragment.showCorrectAnswer();
            questionFragment.disableAnswer();
        }

        //We will navigate to new result activity here

        Intent intent=new Intent(QuestionActivity.this, ResultActivity.class);
        Common.timer=Common.TOTLA_TIME-time_lay;
        Common.not_answer_count=Common.questionList.size()-(Common.right_answer_count+Common.wrong_answer_count);
        Common.data_question=new StringBuilder(new Gson().toJson(Common.answerSheetList));
        startActivityForResult(intent , CODE_GET_RESULT);


    }

    private void countCorrectAnswer() {
        Common.right_answer_count=Common.wrong_answer_count=0;
        for (CurrentQuestion item:Common.answerSheetList){
            if (item.getType()== Common.ANSWER_TYPE.RIGHT_ANSWER){
                Common.right_answer_count++;
                Log.i("Right answer count ", String.valueOf(Common.right_answer_count));
            }else if (item.getType()== Common.ANSWER_TYPE.WRONG_ANSWER){
                Common.wrong_answer_count++;
                Log.i("Wrong answer count ", String.valueOf(Common.wrong_answer_count));
            }
        }
    }

    private void genFragmentList() {
        for (int i=0; i<Common.questionList.size(); i++){
            Bundle bundle=new Bundle();
            bundle.putInt("index", i);
            QuestionFragment fragment=new QuestionFragment();
            fragment.setArguments(bundle);

            Common.fragmentList.add(fragment);
        }
    }


    private void countTimer() {

        if (Common.countDownTimer==null){
            Common.countDownTimer=new CountDownTimer(Common.TOTLA_TIME, 1000) {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTick(long l) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(l),
                            TimeUnit.MILLISECONDS.toSeconds(l)
                                    -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
                    time_lay-=1000;
                }

                @Override
                public void onFinish() {
                    finishGame();
                }
            }.start();
        }else {
            Common.countDownTimer.cancel();
            Common.countDownTimer=new CountDownTimer(Common.TOTLA_TIME, 1000) {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTick(long l) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(l),
                            TimeUnit.MILLISECONDS.toSeconds(l)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
                    time_lay-=1000;
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }

    }


    private void takeQuestion() {
        Common.questionList= DBHelper.getInstance(this).getQuestionByCategory(Common.selectedCategory.getId());
        if (Common.questionList.size()==0){
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Oops!..")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setDescription("We don't have any question in this "+Common.selectedCategory.getName()+" category")
                    .setPositiveText("OK")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();
        }else {

            if (Common.answerSheetList.size()>0){
                Common.answerSheetList.clear();
                Common.fragmentList.clear();

            }

            //Generate answer sheet item from question
            //30 question
            for (int i=0; i<Common.questionList.size(); i++){
                Common.answerSheetList.add(new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER));
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem=menu.findItem(R.id.menu_wrong_answer);
        RelativeLayout constraintLayout=(RelativeLayout) menuItem.getActionView();
        txt_wrong_answer=constraintLayout.findViewById(R.id.txt_wrong_answer);
        txt_wrong_answer.setText(String.valueOf(0));


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.menu_finish_game){
            if (!isAnswerModeView){
                new MaterialStyledDialog.Builder(QuestionActivity.this)
                        .setTitle("Finish?")
                        .setIcon(android.R.drawable.ic_lock_power_off)
                        .setDescription("Do you really want to finish?")
                        .setNegativeText("No")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Yes")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                finishGame();
                            }
                        }).show();
            }else
                finishGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CODE_GET_RESULT){
            if (resultCode== Activity.RESULT_OK){
                String action =data.getStringExtra("action");
                if (action==null|| TextUtils.isEmpty(action)){
                    int questionNum=data.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1);
                    viewPager.setCurrentItem(questionNum);

                    isAnswerModeView=true;
                    Common.countDownTimer.cancel();
                    txt_wrong_answer.setVisibility(View.GONE);
                    txt_right_answer.setVisibility(View.GONE);
                    txt_timer.setVisibility(View.GONE);

                }else {

                    if (action.equals("viewquizanswer")){
                        viewPager.setCurrentItem(0);
                        isAnswerModeView=true;
                        Common.countDownTimer.cancel();
                        txt_wrong_answer.setVisibility(View.GONE);
                        txt_right_answer.setVisibility(View.GONE);
                        txt_timer.setVisibility(View.GONE);

                        for (int i=0; i<Common.fragmentList.size(); i++)
                        {
                            Common.fragmentList.get(i).showCorrectAnswer();
                            Common.fragmentList.get(i).disableAnswer();

                        }
                    }else if (action.equals("doitagain")) {
                        viewPager.setCurrentItem(0);
                        isAnswerModeView=false;
                        countTimer();
                        txt_wrong_answer.setVisibility(View.VISIBLE);
                        txt_right_answer.setVisibility(View.VISIBLE);
                        txt_timer.setVisibility(View.VISIBLE);

                        for (CurrentQuestion item:Common.answerSheetList){
                            item.setType(Common.ANSWER_TYPE.NO_ANSWER);//reset all answers
                        }
                        answerSheetAdapter.notifyDataSetChanged();

                        answerSheetHelperAdapter.notifyDataSetChanged();
                        for (int i=0; i<Common.fragmentList.size(); i++){
                            Common.fragmentList.get(i).resetQuestion();
                        }

                    }

                }
            }
        }
    }
}
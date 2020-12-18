package com.jaloliddinabdullaev.abiturient;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaloliddinabdullaev.abiturient.Common.Common;
import com.jaloliddinabdullaev.abiturient.Interface.IQuestion;
import com.jaloliddinabdullaev.abiturient.Model.CurrentQuestion;
import com.jaloliddinabdullaev.abiturient.Model.Question;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class QuestionFragment extends Fragment implements IQuestion {

    TextView text_question_text;
    CheckBox ckbA, ckbB, ckbC, ckbD;

    FrameLayout layout_image;
    ProgressBar progressBar;
    Question question;
    int questionIndex=-1;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance(String param1, String param2) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView =  inflater.inflate(R.layout.fragment_question, container, false);
        //Get Question
        questionIndex=getArguments().getInt("index", -1);
        question= Common.questionList.get(questionIndex);
        if (question!=null) {
            progressBar=itemView.findViewById(R.id.process_bar);
            layout_image = itemView.findViewById(R.id.layout_image);

            if (question.isImageQuestion()){
                ImageView img_question=itemView.findViewById(R.id.img_question);
                Picasso.get().load(question.getQuestionImage()).into(img_question, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }else
                layout_image.setVisibility(View.GONE);
            //View
            text_question_text = itemView.findViewById(R.id.txt_question_text);
            text_question_text.setText(question.getQuestionText());
            ckbA = itemView.findViewById(R.id.ckba);
            ckbA.setText(question.getAnswerA());
            ckbA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        Common.selected_value.add(ckbA.getText().toString());
                    else
                        Common.selected_value.remove(ckbA.getText().toString());
                }
            });
            ckbB = itemView.findViewById(R.id.ckbb);
            ckbB.setText(question.getAnswerB());
            ckbB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        Common.selected_value.add(ckbB.getText().toString());
                    else
                        Common.selected_value.remove(ckbB.getText().toString());
                }
            });
            ckbC = itemView.findViewById(R.id.ckbc);
            ckbC.setText(question.getAnswerC());
            ckbC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        Common.selected_value.add(ckbC.getText().toString());
                    else
                        Common.selected_value.remove(ckbC.getText().toString());
                }
            });
            ckbD = itemView.findViewById(R.id.ckbd);
            ckbD.setText(question.getAnswerD());
            ckbD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        Common.selected_value.add(ckbD.getText().toString());
                    else
                        Common.selected_value.remove(ckbD.getText().toString());
                }
            });


        }
        return itemView;
    }

    @Override
    public CurrentQuestion getSelectedAnswer() {
        CurrentQuestion currentQuestion=new CurrentQuestion(questionIndex, Common.ANSWER_TYPE.NO_ANSWER);
        StringBuilder result=new StringBuilder();
        if (Common.selected_value.size()>1){
            //multichoice
            Object[] arrayAnswer=Common.selected_value.toArray();
            for (int i=0; i<arrayAnswer.length; i++){
                if (i<arrayAnswer.length-1){
                    result.append(new StringBuilder(((String)arrayAnswer[i]).substring(0, 1)).append(","));
                }else {
                    result.append(new StringBuilder((String)arrayAnswer[i]).substring(0, 1));
                }
            }
        }else if (Common.selected_value.size()==1){
            Object[] arrayAnswer=Common.selected_value.toArray();
            result.append((String)arrayAnswer[0]).substring(0,1);
        }
        if (question!=null){
            if (!TextUtils.isEmpty(result)){
                if (result.toString().equals(question.getCorrectAnswer())){
                    currentQuestion.setType(Common.ANSWER_TYPE.RIGHT_ANSWER);
                }else {
                    currentQuestion.setType(Common.ANSWER_TYPE.WRONG_ANSWER);
                }
            }else {
                currentQuestion.setType(Common.ANSWER_TYPE.NO_ANSWER);
            }
        }else {
            Toast.makeText(getContext(), "Cannot get question", Toast.LENGTH_LONG).show();
            currentQuestion.setType(Common.ANSWER_TYPE.NO_ANSWER);
        }
        Common.selected_value.clear(); //Always clear selected_value when compare done
        return currentQuestion;
    }

    @Override
    public void showCorrectAnswer() {
        //Bold Correct answer
        String[] correctAnswer=question.getCorrectAnswer().split(",");
        for (String answer:correctAnswer){
            if (answer.equals("A")){
                ckbA.setTypeface(null, Typeface.BOLD);
                ckbA.setTextColor(Color.RED);
            }else if (answer.equals("B")){
                ckbB.setTypeface(null, Typeface.BOLD);
                ckbB.setTextColor(Color.RED);
            }else if (answer.equals("C")){
                ckbC.setTypeface(null, Typeface.BOLD);
                ckbC.setTextColor(Color.RED);
            }else if (answer.equals("D")){
                ckbD.setTypeface(null, Typeface.BOLD);
                ckbD.setTextColor(Color.RED);
            }
        }
    }
    @Override
    public void disableAnswer() {
        ckbA.setEnabled(false);
        ckbB.setEnabled(false);
        ckbC.setEnabled(false);
        ckbD.setEnabled(false);
    }

    @Override
    public void resetQuestion() {
        ckbA.setEnabled(true);
        ckbB.setEnabled(true);
        ckbC.setEnabled(true);
        ckbD.setEnabled(true);

        ckbA.setChecked(false);
        ckbB.setChecked(false);
        ckbC.setChecked(false);
        ckbD.setChecked(false);

        ckbA.setTypeface(null, Typeface.NORMAL);
        ckbA.setTextColor(Color.BLACK);
        ckbB.setTypeface(null, Typeface.NORMAL);
        ckbB.setTextColor(Color.BLACK);
        ckbC.setTypeface(null, Typeface.NORMAL);
        ckbC.setTextColor(Color.BLACK);
        ckbD.setTypeface(null, Typeface.NORMAL);
        ckbD.setTextColor(Color.BLACK);
    }
}
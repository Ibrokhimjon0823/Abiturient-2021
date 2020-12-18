package com.jaloliddinabdullaev.abiturient.Interface;

import com.jaloliddinabdullaev.abiturient.Model.CurrentQuestion;

public interface IQuestion {
    CurrentQuestion getSelectedAnswer();//get selected answer from user select
    void showCorrectAnswer();//Bold correct answer text
    void disableAnswer();
    void resetQuestion();

}

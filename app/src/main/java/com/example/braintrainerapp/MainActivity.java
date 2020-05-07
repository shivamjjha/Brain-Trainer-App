package com.example.braintrainerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // 'a' and 'b' are two random numbers generated for each question
    // 'counter' keeps track of the manipulation to be done (addition, subtraction, etc..)
    private static int a, b, counter = 0, numberOfCorrectAnswers = 0, numberOfQuestions = 1;
    private static Random rand;
    private double answer;
    private String question;

    private Button goButton;
    private static androidx.gridlayout.widget.GridLayout answerGridLayout, questionGridLayout;
    private TextView timeTextView, givenQuestionTextView, scoreTextView, choiceTextView, whetherCorrectTextView;

    private static String getQuestion() {
        a = rand.nextInt(20) + 1;
        b = rand.nextInt(20) + 1;

        int temp = rand.nextInt(4) + 1;
        switch (temp) {
            case 1: // addition
                    counter = 1;
                    return a + "+" + b;
                    // return eval(a + "+" + b);

            case 2: // subtraction
                    counter = 2;
                    return a + "-" + b;
                    // return eval(a + "-" + b);

            case 3: // multiplication
                    counter = 3;
                    return a + "*" + b;
                    // return eval(a + "*" + b);

            case 4: // division
                    counter = 4;
                    return a + "/" + b;
                    // return eval(a + "/" + b);
        }
        return null;
    }

    private void setChoices() {
        int answerChildNumber = rand.nextInt(4);
        scoreTextView = (TextView) answerGridLayout.getChildAt(answerChildNumber);
        scoreTextView.setText(String.valueOf(answer));

        for(int childNumber = 0; childNumber < answerGridLayout.getChildCount(); ++ childNumber) {
            scoreTextView = (TextView) answerGridLayout.getChildAt(childNumber);
            if(childNumber != answerChildNumber){
                switch (counter) {
                    case 1: // addition
                            scoreTextView.setText(String.valueOf((childNumber + 1) + answer));

                    case 2: // subtraction
                            scoreTextView.setText(String.valueOf(-(childNumber + 1) + answer));

                    case 3: // multiplication
                            scoreTextView.setText(String.valueOf((childNumber + 1) * answer));

                    case 4: // division
                            scoreTextView.setText(String.valueOf((answer) / (childNumber + 2)));
                }
            }
        }
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public void go(View view) {
        answerGridLayout = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.answerGridLayout);

        for(int childNumber = 0; childNumber < answerGridLayout.getChildCount(); ++ childNumber) {
            scoreTextView = (TextView) answerGridLayout.getChildAt(childNumber);
            scoreTextView.setClickable(true);
        }

        final Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        goButton = (Button) findViewById(R.id.goButton);

        timeTextView = (TextView) findViewById(R.id.timeTextView);
        givenQuestionTextView = (TextView) findViewById(R.id.givenQuestionTextView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        whetherCorrectTextView = (TextView) findViewById(R.id.whetherCorrectTextView);

        answerGridLayout = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.answerGridLayout);
        questionGridLayout = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.questionGridLayout);

        rand = new Random();
        goButton.setVisibility(View.INVISIBLE);
        playAgainButton.setVisibility(View.INVISIBLE);
        whetherCorrectTextView.setVisibility(View.INVISIBLE);
        answerGridLayout.setVisibility(View.VISIBLE);
        questionGridLayout.setVisibility(View.VISIBLE);

        scoreTextView.setText("0/1");

        new CountDownTimer(40000, 1000){
            @Override
            public void onTick(long millisLeftUntilFinished){
                timeTextView.setText(String.format("%s s", String.valueOf(millisLeftUntilFinished / 1000)));
            }

            @Override
            public void onFinish(){
                playAgainButton.setVisibility(View.VISIBLE);

                for(int childNumber = 0; childNumber < answerGridLayout.getChildCount(); ++ childNumber) {
                    scoreTextView = (TextView) answerGridLayout.getChildAt(childNumber);
                    scoreTextView.setClickable(false);
                }

                scoreTextView = (TextView) findViewById(R.id.scoreTextView);
            }
        }.start();


        question = getQuestion();
        answer = eval(question);
        givenQuestionTextView.setText(question);

        setChoices();
    }

    public void userResponse(View view) {
        whetherCorrectTextView.setVisibility(View.VISIBLE);
        if(Double.parseDouble(((TextView) view).getText().toString()) == answer){
            whetherCorrectTextView.setText(R.string.correct_answer);
            ++numberOfCorrectAnswers;
        } else {
            whetherCorrectTextView.setText(R.string.incorrect_answer);
        }

        String temp =  (numberOfCorrectAnswers) + "/" + (numberOfQuestions);
        scoreTextView.setText(temp);
        ++numberOfQuestions;

        question = getQuestion();
        answer = eval(question);
        givenQuestionTextView.setText(question);
        setChoices();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

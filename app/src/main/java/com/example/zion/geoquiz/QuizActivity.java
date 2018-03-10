package com.example.zion.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

  private static final String TAG = "QuizActivity";
  private static final String KEY_INDEX = "index";
  private static final String KEY_INDEX_2 = "index";
  private static final String BOOLEAN_INDEX = "index";
  private static final int REQUEST_CODE_CHEAT = 0;

  private Button mTrueButton;
  private Button mFalseButton;
  private Button mCheatButton;
  private ImageButton mNextButton;
  private ImageButton mBackButton;
  private TextView mQuestionTextView;
  private TextView mUsingCheat;

  private Question[] mQuestionBank = new Question[]{
      new Question(R.string.question_australia, true),
      new Question(R.string.question_oceans, true),
      new Question(R.string.question_mideast, false),
      new Question(R.string.question_africa, false),
      new Question(R.string.question_americas, true),
      new Question(R.string.question_asia, true),
  };

  private boolean mIsCheater;
  private int useCheat = 3;
  private int mCurrentIndex = 0;
  private int trueAnswer = 0;
  private int answerCount = 0;
  private int nextCount = 0;
  private boolean checkAnswerOfQuestion = true;
  private boolean accessQuestions = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate(Bundle) called");
    setContentView(R.layout.activity_quiz);

    if (savedInstanceState != null) {
      mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
      mIsCheater = savedInstanceState.getBoolean(BOOLEAN_INDEX, false);
      useCheat = savedInstanceState.getInt(KEY_INDEX_2, 3);
    }

    mUsingCheat = findViewById(R.id.usingCheat);
    mUsingCheat.setText("Cards left: " + useCheat);

    mQuestionTextView = findViewById(R.id.question_text_view);
    /*mQuestionTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
      }
    });*/

    mTrueButton = findViewById(R.id.true_button);
    mTrueButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (checkAnswerOfQuestion) {
          checkAnswer(true);
        }
        checkAnswerOfQuestion = false;
        accessQuestions = true;
      }
    });

    mFalseButton = findViewById(R.id.false_button);
    mFalseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (checkAnswerOfQuestion) {
          checkAnswer(false);
        }
        checkAnswerOfQuestion = false;
        accessQuestions = true;
      }
    });

    mNextButton = findViewById(R.id.next_button);
    mNextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (accessQuestions) {
          mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
          mIsCheater = false;
          updateQuestion();
          checkAnswerOfQuestion = true;
          accessQuestions = false;
          nextCount++;
        }
      }
    });

    mBackButton = findViewById(R.id.back_button);
    mBackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (accessQuestions && nextCount == answerCount) {
          //int sizeQ = mQuestionBank.length - 1;
          //if (mCurrentIndex == 0) {
          //mCurrentIndex = sizeQ;
          //updateQuestion();
          //} else {
          if (mCurrentIndex != 0) {
            mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
            updateQuestion();
            checkAnswerOfQuestion = true;
            accessQuestions = false;
          }
          //}

        }

      }
    });

    mCheatButton = findViewById(R.id.cheat_button);
    mCheatButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Start Cheat Activity
        if (useCheat > 0 && useCheat <= 3) {
          int useCheatForCheatActivity = useCheat;
          boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
          Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, useCheatForCheatActivity);
          startActivityForResult(intent, REQUEST_CODE_CHEAT);
        }
      }
    });

    updateQuestion();

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    if (requestCode == REQUEST_CODE_CHEAT) {
      if (data == null) {
        return;
      }
      mIsCheater = CheatActivity.wasAnswerShown(data);
      useCheat--;
      mUsingCheat = findViewById(R.id.usingCheat);
      mUsingCheat.setText("Cards left: " + useCheat);

    }
  }

  private void updateQuestion() {
    //Log.d(TAG, "Updating question text", new Exception());
    int question = mQuestionBank[mCurrentIndex].getTextResId();
    mQuestionTextView.setText(question);
  }

  private void checkAnswer(boolean userPressedTrue) {
    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

    int messageResId = 0;

    if (mIsCheater) {
      messageResId = R.string.judgment_toast;
      answerCount++;
      if (useCheat > 0 && useCheat <= 3) {
        //useCheat--;
        //mUsingCheat = findViewById(R.id.usingCheat);
        //mUsingCheat.setText("Cards left: " + useCheat);
      }
      if (userPressedTrue == answerIsTrue) {
        trueAnswer++;
      }
    } else {
      if (userPressedTrue == answerIsTrue) {
        messageResId = R.string.correct_toast;
        trueAnswer++;
        answerCount++;
      } else {
        messageResId = R.string.incorrect_toast;
        answerCount++;
      }
    }

    if (answerCount == mQuestionBank.length) {

      int truePercent = (trueAnswer * 100) / mQuestionBank.length;

      //int messageFinal = R.string.corr_ans;

      Toast t = Toast.makeText(this,
          "Correctly answered: " + trueAnswer + "/" + mQuestionBank.length + "(" + truePercent
              + " %)",
          Toast.LENGTH_SHORT);
      t.setGravity(Gravity.TOP, 0, 0);
      t.show();

      trueAnswer = 0;
      answerCount = 0;
    }

    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "onStart called");
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume called");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause called");
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    savedInstanceState.putBoolean(BOOLEAN_INDEX, mIsCheater);
    savedInstanceState.putInt(KEY_INDEX_2, useCheat);
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "opStop called");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy called");
  }

}

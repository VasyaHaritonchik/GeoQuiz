package com.example.zion.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

  private static final String EXTRA_ANSWER_IS_TRUE = "com.example.zion.geoquiz.answer_is";
  private static final String EXTRA_ANSWER_IS_TRUE_2 = "help";
  private static final String EXTRA_ANSWER_SHOWN = "com.example.zion.geoquiz.answer_shown";
  private static final String KEY_INDEX_FOR_CHEATING = "index";

  private boolean mAnswerIsTrue;

  private TextView mAnswerTextView;
  private TextView mViewApi;
  private TextView mCheckCheating;
  private Button mShowAnswerButton;

  //private int checkDisplay;
  private int checkCheat;

  public static Intent newIntent(Context packageContext, boolean answerIsTrue, int useCheatForCheatActivity) {
    Intent intent = new Intent(packageContext, CheatActivity.class);
    intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
    intent.putExtra(EXTRA_ANSWER_IS_TRUE_2, useCheatForCheatActivity);
    return intent;
  }

  public static boolean wasAnswerShown(Intent result) {
    return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cheat);

    int apiLevel;
    apiLevel = VERSION.SDK_INT;

    mViewApi = findViewById(R.id.view_api);
    mViewApi.setText("API level " + apiLevel);

    //For land layout!!!!!!!!!!!!!!!!!!!
    if (savedInstanceState != null) {
      mAnswerIsTrue = savedInstanceState.getBoolean(KEY_INDEX_FOR_CHEATING, false);
      mAnswerTextView = findViewById(R.id.answer_text_view);
      if (mAnswerIsTrue) {
        mAnswerTextView.setText(R.string.true_button);
      } else {
        mAnswerTextView.setText(R.string.false_button);
      }
      setAnswerShownResult(mAnswerIsTrue);
    }

    //Continue code
    mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
    checkCheat = getIntent().getIntExtra(EXTRA_ANSWER_IS_TRUE_2, 3);

    //int i = checkCheat;

    mCheckCheating = findViewById(R.id.countCheating);
    mCheckCheating.setText("Cards left: " + checkCheat);

    mAnswerTextView = findViewById(R.id.answer_text_view);
    mShowAnswerButton = findViewById(R.id.show_answer_button);
    mShowAnswerButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mAnswerIsTrue) {
          mAnswerTextView.setText(R.string.true_button);
          checkCheat--;
          mCheckCheating.setText("Cards left: " + checkCheat);
        } else {
          mAnswerTextView.setText(R.string.false_button);
          checkCheat--;
          mCheckCheating.setText("Cards left: " + checkCheat);
        }
        setAnswerShownResult(true);

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
          int cx = mShowAnswerButton.getWidth() / 2;
          int cy = mShowAnswerButton.getHeight() / 2;
          float radius = mShowAnswerButton.getWidth();
          Animator anim = ViewAnimationUtils
              .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
          anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              mShowAnswerButton.setVisibility(View.INVISIBLE);
            }
          });
          anim.start();
        } else {
          mShowAnswerButton.setVisibility(View.INVISIBLE);
        }
      }
    });
  }

  private void setAnswerShownResult(boolean isAnswerShown) {
    Intent data = new Intent();
    data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
    setResult(RESULT_OK, data);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putBoolean(KEY_INDEX_FOR_CHEATING, mAnswerIsTrue);
  }
}

package com.example.techflex_e_literacy.quiz;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

public class TrialActivity2 extends AppCompatActivity {
    private static final long  START_TIME_IN_MILLIS = 2700000;
    private static final String DEFAULT = "N/A";
    private static final String TESTING_MESSAGE ="Test";
    private TextView mScoreView,reset, pause;
    private TextView mQuestionView, count_down, total_question, course_code, searchCourseView;
    private Button prev,next,submit;
    ProgressBar searchCourseProBar;
    Toolbar toolbar;
    EditText answer;
    private int mScore = 0;
    int total = 0;
    int points = 0;
    long total_question_number = 0;
    int currentQuestion = 0;
    int correct = 0;
    int wrong = 0;
    String query;
    String q;
    HashMap<Integer, Integer> answered = new HashMap<>();
    DatabaseReference databaseReference;

    private CountDownTimer mCountDownTimer;
    private boolean mTimeRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    String timeLeftFormatted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fbq_activity);


        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScoreView = findViewById(R.id.score);
        mQuestionView = findViewById(R.id.question);
        count_down = findViewById(R.id.textview_count_down);
        searchCourseProBar= findViewById(R.id.courseSearch);
        searchCourseView = findViewById(R.id.loadingCourse);
        total_question = findViewById(R.id.question_count);
        course_code = findViewById(R.id.course_code);
        answer = findViewById(R.id.answer);
        reset = findViewById(R.id.button_reset);
        pause = findViewById(R.id.pause);
        //prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp3();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeRunning){
                    pauseTimer();
                }else{
                    startTimer();
                    next.setEnabled(true);
                    submit.setEnabled(true);
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        handleIntent(getIntent());

    }
    public void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                upDateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimeRunning = false;
                pause.setText("Retake Quiz");
                pause.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                count_down.setText("Time Over");
                count_down.setTextColor(Color.WHITE);
                count_down.setText("Done!");
                count_down.setTextColor(Color.WHITE);
                Log.i("yo",answered.toString());
                Log.v("yo",answer.getText().toString());
                Intent intent = new Intent(TrialActivity2.this, Result_Activity2.class);
                intent.putExtra("Total", String.valueOf(total));
                intent.putExtra("Correct", String.valueOf(correct));
                intent.putExtra("Incorrect", String.valueOf(wrong));
                intent.putExtra("points", String.valueOf(points));
                intent.putExtra("total_question", String.valueOf(total_question_number));
                intent.putExtra("query",q);
                intent.putExtra("count_down", String.valueOf(count_down));
                intent.putExtra("score", String.valueOf(mScore));
                intent.putExtra("answered",answered.toString());
                intent.putExtra("answer",String.valueOf(answer));
                startActivity(intent);

            }
        }.start();
        mTimeRunning = true;
        pause.setText("Pause Quiz");
        reset.setVisibility(View.INVISIBLE);
    }
    public void pauseTimer(){
        next.setEnabled(false);
        mCountDownTimer.cancel();
        mTimeRunning = false;
        pause.setText("Resume Quiz");
        reset.setVisibility(View.VISIBLE);

    }
    public void resetTimer(){
        next.setEnabled(false);
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        upDateCountDownText();
        reset.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);

    }
    public void upDateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        count_down.setText(timeLeftFormatted);
        if (mTimeLeftInMillis < 6000) {
            count_down.setTextColor(Color.RED);
        } else {
            count_down.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            SharedPreferences sharedPreferences = getSharedPreferences("Activation", Context.MODE_PRIVATE);
            String code = sharedPreferences.getString("code",DEFAULT);
            if (code.equals(DEFAULT)) {
                return;
            }else {
                Intent intent = new Intent(TrialActivity2.this,FBQAcitvity.class);
                startActivity(intent);
                Log.v("TAG","Save Code IS: "+code);
            }

        }
    }
    public void quitResult(){
        Intent i = new Intent(TrialActivity2.this, Result_Activity2.class);
        i.putExtra("Total", String.valueOf(total));
        i.putExtra("count_down", String.valueOf(count_down));
        i.putExtra("Correct", String.valueOf(correct));
        i.putExtra("Incorrect", String.valueOf(wrong));
        i.putExtra("points", String.valueOf(points));
        i.putExtra("total_question",String.valueOf(total_question_number));
        i.putExtra("query",q);
        i.putExtra("score", String.valueOf(mScore));
        i.putExtra("answered",answered.toString());
        i.putExtra("answer",answer.getText().toString());
        i.putExtra("answer",String.valueOf(answer));
        startActivity(i);
       // stopTimer();
        //prev.setEnabled(false);
        next.setEnabled(false);
        submit.setEnabled(false);
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            startQuiz();
            //updateQuestion(query);
        }
    }
    public  void updateQuestion(final String query1){
        q = query1;
        Log.i("testing",query1);
        //prev.setEnabled(false);
        submit.setEnabled(true);
        next.setEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/fbq/"+query1.trim().toLowerCase());
        searchCourseProBar.setVisibility(View.VISIBLE);
        searchCourseView.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchCourseProBar.setVisibility(View.GONE);
                searchCourseView.setVisibility(View.GONE);
                if (!dataSnapshot.exists()){
                    showPopUp();
                    return;
                }
                findViewById(R.id.quiz_layout).setVisibility(View.VISIBLE);
                total_question_number = (dataSnapshot.getChildrenCount());
                total_question.setText("Question: "+currentQuestion+"/"+ total_question_number+"");
                course_code.setText(query1.trim().toUpperCase());
                //startTimer(60, count_down);

                total++;
                if (total > total_question_number) {
                    total--;
                    Log.v("yo",answered.toString());
                    Log.v("yo",answer.getText().toString());
                    // open result activity
                    Intent i = new Intent(TrialActivity2.this, Result_Activity2.class);
                    i.putExtra("Total", String.valueOf(total));
                    i.putExtra("count_down", String.valueOf(count_down));
                    i.putExtra("Correct", String.valueOf(correct));
                    i.putExtra("Incorrect", String.valueOf(wrong));
                    i.putExtra("points", String.valueOf(points));
                    i.putExtra("total_question",String.valueOf(total_question_number));
                    i.putExtra("query",query1);
                    i.putExtra("score", String.valueOf(mScore));
                    i.putExtra("answered",answered.toString());
                    i.putExtra("answer",answer.getText().toString());
                    i.putExtra("answer",String.valueOf(answer));
                    startActivity(i);
                    //stopTimer();
                   // prev.setEnabled(false);
                    next.setEnabled(false);
                    submit.setEnabled(false);
                } else {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/fbq/"+query1.trim().toLowerCase()).child(String.valueOf(total));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                final QuestionLibraryFBQ fbq = dataSnapshot.getValue(QuestionLibraryFBQ.class);
                                mQuestionView.setText(fbq.getQuestion());
                                currentQuestion++;

                                if (currentQuestion > 5 && mCountDownTimer != null){
                                    showPopUp2();
                                    mCountDownTimer.cancel();
                                    //prev.setEnabled(false);
                                    next.setEnabled(false);
                                    submit.setEnabled(true);

                                }
                                next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (answer.getText().toString().toLowerCase().trim().equals(fbq.getAnswer())){
                                            answered.put(total,0);
                                            mScore = mScore+1;
                                            updateScore(mScore);
                                            Toast.makeText(TrialActivity2.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            next.setBackgroundColor(Color.GREEN);

                                            next.setBackgroundColor(Color.parseColor("#03A9f4"));
                                            answer.getText().clear();
                                            updateQuestion(query1);



                                            searchCourseProBar.setVisibility(View.GONE);
                                            searchCourseView.setVisibility(View.GONE);
                                        }else {
                                            Toast.makeText(TrialActivity2.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            answered.put(total,0);
                                            wrong = wrong + 1;
                                            points = points -5;
                                            next.setBackgroundColor(Color.RED);
                                            answer.getText().clear();
                                            updateQuestion(query1);

                                            searchCourseProBar.setVisibility(View.GONE);
                                            searchCourseView.setVisibility(View.GONE);
                                            //next.setBackgroundColor(Color.parseColor("#03A9f4"));
                                        }

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void showPopUp() {
        new AlertDialog.Builder(TrialActivity2.this)
                .setTitle("Attention")
                .setMessage("Check spellings\nCheck internet connection if first-time use\nContact admin")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setCancelable(false).show();


    }
    void startQuiz() {
        new AlertDialog.Builder(TrialActivity2.this)
                .setTitle("!Attention")
                .setMessage("Are You Ready To  Start Quiz?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateQuestion(query);
                        startTimer();

                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).show();

    }
    void showPopUp2() {
        new AlertDialog.Builder(TrialActivity2.this)
                //builder.setIcon(R.drawable.back_img);
                .setTitle("Attention!")
                .setMessage("Maximum Attended Reached for Demo Version\nKindly Subscribe for more questions")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrialActivity2.this,Bill.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(TrialActivity2.this,UserActivity.class);
                startActivity(intent);
            }
        }).setCancelable(false).show();
    }
    void showPopUp3() {
        new AlertDialog.Builder(TrialActivity2.this)
                .setMessage("are you sure you  want to  quit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quitResult();

                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setCancelable(false).show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
   /*public void reverseTimer(int seconds, final TextView tv) {
        mCountDownTimer = new CountDownTimer(seconds * 1000 + 1000, 1000) {
            @Override
            public void onTick(long millsUntilFinised) {
                int seconds = (int) (millsUntilFinised / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

                if (millsUntilFinised < 10000) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.WHITE);
                }

            }

            @Override
            public void onFinish() {
                tv.setText("Done!");
                tv.setTextColor(Color.WHITE);
                Log.i("yo",answered.toString());
                Log.v("yo",answer.getText().toString());
                Intent intent = new Intent(TrialActivity2.this, Result_Activity2.class);
                intent.putExtra("Total", String.valueOf(total));
                intent.putExtra("Correct", String.valueOf(correct));
                intent.putExtra("Incorrect", String.valueOf(wrong));
                intent.putExtra("points", String.valueOf(points));
                intent.putExtra("total_question", String.valueOf(total_question_number));
                intent.putExtra("query",q);
                intent.putExtra("count_down", String.valueOf(count_down));
                intent.putExtra("score", String.valueOf(mScore));
                intent.putExtra("answered",answered.toString());
                intent.putExtra("answer",String.valueOf(answer));
                startActivity(intent);
            }

        }.start();
    }
    public void stopTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();

        }
    }
    public void startTimer(int seconds, final TextView tv) {
        if (mCountDownTimer == null) {
            reverseTimer(seconds, tv);
        }
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateScore(int point) {
        mScoreView.setText("Score: " + mScore);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TrialActivity2.this, UserActivity.class));
        finish();
    }
}

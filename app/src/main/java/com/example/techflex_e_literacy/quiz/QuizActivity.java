package com.example.techflex_e_literacy.quiz;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.techflex_e_literacy.R;
import com.example.techflex_e_literacy.mainActivity.UserActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView mScoreView;
    private TextView mQuestionView, count_down, total_question, course_code, loadingCousreText;
    private Button mButtonChoice1;
    private Button mButtonChoice2;
    private Button mButtonChoice3, mButtonChoice4, quit;
    ProgressBar searchCourseProBar;
    Toolbar toolbar;
    private int mScore = 0;
    int total = 0;
    int points = 0;
    long total_question_number = 0;
    int currentQuestion = 0;
    int correct = 0;
    int wrong = 0;
    String query;
    String q;
    HashMap<Integer, Integer>  answered = new HashMap<>();
    DatabaseReference databaseReference;
    DatabaseReference mDatabaseReference;
    private CountDownTimer mCountDownTimer;
    List<SubscriptionValidation> mValidationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);

        toolbar = findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listView = findViewById(R.id.listview_sub);
        mValidationList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("courseRegDb");


        mScoreView = findViewById(R.id.score);
        mQuestionView = findViewById(R.id.question);
        mButtonChoice1 = findViewById(R.id.choice1);
        mButtonChoice2 = findViewById(R.id.choice2);
        mButtonChoice3 = findViewById(R.id.choice3);
        mButtonChoice4 = findViewById(R.id.choice4);
        count_down = findViewById(R.id.textview_count_down);
        searchCourseProBar = findViewById(R.id.courseSearch);
        total_question = findViewById(R.id.question_count);
        loadingCousreText = findViewById(R.id.loadingCourse);
        course_code = findViewById(R.id.course_code);
        quit = findViewById(R.id.quit);
        //num = FirebaseDatabase.getInstance().getReference("NumberOfQuestion");
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp3();
            }
        });


        handleIntent(getIntent());
    }


    @Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            updateQuestions(query);
        }
    }

    void showPopUp3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Attention!");
        builder.setMessage("If you  exit quiz your score won't be saved");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
        AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    void showPopUp2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Attention!");
        builder.setMessage("Maximum Attended Reached for Demo Version\nPlease Kindly Subscribe for more course");
        builder.setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QuizActivity.this, Bill.class);
                startActivity(intent);
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialogInterface.dismiss();
                Intent intent = new Intent(QuizActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
        AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    void showPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Attention");
        builder.setMessage("Check spellings\nCheck internet connection if first-time use\nContact admin");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        });
        builder.show();


    }

    void expiredSubscriptionPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setIcon(R.drawable.noun1);
        builder.setTitle("Attention!");
        builder.setMessage("Your Subscription has Expired, Kindly renew");
        builder.setPositiveButton("Renew", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QuizActivity.this, Bill.class);
                startActivity(intent);
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(QuizActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
        builder.show();
        AlertDialog alertDialog = builder.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

   /* public void totalQuestionNumber() {
        int number_of_question = total;
        String id = num.push().getKey();
        NumberOfQuestion numberOfQuestion = new NumberOfQuestion(number_of_question, id);
        num.child(id).setValue(numberOfQuestion);
        //Toast.makeText(this,"CourseAdded Successfully",Toast.LENGTH_SHORT).show();
        Log.d("TAG", number_of_question + "");

    }*/


    private void updateQuestions(final String query1) {
        q = query1;
        Log.i("teating",query1);
        mButtonChoice1.setEnabled(true);
        mButtonChoice2.setEnabled(true);
        mButtonChoice3.setEnabled(true);
        mButtonChoice4.setEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/quiz/" + query1.trim().toLowerCase());
        searchCourseProBar.setVisibility(View.VISIBLE);
        loadingCousreText.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchCourseProBar.setVisibility(View.GONE);
                loadingCousreText.setVisibility(View.GONE);
                if (!dataSnapshot.exists()) {
                    showPopUp();
                    return;
                }

                //if (dataSnapshot.exists()) {
                findViewById(R.id.quiz_layout).setVisibility(View.VISIBLE);
                total_question_number = (dataSnapshot.getChildrenCount());
                total_question.setText("Question: " + currentQuestion + "/" + total_question_number + "");
                course_code.setText(query1.trim().toUpperCase());
                startTimer(10, count_down);
                //}

                total++;

                if (total > total_question_number) {
                    total--;
                    Log.i("yo", answered.toString() + total);
                    // open result activity
                    finish();
                    Intent i = new Intent(QuizActivity.this, Result_Activity.class);
                    i.putExtra("Total", String.valueOf(total));
                    i.putExtra("Correct", String.valueOf(correct));
                    i.putExtra("Incorrect", String.valueOf(wrong));
                    i.putExtra("points", String.valueOf(points));
                    i.putExtra("total_question", String.valueOf(total_question_number));
                    i.putExtra("query",query1);
                    i.putExtra("answered",answered.toString());
                    i.putExtra("score",mScore);
                    i.putExtra("current_q",currentQuestion);

                    startActivity(i);
                    stopTimer();
                    mButtonChoice1.setEnabled(false);
                    mButtonChoice2.setEnabled(false);
                    mButtonChoice3.setEnabled(false);
                    mButtonChoice4.setEnabled(false);
                }else {
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("e_literacy/exam/quiz/" + query1.trim().toLowerCase()).child(String.valueOf(total));
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final QuestionLibrary questionLibrary = dataSnapshot.getValue(QuestionLibrary.class);
                                mQuestionView.setText(questionLibrary.getQuestion());
                                mButtonChoice1.setText(questionLibrary.getOption1());
                                mButtonChoice2.setText(questionLibrary.getOption2());
                                mButtonChoice3.setText(questionLibrary.getOption3());
                                mButtonChoice4.setText(questionLibrary.getOption4());
                                currentQuestion++;


//                                if (currentQuestion > 5 && mCountDownTimer != null) {
//                                    showPopUp2();
//                                    stopTimer();
//                                    //totalQuestionNumber();
//                                    mButtonChoice1.setEnabled(false);
//                                    mButtonChoice2.setEnabled(false);
//                                    mButtonChoice3.setEnabled(false);
//                                    mButtonChoice4.setEnabled(false);
//
//
//                                }

                                mButtonChoice1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,0);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);

                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,0);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice1.setBackgroundColor(Color.RED);
                                            if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                                mButtonChoice2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,1);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,1);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice2.setBackgroundColor(Color.RED);
                                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                                mButtonChoice3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,2);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,2);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice3.setBackgroundColor(Color.RED);
                                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                                mButtonChoice4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mButtonChoice1.setEnabled(false);
                                        mButtonChoice2.setEnabled(false);
                                        mButtonChoice3.setEnabled(false);
                                        mButtonChoice4.setEnabled(false);
                                        if (mButtonChoice4.getText().toString().equals(questionLibrary.getAnswer())) {
                                            answered.put(total,3);
                                            mScore = mScore + 1;
                                            updateScore(mScore);
                                            Toast.makeText(QuizActivity.this, "correct Answer", Toast.LENGTH_SHORT).show();
                                            mButtonChoice4.setBackgroundColor(Color.GREEN);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    correct++;
                                                    points = points + 15;
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);
                                                }
                                            }, 1500);
                                        } else {
                                            answered.put(total,3);
                                            Toast.makeText(QuizActivity.this, "wrong Answer", Toast.LENGTH_SHORT).show();
                                            wrong = wrong + 1;
                                            points = points - 5;
                                            mButtonChoice4.setBackgroundColor(Color.RED);
                                            if (mButtonChoice1.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice1.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice2.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice2.setBackgroundColor(Color.GREEN);
                                            } else if (mButtonChoice3.getText().toString().equals(questionLibrary.getAnswer())) {
                                                mButtonChoice3.setBackgroundColor(Color.GREEN);
                                            }
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mButtonChoice1.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice2.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice3.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    mButtonChoice4.setBackgroundColor(Color.parseColor("#03A9f4"));
                                                    updateQuestions(query1);
                                                    searchCourseProBar.setVisibility(View.GONE);
                                                    loadingCousreText.setVisibility(View.GONE);

                                                }
                                            }, 1500);
                                        }
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(QuizActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void reverseTimer(int seconds, final TextView tv) {
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
                Intent intent = new Intent(QuizActivity.this, Result_Activity.class);
                intent.putExtra("Total", String.valueOf(total));
                intent.putExtra("Correct", String.valueOf(correct));
                intent.putExtra("Incorrect", String.valueOf(wrong));
                intent.putExtra("points", String.valueOf(points));
                intent.putExtra("total_question", String.valueOf(total_question_number));
                intent.putExtra("query",q);
                intent.putExtra("answered",answered.toString());
                intent.putExtra("score", String.valueOf(mScore));
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
    }

    private void updateScore(int point) {
        mScoreView.setText("Score: " + mScore);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public Boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobile != null && mobile.isConnected()) || (wifi != null && wifi.isConnected()))
                return true;
            else return false;
        } else
            return false;
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(QuizActivity.this, UserActivity.class));
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Shared shared = new Shared(getApplicationContext());
        //to  check  b  is true or false
        shared.firstTime();
    }
}



    //Getting Users Subscription Start Date and Users Subscription End Date
   /* public class SubscriptionAdapter extends ArrayAdapter<SubscriptionValidation> {
        private Activity context;
        private List<SubscriptionValidation> mValidations;


        public SubscriptionAdapter(Activity context, List<SubscriptionValidation> mvalidation) {
            super(context, R.layout.list_layout, mvalidation);
            this.context = context;
            this.mValidations = mvalidation;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

            TextView startDate = listViewItem.findViewById(R.id.textView1);
            TextView endDate = listViewItem.findViewById(R.id.textView2);
            SubscriptionValidation subscriptionValidation = mValidations.get(position);
            startDate.setText(subscriptionValidation.getStartDate());
            endDate.setText(subscriptionValidation.getEndDate());
            //Log.d("TAG",subscriptionValidation.getStartDate());
            //Log.d("TAG",subscri*/
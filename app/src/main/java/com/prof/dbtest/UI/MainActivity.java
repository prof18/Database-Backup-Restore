/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Marco Gomiero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.prof.dbtest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.prof.dbtest.DB.DBHelper;
import com.prof.dbtest.Data.Exam;
import com.prof.dbtest.Data.Student;
import com.prof.dbtest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);

        final DBHelper db = new DBHelper(getApplicationContext());

        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        final com.getbase.floatingactionbutton.FloatingActionButton action_a = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_a);
        final com.getbase.floatingactionbutton.FloatingActionButton action_b = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.action_b);

        //"multiple choice" fab. One to add a new student, the other to add a new exam
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {

                TableLayout table = (TableLayout) findViewById(R.id.table);
                table.removeAllViews();

                action_a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addStud = new Intent(MainActivity.this, AddStudent.class);
                        startActivity(addStud);
                    }
                });

                action_b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addExam = new Intent(MainActivity.this, AddExam.class);
                        startActivity(addExam);
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {

            }
        });

        //button that shows student, exam and clears the view
        final Button showStud = (Button) findViewById(R.id.button);
        final Button showExam = (Button) findViewById(R.id.button2);
        final Button clear = (Button) findViewById(R.id.button3);

        showStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Student> students = db.getAllStudent();

                TableLayout table = (TableLayout) findViewById(R.id.table);
                //table customization
                TableLayout.LayoutParams layoutParamsT = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParamsT.setMargins(30, 20, 40, 0);
                table.removeAllViews();

                TableRow row = new TableRow(getApplicationContext());
                TableRow.LayoutParams rLayoutParamsTR = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                rLayoutParamsTR.setMargins(30, 20, 40, 0);
                row.removeAllViews();

                //row population
                TextView tvIdTitle = new TextView(getApplicationContext());
                TextView tvNameTitle = new TextView(getApplicationContext());
                TextView tvSurnameTitle = new TextView(getApplicationContext());
                TextView tvDateTItle = new TextView(getApplicationContext());

                tvIdTitle.setText("ID");
                tvIdTitle.setTextSize(23);
                tvNameTitle.setText("Name");
                tvNameTitle.setTextSize(23);
                tvSurnameTitle.setText("Surname");
                tvSurnameTitle.setTextSize(23);
                tvDateTItle.setText(String.valueOf("Date of Born"));
                tvDateTItle.setTextSize(23);

                row.addView(tvIdTitle,rLayoutParamsTR);
                row.addView(tvNameTitle,rLayoutParamsTR);
                row.addView(tvSurnameTitle,rLayoutParamsTR);
                row.addView(tvDateTItle,rLayoutParamsTR);

                table.addView(row,layoutParamsT);

                for (Student stud : students) {

                    TableRow rowEl = new TableRow(getApplicationContext());
                    //table customization
                    TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 20, 40, 20);
                    TableRow.LayoutParams rLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    rLayoutParams.setMargins(30, 20, 40, 0);

                    //table population
                    int id = stud.getId();
                    String name = stud.getName();
                    String surname = stud.getSurname();
                    long millis = stud.getBorn();

                    TextView tvId = new TextView(getApplicationContext());
                    TextView tvName = new TextView(getApplicationContext());
                    TextView tvSurname = new TextView(getApplicationContext());
                    TextView tvDate = new TextView(getApplicationContext());

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = formatter.format(new Date(millis));

                    tvId.setText(String.valueOf(id));
                    tvName.setText(name);
                    tvSurname.setText(surname);
                    tvDate.setText(dateString);

                    rowEl.addView(tvId,rLayoutParams);
                    rowEl.addView(tvName,rLayoutParams);
                    rowEl.addView(tvSurname,rLayoutParams);
                    rowEl.addView(tvDate,rLayoutParams);

                    table.addView(rowEl,layoutParams);
                }
            }
        });

        showExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Exam> exams = db.getAllExam();

                TableLayout table = (TableLayout) findViewById(R.id.table);
                //table customization
                TableLayout.LayoutParams layoutParamsT = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParamsT.setMargins(30, 20, 40, 0);
                TableRow.LayoutParams rLayoutParamsTR = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                rLayoutParamsTR.setMargins(30, 20, 40, 0);
                table.removeAllViews();
                TableRow row = new TableRow(getApplicationContext());
                row.removeAllViews();

                //table population
                TextView tvIdTitle = new TextView(getApplicationContext());
                tvIdTitle.setTextSize(25);
                TextView tvNameTitle = new TextView(getApplicationContext());
                tvNameTitle.setTextSize(25);
                TextView tvSurnameTitle = new TextView(getApplicationContext());
                tvSurnameTitle.setTextSize(25);

                tvIdTitle.setText("Exam ID");
                tvNameTitle.setText("Student ID");
                tvSurnameTitle.setText("Evaluation");

                row.addView(tvIdTitle,rLayoutParamsTR);
                row.addView(tvNameTitle,rLayoutParamsTR);
                row.addView(tvSurnameTitle,rLayoutParamsTR);

                table.addView(row, layoutParamsT);

                for (Exam e : exams) {
                    TableRow rowEl = new TableRow(getApplicationContext());

                    //table customization
                    TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 20, 40, 20);
                    TableRow.LayoutParams rLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    rLayoutParams.setMargins(30, 20, 40, 0);

                    //table population
                    int id = e.getId();
                    int stud = e.getStudent();
                    int eval = e.getEvaluation();

                    TextView tvId = new TextView(getApplicationContext());
                    TextView tvName = new TextView(getApplicationContext());
                    TextView tvSurname = new TextView(getApplicationContext());

                    tvId.setText(String.valueOf(id));
                    tvName.setText(String.valueOf(stud));
                    tvSurname.setText(String.valueOf(eval));

                    rowEl.addView(tvId,rLayoutParams);
                    rowEl.addView(tvName,rLayoutParams);
                    rowEl.addView(tvSurname,rLayoutParams);

                    table.addView(rowEl,layoutParams);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout table = (TableLayout) findViewById(R.id.table);
                table.removeAllViews();
            }
        });

        db.closeDB();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        if(fabMenu.isExpanded())
            fabMenu.collapse();
    }
}

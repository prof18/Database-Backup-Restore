/*
 *   Copyright 2016 Marco Gomiero
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */


package com.prof.dbtest.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prof.dbtest.db.DBHelper;
import com.prof.dbtest.data.Exam;
import com.prof.dbtest.R;

import java.util.ArrayList;

public class AddExam extends AppCompatActivity {

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //open a db instance
        db = new DBHelper(getApplicationContext());

        final Spinner stud_id = findViewById(R.id.spinner2);
        final EditText examID = findViewById(R.id.edit_id);
        final Spinner mark = findViewById(R.id.spinner);

        //populate the spinner with the id of the student already present in the db
        ArrayList<String> idList =  db.getStudId();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,idList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stud_id.setAdapter(spinnerAdapter);
        stud_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });


        FloatingActionButton fab = findViewById(R.id.addEx);
        //add a new exam when the fab is clicked
        fab.setOnClickListener(v -> {
            Exam e = new Exam();

            try {
                int id = Integer.valueOf(examID.getText().toString());
                int studId = Integer.valueOf(stud_id.getSelectedItem().toString());
                int eval = Integer.valueOf(mark.getSelectedItem().toString());

                e.setId(id);
                e.setStudent(studId);
                e.setEvaluation(eval);

                long id_db = db.addExam(e);

                Toast.makeText(AddExam.this, "Exam Added", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception ex) {
                Toast.makeText(AddExam.this, "Unable to add exam. Check the data", Toast.LENGTH_SHORT).show();
            }
        });

        db.closeDB();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

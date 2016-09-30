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

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.prof.dbtest.DB.DBHelper;
import com.prof.dbtest.Data.Exam;
import com.prof.dbtest.R;

import java.util.ArrayList;

public class AddExam extends AppCompatActivity {

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //open a db instance
        db = new DBHelper(getApplicationContext());

        final Spinner stud_id = (Spinner) findViewById(R.id.spinner2);
        final EditText examID = (EditText) findViewById(R.id.edit_id);
        final Spinner mark = (Spinner) findViewById(R.id.spinner);

        //populate the spinner with the id of the student already present in the db
        ArrayList<String> idList =  db.getStudId();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,idList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stud_id.setAdapter(spinnerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addEx);
        //add a new exam when the fab is clicked
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exam e = new Exam();

                int id = Integer.valueOf(examID.getText().toString());
                int studId = Integer.valueOf(stud_id.getSelectedItem().toString());
                int eval = Integer.valueOf(mark.getSelectedItem().toString());

                e.setId(id);
                e.setStudent(studId);
                e.setEvaluation(eval);

                long id_db = db.addExam(e);

                Toast.makeText(AddExam.this, "Exam Added", Toast.LENGTH_SHORT).show();
                finish();
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

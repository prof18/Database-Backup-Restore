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

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prof.dbtest.DB.DBHelper;
import com.prof.dbtest.Data.Student;
import com.prof.dbtest.R;

import java.util.Calendar;

public class AddStudent extends AppCompatActivity {

    Calendar date;
    Long millisDate;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);

        final EditText editId = (EditText) findViewById(R.id.edit_stud_id);
        final EditText editName = (EditText) findViewById(R.id.edit_name);
        final EditText editSurname = (EditText) findViewById(R.id.edit_surname);
        final TextView editDate = (TextView) findViewById(R.id.edit_date);

        //open a db instance
        db = new DBHelper(getApplicationContext());

        //select the date of born
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date_selected = dayOfMonth + "/" + (month + 1) + "/"
                                + year;
                        editDate.setText(date_selected);

                        date = Calendar.getInstance();
                        date.set(year,month,dayOfMonth);
                        millisDate = date.getTimeInMillis();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddStudent.this, listener,1993,8,5);
                datePickerDialog.show();
            }
        });

        android.support.design.widget.FloatingActionButton fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.addEx);
        //add a new student when the fab is clicked
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(editId.getText().toString());
                String name = editName.getText().toString();
                String surname = editSurname.getText().toString();
                Student stud = new Student();
                stud.setId(id);
                stud.setName(name);
                stud.setSurname(surname);
                stud.setBorn(millisDate);

                long id_db = db.addStudent(stud);

                Toast.makeText(AddStudent.this, "Student added", Toast.LENGTH_SHORT).show();
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

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
import java.util.List;

public class AddExam extends AppCompatActivity {

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(getApplicationContext());

        final Spinner stud_id = (Spinner) findViewById(R.id.spinner2);
        final EditText examID = (EditText) findViewById(R.id.edit_id);
        final Spinner mark = (Spinner) findViewById(R.id.spinner);

        ArrayList<String> idList =  db.getStudId();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,idList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stud_id.setAdapter(spinnerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addEx);
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


        db.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}

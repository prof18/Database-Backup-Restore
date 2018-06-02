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

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.prof.dbtest.db.DBHelper;
import com.prof.dbtest.data.Exam;
import com.prof.dbtest.data.Student;
import com.prof.dbtest.R;
import com.prof.dbtest.backup.LocalBackup;
import com.prof.dbtest.backup.RemoteBackup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.prof.dbtest.db.DBHelper.getDatabaseVersion;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Google Drive Activity";

    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_CODE_OPENING = 1;
    public static final int REQUEST_CODE_CREATION = 2;
    public static final int REQUEST_CODE_PERMISSIONS = 2;

    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean isBackup = true;

    private MainActivity activity;

    private RemoteBackup remoteBackup;
    private LocalBackup localBackup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);

        remoteBackup = new RemoteBackup(this);
        localBackup = new LocalBackup(this);

        final DBHelper db = new DBHelper(getApplicationContext());
        setupUI(db);
        db.closeDB();
    }

    public void setupUI(DBHelper db) {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showMultichoice());


        //button that shows student, exam and clears the view
        Button showStud = findViewById(R.id.button);
        Button showExam = findViewById(R.id.button2);
        Button clear = findViewById(R.id.button3);

        showStud.setOnClickListener(v -> {
            ArrayList<Student> students = db.getAllStudent();

            TableLayout table = findViewById(R.id.table);
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
            tvIdTitle.setTextSize(20);
            tvIdTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvNameTitle.setText("Name");
            tvNameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvNameTitle.setTextSize(20);
            tvSurnameTitle.setText("Surname");
            tvSurnameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvSurnameTitle.setTextSize(20);
            tvDateTItle.setText(String.valueOf("Birth"));
            tvDateTItle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvDateTItle.setTextSize(20);

            row.addView(tvIdTitle, rLayoutParamsTR);
            row.addView(tvNameTitle, rLayoutParamsTR);
            row.addView(tvSurnameTitle, rLayoutParamsTR);
            row.addView(tvDateTItle, rLayoutParamsTR);

            table.addView(row, layoutParamsT);

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
                tvId.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvName.setText(name);
                tvName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvSurname.setText(surname);
                tvSurname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvDate.setText(dateString);
                tvDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                rowEl.addView(tvId, rLayoutParams);
                rowEl.addView(tvName, rLayoutParams);
                rowEl.addView(tvSurname, rLayoutParams);
                rowEl.addView(tvDate, rLayoutParams);

                table.addView(rowEl, layoutParams);
            }
        });

        showExam.setOnClickListener(v -> {
            ArrayList<Exam> exams = db.getAllExam();

            TableLayout table = findViewById(R.id.table);
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
            tvIdTitle.setTextSize(20);
            tvIdTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            TextView tvNameTitle = new TextView(getApplicationContext());
            tvNameTitle.setTextSize(20);
            tvNameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            TextView tvSurnameTitle = new TextView(getApplicationContext());
            tvSurnameTitle.setTextSize(20);
            tvSurnameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            tvIdTitle.setText("Exam ID");
            tvNameTitle.setText("Student ID");
            tvSurnameTitle.setText("Mark");

            row.addView(tvIdTitle, rLayoutParamsTR);
            row.addView(tvNameTitle, rLayoutParamsTR);
            row.addView(tvSurnameTitle, rLayoutParamsTR);

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
                tvId.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvName.setText(String.valueOf(stud));
                tvName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvSurname.setText(String.valueOf(eval));
                tvSurname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                rowEl.addView(tvId, rLayoutParams);
                rowEl.addView(tvName, rLayoutParams);
                rowEl.addView(tvSurname, rLayoutParams);

                table.addView(rowEl, layoutParams);
            }
        });

        clear.setOnClickListener(v -> {
            TableLayout table = findViewById(R.id.table);
            table.removeAllViews();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        final DBHelper db = new DBHelper(getApplicationContext());

        switch (id) {
            case R.id.action_backup:
                String outFileName = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
                localBackup.performBackup(db, outFileName);
                break;
            case R.id.action_import:
                localBackup.performRestore(db);
                break;
            case R.id.action_backup_Drive:
                isBackup = true;
                remoteBackup.connectToDrive(isBackup);
                break;
            case R.id.action_import_Drive:
                isBackup = false;
                remoteBackup.connectToDrive(isBackup);
                break;
            case R.id.action_delete_all:
                //reinitialize the backup
                SQLiteDatabase database = db.getWritableDatabase();
                db.onUpgrade(database, getDatabaseVersion(), getDatabaseVersion());
                TableLayout table = findViewById(R.id.table);
                table.removeAllViews();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    remoteBackup.connectToDrive(isBackup);
                }
                break;

            case REQUEST_CODE_CREATION:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Backup successfully saved.");
                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_OPENING:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    remoteBackup.mOpenItemTaskSource.setResult(driveId);
                } else {
                    remoteBackup.mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }

        }
    }


    private void showMultichoice() {
        AlertDialog.Builder builderChoose = new AlertDialog.Builder(activity);
        final CharSequence[] items = {"Add student", "Add Exam"};


        builderChoose
                .setItems(items, (dialog, which) -> {

                    switch (which) {
                        case 0:
                            Intent addStud = new Intent(MainActivity.this, AddStudent.class);
                            startActivity(addStud);
                            break;

                        case 1:
                            Intent addExam = new Intent(MainActivity.this, AddExam.class);
                            startActivity(addExam);
                            break;

                        default:
                            break;
                    }


                });
        builderChoose.show();
    }
}


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

package com.prof.dbtest.UI;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.prof.dbtest.DB.DBHelper;
import com.prof.dbtest.Data.Exam;
import com.prof.dbtest.Data.Student;
import com.prof.dbtest.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.prof.dbtest.DB.DBHelper.getDatabaseVersion;

public class MainActivity extends AppCompatActivity {

    String outFileName = null;

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
        if (fabMenu.isExpanded())
            fabMenu.collapse();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        final DBHelper db = new DBHelper(getApplicationContext());

        //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
        if (id == R.id.action_backup) {

            verifyStoragePermissions(this);
            outFileName = Environment.getExternalStorageDirectory() + File.separator + "DBTest" + File.separator;
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "DBTest");

            boolean success = true;
            if (!folder.exists())
                success = folder.mkdirs();
            if (success) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Backup Name");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        outFileName = outFileName + m_Text + ".db";
                        db.backup(outFileName);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            } else
                Toast.makeText(this, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
        }

        //ask to the user what backup to restore
        if (id == R.id.action_import) {

            verifyStoragePermissions(this);

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "DBTest");
            if (folder.exists()) {

                final File[] files = folder.listFiles();

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);
                for (File file : files)
                    arrayAdapter.add(file.getName());

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                builderSingle.setTitle("Restore:");
                builderSingle.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    db.importDB(files[which].getPath());
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Unable to restore. Retry", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builderSingle.show();
            } else
                Toast.makeText(this, "Backup folder not present.\nDo a backup before a restore!", Toast.LENGTH_SHORT).show();
        }

        //reinitialize the backup
        if (id == R.id.action_delete_all) {

            SQLiteDatabase database = db.getWritableDatabase();
            db.onUpgrade(database, getDatabaseVersion(), getDatabaseVersion());
            TableLayout table = (TableLayout) findViewById(R.id.table);
            table.removeAllViews();
        }
        return super.onOptionsItemSelected(item);
    }

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //check permissions.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}


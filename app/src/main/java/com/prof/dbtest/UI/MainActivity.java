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
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.prof.dbtest.DB.DBHelper;
import com.prof.dbtest.Data.Exam;
import com.prof.dbtest.Data.Student;
import com.prof.dbtest.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.prof.dbtest.DB.DBHelper.DATABASE_NAME;
import static com.prof.dbtest.DB.DBHelper.getDatabaseVersion;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Google Drive Activity";

    private static final int REQUEST_CODE_OPENER = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean bckORrst = true;

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

        switch (id) {

            case R.id.action_backup:
                String outFileName = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
                performBackup(db, outFileName);
                break;
            case R.id.action_import:
                performRestore(db);
                break;
            case R.id.action_backup_Drive:
                bckORrst = true;
                if (mGoogleApiClient != null)
                    mGoogleApiClient.disconnect();
                mGoogleApiClient = gApiCLient(mGoogleApiClient);
                mGoogleApiClient.connect();
                break;
            case R.id.action_import_Drive:
                bckORrst = false;
                if (mGoogleApiClient != null)
                    mGoogleApiClient.disconnect();
                mGoogleApiClient = gApiCLient(mGoogleApiClient);
                mGoogleApiClient.connect();
                break;
            case R.id.action_delete_all:
                //reinitialize the backup
                SQLiteDatabase database = db.getWritableDatabase();
                db.onUpgrade(database, getDatabaseVersion(), getDatabaseVersion());
                TableLayout table = (TableLayout) findViewById(R.id.table);
                table.removeAllViews();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    private void performBackup(final DBHelper db, final String outFileName) {

        verifyStoragePermissions(this);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));

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
                    String out = outFileName + m_Text + ".db";
                    db.backup(out);
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
    private void performRestore(final DBHelper db) {

        verifyStoragePermissions(this);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));
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

    private void saveFileToDrive() {

        //database path on the device
        final String inFileName = getApplicationContext().getDatabasePath(DATABASE_NAME).toString();

        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new Drive backup.");
                    Toast.makeText(MainActivity.this, "Error on loading Google Drive. Retry", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Backup to drive started.");

                try {

                    File dbFile = new File(inFileName);
                    FileInputStream fis = new FileInputStream(dbFile);
                    OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    //drive file metadata
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setTitle("database_backup.db")
                            .setMimeType("application/db")
                            .build();

                    // Create an intent for the file chooser, and start it.
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(driveContentsResult.getDriveContents())
                            .build(mGoogleApiClient);

                    startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void importFromDrive(DriveFile dbFile) {

        //database path on the device
        final String inFileName = getApplicationContext().getDatabasePath(DATABASE_NAME).toString();

        dbFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to open Drive backup.");
                    Toast.makeText(MainActivity.this, "Error on loading from Google Drive. Retry", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Backup to drive started.");

                // DriveContents object contains pointers to the actual byte stream
                DriveContents contents = driveContentsResult.getDriveContents();

                try {

                    ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                    FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                    // Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(inFileName);

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    // Close the streams
                    output.flush();
                    output.close();
                    fileInputStream.close();

                    Toast.makeText(getApplicationContext(), "Import Completed", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error on loading", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Connect the Client
    private GoogleApiClient gApiCLient(GoogleApiClient mGoogleApiClient) {

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        return mGoogleApiClient;
    }

    @Override
    // Called whenever the API client fails to connect.
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");

        //when the client is connected i have two possibility: backup (bckORrst -> true) or restore (bckORrst -> false)
        if (bckORrst)
            saveFileToDrive();
        else {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"application/db"})
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                Log.i(TAG, "Open File Intent send");
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send Open File Intent", e);
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Backup successfully saved.");
                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    //Toast.makeText(this, driveId.toString(), Toast.LENGTH_SHORT).show();
                    DriveFile file = driveId.asDriveFile();
                    importFromDrive(file);
                }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onPause();
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


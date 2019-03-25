package com.waiting.studentlist;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.waiting.studentlist.sqlite.DatabaseHelper;
import com.waiting.studentlist.sqlite.model.StudentData;
import com.waiting.studentlist.utils.MyDividerItemDecoration;
import com.waiting.studentlist.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewAdapter mAdapter;
    private List<StudentData> studentsList = new ArrayList<>();
    private RecyclerView recyclerView;

    private String SelectedPriority;

    private TextView noStudentsView;

    //Data base methods object reference
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the toolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Getting views reference
        recyclerView = findViewById(R.id.recycler_view);
        noStudentsView = findViewById(R.id.empty_students_view);

        db = new DatabaseHelper(this);

        //Prepare all data from the database to display
        studentsList.addAll(db.getAllStudents());

        //The button we use to add new student
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        //Setting up the recycler view
        mAdapter = new RecyclerViewAdapter(this, studentsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }


    /**
     * Inserting new name in db
     * and refreshing the list
     */
    private void addNewStudent(StudentData student) {

        //Log.i(TAG, "addNewStudent: " + student.getPriority());

        // inserting student in db and getting
        // newly inserted name id
        long id = db.insertStudent(student);

        // get the newly inserted name from db
        StudentData n = db.getStudent(id);

        if (n != null) {
            // adding new name to the list.
            studentsList.add(n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            Toast.makeText(this, "New student has been added", Toast.LENGTH_SHORT).show();

            toggleEmptyNotes();
        }
    }


    /**
     * Updating student data in db and updating
     * item in the list by its position
     */
    private void updateStudent(StudentData student, int position) {

        //Get the student position to get its data.
        StudentData n = studentsList.get(position);

        // updating name text
        n.setName(student.getName());
        n.setCourse(student.getCourse());
        n.setPriority(student.getPriority());

        // updating name in db
        db.updateStudent(n);

        // refreshing the list
        studentsList.set(position, n);
        mAdapter.notifyItemChanged(position);

        Toast.makeText(this, "Student data has been updated", Toast.LENGTH_SHORT).show();

        toggleEmptyNotes();
    }


    /**
     * Deleting student from SQLite and removing the
     * item from the list by its position
     */
    private void deleteStudent(int position) {

        // deleting the name from db
        db.deleteStudent(studentsList.get(position));

        // removing the name from the list
        studentsList.remove(position);
        mAdapter.notifyItemRemoved(position);

        Toast.makeText(this, "Student has beed deleted", Toast.LENGTH_SHORT).show();

        toggleEmptyNotes();
    }


    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 1
     */
    private void showActionsDialog(final int position) {

        //The buttons we need to show
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, studentsList.get(position), position);
                } else {
                    deleteStudent(position);
                }
            }
        });
        builder.show();
    }

    //View view;
    private void showNoteDialog(final boolean shouldUpdate, final StudentData student, final int position) {

        //Getting the design of the dialog
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.student_dialog_design, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        //Getting the reference of the views.
        final EditText studentName = view.findViewById(R.id.student);
        final EditText studentCourse = view.findViewById(R.id.course);
        final RadioGroup studentPriorityRadioButton = view.findViewById(R.id.radioGroup);

        //For the title
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? "New student" : "Edit"); //To check if clicked edit or add new student

        if (shouldUpdate && student != null) {

            //Show the data to be updated
            studentName.setText(student.getName());
            studentCourse.setText(student.getCourse());

            //Set the student SelectedPriority
            if (student.getPriority().equals("1st Year")) {

                //Set the student SelectedPriority
                studentPriorityRadioButton.check(R.id.firstYear);
                SelectedPriority = "1st Year";

            } else if (student.getPriority().equals("2nd Year")) {

                studentPriorityRadioButton.check(R.id.secondYear);
                SelectedPriority = "2nd Year";

            } else if (student.getPriority().equals("3rd Year")) {

                studentPriorityRadioButton.check(R.id.thirdYear);
                SelectedPriority = "3rd Year";

            } else if (student.getPriority().equals("4th Year")) {

                studentPriorityRadioButton.check(R.id.fourthYear);
                SelectedPriority = "4th Year";

            } else {

                studentPriorityRadioButton.check(R.id.graduated);
                SelectedPriority = "Graduated";

            }
        }


        //init the buttons of the dialog
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(studentName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter student!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating student
                if (shouldUpdate && student != null) {
                    // update student by it's id
                    getSelectedRadioButton(studentPriorityRadioButton.getCheckedRadioButtonId());

                    StudentData s = new StudentData(
                            studentName.getText().toString(),
                            studentCourse.getText().toString(),
                            SelectedPriority
                    );

                    updateStudent(s, position);

                } else {

                    Log.i("MainActivity", "onClick: " + studentPriorityRadioButton.getCheckedRadioButtonId());

                    // create new student
                    getSelectedRadioButton(studentPriorityRadioButton.getCheckedRadioButtonId());

                    StudentData newStudent = new StudentData(
                            studentName.getText().toString(),
                            studentCourse.getText().toString(),
                            SelectedPriority
                    );

                    addNewStudent(newStudent);
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check studentsList.size() > 0
        // if there is no data in the SQLite database
        // we will show there is no data.
        if (db.getStudentsCount() > 0) {
            noStudentsView.setVisibility(View.GONE);
        } else {
            noStudentsView.setVisibility(View.VISIBLE);
        }
    }


    //Getting the selected radio button
    private void getSelectedRadioButton(int selectedId) {

        // Check which radio button was clicked
        switch (selectedId) {

            case R.id.firstYear:
                SelectedPriority = "1st Year";
                break;

            case R.id.secondYear:
                SelectedPriority = "2nd Year";
                break;

            case R.id.thirdYear:
                SelectedPriority = "3rd Year";
                break;

            case R.id.fourthYear:
                SelectedPriority = "4th Year";
                break;

            case R.id.graduated:
                SelectedPriority = "Graduated";
                break;
        }
    }
}

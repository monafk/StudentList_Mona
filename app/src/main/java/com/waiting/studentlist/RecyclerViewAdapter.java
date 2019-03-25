package com.waiting.studentlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.waiting.studentlist.sqlite.model.StudentData;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<StudentData> studentDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //View variables
        public TextView name;
        public TextView course;
        public TextView priority;

        public MyViewHolder(View view) {
            super(view);

            //Getting the reference of the views.
            name = view.findViewById(R.id.student);
            course = view.findViewById(R.id.course);
            priority = view.findViewById(R.id.priority);
        }
    }


    public RecyclerViewAdapter(Context context, List<StudentData> studentDataList) {
        this.context = context;
        this.studentDataList = studentDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(context)
                        .inflate(R.layout.student_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentData studentData = studentDataList.get(position);

        //Display the studentData data
        holder.name.setText("Student: " + studentData.getName());
        holder.course.setText("Course: " + studentData.getCourse());
        holder.priority.setText("Priority: " + studentData.getPriority());
    }

    @Override
    public int getItemCount() {
        return studentDataList.size();
    }

}

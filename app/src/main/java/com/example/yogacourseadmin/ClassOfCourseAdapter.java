package com.example.yogacourseadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassOfCourseAdapter extends RecyclerView.Adapter<ClassOfCourseAdapter.ClassOfCourseViewHolder> {
    private Context context;
    private ArrayList<YogaClass> yogaClasses;
    private DBHelper dbHelper;

    public ClassOfCourseAdapter(Context context, ArrayList<YogaClass> yogaClasses) {
        this.context = context;
        this.yogaClasses = yogaClasses;
    }

    @NonNull
    @Override
    public ClassOfCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassOfCourseAdapter.ClassOfCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassOfCourseViewHolder holder, int position) {
        YogaClass yogaClass = yogaClasses.get(position);
        holder.yogaClassName.setText("Class " + String.valueOf(yogaClass.getClassId()));
    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }
    public static class ClassOfCourseViewHolder extends RecyclerView.ViewHolder {
        TextView yogaClassName;

        public ClassOfCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            yogaClassName = itemView.findViewById(R.id.className);
        }
    }

}

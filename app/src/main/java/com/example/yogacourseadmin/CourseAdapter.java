package com.example.yogacourseadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private Context context;
    private ArrayList<Course> courses;
    private DBHelper dbHelper; // Thêm DBHelper vào adapter

    public CourseAdapter(Context context, ArrayList<Course> courses) {
        this.context = context;
        this.courses = courses;
        this.dbHelper = new DBHelper(context); // Khởi tạo DBHelper
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseName.setText("Course " + course.getIdCourse());
        // Thêm sự kiện cho tên khóa học để mở ListClassByCourseIdActivity
        holder.courseName.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassesOfCourse.class);
            intent.putExtra("courseId", String.valueOf(course.getIdCourse())); // Gửi ID khóa học
            context.startActivity(intent); // Bắt đầu hoạt động hiển thị danh sách lớp
        });
        // Thêm sự kiện cho nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteCourse(course.getIdCourse());
            if (isDeleted) {
                Toast.makeText(context, "Course deleted successfully!", Toast.LENGTH_SHORT).show();
                courses.remove(position); // Xóa khóa học khỏi danh sách
                notifyItemRemoved(position); // Thông báo rằng item đã bị xóa
            } else {
                Toast.makeText(context, "Failed to delete course", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện cho nút chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddCourseActivity.class);
            intent.putExtra("courseId", course.getIdCourse()); // Gửi ID khóa học
            intent.putExtra("textViewEdit","Edit Course "+String.valueOf(course.getIdCourse()));
            intent.putExtra("textButtonEdit","Edit");
            context.startActivity(intent); // Bắt đầu hoạt động chỉnh sửa
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        Button btnEdit, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.courseName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}


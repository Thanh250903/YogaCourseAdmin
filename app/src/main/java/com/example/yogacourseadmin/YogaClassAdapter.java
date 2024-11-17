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

public class YogaClassAdapter extends RecyclerView.Adapter<YogaClassAdapter.YogaClassViewHolder> {
    private Context context;
    private ArrayList<YogaClass> yogaClasses;
    private DBHelper dbHelper; // Thêm DBHelper vào adapter

    public YogaClassAdapter(Context context, ArrayList<YogaClass> yogaClasses) {
        this.context = context;
        this.yogaClasses = yogaClasses;
        this.dbHelper = new DBHelper(context); // Khởi tạo DBHelper
    }


    @NonNull
    @Override
    public YogaClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new YogaClassAdapter.YogaClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YogaClassViewHolder holder, int position) {
        YogaClass yogaClass = yogaClasses.get(position);
        holder.yogaClassName.setText("Class " + yogaClass.getClassId());

        // Thêm sự kiện cho nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteClass(yogaClass.getClassId());
            if (isDeleted) {
                Toast.makeText(context, "Class deleted successfully!", Toast.LENGTH_SHORT).show();
                yogaClasses.remove(position); // Xóa khóa học khỏi danh sách
                notifyItemRemoved(position); // Thông báo rằng item đã bị xóa
            } else {
                Toast.makeText(context, "Failed to delete Class", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện cho nút chỉnh sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddClassActivity.class);
            intent.putExtra("classId", yogaClass.getClassId()); // Gửi ID lớp học
            intent.putExtra("textEdit","Edit Class " + String.valueOf(yogaClass.getClassId()));
            intent.putExtra("textBtnEdit","Edit");
            context.startActivity(intent); // Bắt đầu hoạt động chỉnh sửa
        });
    }

    @Override
    public int getItemCount() {
        return yogaClasses.size();
    }
    public static class YogaClassViewHolder extends RecyclerView.ViewHolder {
        TextView yogaClassName;
        Button btnEdit, btnDelete;

        public YogaClassViewHolder(@NonNull View itemView) {
            super(itemView);
            yogaClassName = itemView.findViewById(R.id.courseName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

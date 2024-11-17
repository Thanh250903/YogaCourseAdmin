package com.example.yogacourseadmin;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassesOfCourse extends AppCompatActivity {
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private ArrayList<YogaClass> yogaClassList;
    private ClassOfCourseAdapter classOfCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_classes_of_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Đóng activity hiện tại
            }
        });
        String courseId = getIntent().getStringExtra("courseId");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Classes of Course " + courseId);
        }
        // Khởi tạo DBHelper và RecyclerView
        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerView); // Khởi tạo RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách lớp học theo courseId
        yogaClassList = dbHelper.getClassesByCourseId(Integer.parseInt(courseId));
        // Khởi tạo adapter và thiết lập cho RecyclerView
        classOfCourseAdapter = new ClassOfCourseAdapter(this, yogaClassList);
        recyclerView.setAdapter(classOfCourseAdapter);
    }
}
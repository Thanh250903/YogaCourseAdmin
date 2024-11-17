package com.example.yogacourseadmin.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yogacourseadmin.AddCourseActivity;
import com.example.yogacourseadmin.Course;
import com.example.yogacourseadmin.CourseAdapter;
import com.example.yogacourseadmin.DBHelper;
import com.example.yogacourseadmin.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private DBHelper dbHelper;
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private Button addCourseBtn;
    private ArrayList<Course> courseList;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflating view bằng cách sử dụng ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo RecyclerView và Adapter
        recyclerView = binding.recyclerView;
        addCourseBtn = binding.addCourse;
        dbHelper = new DBHelper(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseAdapter);
        LoadCourses();

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Logic load lại dữ liệu tại đây
        LoadCourses(); // Gọi hàm load dữ liệu của bạn
    }
    public void LoadCourses(){
        courseList = dbHelper.getAllCourses();
        courseAdapter = new CourseAdapter(getContext(), courseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseAdapter);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

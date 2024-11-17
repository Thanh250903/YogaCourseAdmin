package com.example.yogacourseadmin.ui.search;

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
import android.widget.EditText;

import com.example.yogacourseadmin.ClassOfCourseAdapter;
import com.example.yogacourseadmin.DBHelper;
import com.example.yogacourseadmin.YogaClass;
import com.example.yogacourseadmin.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private ArrayList<YogaClass> yogaClassList;
    private ClassOfCourseAdapter classOfCourseAdapter;
    private EditText inputSearch;
    private Button btnSearch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Khởi tạo DBHelper và RecyclerView
        dbHelper = new DBHelper(getContext());
        recyclerView = binding.recyclerView; // Khởi tạo RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy tham chiếu đến EditText và Button từ giao diện
        inputSearch = binding.inputSearch;  // EditText trong layout
        btnSearch = binding.btnSearch;      // Button trong layout
        // Gán sự kiện click cho nút tìm kiếm
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teacherName = inputSearch.getText().toString();
                if (!teacherName.isEmpty()) {
                    loadClassesByTeacher(teacherName);
                }
            }
        });

        // Khởi tạo adapter và thiết lập cho RecyclerView
        yogaClassList = new ArrayList<>();
        classOfCourseAdapter = new ClassOfCourseAdapter(getContext(), yogaClassList);
        recyclerView.setAdapter(classOfCourseAdapter);

        return root;
    }
    // Hàm load dữ liệu lớp học theo tên giáo viên
    private void loadClassesByTeacher(String teacherName) {
        yogaClassList.clear();  // Xóa danh sách cũ
        yogaClassList.addAll(dbHelper.getClassesByTeacher(teacherName));  // Thêm danh sách mới
        classOfCourseAdapter.notifyDataSetChanged();  // Cập nhật lại adapter
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
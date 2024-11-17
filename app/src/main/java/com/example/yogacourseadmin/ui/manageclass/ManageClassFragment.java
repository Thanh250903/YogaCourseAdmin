package com.example.yogacourseadmin.ui.manageclass;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yogacourseadmin.AddClassActivity;
import com.example.yogacourseadmin.DBHelper;
import com.example.yogacourseadmin.YogaClass;
import com.example.yogacourseadmin.YogaClassAdapter;
import com.example.yogacourseadmin.databinding.FragmentManageClassBinding;

import java.util.ArrayList;
//import com.example.yogacourse.ui.home.HomeViewModel;

public class ManageClassFragment extends Fragment {

    private FragmentManageClassBinding binding;
    Button addBtn;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private YogaClassAdapter yogaClassAdapter;
    private ArrayList<YogaClass> yogaClassList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentManageClassBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addBtn = binding.addClass;
        // Khởi tạo RecyclerView và Adapter
        recyclerView = binding.recyclerView;
        dbHelper = new DBHelper(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(yogaClassAdapter);
        LoadClasses();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddClassActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Logic load lại dữ liệu tại đây
        LoadClasses(); // Gọi hàm load dữ liệu của bạn
    }
    public void LoadClasses(){
        yogaClassList = dbHelper.getAllClasses();
        yogaClassAdapter = new YogaClassAdapter(getContext(), yogaClassList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(yogaClassAdapter);
    }
}
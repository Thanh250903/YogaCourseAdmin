package com.example.yogacourseadmin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

public class AddClassActivity extends AppCompatActivity {

    AutoCompleteTextView autocompletetextview;
    DBHelper dbHelper;
    ArrayList<Course> courseList;
    EditText teacher, date, comments;
    Button add_btn;
    ImageButton back_imgbtn;
    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbHelper = new DBHelper(this);

        autocompletetextview = findViewById(R.id.auto_complete_txt);
        teacher = findViewById(R.id.input_teacher);
        date = findViewById(R.id.input_date);
        comments = findViewById(R.id.input_comments);
        add_btn = findViewById(R.id.add_button);
        back_imgbtn = findViewById(R.id.close_btn);
        txtView = findViewById(R.id.text_add_class);

        courseList = dbHelper.getAllCourses();
        back_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Đóng activity hiện tại
            }
        });

        // Lấy Intent
        int classId = getIntent().getIntExtra("classId", -1); // -1 là giá trị mặc định nếu không tìm thấy

        if (classId != -1) {
            // Nếu classId không phải là -1, có nghĩa là đã nhận được dữ liệu
            txtView.setText(getIntent().getStringExtra("textEdit"));
            add_btn.setText(getIntent().getStringExtra("textBtnEdit"));
            loadYogaClassData(classId);
        }
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddClassActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Hiển thị ngày theo định dạng dd/MM/yyyy trên giao diện
                                String displayDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                date.setText(displayDate);

                                // Chuyển ngày sang định dạng yyyy-MM-dd để lưu vào SQLite
                                String saveDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                date.setTag(saveDate);  // Sử dụng tag để lưu ngày trong định dạng yyyy-MM-dd
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các trường EditText
                String Autocompletetextview = autocompletetextview.getText().toString();
                String Teacher = teacher.getText().toString();
                String Date = (String) date.getTag();
                String Comments = comments.getText().toString();

                if (Date == null || Date.isEmpty()) {
                    Toast.makeText(AddClassActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Kiểm tra xem các trường dữ liệu có hợp lệ không
                if (Teacher.isEmpty() || Date.isEmpty()) {
                    Toast.makeText(AddClassActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                String idString = Autocompletetextview.replaceAll("[^0-9]", ""); // Loại bỏ tất cả ký tự không phải số
                Integer CourseId = null;

                if (!idString.isEmpty()) {
                    CourseId = Integer.parseInt(idString); // Chuyển đổi chuỗi thành số nguyên nếu chuỗi không rỗng
                }

                Log.d("AddClassActivity", "Adding class with: classId=" + classId + ", Teacher=" + Teacher + ", Date=" + Date + ", Comments=" + Comments + ", CourseId=" + CourseId);

                // Gọi phương thức addCourse từ DBHelper
                boolean isAdded = dbHelper.addClass(classId, Teacher, Date, Comments, CourseId);

                if (isAdded) {
                    Toast.makeText(AddClassActivity.this, "Class added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddClassActivity.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Extract course names (or any other relevant property)
        ArrayList<String> courseNames = new ArrayList<>();
        for (Course course : courseList) {
            courseNames.add("Course "+course.getIdCourse());  // Assuming you're displaying the course type
        }

        // Create an ArrayAdapter and set it to AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.list_item_course, courseNames);

        autocompletetextview.setAdapter(adapter);
        autocompletetextview.setThreshold(1); //Khi người dùng nhập ít nhất 1 ký tự, danh sách gợi ý sẽ hiện ra.
    }
    private void loadYogaClassData(int yogaClassId) {
        // Lấy dữ liệu cho khóa học từ cơ sở dữ liệu
        YogaClass yogaClass = getYogaClassById(yogaClassId); // Bạn cần implement phương thức này trong DBHelper

        if (yogaClass != null) {
            // Thiết lập dữ liệu vào các trường
            autocompletetextview.setText("Course "+String.valueOf(yogaClass.getCourseId()));
            if(yogaClass.getCourseId() < 1){
                autocompletetextview.setText("");
            }
            teacher.setText(yogaClass.getTeacher());
            // Lấy ngày từ YogaClass theo định dạng yyyy-MM-dd
            String dbDate = yogaClass.getDate(); // Ngày từ cơ sở dữ liệu theo định dạng yyyy-MM-dd

            if (dbDate != null && !dbDate.isEmpty()) {
                // Tách chuỗi ngày yyyy-MM-dd thành các phần riêng biệt
                String[] dateParts = dbDate.split("-");

                if (dateParts.length == 3) {
                    // Chuyển đổi ngày từ yyyy-MM-dd sang dd/MM/yyyy
                    String displayDate = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
                    date.setText(displayDate);  // Hiển thị ngày theo định dạng dd/MM/yyyy
                    date.setTag(dbDate);  // Lưu lại ngày theo định dạng yyyy-MM-dd vào tag
                } else {
                    date.setText(""); // Nếu có lỗi định dạng, để trống
                    date.setTag(null); // Đảm bảo tag cũng null khi không có giá trị hợp lệ
                }
            } else {
                date.setText(""); // Nếu không có ngày, để trống
                date.setTag(null); // Đảm bảo tag cũng null khi không có giá trị hợp lệ
            }

            comments.setText(String.valueOf(yogaClass.getComments()));
        }
    }

    private YogaClass getYogaClassById(int classId) {
        // Gọi phương thức trong DBHelper để lấy thông tin khóa học
        DBHelper dbHelper = new DBHelper(this);
        return dbHelper.getClassById(classId); // Implement phương thức này trong DBHelper
    }
}
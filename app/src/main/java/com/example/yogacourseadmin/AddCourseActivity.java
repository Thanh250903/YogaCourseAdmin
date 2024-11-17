package com.example.yogacourseadmin;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.Calendar;

public class AddCourseActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    EditText timeofcourse, capacity, duration, priceperclass, description;
    RadioGroup typeclass;
    Button add_btn;
    ImageButton back_imgbtn;
    TextView txtView;
    Spinner dateoftheweek;
    ImageView imgCourse;
    Button btnSelectImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_course);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DBHelper(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dateoftheweek = findViewById(R.id.spinner_day_of_week);
        timeofcourse = findViewById(R.id.input_time);
        capacity = findViewById(R.id.input_capacity);
        duration = findViewById(R.id.input_duration);
        priceperclass = findViewById(R.id.input_price);
        description = findViewById(R.id.input_description);
        typeclass = findViewById(R.id.radio_group_class_type);
        txtView =findViewById(R.id.text_add);
        add_btn = findViewById(R.id.button_add);
        back_imgbtn= findViewById(R.id.imgbtn_back);

        back_imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });
        timeofcourse.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View view) {
                // Take current time
                Calendar canlender = Calendar.getInstance();
                int hour = canlender.get(Calendar.HOUR_OF_DAY);
                int minute = canlender.get(Calendar.MINUTE);

                // showtime by TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddCourseActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeofcourse.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, true);
                    timePickerDialog.show();
            }
        });

        int courseId = getIntent().getIntExtra("courseId", -1); // -1 là giá trị mặc định nếu không tìm thấy

        if (courseId != -1) {
            txtView.setText(getIntent().getStringExtra("textViewEdit"));
            add_btn.setText(getIntent().getStringExtra("textButtonEdit"));
            loadCourseData(courseId);
        }
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Lấy dữ liệu từ các trường EditText
                String dateOfWeek = dateoftheweek.getSelectedItem().toString();
                String timeOfCourse = timeofcourse.getText().toString();
                String capacityStr = capacity.getText().toString();
                String durationStr = duration.getText().toString();
                String pricePerClassStr = priceperclass.getText().toString();
                String courseDescription = description.getText().toString();

                // Lấy giá trị của radio button (dành cho loại lớp học)
                int selectedType = typeclass.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedType);
                String classType = selectedRadioButton.getText().toString();

                // Check data valid or not
                if (dateOfWeek.isEmpty() || timeOfCourse.isEmpty() || capacityStr.isEmpty() || durationStr.isEmpty() || pricePerClassStr.isEmpty()) {
                    Toast.makeText(AddCourseActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Chuyển capacity, duration và pricePerClass thành kiểu số
                int capacityInt = Integer.parseInt(capacityStr);
                int durationInt = Integer.parseInt(durationStr);
                double pricePerClassDouble = Double.parseDouble(pricePerClassStr);

                // Gọi phương thức addCourse từ DBHelper
                boolean isAdded = dbHelper.addCourse(courseId,dateOfWeek, timeOfCourse, capacityInt, durationInt, pricePerClassDouble, classType, courseDescription);

                if (isAdded) {
                    Toast.makeText(AddCourseActivity.this, "Course added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddCourseActivity.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadCourseData(int courseId) {
        // Lấy dữ liệu cho khóa học từ cơ sở dữ liệu
        Course course = getCourseById(courseId); // Bạn cần implement phương thức này trong DBHelper

        if (course != null) {
            // Thiết lập dữ liệu vào các trường
            String[] daysOfWeekArray = getResources().getStringArray(R.array.days_of_week); // Chuỗi ngày trong strings.xml
            int position = Arrays.asList(daysOfWeekArray).indexOf(course.getDateoftheweek());
            if (position >= 0) {
                dateoftheweek.setSelection(position);
            }
            timeofcourse.setText(course.getTimeofcourse());
            capacity.setText(String.valueOf(course.getCapacity()));
            duration.setText(String.valueOf(course.getDuration()));
            priceperclass.setText(String.valueOf(course.getPriceperclass()));
            description.setText(course.getDescription());
            // Thiết lập loại lớp học cho RadioGroup
            String classType = course.getTypeclass();
            switch (classType) {
                case "Flow Yoga":
                    typeclass.check(R.id.radio_flow_yoga);
                    break;
                case "Aerial Yoga":
                    typeclass.check(R.id.radio_aerial_yoga);
                    break;
                case "Family Yoga":
                    typeclass.check(R.id.radio_family_yoga);
                    break;
                default:
                    // Nếu không có loại lớp học nào phù hợp
                    typeclass.clearCheck(); // Hoặc có thể chọn một giá trị mặc định
                    break;
            }
        }
    }
    private Course getCourseById(int courseId) {
        // Gọi phương thức trong DBHelper để lấy thông tin khóa học
        DBHelper dbHelper = new DBHelper(this);
        return dbHelper.getCourseById(courseId); // Implement phương thức này trong DBHelper
    }
}
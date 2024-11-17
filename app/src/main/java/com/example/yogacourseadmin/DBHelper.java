package com.example.yogacourseadmin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class DBHelper extends SQLiteOpenHelper {

    FirebaseDatabase database;
    private Context context;
    // Database Info
    private static final String DATABASE_NAME = "YogaCourse.db";
    private static final int DATABASE_VERSION = 2;
    private final String DATABASE_URL = "https://yogacourseadmin-default-rtdb.firebaseio.com/";

    // Table Names
    private static final String TABLE_COURSE = "course";
    private static final String TABLE_CLASS = "class";

    // Course Table Columns
    private static final String COLUMN_COURSE_ID = "course_id";
    private static final String COLUMN_DATE_OF_WEEK = "dateoftheweek";
    private static final String COLUMN_TIME_OF_COURSE = "timeofcourse";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PRICE_PER_CLASS = "priceperclass";
    private static final String COLUMN_TYPE_CLASS = "typeclass";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_URL = "imageUrl";

    // Class Table Columns
    private static final String COLUMN_CLASS_ID = "class_id";
    private static final String COLUMN_TEACHER = "teacher";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_COMMENTS = "comments";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = FirebaseDatabase.getInstance(DATABASE_URL);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the course table
        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSE +
                "(" +
                COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATE_OF_WEEK + " TEXT," +
                COLUMN_TIME_OF_COURSE + " TEXT," +
                COLUMN_CAPACITY + " INTEGER," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_PRICE_PER_CLASS + " REAL," +
                COLUMN_TYPE_CLASS + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT" +
                ")";

        // Create the class table
        String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASS +
                "(" +
                COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TEACHER + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_COMMENTS + " TEXT," +
                COLUMN_COURSE_ID + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + ")" +
                ")";

        // Use sqLiteDatabase to execute SQL commands
        sqLiteDatabase.execSQL(CREATE_COURSE_TABLE);
        sqLiteDatabase.execSQL(CREATE_CLASS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        onCreate(db);
    }

    public boolean addCourse(int courseId,String dateoftheweek, String timeofcourse, int capacity, int duration, double priceperclass, String typeclass, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_DATE_OF_WEEK, dateoftheweek);
        cv.put(COLUMN_TIME_OF_COURSE, timeofcourse);
        cv.put(COLUMN_CAPACITY, capacity);
        cv.put(COLUMN_DURATION, duration);
        cv.put(COLUMN_PRICE_PER_CLASS, priceperclass);
        cv.put(COLUMN_TYPE_CLASS, typeclass);
        cv.put(COLUMN_DESCRIPTION, description);
        if (courseId == -1) {
            long result = db.insert(TABLE_COURSE, null, cv);

            // Kiểm tra xem có lưu được vào SQLite không
            if (result == -1) {
                return false; // Nếu lưu thất bại, trả về false
            } else {
                // Nếu lưu thành công, tiếp tục đẩy dữ liệu lên Firebase bằng HashMap
                HashMap<String, Object> courseData = new HashMap<>();
                // Chuyển đổi ContentValues sang HashMap
                for (String key : cv.keySet()) {
                    courseData.put(key, cv.get(key));
                }  // Gọi phương thức đồng bộ dữ liệu lên Firebase
                addCourseToFirebase(result,courseData);
                return true;
            }
        }else { // Nếu courseId không phải -1, tức là cập nhật
            int rowsAffected = db.update(TABLE_COURSE, cv, COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
            // Kiểm tra xem có cập nhật thành công không
            if (rowsAffected > 0) {
                // Cập nhật dữ liệu lên Firebase
                HashMap<String, Object> courseData = new HashMap<>(); // Lấy tất cả dữ liệu từ cv
                for (String key : cv.keySet()) {
                    courseData.put(key, cv.get(key));
                }
                updateCourseInFirebase(courseId, courseData); // Gọi phương thức cập nhật dữ liệu lên Firebase
                return true;
            } else {
                return false; // Nếu không có dòng nào bị ảnh hưởng, trả về false
            }
        }

    }

    private void addCourseToFirebase(long courseId,HashMap<String, Object> courseData) {

        try {// Lấy tham chiếu tới Firebase Realtime Database
        Log.d("Firebase", "Preparing to add course to Firebase with ID" + courseId);
        DatabaseReference myRef = database.getReference("course");
            myRef.child(String.valueOf(courseId)).setValue(courseData)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Course added to Firebase successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Error adding course to Firebase", e));
        } catch (Exception e) {
            Log.e("error", "Error adding course from Firebase", e);
        }
    }
    private void updateCourseInFirebase(int courseId, HashMap<String, Object> courseData) {
        try {
            DatabaseReference myRef = database.getReference("course");
            myRef.child(String.valueOf(courseId)).updateChildren(courseData); // Cập nhật dữ liệu khóa học
        } catch (Exception e) {
            Log.e("error", "Error updating course from Firebase", e);
        }
    }
    public boolean deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Xóa khóa học theo courseId từ SQLite
        int rowsDeleted = db.delete(TABLE_COURSE, COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});

        // Kiểm tra xem có xóa được không
        if (rowsDeleted > 0) {
            // Nếu xóa thành công, tiếp tục xóa từ Firebase
            deleteCourseFromFirebase(courseId);
            return true; // Trả về true nếu đã xóa thành công
        }

        return false; // Trả về false nếu không xóa được
    }

    // Phương thức xóa khóa học từ Firebase
    private void deleteCourseFromFirebase(int courseId) {
        try {
            DatabaseReference myRef = database.getReference("course");
            myRef.child(String.valueOf(courseId)).removeValue();
        } catch (Exception e) {
            Log.e("error", "Error deleting course from Firebase", e);
        }
    }

    // Phương thức để lấy tất cả khóa học từ SQLite
    public ArrayList<Course> getAllCourses() {
        ArrayList<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn tất cả các hàng từ bảng "course"
        String query = "SELECT * FROM " + TABLE_COURSE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Lấy dữ liệu từ từng hàng
                    int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));
                    String dateOfWeek = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_WEEK));
                    String timeOfCourse = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME_OF_COURSE));
                    int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                    double pricePerClass = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_PER_CLASS));
                    String typeClass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE_CLASS));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL));

                    // Tạo đối tượng Course và thêm vào danh sách
                    Course course = new Course(courseId,dateOfWeek, timeOfCourse, capacity, duration, pricePerClass, typeClass, description,imageUrl);
                    courseList.add(course);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return courseList;
    }
    public Course getCourseById(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSE, null, COLUMN_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Lấy dữ liệu từ cursor
                String dateOfWeek = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_WEEK));
                String timeOfCourse = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME_OF_COURSE));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                double pricePerClass = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE_PER_CLASS));
                String typeClass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE_CLASS));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL));

                // Tạo đối tượng Course
                Course course = new Course(courseId, dateOfWeek, timeOfCourse, capacity, duration, pricePerClass, typeClass, description,imageUrl );
                cursor.close();
                return course;
            }
            cursor.close();
        }
        return null; // Trả về null nếu không tìm thấy khóa học
    }
    public boolean addClass(int classId, String teacher, String date, String comments, Integer courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TEACHER, teacher);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_COURSE_ID, courseId);

        if (classId == -1) {
            long result = db.insert(TABLE_CLASS, null, cv);
            if (result == -1) {
                Log.e("DBHelper", "Failed to insert class into SQLite");
                return false;
            } else {
                Log.d("DBHelper", "Class inserted into SQLite with ID: " + result);
                HashMap<String, Object> classData = new HashMap<>();
                for (String key : cv.keySet()) {
                    classData.put(key, cv.get(key));
                }
                addClassToFirebase(result, classData);
                return true;
            }
        } else {
            int rowsAffected = db.update(TABLE_CLASS, cv, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(classId)});
            if (rowsAffected > 0) {
                Log.d("DBHelper", "Class updated in SQLite with ID: " + classId);
                HashMap<String, Object> classData = new HashMap<>();
                for (String key : cv.keySet()) {
                    classData.put(key, cv.get(key));
                }
                updateClassInFirebase(classId, classData);
                return true;
            } else {
                Log.e("DBHelper", "Failed to update class in SQLite");
                return false;
            }
        }
    }

    private void addClassToFirebase(long classId, HashMap<String, Object> classData) {
        try {
            Log.d("Firebase", "Preparing to add class to Firebase with ID: " + classId);
            DatabaseReference myRef = database.getReference("class");
            myRef.child(String.valueOf(classId)).setValue(classData)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Class added successfully to Firebase"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Error adding class to Firebase", e));
        } catch (Exception e) {
            Log.e("DBHelper", "Error adding class to Firebase", e);
        }
    }

    private void updateClassInFirebase(int classId, HashMap<String, Object> classData) {
        try {
            Log.d("Firebase", "Preparing to update class in Firebase with ID: " + classId);
            DatabaseReference myRef = database.getReference("class");
            myRef.child(String.valueOf(classId)).updateChildren(classData)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Class updated successfully in Firebase"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Error updating class in Firebase", e));
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating class in Firebase", e);
        }
    }

    public boolean deleteClass(int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CLASS, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(classId)});
        if (rowsDeleted > 0) {
            deleteClassFromFirebase(classId);
            return true;
        }
        return false;
    }

    private void deleteClassFromFirebase(int classId) {
        try {
            DatabaseReference myRef = database.getReference("class");
            myRef.child(String.valueOf(classId)).removeValue();
        } catch (Exception e) {
            Log.e("error", "Error deleting class from Firebase", e);
        }
    }
    public ArrayList<YogaClass> getAllClasses() {
        ArrayList<YogaClass> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CLASS;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int classId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID));
                    String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    String comments = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS));
                    int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));

                    YogaClass yogaClass = new YogaClass(classId, teacher, date, comments, courseId);
                    classList.add(yogaClass);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return classList;
    }

    public YogaClass getClassById(int classId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASS, null, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(classId)}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));

                YogaClass yogaClass = new YogaClass(classId, teacher, date, comments, courseId);
                cursor.close();
                return yogaClass;
            }
            cursor.close();
        }
        return null;
    }
    public ArrayList<YogaClass> getClassesByCourseId(int courseId) {
        ArrayList<YogaClass> classList = new ArrayList<>(); // Khai báo và khởi tạo bằng ArrayList
        SQLiteDatabase db = this.getReadableDatabase();

        // Thực hiện truy vấn để lấy dữ liệu từ bảng lớp
        String query = "SELECT * FROM " + TABLE_CLASS + " WHERE " + COLUMN_COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});

        // Kiểm tra dữ liệu và thêm vào classList
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Tạo một đối tượng YogaClass từ dữ liệu trong cursor
                    int classId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID));
                    String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    String comments = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS));
                    YogaClass yogaClass = new YogaClass(classId, teacher, date, comments, courseId);
                    // Thêm đối tượng vào classList
                    classList.add(yogaClass);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return classList; // Trả về danh sách kiểu ArrayList
    }
    public ArrayList<YogaClass> getClassesByTeacher(String teacherName) {
        ArrayList<YogaClass> yogaClassList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CLASS + " WHERE " + COLUMN_TEACHER + " LIKE ?";

        // Sử dụng `%` để tìm kiếm chuỗi con
        Cursor cursor = db.rawQuery(query, new String[] {"%" + teacherName + "%"});

        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu từng lớp học từ cơ sở dữ liệu
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID));
                String teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String comments = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENTS));
                int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID));

                // Tạo đối tượng YogaClass và thêm vào danh sách
                YogaClass yogaClass = new YogaClass(id, teacher, date, comments, courseId);
                yogaClassList.add(yogaClass);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return yogaClassList;
    }

}


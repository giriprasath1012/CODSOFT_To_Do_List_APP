package com.example.to_do_list;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class Add_Task extends AppCompatActivity {

    int Year;
    int month;
    int Date;
    int hour;
    int Minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TextInputEditText titleEditText = findViewById(R.id.titleEditText);
        TextView dateTextView = findViewById(R.id.dateTextView);
        ImageView calendarImageView = findViewById(R.id.calendarImageView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        ImageView clockImageView = findViewById(R.id.clockImageView);
        TextInputEditText contentEditText = findViewById(R.id.contentEditText);
        Button saveButton = findViewById(R.id.saveButton);

        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Add_Task.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        Calendar currentDate = Calendar.getInstance();

                        if (selectedDate.before(currentDate)) {
                            Toast.makeText(Add_Task.this, "Please select a date in the future", Toast.LENGTH_SHORT).show();
                        } else {
                            Year = year;
                            month = monthOfYear;
                            Date = dayOfMonth;
                            String selectedDateStr = Year + "/" + (month + 1) + "/" + Date;
                            dateTextView.setText(selectedDateStr);
                        }
                    }
                }, currentYear, currentMonth, currentDay);

                datePickerDialog.show();
            }
        });

        clockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar currentTime = Calendar.getInstance();

                TimePickerDialog timePickerDialog = new TimePickerDialog(Add_Task.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);

                            hour = hourOfDay;
                            Minute = minute;
                            String selectedTimeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                            timeTextView.setText(selectedTimeStr);

                    }
                }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String date = dateTextView.getText().toString().trim();
                String time = timeTextView.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time))
                {
                    Toast.makeText(getApplicationContext(), "Please enter title, date, and time", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Database db = new Database(getApplicationContext(), "todolist", null, 1);

                   // Toast.makeText(getApplicationContext(), "Title " + title + " Date " + date + " Time " + time + " Content " + content, Toast.LENGTH_SHORT).show();

                    db.insertitem(title, date, time, content);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Year, month, Date, hour, Minute);
                    calendar.set(Calendar.SECOND, 0);

                    long currentTimeMillis = System.currentTimeMillis();
                    long timeDifference = calendar.getTimeInMillis() - currentTimeMillis;

                    Intent reminderIntent = new Intent(Add_Task.this, ReminderBroadcastReceiver.class);
                    reminderIntent.putExtra("title", title);
                    reminderIntent.putExtra("content", content);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(Add_Task.this, 0, reminderIntent, PendingIntent.FLAG_IMMUTABLE);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeMillis + timeDifference, pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis + timeDifference, pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTimeMillis + timeDifference, pendingIntent);
                    }

                    startActivity(new Intent(Add_Task.this, MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Task Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

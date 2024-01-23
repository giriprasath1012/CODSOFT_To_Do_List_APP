package com.example.to_do_list;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Edit_Task extends AppCompatActivity {

    int Year,month,Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Intent intent=getIntent();


        String id=intent.getStringExtra("id");
        String title=intent.getStringExtra("title");
        String content=intent.getStringExtra("content");
        String date=intent.getStringExtra("date");

        //Toast.makeText(getApplicationContext(),"Id "+id+"Title "+title+"Content "+content+"Date "+date,Toast.LENGTH_SHORT).show();

        EditText edtitle=findViewById(R.id.edtitle);
        EditText edcontent=findViewById(R.id.edcontent);
        TextView eddate=findViewById(R.id.eddate);

        //edtitle.setKeyListener(null);

        edtitle.setText(title);
        eddate.setText(date);
        edcontent.setText(content);



        eddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Edit_Task.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        Calendar currentDate = Calendar.getInstance();

                        if (selectedDate.before(currentDate)) {
                            Toast.makeText(getApplicationContext(), "Please select a date in the future", Toast.LENGTH_SHORT).show();
                        } else {
                            Year = year;
                            month = monthOfYear;
                            Date = dayOfMonth;
                            String selectedDateStr = Year + "/" + (month + 1) + "/" + Date;
                            eddate.setText(selectedDateStr);
                        }
                    }
                }, currentYear, currentMonth, currentDay);

                datePickerDialog.show();
            }
        });

        Button update=findViewById(R.id.updateButton);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedcontent=edcontent.getText().toString();
                String updatedtitle=edtitle.getText().toString();
                String updateddate=eddate.getText().toString();

                Database db = new Database(getApplicationContext(), "todolist", null, 1);
                db.updateitem(Integer.parseInt(id),updatedtitle,updatedcontent,updateddate);
                Toast.makeText(getApplicationContext(), "Task Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Edit_Task.this, MainActivity.class));
            }
        });


    }
}
package com.example.to_do_list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String[][] item_details = {};
    HashMap<String, String> item;
    ArrayList<HashMap<String, String>> list;
    SimpleAdapter sa;
    ListView lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addtask = findViewById(R.id.addTaskButton);

        addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Add_Task.class));
            }
        });

        updateView();

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent it=new Intent(MainActivity.this,Edit_Task.class);
                it.putExtra("id",item_details[i][0]);
                it.putExtra("title",item_details[i][1]);
                it.putExtra("content",item_details[i][4]);
                it.putExtra("date",item_details[i][2]);
                startActivity(it);
            }
        });
    }

    public void onDeleteButtonClick(View view) {
        int position = lst.getPositionForView((View) view.getParent());
        String clickedItemId = list.get(position).get("id");
        deleteItem(clickedItemId);
    }

    private void deleteItem(String itemId) {
        Database db = new Database(getApplicationContext(), "todolist", null, 1);
        db.deleteitem(Integer.parseInt(itemId));
        Toast.makeText(getApplicationContext(), "Task Deleted", Toast.LENGTH_SHORT).show();
        updateView();
    }

    public void onupdatestatus(View view) {
        int position = lst.getPositionForView((View) view.getParent());
        String clickedItemId = list.get(position).get("id");
        updateStatusItem(clickedItemId);
    }

    private void updateStatusItem(String itemId) {
        Database db = new Database(getApplicationContext(), "todolist", null, 1);
        db.updatestatus(Integer.parseInt(itemId));
        Toast.makeText(getApplicationContext(), "Task Completed", Toast.LENGTH_SHORT).show();
        updateView();
    }

    private void updateView() {
        Database db = new Database(getApplicationContext(), "todolist", null, 1);
        ArrayList dbData = db.getallitem();
        lst = findViewById(R.id.ListView);

        item_details = new String[dbData.size()][];
        for (int i = 0; i < item_details.length; i++) {
            item_details[i] = new String[7];
            String arrData = dbData.get(i).toString();
            String[] strData = arrData.split(java.util.regex.Pattern.quote("$"));
            item_details[i][0] = strData[0];
            item_details[i][1] = strData[1];
            item_details[i][2] = strData[2];
            item_details[i][3] = strData[3];
            item_details[i][4] = strData[4];
            item_details[i][5] = strData[5];
            item_details[i][6] = strData[6];
        }

        list = new ArrayList<>();
        for (int i = 0; i < item_details.length; i++) {



            item = new HashMap<>();
            item.put("id", item_details[i][0]);
            item.put("title", item_details[i][1]);

            String month = "", day = "", year = "";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

            try {
                Date date = dateFormat.parse(item_details[i][2]);

                // Extract month, day, and year
                if (date != null) {
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                    month = monthFormat.format(date);

                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                    day = dayFormat.format(date);

                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                    year = yearFormat.format(date);
                } else {
                    // Handle the case where date parsing fails
                    // This might happen if the input date string is not in the expected format
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            item.put("day", day);
            item.put("month", month);
            item.put("year", year);
            item.put("time", item_details[i][3]);
            item.put("content", item_details[i][4]);
            item.put("createdate", item_details[i][5]);
            item.put("status", item_details[i][6]);
            list.add(item);
        }

        sa = new SimpleAdapter(this, list,
                R.layout.task_items,
                new String[]{"title", "content", "createdate", "status", "day", "month", "year"},
                new int[]{R.id.titleTextView, R.id.contentTextView, R.id.dateInitTextView, R.id.statusTextView, R.id.dateTextView, R.id.monthTextView, R.id.yearTextView});
        lst.setAdapter(sa);
        sa.notifyDataSetChanged();
    }


}

package com.example.db_androidstudio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    ListView listView;
    GridLayout filterButtons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBHelper helper = new DBHelper(this);
        try {
            database = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cursor cursor1 = database.rawQuery("SELECT * FROM categories" , null);
        cursor1.moveToFirst();
        filterButtons = findViewById(R.id.filters);

        Button filter_button_all = new Button(this);
        filter_button_all.setText("Все");
        filter_button_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllItems();
            }
        });
        filterButtons.addView(filter_button_all);

        while (!cursor1.isAfterLast()) {
            String id = cursor1.getString(0);
            String category_name = cursor1.getString(1);

            Button filter_button = new Button(this);
            filter_button.setText(category_name);
            filter_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<HashMap<String,String >> items = new ArrayList<>();
                    HashMap<String,String> item;
                    Cursor cursor3 = database.rawQuery("SELECT item_name, category_name, description, price " +
                            "FROM items JOIN categories " +
                            "ON items.category_id = categories.id WHERE category_name = ?", new String[]{category_name});
                    cursor3.moveToFirst();
                    while (!cursor3.isAfterLast()) {
                        item = new HashMap<>();
                        item.put("item_name", cursor3.getString(0));
                        item.put("category_name", cursor3.getString(1));
                        item.put("description", cursor3.getString(2));
                        item.put("price", cursor3.getString(3) + " ₽.");
                        items.add(item);
                        cursor3.moveToNext();
                    }
                    cursor3.close();

                    SimpleAdapter adapter = new SimpleAdapter(
                            MainActivity.this,
                            items,
                            R.layout.listview_item,
                            new String[]{"item_name", "category_name", "description", "price"},
                            new int[]{R.id.itemName,R.id.itemCategory,R.id.itemDesc,R.id.itemPrice}
                    );

                    listView = findViewById(R.id.listview_item);
                    listView.setAdapter(adapter);
                }
            });

            filterButtons.addView(filter_button);

            cursor1.moveToNext();
        }
        cursor1.close();

        getAllItems();
    }

    private void getAllItems() {
        ArrayList<HashMap<String,String >> items = new ArrayList<>();
        HashMap<String,String> item;
        Cursor cursor2 = database.rawQuery("SELECT item_name, category_name, description, price " +
                "FROM items JOIN categories " +
                "ON items.category_id = categories.id" , null);
        cursor2.moveToFirst();
        while (!cursor2.isAfterLast()) {
            item = new HashMap<>();
            item.put("item_name", cursor2.getString(0));
            item.put("category_name", cursor2.getString(1));
            item.put("description", cursor2.getString(2));
            item.put("price", cursor2.getString(3) + " ₽.");
            items.add(item);
            cursor2.moveToNext();
        }
        cursor2.close();

        SimpleAdapter adapter2 = new SimpleAdapter(
                this,
                items,
                R.layout.listview_item,
                new String[]{"item_name", "category_name", "description", "price"},
                new int[]{R.id.itemName,R.id.itemCategory,R.id.itemDesc,R.id.itemPrice}
        );

        listView = findViewById(R.id.listview_item);
        listView.setAdapter(adapter2);
    }
}
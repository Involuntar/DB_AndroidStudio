package com.example.db_androidstudio;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
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

    private static boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                break;
        }
        return false;
    }

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

        GridLayout.LayoutParams extraParams = new GridLayout.LayoutParams();
        extraParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        extraParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        extraParams.columnSpec = GridLayout.spec(0, 1f);
        extraParams.rowSpec = GridLayout.spec(0);
        extraParams.setMargins(10, 10, 10, 10);

        GradientDrawable shape = new GradientDrawable();
        GradientDrawable shapeAll = new GradientDrawable();

        shape.setCornerRadius(16f); // скругление
        shapeAll.setCornerRadius(16f);

        shape.setColor(Color.parseColor("#FF6200EE")); // фон
        shapeAll.setColor(Color.parseColor("#FE0000"));

        filter_button_all.setTextColor(Color.WHITE);
        filter_button_all.setOnTouchListener(MainActivity::onTouch);
        filter_button_all.setBackground(shapeAll);
        filter_button_all.setLayoutParams(extraParams);
        filterButtons.addView(filter_button_all);

        int buttonsCount = 1;
        while (!cursor1.isAfterLast()) {
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
                        item.put("category_name", "Категория: " + cursor3.getString(1));
                        item.put("description", cursor3.getString(2) != null ? "Описание:\n" + cursor3.getString(2) : "Описание:\nНет Описания");
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

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT; // равная ширина
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(buttonsCount % 3, 1f);
            params.rowSpec = GridLayout.spec(buttonsCount / 3);
            params.setMargins(10, 10, 10, 10);

            filter_button.setTextColor(Color.WHITE);
            filter_button.setLayoutParams(params);
            filter_button.setBackground(shape);
            filter_button.setOnTouchListener(MainActivity::onTouch);

            filterButtons.addView(filter_button);

            buttonsCount += 1;
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
            item.put("category_name", "Категория: " + cursor2.getString(1));
            item.put("description", cursor2.getString(2) != null ? "Описание:\n" + cursor2.getString(2) : "Описание:\nНет Описания");
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
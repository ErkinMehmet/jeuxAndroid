package com.np.brickbreaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private RecyclerView recyclerView;
    TextView remainingPointsText;
    private Button prevButton, nextButton,returnButton;
    private StoreAdapter adapter;
    private List<StoreItem> items = new ArrayList<>();
    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 6;
    private int points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        remainingPointsText = findViewById(R.id.remainingPointsText);
        recyclerView = findViewById(R.id.recyclerView);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        returnButton=findViewById(R.id.returnButton);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        loadItemsFromJson();

        prevButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
            }
        });

        nextButton.setOnClickListener(v -> {
            if ((currentPage + 1) * ITEMS_PER_PAGE < items.size()) {
                currentPage++;
                updatePage();
            }
        });

        returnButton.setOnClickListener(v -> {
            finish();// since main activity was not closed, we can just finish the store act to go back
        });

        updatePage();

        points = getIntent().getIntExtra("points", 0);
        remainingPointsText.setText("Remaining Points: " + points);

    }

    private void loadItemsFromJson() {
        try {
            InputStream is = getAssets().open("shop/bgs.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray backgrounds = jsonObject.getJSONArray("backgrounds");

            for (int i = 0; i < backgrounds.length(); i++) {
                JSONObject item = backgrounds.getJSONObject(i);
                items.add(new StoreItem(
                        item.getString("id"),
                        item.getString("name"),
                        item.getString("description"),
                        item.getInt("cost"),
                        item.getString("image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePage() {
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, items.size());
        List<StoreItem> pageItems = items.subList(start, end);

        if (adapter == null) {
            adapter = new StoreAdapter(pageItems, this::buyItem);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateItems(pageItems);
        }
    }

    private void buyItem(StoreItem item) {
        SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        int points = prefs.getInt("points", 0);

        if (points >= item.getCost()) {
            prefs.edit().putInt("points", points - item.getCost()).apply();
            Toast.makeText(this, "Purchased " + item.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not enough points", Toast.LENGTH_SHORT).show();
        }
    }
}

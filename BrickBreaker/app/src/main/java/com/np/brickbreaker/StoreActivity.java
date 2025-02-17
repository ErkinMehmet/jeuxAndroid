package com.np.brickbreaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.np.brickbreaker.utils.StoreUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    FileInputStream fis;
    byte[] buffer;
    String json;
    JSONObject jsonObject;
    JSONArray backgrounds;

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

        try {
            File file = new File(getFilesDir(), "shop/bgs2.json");// internal storage
            if (!file.exists()) {
                try {
                    InputStream is = getAssets().open("shop/bgs_init.json"); // assets
                    StoreUtils.copyOrReplaceBgStorage(file,is);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fis = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();

            json = new String(buffer, "UTF-8");
            Log.e("info",json);
            Log.e("FilePath", "Absolute path of the file: " + file.getAbsolutePath());

            jsonObject = new JSONObject(json);
            backgrounds = jsonObject.getJSONArray("backgrounds");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
            Intent resultIntent = new Intent();
            resultIntent.putExtra("points", points);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        updatePage();

        points = getIntent().getIntExtra("points", 0);
        remainingPointsText.setText("Remaining Points: " + points);

        sharedPreferences = getSharedPreferences("GameData", MODE_PRIVATE);
        editor = sharedPreferences.edit();



    }

    private void loadItemsFromJson() {

        try {
            for (int i = 0; i < backgrounds.length(); i++) {
                JSONObject item = backgrounds.getJSONObject(i);
                items.add(new StoreItem(
                        item.getString("id"),
                        item.getString("name"),
                        item.getString("description"),
                        item.getInt("cost"),
                        item.getString("image"),
                        item.getBoolean("purchased")
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

    private void buyItem(StoreItem item) throws JSONException, IOException {
        if (item.purchased) {
            Toast.makeText(this, "This item has already been purchased!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (points >= item.getCost()) {

            for (int i = 0; i < backgrounds.length(); i++) {
                JSONObject jsonItem = backgrounds.getJSONObject(i);
                if (jsonItem.getString("id").equals(item.getId())) {
                    jsonItem.put("purchased", true); // Update the 'purchased' field
                    backgrounds.put(i, jsonItem);
                    break;
                }
            }
            String updatedJson = jsonObject.toString();
            Log.d("StoreActivity", "Updated JSON: " + updatedJson);
            File dir = new File(getFilesDir(), "shop");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "bgs2.json");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(updatedJson.getBytes());
                Log.d("StoreActivity", "File written successfully: " + file.getAbsolutePath());
            }

            points -= item.getCost();
            item.purchased=true;
            editor.putInt("points", points).apply();
            editor.apply();
            remainingPointsText.setText("Remaining Points: " + points);
            Toast.makeText(this, "Purchased " + item.getName(), Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Not enough points", Toast.LENGTH_SHORT).show();
        }
    }
}

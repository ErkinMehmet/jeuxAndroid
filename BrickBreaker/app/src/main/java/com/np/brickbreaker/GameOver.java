package com.np.brickbreaker;

import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {
    TextView tvPoints;
    ImageView ivNewHighest;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        ivNewHighest=findViewById(R.id.ivNewHighest);
        tvPoints=findViewById(R.id.tvPoints);
        int points=getIntent().getExtras().getInt("points");
        if (points==240) {
            ivNewHighest.setVisibility(GameView.VISIBLE);
        }
        tvPoints.setText(""+points);

        Drawable drawable = getResources().getDrawable(R.drawable.bg1);
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.setTileModeX(Shader.TileMode.MIRROR);  // Stretch horizontally
            bitmapDrawable.setTileModeY(Shader.TileMode.MIRROR);  // REPEAT TO REPEAT
        }
        LinearLayout layout = findViewById(R.id.game_over_layout);
        layout.setBackground(drawable);
    }

    public void restart(View view) {
        Intent intent=new Intent(GameOver.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }
}

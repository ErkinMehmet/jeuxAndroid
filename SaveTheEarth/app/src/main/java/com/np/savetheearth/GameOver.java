package com.np.savetheearth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {

    TextView tvPoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        tvPoints=findViewById(R.id.tvPoints);
        int points=getIntent().getExtras().getInt("points");
        tvPoints.setText(""+points);
    }

    public void restart(View view){
        Intent intent=new Intent(GameOver.this,GameActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }


}

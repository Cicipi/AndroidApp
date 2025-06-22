package com.example.lastone;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button settingsButton;
    private String difficulty = "easy"; // По умолчанию легкий уровень

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Обработка нажатия кнопки настроек
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (difficulty) {
                    case "easy":
                        difficulty = "medium";
                        settingsButton.setText(R.string.medium);
                        break;
                    case "medium":
                        difficulty = "hard";
                        settingsButton.setText(R.string.hard);
                        break;
                    case "hard":
                        difficulty = "easy";
                        settingsButton.setText(R.string.easy);
                        break;
                }
            }
        });

        // Обработка нажатия кнопки старта
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("difficulty", difficulty);
                startActivity(intent);
            }
        });
    }
}
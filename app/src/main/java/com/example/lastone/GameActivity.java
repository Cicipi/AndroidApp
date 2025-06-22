package com.example.lastone;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView exampleTextView;
    private ProgressBar timerProgressBar;
    private TextView timerTextView;
    private TextView scoreTextView;
    private Button[] answerButtons;
    private Button menuButton;

    private String currentExample;
    private int correctAnswer;
    private int timeLeft = 10;
    private CountDownTimer timer;
    private String difficulty;
    private final Random random = new Random();
    private int score = 0;
    private boolean gameActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Инициализация UI элементов
        exampleTextView = findViewById(R.id.exampleTextView);
        timerProgressBar = findViewById(R.id.timerProgressBar);
        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        menuButton = findViewById(R.id.menuButton);

        answerButtons = new Button[]{
                findViewById(R.id.answerButton1),
                findViewById(R.id.answerButton2),
                findViewById(R.id.answerButton3),
                findViewById(R.id.answerButton4)
        };

        // Назначаем обработчик клика для всех кнопок ответов
        for (Button button : answerButtons) {
            button.setOnClickListener(this);
        }

        // Получаем уровень сложности
        difficulty = getIntent().getStringExtra("difficulty");
        if (difficulty == null) difficulty = "easy";

        // Настройка кнопки меню
        menuButton.setVisibility(View.GONE);
        menuButton.setOnClickListener(v -> returnToMenu());

        // Начало игры
        startNewGame();
    }

    @Override
    public void onClick(View v) {
        Log.d("GameActivity", "Button clicked");
        if (!gameActive) return;

        for (int i = 0; i < answerButtons.length; i++) {
            if (v.getId() == answerButtons[i].getId()) {
                checkAnswer(i);
                return;
            }
        }
    }

    private void startNewGame() {
        score = 0;
        gameActive = true;
        updateScore();
        generateNewExample();
        startTimer();

        // Активируем все кнопки ответов
        for (Button button : answerButtons) {
            button.setEnabled(true);
            button.setAlpha(1f); // Убедимся, что кнопки не прозрачные
        }

        menuButton.setVisibility(View.GONE);
    }

    private void updateScore() {
        scoreTextView.setText("Score: " + score);
    }

    private void generateNewExample() {
        String[] result;
        switch (difficulty) {
            case "medium":
                result = generateMediumExample();
                break;
            case "hard":
                result = generateHardExample();
                break;
            default:
                result = generateEasyExample();
        }

        currentExample = result[0];
        correctAnswer = Integer.parseInt(result[1]);
        exampleTextView.setText(currentExample);

        // Установка вариантов ответов
        List<Integer> answers = generateAnswers(correctAnswer);
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(String.valueOf(answers.get(i)));
        }
    }

    private String[] generateEasyExample() {
        int num1 = random.nextInt(20) + 1;
        int num2 = random.nextInt(20) + 1;
        String operator = random.nextBoolean() ? "+" : "-";
        int answer = operator.equals("+") ? num1 + num2 : num1 - num2;
        return new String[]{num1 + " " + operator + " " + num2, String.valueOf(answer)};
    }

    private String[] generateMediumExample() {
        int num1 = random.nextInt(15) + 1;
        int num2 = random.nextInt(15) + 1;
        String operator;
        int answer;

        switch (random.nextInt(3)) {
            case 0: operator = "+"; answer = num1 + num2; break;
            case 1: operator = "-"; answer = num1 - num2; break;
            default: operator = "*"; answer = num1 * num2;
        }
        return new String[]{num1 + " " + operator + " " + num2, String.valueOf(answer)};
    }

    private String[] generateHardExample() {
        int num1 = random.nextInt(12) + 1;
        int num2 = random.nextInt(12) + 1;
        String operator;
        int answer;

        switch (random.nextInt(4)) {
            case 0: operator = "+"; answer = num1 + num2; break;
            case 1: operator = "-"; answer = num1 - num2; break;
            case 2: operator = "*"; answer = num1 * num2; break;
            default:
                operator = "/";
                int product = num1 * num2;
                return new String[]{product + " " + operator + " " + num1, String.valueOf(num2)};
        }
        return new String[]{num1 + " " + operator + " " + num2, String.valueOf(answer)};
    }

    private List<Integer> generateAnswers(int correctAnswer) {
        List<Integer> answers = new ArrayList<>();
        answers.add(correctAnswer);

        while (answers.size() < 4) {
            int offset;
            switch (difficulty) {
                case "easy": offset = random.nextInt(5) + 1; break;
                case "medium": offset = random.nextInt(10) + 1; break;
                default: offset = random.nextInt(15) + 1;
            }
            int wrongAnswer = correctAnswer + (random.nextBoolean() ? offset : -offset);

            if (wrongAnswer != correctAnswer && !answers.contains(wrongAnswer)) {
                answers.add(wrongAnswer);
            }
        }

        Collections.shuffle(answers);
        return answers;
    }

    private void startTimer() {
        timeLeft = 10;
        timerProgressBar.setProgress(100);
        timerTextView.setText("Time left: " + timeLeft);

        if (timer != null) timer.cancel();

        timer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000);
                timerTextView.setText("Time left: " + timeLeft);
                timerProgressBar.setProgress((int) (millisUntilFinished / 100));
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                timerTextView.setText("Time's up!");
                handleWrongAnswer();
            }
        }.start();
    }

    private void checkAnswer(int selectedIndex) {
        if (!gameActive) return;

        int selectedAnswer = Integer.parseInt(answerButtons[selectedIndex].getText().toString());
        if (selectedAnswer == correctAnswer) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer();
        }
    }

    private void handleCorrectAnswer() {
        if (timer != null) timer.cancel();

        score++;
        updateScore();
        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> {
            if (gameActive) {
                generateNewExample();
                startTimer();
            }
        }, 1000);
    }

    private void handleWrongAnswer() {
        gameActive = false;
        if (timer != null) timer.cancel();

        Toast.makeText(this, "Game Over! Score: " + score, Toast.LENGTH_LONG).show();
        endGame();
    }

    private void endGame() {
        menuButton.setVisibility(View.VISIBLE);
        for (Button button : answerButtons) {
            button.setEnabled(false);
            button.setAlpha(0.5f); // Визуально показываем неактивные кнопки
        }
    }

    private void returnToMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
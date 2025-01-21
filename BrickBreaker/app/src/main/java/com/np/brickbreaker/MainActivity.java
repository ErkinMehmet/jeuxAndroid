package com.np.brickbreaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.np.brickbreaker.models.GameState;

public class MainActivity extends AppCompatActivity {

    private boolean isGameRunning=false;
    private static GameView gameView;
    private FrameLayout gameContainer;
    private ImageButton restartButton, resumeButton, pauseButton, quitButton, menuButton,saveButton,loadButton;
    private Bitmap restartIcon, resumeIcon, pauseIcon, quitIcon, menuIcon,saveIcon,loadIcon;
    private SharedPreferences gameDataSharedPreferences;
    private SharedPreferences.Editor gameDataeditor;
    TextView btnLoadGame;

    private String[] slotOptions = {"Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Show the App Open Ad as soon as the app is opened
        MyApp app = (MyApp) getApplicationContext();
        app.showAppOpenAdIfAvailable();
        gameDataSharedPreferences = getSharedPreferences("GameData", MODE_PRIVATE);
        gameDataeditor = gameDataSharedPreferences.edit();

        // = findViewById(R.id.btnLoadGame);
        /*btnLoadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("info","i am clicking the load from main");
                loadGame(view, 1);
            }
        });*/



    }
    @Override
    protected void onResume() {
        super.onResume();
        MyApp app = (MyApp) getApplicationContext();
        app.showAppOpenAdIfAvailable();
    }

    public void loadButtonClick(View view){
        Log.e("info","i am clicking the load from main");
        if (gameView !=null) {
            startGame(null);
            loadGame(view, 2);
        } else {
            loadGame(view, 1);
        }

    }

    public void startGame(View view) {
        initiateGame(1);
    }

    public void initiateGame(int option){
        if (option==1) {
            // start a new game (gameView exists or not)
            if (gameView != null) {
                // if the game exists, ask to confirm, then just initialize everything
                showNewGameConfirmationDialog();
            } else {
                Toast.makeText(this, "Starting a New Game!", Toast.LENGTH_SHORT).show();
                gameView = new GameView(this,this); // create a game view if it does not exist yet
            }
        } else if (option==2){
            // resume (gameView exists)

        } else {
            // loads a game
            if (gameView == null) {
                gameView = new GameView(this,this); // create a game view if it does not exist yet
            }
            Toast.makeText(this, "Loading a Game!", Toast.LENGTH_SHORT).show();
        }

        // goes back to the same game from game over
        if (gameView.getParent() != null) {
            ((ViewGroup) gameView.getParent()).removeView(gameView);
        }
        isGameRunning=true;

        gameContainer = new FrameLayout(this);
        setContentView(gameContainer);
        gameContainer.addView(gameView);

        // Load the icons (you've already done this part)
        restartIcon = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        resumeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.resume);
        pauseIcon = BitmapFactory.decodeResource(getResources(), R.drawable.pause2);
        quitIcon = BitmapFactory.decodeResource(getResources(), R.drawable.exit);
        menuIcon = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
        saveIcon = BitmapFactory.decodeResource(getResources(), R.drawable.save);
        loadIcon = BitmapFactory.decodeResource(getResources(), R.drawable.load);

        // Scale the icons as needed
        restartIcon = Bitmap.createScaledBitmap(restartIcon, restartIcon.getWidth() / 2, restartIcon.getHeight() / 2, false);
        resumeIcon = Bitmap.createScaledBitmap(resumeIcon, resumeIcon.getWidth() / 2, resumeIcon.getHeight() / 2, false);
        pauseIcon = Bitmap.createScaledBitmap(pauseIcon, pauseIcon.getWidth() / 2, pauseIcon.getHeight() / 2, false);
        quitIcon = Bitmap.createScaledBitmap(quitIcon, quitIcon.getWidth() / 2, quitIcon.getHeight() / 2, false);
        menuIcon = Bitmap.createScaledBitmap(menuIcon, menuIcon.getWidth() / 2, menuIcon.getHeight() / 2, false);
        saveIcon = Bitmap.createScaledBitmap(saveIcon, saveIcon.getWidth() / 2, saveIcon.getHeight() / 2, false);
        loadIcon = Bitmap.createScaledBitmap(loadIcon, loadIcon.getWidth() / 2, loadIcon.getHeight() / 2, false);

        // Create the buttons (ImageButtons)
        restartButton = new ImageButton(this);
        resumeButton = new ImageButton(this);
        pauseButton = new ImageButton(this);
        quitButton = new ImageButton(this);
        menuButton = new ImageButton(this);
        saveButton = new ImageButton(this);
        loadButton = new ImageButton(this);

        // Set the icons for the buttons
        restartButton.setImageBitmap(restartIcon);
        resumeButton.setImageBitmap(resumeIcon);
        pauseButton.setImageBitmap(pauseIcon);
        quitButton.setImageBitmap(quitIcon);
        menuButton.setImageBitmap(menuIcon);
        saveButton.setImageBitmap(saveIcon);
        loadButton.setImageBitmap(loadIcon);

        restartButton.setBackgroundColor(Color.TRANSPARENT);
        resumeButton.setBackgroundColor(Color.TRANSPARENT);
        pauseButton.setBackgroundColor(Color.TRANSPARENT);
        quitButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        saveButton.setBackgroundColor(Color.TRANSPARENT);
        loadButton.setBackgroundColor(Color.TRANSPARENT);

        // Create a LinearLayout for horizontal positioning
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL); // Make the layout horizontal
        buttonContainer.setPadding(0, 20, 0, 0);  // Set padding to the top (20dp)
        buttonContainer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        // Set layout parameters for each button
        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsButton.setMargins(10, 0, 10, 0); // Add some horizontal spacing between buttons

        restartButton.setLayoutParams(paramsButton);
        resumeButton.setLayoutParams(paramsButton);
        pauseButton.setLayoutParams(paramsButton);
        quitButton.setLayoutParams(paramsButton);
        menuButton.setLayoutParams(paramsButton);
        saveButton.setLayoutParams(paramsButton);
        loadButton.setLayoutParams(paramsButton);

        restartButton.setOnClickListener(v -> gameView.restartGame());
        resumeButton.setOnClickListener(v ->toggleGamePause());
        pauseButton.setOnClickListener(v -> toggleGamePause());
        quitButton.setOnClickListener(v -> gameView.quitGame());
        menuButton.setOnClickListener(v -> gameView.openMenu());
        saveButton.setOnClickListener(v -> saveGame());
        loadButton.setOnClickListener(v -> loadGame(null,2));

        buttonContainer.addView(restartButton);
        buttonContainer.addView(resumeButton);
        buttonContainer.addView(pauseButton);
        buttonContainer.addView(quitButton);
        buttonContainer.addView(menuButton);
        buttonContainer.addView(saveButton);
        buttonContainer.addView(loadButton);

        gameContainer.addView(buttonContainer);


    }
    public void saveGame() {
        View choiceDialogView = getLayoutInflater().inflate(R.layout.choice_dialogue_customed, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(choiceDialogView)
                .setCancelable(true);;
        /*builder.setTitle("Choose Save Slot")
                .setItems(slotOptions, (dialog, which) -> {
                    if (isSlotAlreadySaved(which)) {
                        // If there's data, ask the user if they want to overwrite it
                        showOverwriteConfirmationDialog(which);
                    } else {
                        // If no data exists, save immediately
                        saveGameDataToSlot(which);
                    }
                })
                .setCancelable(true)
                .show();*/
        AlertDialog alertDialog = builder.create();
        TextView dialogText=choiceDialogView.findViewById(R.id.dialogMessage);
        dialogText.setText("Choose Save Slot");

        Button slot1Button = choiceDialogView.findViewById(R.id.slot1);
        slot1Button.setOnClickListener(v -> handleSlotSelection(0, alertDialog));

        Button slot2Button = choiceDialogView.findViewById(R.id.slot2);
        slot2Button.setOnClickListener(v -> handleSlotSelection(1, alertDialog));

        Button slot3Button = choiceDialogView.findViewById(R.id.slot3);
        slot3Button.setOnClickListener(v -> handleSlotSelection(2, alertDialog));

        Button slot4Button = choiceDialogView.findViewById(R.id.slot4);
        slot4Button.setOnClickListener(v -> handleSlotSelection(3, alertDialog));

        Button slot5Button = choiceDialogView.findViewById(R.id.slot5);
        slot5Button.setOnClickListener(v -> handleSlotSelection(4, alertDialog));

        alertDialog.show();
    }

    private void handleSlotSelection(int slotIndex, AlertDialog alertDialog){
        if (isSlotAlreadySaved(slotIndex)) {
            showOverwriteConfirmationDialog(slotIndex);
            alertDialog.cancel();
        } else {
            saveGameDataToSlot(slotIndex);
            alertDialog.cancel();
        }
    }

    private boolean isSlotAlreadySaved(int slotIndex) {
        //gameDataSharedPreferences = getSharedPreferences("GameData", MODE_PRIVATE);
        return gameDataSharedPreferences.contains("game_state_slot_" + (slotIndex + 1));
    }

    private void showNewGameConfirmationDialog() {
        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_customed, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialogView) // Set the custom layout
                .setCancelable(true); // Allow dismissal on touch outside
        AlertDialog alertDialog = builder.create();
        TextView dialogText = dialogView.findViewById(R.id.dialogMessage);
        dialogText.setText("Starting a new game will make you lose your current progress. Do you want to continue?");
        Button btnYes = dialogView.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(v -> {
            gameView.restartGame();
            gameView.initiateEverything();
            if (!gameView.gamePaused) {
                toggleGamePause(); // to pause the game
            }
            alertDialog.dismiss(); // Close the dialog
        });

        Button btnNo = dialogView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void showOverwriteConfirmationDialog(int slotIndex) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_customed, null);
        AlertDialog.Builder builder =new AlertDialog.Builder(this)
                //.setTitle("Overwrite Saved Game")
                .setView(dialogView)
                /*.setMessage("A game is already saved in this slot. Do you want to overwrite it?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    saveGameDataToSlot(slotIndex);
                })
                .setNegativeButton("No", (dialog, which) -> {
                })*/
                .setCancelable(true);

                //.show();
        AlertDialog alertDialog = builder.create();
        Button btnYes = dialogView.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(v -> {
            saveGameDataToSlot(slotIndex);
            alertDialog.cancel();
        });

        // Set up the No button action
        Button btnNo = dialogView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> {
            alertDialog.cancel();
        });
        TextView dialogText=dialogView.findViewById(R.id.dialogMessage);
        dialogText.setText("A game is already saved in this slot. Do you want to overwrite it?");

        alertDialog.show();
    }

    private void saveGameDataToSlot(int slotIndex) {
        //gameDataSharedPreferences = getSharedPreferences("GameData", MODE_PRIVATE);

        // Example game state data (replace with actual values)
        GameState gameState=gameView.getGameState();
        Gson gson = new Gson();
        String gameStateJson = gson.toJson(gameState);

        // Save the data based on the selected slot
        switch (slotIndex) {
            case 0: // Slot 1
                gameDataeditor.putString("game_state_slot_1", gameStateJson);
                break;
            case 1: // Slot 2
                gameDataeditor.putString("game_state_slot_2", gameStateJson);
                break;
            case 2: // Slot 3
                gameDataeditor.putString("game_state_slot_3", gameStateJson);
                break;
            case 3: // Slot 4
                gameDataeditor.putString("game_state_slot_4", gameStateJson);
                break;
            case 4: // Slot 5
                gameDataeditor.putString("game_state_slot_5", gameStateJson);
                break;
        }
        gameDataeditor.apply();
        Toast.makeText(this, "Game saved to Slot " + (slotIndex + 1), Toast.LENGTH_SHORT).show();
    }

    public void loadGame(View view,int askLosingProgress) {
        // Show a dialog to let the user select a save slot
        View choiceDialogView = getLayoutInflater().inflate(R.layout.choice_dialogue_customed, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(choiceDialogView)
                .setCancelable(true);
        /*builder.setTitle("Choose Save Slot")
                .setItems(slotOptions, (dialog, which) -> {
                    showLosingProgressialog(which,askLosingProgress);
                })
                .setCancelable(true)
                .show();*/
        AlertDialog alertDialog = builder.create();
        TextView dialogText=choiceDialogView.findViewById(R.id.dialogMessage);
        dialogText.setText("Choose Save Slot");

        Button slot1Button = choiceDialogView.findViewById(R.id.slot1);
        slot1Button.setOnClickListener(v -> {
            showLosingProgressialog(0, askLosingProgress);
            alertDialog.cancel();  // Dismiss the dialog after the action
        });

        Button slot2Button = choiceDialogView.findViewById(R.id.slot2);
        slot2Button.setOnClickListener(v -> {
            showLosingProgressialog(1, askLosingProgress);
            alertDialog.cancel(); // Dismiss the dialog after the action
        });

        Button slot3Button = choiceDialogView.findViewById(R.id.slot3);
        slot3Button.setOnClickListener(v -> {
            showLosingProgressialog(2, askLosingProgress);
            alertDialog.cancel();  // Dismiss the dialog after the action
        });

        Button slot4Button = choiceDialogView.findViewById(R.id.slot4);
        slot4Button.setOnClickListener(v -> {
            showLosingProgressialog(3, askLosingProgress);
            alertDialog.cancel();  // Dismiss the dialog after the action
        });

        Button slot5Button = choiceDialogView.findViewById(R.id.slot5);
        slot5Button.setOnClickListener(v -> {
            showLosingProgressialog(4, askLosingProgress);
            alertDialog.cancel();  // Dismiss the dialog after the action
        });

        alertDialog.show();
    }

    public void toggleGamePause() {
        gameView.gamePaused=!gameView.gamePaused;
        gameView.invalidate();
    }

    private void showLosingProgressialog(int slotIndex,int askLosingProgress) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_customed, null);
        String gameStateJson = null;
        switch (slotIndex) {
            case 0:
                gameStateJson = gameDataSharedPreferences.getString("game_state_slot_1", null);
                break;
            case 1:
                gameStateJson = gameDataSharedPreferences.getString("game_state_slot_2", null);
                break;
            case 2:
                gameStateJson = gameDataSharedPreferences.getString("game_state_slot_3", null);
                break;
            case 3:
                gameStateJson =gameDataSharedPreferences.getString("game_state_slot_4", null);
                break;
            case 4:
                gameStateJson = gameDataSharedPreferences.getString("game_state_slot_5", null);
                break;
        }

        if (gameStateJson == null) {
            Toast.makeText(this, "No saved game data in this slot!", Toast.LENGTH_SHORT).show();
        } else {
            final String gameStateJson2=gameStateJson;
            if (askLosingProgress==2){
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(true);;
                        /*.setTitle("Unsaved Progress Might Be Lost")
                        .setMessage("Unsaved progress might be lost. Sure to proceed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            gameDataeditor.putString("loaded_game_state", gameStateJson2);  // Set the "is_game_loaded" flag to true
                            gameDataeditor.apply();
                            gameView.initializeGame();
                            Toast.makeText(this, "Game loaded from Slot " + (slotIndex + 1), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                        })
                        .setCancelable(true)
                        .show();*/
                AlertDialog alertDialog = builder.create();
                Button btnYes = dialogView.findViewById(R.id.btnYes);
                btnYes.setOnClickListener(v -> {
                    alertDialog.cancel();
                    gameDataeditor.putString("loaded_game_state", gameStateJson2);
                    // Set the "is_game_loaded" flag to true
                    gameDataeditor.apply();
                    gameView.initializeGame();
                    Toast.makeText(this, "Game loaded from Slot " + (slotIndex + 1), Toast.LENGTH_SHORT).show();
                });
                Button btnNo = dialogView.findViewById(R.id.btnNo);
                btnNo.setOnClickListener(v -> {
                    alertDialog.cancel();
                });
                TextView dialogText=dialogView.findViewById(R.id.dialogMessage);
                dialogText.setText("Unsaved progress might be lost. Sure to proceed?");
                alertDialog.show();
            } else {
                gameDataeditor.putString("loaded_game_state", gameStateJson2);  // Set the "is_game_loaded" flag to true
                gameDataeditor.apply();
                Log.e("info","trying to load gaem from main menu");
                initiateGame(3);
                Toast.makeText(this, "Game loaded from Slot " + (slotIndex + 1), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void returnToGame(View view) {
        /*if (gameView != null) {
            Toast.makeText(this, "Resuming the Game!", Toast.LENGTH_SHORT).show();
            if (gameView.getParent() != null) {
                ((ViewGroup) gameView.getParent()).removeView(gameView);
            }
            setContentView(gameView); // Restore the GameView
            isGameRunning = true; // Mark that the game is running
        }*/
       initiateGame(2);
    }

    public void viewRecords(View view) {
        int points=0;
        if (gameView != null) {
            points=gameView.points;
            Intent intent = new Intent(this, StoreActivity.class);
            intent.putExtra("points", points);
            startActivity(intent);
            Toast.makeText(this, "Viewing Records!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You must start or load a game before going to the store", Toast.LENGTH_LONG).show();
        }

    }

    public void quitGame(View view) {
        Toast.makeText(this, "Quitting the Game. Goodbye!", Toast.LENGTH_SHORT).show();
        finishAffinity(); // Closes all activities in the task
        System.exit(0);   // Terminates the app process
    }
}

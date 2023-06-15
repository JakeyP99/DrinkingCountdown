package com.example.countingdowngame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends ButtonUtilsActivity {
    private final Map<Player, Set<Settings_WildCard_Probabilities>> usedWildCard = new HashMap<>();
    private final Set<Settings_WildCard_Probabilities> usedWildCards = new HashSet<>();
    private TextView numberText;
    private TextView nextPlayerText;
    private Button btnWild;
    private Button btnGenerate;
    private Button btnBackWild;
    private ImageView playerImage;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds
    private TextView wildTextView;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Game.getInstance().endGame();
            gotoHomeScreen();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to go to the home screen", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }
    //-----------------------------------------------------Create game---------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start);
        initializeViews();
        setupButtons();
        startGame();
    }

    private void initializeViews() {
        playerImage = findViewById(R.id.playerImage);
        numberText = findViewById(R.id.numberText);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnWild = findViewById(R.id.btnWild);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnBackWild = findViewById(R.id.btnBackWildCard);
        wildTextView = findViewById(R.id.wild_textview);

    }
    private void startGame() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        // Load player data from PlayerModel
        List<Player> playerList = PlayerModel.loadSelectedPlayers(this);
        if (!playerList.isEmpty()) {
            // Set the player list in Game class
            Game.getInstance().setPlayers(playerList.size());
            Game.getInstance().setPlayerList(playerList);

            // Set the game object for each player
            for (Player player : playerList) {
                player.setGame(Game.getInstance());
            }
        }

        Game.getInstance().startGame(startingNumber, (e) -> {
            if (e.type == GameEventType.NEXT_PLAYER) {
                renderPlayer();
            }
        });

        renderPlayer();
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupButtons() {

        ImageButton imageButtonExit = findViewById(R.id.imageBtnExit);
        View wildText = findViewById(R.id.wild_textview);


        btnBackWild.setVisibility(View.INVISIBLE);

        btnUtils.setButton(btnGenerate, () -> {
            Game.getInstance().nextNumber(this::gotoGameEnd);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);

            wildText.setVisibility(View.INVISIBLE);



        });

        btnUtils.setButton(btnBackWild, () -> {
            btnGenerate.setVisibility(View.VISIBLE);
            Game.getInstance().getCurrentPlayer().useSkip();
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);

            wildText.setVisibility(View.INVISIBLE);
            btnBackWild.setVisibility(View.INVISIBLE);


        });

        btnUtils.setButton(btnWild, () -> {
            Game.getInstance().getCurrentPlayer().useWildCard();
            wildCardActivate(Game.getInstance().getCurrentPlayer());
            wildText.setVisibility(View.VISIBLE);

            btnWild.setVisibility(View.INVISIBLE);


            btnBackWild.setVisibility(View.VISIBLE);
            btnGenerate.setVisibility(View.INVISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);


        });

        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame();
            gotoHomeScreen();
        });
    }


    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        List<Player> playerList = PlayerModel.loadSelectedPlayers(this);

        if (!playerList.isEmpty()) {
            String playerName = currentPlayer.getName();
            String playerImageString = currentPlayer.getPhoto();

            nextPlayerText.setText(playerName + "'s Turn");

            if (playerImageString != null) {
                byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                playerImage.setImageBitmap(decodedBitmap);
            }
        }

        if (currentPlayer.getWildCardAmount() > -3) {
            btnWild.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
        }

        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
        setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());

    }

    //-----------------------------------------------------Wild Card, and Skip Functionality---------------------------------------------------//

    private void wildCardActivate(Player player) {

        Settings_WildCard_Choice settings = new Settings_WildCard_Choice();
        Settings_WildCard_Probabilities[][] probabilitiesArray = settings.loadWildCardProbabilitiesFromStorage(getApplicationContext());

        Settings_WildCard_Probabilities[] deletableProbabilities = probabilitiesArray[0];
        Settings_WildCard_Probabilities[] nonDeletableProbabilities = probabilitiesArray[1];

        List<Settings_WildCard_Probabilities> allProbabilities = new ArrayList<>();
        allProbabilities.addAll(Arrays.asList(deletableProbabilities));
        allProbabilities.addAll(Arrays.asList(nonDeletableProbabilities));

        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        player.useWildCard();

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<Settings_WildCard_Probabilities> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        if (wildCardsEnabled) {
            List<Settings_WildCard_Probabilities> unusedCards = allProbabilities.stream()
                    .filter(Settings_WildCard_Probabilities::isEnabled)
                    .filter(c -> !usedWildCards.contains(c))
                    .collect(Collectors.toList());

            if (unusedCards.isEmpty()) {
                wildActivityTextView.setText("No wild cards available");
                btnWild.setVisibility(View.INVISIBLE);
                return;
            }

            int totalWeight = unusedCards.stream()
                    .mapToInt(Settings_WildCard_Probabilities::getProbability)
                    .sum();

            if (totalWeight <= 0) {
                wildActivityTextView.setText("No wild cards available");
                return;
            }

            Random random = new Random();
            boolean foundUnusedCard = false;
            while (!foundUnusedCard) {
                int randomWeight = random.nextInt(totalWeight);
                int weightSoFar = 0;

                for (Settings_WildCard_Probabilities activityProbability : unusedCards) {
                    weightSoFar += activityProbability.getProbability();

                    if (randomWeight < weightSoFar) {
                        if (usedCards != null && !usedCards.contains(activityProbability)) {
                            selectedActivity = activityProbability.getText();
                            foundUnusedCard = true;
                            usedCards.add(activityProbability);
                        }
                        break;
                    }
                }
            }
            usedWildCard.put(player, usedCards);
        }


        if (selectedActivity != null) {
            wildActivityTextView.setText(selectedActivity);
            for (Settings_WildCard_Probabilities wc : allProbabilities) {
                if (wc.getText().equals(selectedActivity)) {
                    player.addUsedWildCard(wc);
                    usedCards.add(wc);
                    break;
                }
            }
        }


        if (selectedActivity != null && selectedActivity.equals("Double the current number and go again!")) {
            int currentNumber = Game.getInstance().getCurrentNumber();
            int updatedNumber = currentNumber * 2;
            Game.getInstance().setCurrentNumber(updatedNumber);
            Game.getInstance().addUpdatedNumber(updatedNumber);
            numberText.setText(String.valueOf(updatedNumber));
            setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
        }

        if (selectedActivity != null && selectedActivity.equals("Half the current number and go again!")) {
            int currentNumber = Game.getInstance().getCurrentNumber();
            int updatedNumber = Math.max(currentNumber / 2, 1);
            Game.getInstance().setCurrentNumber(updatedNumber);
            Game.getInstance().addUpdatedNumber(updatedNumber);
            numberText.setText(String.valueOf(updatedNumber));
            setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
        }


        if (selectedActivity != null && selectedActivity.equals("Reset the number!")) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                throw new RuntimeException("Missing extras");
            }
            int startingNumber = extras.getInt("startingNumber");
            Game.getInstance().setCurrentNumber(startingNumber);
            Game.getInstance().addUpdatedNumber(startingNumber);
            numberText.setText(String.valueOf(startingNumber));
            setTextViewSizeBasedOnInt(numberText, String.valueOf(startingNumber));
        }

        if (selectedActivity != null && selectedActivity.equals("Reverse the turn order!")) {
            reverseTurnOrder(player);
        }

    }

    private void reverseTurnOrder(Player player) {
        Game game = Game.getInstance();
        List<Player> players = game.getPlayers();
        Collections.reverse(players);

        int currentPlayerIndex = players.indexOf(player);

        if (currentPlayerIndex != -1) {
            int lastIndex = players.size() - 1;
            int newIndex = lastIndex - currentPlayerIndex;

            // Move the player to the new index
            players.remove(currentPlayerIndex);
            players.add(newIndex, player);

            // Update the current player ID if necessary
            if (game.getCurrentPlayer() == player) {
                game.setCurrentPlayerId(newIndex);
            }
        }

        game.setPlayerList(players);
    }

    private void setTextViewSizeBasedOnInt(TextView textView, String text) {
        int defaultTextSize = 70;
        int minSize = 47;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }
    }

    private void setNameSizeBasedOnInt(TextView textView, String text) {
        int textSize;

        if (text.length() > 19) {
            textSize = 22;
        } else if (text.length() > 14) {
            textSize = 30;
        } else if (text.length() > 8) {
            textSize = 35;
        } else {
            textSize = 45;
        }
        textView.setTextSize(textSize);
    }
}

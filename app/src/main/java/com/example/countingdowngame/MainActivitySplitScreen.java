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

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.GameEventType;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.stores.PlayerModelLocalStore;

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

public class MainActivitySplitScreen extends ButtonUtilsActivity {

    private final Map<Player, Set<WildCardHeadings>> usedWildCard = new HashMap<>();
    private final Set<WildCardHeadings> usedWildCards = new HashSet<>();
    private TextView numberText;
    private TextView numberTextPlayer2;
    private TextView nextPlayerText;
    private TextView nextPlayerTextPlayer2;
    private Button btnWild;
    private Button btnWildPlayer2;
    private ImageView playerImage;
    private ImageView playerImagePlayer2;
    Button btnGenerate;
    Button btnGeneratePlayer2;
    Button btnBackWild;
    Button btnBackWildPlayer2;
    View wildText;
    View wildTextPlayer2;
    ImageButton imageButtonExit;
    ImageButton imageButtonExitPlayer2;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_DELAY = 3000; // 3 seconds

    private Handler shuffleHandler;
    @Override
    protected void onResume() {
        super.onResume();
        AudioManager.getInstance().playSound(); // Start playing the sound
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioManager.getInstance().stopSound(); // Stop the sound
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Game.getInstance().endGame(this);
            gotoHomeScreen();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to go to the home screen", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, BACK_PRESS_DELAY);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a5_game_start_splitscreen);
        initializeViews();
        setupButtons();
        startGame();
    }

    private void initializeViews() {
        btnGenerate = findViewById(R.id.btnGenerate);
        btnGeneratePlayer2 = findViewById(R.id.btnGeneratePlayer2);

        btnBackWild = findViewById(R.id.btnBackWildCard);
        btnBackWildPlayer2 = findViewById(R.id.btnBackWildCardPlayer2);

        wildText = findViewById(R.id.wild_textview);
        wildTextPlayer2 = findViewById(R.id.wild_textviewPlayer2);

        imageButtonExit = findViewById(R.id.imageBtnExit);
        imageButtonExitPlayer2 = findViewById(R.id.imageBtnExitPlayer2);

        playerImage = findViewById(R.id.playerImage);
        playerImagePlayer2 = findViewById(R.id.playerImagePlayer2);

        btnWild = findViewById(R.id.btnWild);
        btnWildPlayer2 = findViewById(R.id.btnWildPlayer2);

        numberText = findViewById(R.id.numberText);
        numberTextPlayer2 = findViewById(R.id.numberTextPlayer2);

        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        nextPlayerTextPlayer2 = findViewById(R.id.textView_Number_TurnPlayer2);

        shuffleHandler = new Handler();

    }

    private void startGame() {
        int soundResourceId = R.raw.cartoonloop;
        AudioManager.getInstance().initialize(this, soundResourceId);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new RuntimeException("Missing extras");
        }

        int startingNumber = extras.getInt("startingNumber");

        // Load player data from PlayerModel
        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        if (!playerList.isEmpty()) {
            // Set the player list in Game class
            Game.getInstance().setPlayers(this, playerList.size());
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

        btnBackWild.setVisibility(View.INVISIBLE);
        btnBackWildPlayer2.setVisibility(View.INVISIBLE);

        btnUtils.setButton(btnGenerate, this::ButtonGenerateFunction);
        btnUtils.setButton(btnWild, this::ButtonWildFunction);
        btnUtils.setButton(btnBackWild, this::ButtonContinueFunction);
        imageButtonExit.setOnClickListener(view -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });

        btnUtils.setButton(btnGeneratePlayer2, this::ButtonGenerateFunction);
        btnUtils.setButton(btnWildPlayer2, this::ButtonWildFunction);
        btnUtils.setButton(btnBackWildPlayer2, this::ButtonContinueFunction);
        imageButtonExitPlayer2.setOnClickListener(view -> {
            Game.getInstance().endGame(this);
            gotoHomeScreen();
        });
    }
    //-----------------------------------------------------Button Shuffling---------------------------------------------------//

    private void startNumberShuffleAnimation() {
        btnGenerate.setEnabled(false);
        btnWild.setEnabled(false);
        btnGeneratePlayer2.setEnabled(false);
        btnWildPlayer2.setEnabled(false);

        int currentNumber = Game.getInstance().getCurrentNumber();
        final int shuffleDuration = 1500;

        int shuffleInterval = currentNumber >= 1000 ? 50 : 100;

        final Random random = new Random();
        shuffleHandler.postDelayed(new Runnable() {
            int shuffleTime = 0;

            @Override
            public void run() {
                int randomDigit = random.nextInt(currentNumber + 1);
                numberText.setText(String.valueOf(randomDigit));
                numberTextPlayer2.setText(String.valueOf(randomDigit));

                shuffleTime += shuffleInterval;

                if (shuffleTime < shuffleDuration) {
                    shuffleHandler.postDelayed(this, shuffleInterval);
                } else {
                    numberText.setText(String.valueOf(randomDigit));
                    numberTextPlayer2.setText(String.valueOf(randomDigit));

                    Game.getInstance().nextNumber(MainActivitySplitScreen.this, () -> gotoGameEnd());

                    btnGenerate.setEnabled(true);
                    btnWild.setEnabled(true);
                    btnGeneratePlayer2.setEnabled(true);
                    btnWildPlayer2.setEnabled(true);
                }
            }
        }, shuffleInterval);
    }



    //-----------------------------------------------------Render Player---------------------------------------------------//

    private void renderPlayer() {
        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        List<Player> playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();

        if (!playerList.isEmpty()) {
            String playerName = currentPlayer.getName();
            String playerImageString = currentPlayer.getPhoto();

            nextPlayerText.setText(playerName);
            nextPlayerTextPlayer2.setText(playerName);

            btnWild.setText(currentPlayer.getWildCardAmount() + "\n" + "Wild Cards");
            btnWildPlayer2.setText(currentPlayer.getWildCardAmount() + "\n" + "Wild Cards");

            if (playerImageString != null) {
                byte[] decodedString = Base64.decode(playerImageString, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                playerImage.setImageBitmap(decodedBitmap);
                playerImagePlayer2.setImageBitmap(decodedBitmap);

            }
        }
        if (currentPlayer.getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
            btnWildPlayer2.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
            btnWildPlayer2.setVisibility(View.INVISIBLE);
        }

        int currentNumber = Game.getInstance().getCurrentNumber();
        numberText.setText(String.valueOf(currentNumber));
        numberTextPlayer2.setText(String.valueOf(currentNumber));

        setTextViewSizeBasedOnInt(numberText, String.valueOf(currentNumber));
        setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(currentNumber));

        setNameSizeBasedOnInt(nextPlayerText, nextPlayerText.getText().toString());
        setNameSizeBasedOnInt(nextPlayerTextPlayer2, nextPlayerTextPlayer2.getText().toString());

    }

    //-----------------------------------------------------Wild Card, and Skip Functionality---------------------------------------------------//

    private void wildCardActivate(Player player) {
        Game.getInstance().getCurrentPlayer().useWildCard();
        WildCardHeadings[] emptyProbabilitiesArray = new WildCardHeadings[0];
        QuizWildCardsAdapter quizAdapter = new QuizWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.QUIZ);
        TaskWildCardsAdapter taskAdapter = new TaskWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TASK);
        TruthWildCardsAdapter truthAdapter = new TruthWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.TRUTH);
        ExtrasWildCardsAdapter extraAdapter = new ExtrasWildCardsAdapter(emptyProbabilitiesArray, this, WildCardType.EXTRAS);

        WildCardHeadings[] quizProbabilities = quizAdapter.loadWildCardProbabilitiesFromStorage(QuizWildCardsFragment.defaultQuizWildCards);
        WildCardHeadings[] taskProbabilities = taskAdapter.loadWildCardProbabilitiesFromStorage(TaskWildCardsFragment.defaultTaskWildCards);
        WildCardHeadings[] truthProbabilities = truthAdapter.loadWildCardProbabilitiesFromStorage(TruthWildCardsFragment.defaultTruthWildCards);
        WildCardHeadings[] extraProbabilities = extraAdapter.loadWildCardProbabilitiesFromStorage(ExtrasWildCardsFragment.defaultExtrasWildCards);

        List<WildCardHeadings> allProbabilities = new ArrayList<>();
        allProbabilities.addAll(Arrays.asList(quizProbabilities));
        allProbabilities.addAll(Arrays.asList(taskProbabilities));
        allProbabilities.addAll(Arrays.asList(truthProbabilities));
        allProbabilities.addAll(Arrays.asList(extraProbabilities));

        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        final TextView wildActivityTextViewPlayer2 = findViewById(R.id.wild_textviewPlayer2);

        boolean wildCardsEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wild_cards_toggle", true);

        String selectedActivity = null;
        Set<WildCardHeadings> usedCards = usedWildCard.getOrDefault(player, new HashSet<>());
        if (wildCardsEnabled) {
            Random random = new Random();
            int typeChance = random.nextInt(4); // Generate a random number from 0 to 3

            WildCardHeadings[] selectedType = null;

            switch (typeChance) {
                case 0:
                    selectedType = quizProbabilities;
                    break;
                case 1:
                    selectedType = taskProbabilities;
                    break;
                case 2:
                    selectedType = truthProbabilities;
                    break;
                case 3:
                    selectedType = extraProbabilities;
                    break;
                default:
                    break;
            }

            List<WildCardHeadings> unusedCards = Arrays.stream(selectedType)
                    .filter(WildCardHeadings::isEnabled)
                    .filter(c -> !usedWildCards.contains(c))
                    .collect(Collectors.toList());

            if (unusedCards.isEmpty()) {
                wildActivityTextView.setText("No wild cards available");
                btnWild.setVisibility(View.INVISIBLE);
                btnWildPlayer2.setVisibility(View.INVISIBLE);
                return;
            }

            // Calculate the total probabilities within the selected type
            int totalTypeProbabilities = unusedCards.stream()
                    .mapToInt(WildCardHeadings::getProbability)
                    .sum();

            // Generate a random number within the total probabilities
            int selectedIndex = random.nextInt(totalTypeProbabilities);

            // Select the wildcard based on the random number and its probability
            WildCardHeadings selectedCard = null;
            int cumulativeProbability = 0;

            for (WildCardHeadings card : unusedCards) {
                cumulativeProbability += card.getProbability();

                if (selectedIndex < cumulativeProbability) {
                    selectedCard = card;
                    break;
                }
            }

            if (selectedCard != null) {
                selectedActivity = selectedCard.getText();
                assert usedCards != null;
                usedCards.add(selectedCard);
                usedWildCards.add(selectedCard);
                usedWildCard.put(player, usedCards);
            }
        }

        if (selectedActivity != null) {
            wildActivityTextView.setText(selectedActivity);
            wildActivityTextViewPlayer2.setText(selectedActivity);
        }

        if (selectedActivity != null) {
            if (selectedActivity.equals("Double the current number!")) {
                int currentNumber = Game.getInstance().getCurrentNumber();
                int updatedNumber = Math.min(currentNumber * 2, 999999999);
                Game.getInstance().setCurrentNumber(updatedNumber);
                Game.getInstance().addUpdatedNumber(updatedNumber);
                numberText.setText(String.valueOf(updatedNumber));
                numberTextPlayer2.setText(String.valueOf(updatedNumber));
                setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
                setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(updatedNumber));
            } else if (selectedActivity.equals("Half the current number!")) {
                int currentNumber = Game.getInstance().getCurrentNumber();
                int updatedNumber = Math.max(currentNumber / 2, 1);
                Game.getInstance().setCurrentNumber(updatedNumber);
                Game.getInstance().addUpdatedNumber(updatedNumber);
                numberText.setText(String.valueOf(updatedNumber));
                numberTextPlayer2.setText(String.valueOf(updatedNumber));
                setTextViewSizeBasedOnInt(numberText, String.valueOf(updatedNumber));
                setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(updatedNumber));
            } else if (selectedActivity.equals("Reset the number!")) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    throw new RuntimeException("Missing extras");
                }
                int startingNumber = extras.getInt("startingNumber");
                Game.getInstance().setCurrentNumber(startingNumber);
                Game.getInstance().addUpdatedNumber(startingNumber);
                numberText.setText(String.valueOf(startingNumber));
                numberTextPlayer2.setText(String.valueOf(startingNumber));
                setTextViewSizeBasedOnInt(numberText, String.valueOf(startingNumber));
                setTextViewSizeBasedOnInt(numberTextPlayer2, String.valueOf(startingNumber));
            } else if (selectedActivity.equals("Reverse the turn order!")) {
                reverseTurnOrder(player);
            }
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
        int defaultTextSize = 65;
        int minSize = 45;

        if (text.length() > 6) {
            textView.setTextSize(minSize);
        } else {
            textView.setTextSize(defaultTextSize);
        }

    }
    //-----------------------------------------------------Button Functionality---------------------------------------------------//

    private void ButtonGenerateFunction() {
        wildText.setVisibility(View.INVISIBLE);
        numberText.setVisibility(View.VISIBLE);
        nextPlayerText.setVisibility(View.VISIBLE);

        wildTextPlayer2.setVisibility(View.INVISIBLE);
        numberTextPlayer2.setVisibility(View.VISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.VISIBLE);
        startNumberShuffleAnimation();
    }

    private void ButtonWildFunction() {
        btnWild.setVisibility(View.INVISIBLE);
        btnWildPlayer2.setVisibility(View.INVISIBLE);

        wildText.setVisibility(View.VISIBLE);
        wildTextPlayer2.setVisibility(View.VISIBLE);

        btnBackWild.setVisibility(View.VISIBLE);
        btnBackWildPlayer2.setVisibility(View.VISIBLE);

        btnGenerate.setVisibility(View.INVISIBLE);
        btnGeneratePlayer2.setVisibility(View.INVISIBLE);

        numberText.setVisibility(View.INVISIBLE);
        numberTextPlayer2.setVisibility(View.INVISIBLE);

        nextPlayerText.setVisibility(View.INVISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.INVISIBLE);

        playerImage.setVisibility(View.INVISIBLE);
        playerImagePlayer2.setVisibility(View.INVISIBLE);

        Player currentPlayer = Game.getInstance().getCurrentPlayer();
        wildCardActivate(currentPlayer);

    }

    private void ButtonContinueFunction() {
        btnBackWild.setVisibility(View.INVISIBLE);
        btnBackWildPlayer2.setVisibility(View.INVISIBLE);

        btnGenerate.setVisibility(View.VISIBLE);
        btnGeneratePlayer2.setVisibility(View.VISIBLE);


        wildText.setVisibility(View.INVISIBLE);
        wildTextPlayer2.setVisibility(View.INVISIBLE);

        numberText.setVisibility(View.VISIBLE);
        numberTextPlayer2.setVisibility(View.VISIBLE);

        playerImage.setVisibility(View.VISIBLE);
        playerImagePlayer2.setVisibility(View.VISIBLE);

        nextPlayerText.setVisibility(View.VISIBLE);
        nextPlayerTextPlayer2.setVisibility(View.VISIBLE);
        Game.getInstance().getCurrentPlayer().useSkip();

    }

    private void setNameSizeBasedOnInt(TextView textView, String text) {
        int textSize;
        if (text.length() > 24) {
            textSize = 15;
        } else if (text.length() > 18) {
            textSize = 20;
        } else if (text.length() > 14) {
            textSize = 23;
        } else if (text.length() > 8) {
            textSize = 28;
        } else {
            textSize = 38;
        }
        textView.setTextSize(textSize);
    }

}

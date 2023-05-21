package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Settings_PlayerModel extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private RecyclerView playerRecyclerView;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SettingClass.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_player_list);

        playerRecyclerView = findViewById(R.id.playerRecyclerView);
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList);
        loadPlayerData();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        playerRecyclerView.setLayoutManager(layoutManager);

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        playerRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));

        playerRecyclerView.setAdapter(playerListAdapter);

        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(view -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void addNewPlayer(Bitmap bitmap, String name) {
        String photoString = convertBitmapToString(bitmap);

        Player newPlayer = new Player(photoString, name);

        playerList.add(newPlayer);
        playerListAdapter.notifyItemInserted(playerList.size() - 1);

        int position = playerList.indexOf(newPlayer);
        new Handler().postDelayed(() -> animatePlayerImage(position), 100);

        savePlayerData();
    }

    private void deletePlayer(int position) {
        playerList.remove(position);
        playerListAdapter.notifyItemRemoved(position);
        savePlayerData();
    }

    private void animatePlayerImage(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) playerRecyclerView.getLayoutManager();
        assert layoutManager != null;
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View itemView = playerRecyclerView.getChildAt(position - firstVisiblePosition);

            if (itemView != null) {
                ImageView playerPhotoImageView = itemView.findViewById(R.id.playerPhotoImageView);
                Glide.with(this)
                        .load(playerList.get(position).getPhoto())
                        .apply(RequestOptions.circleCropTransform()) // Apply circular transformation
                        .into(playerPhotoImageView);
                float startScale = 0.0f;
                float endScale = 1.0f;
                int duration = 500;
                ScaleAnimation scaleAnimation = new ScaleAnimation(startScale, endScale, startScale, endScale,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(duration);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playerPhotoImageView.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                playerPhotoImageView.startAnimation(scaleAnimation);
            }
        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public SpaceItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;

            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            assert layoutManager != null;
            if (parent.getChildAdapterPosition(view) < layoutManager.getSpanCount()) {
                outRect.top = spacing;
            } else {
                outRect.top = 0;
            }
        }
    }

    private class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
        private final Context context;
        private final List<Player> players;

        public PlayerListAdapter(Context context, List<Player> players) {
            this.context = context;
            this.players = players;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_view_player_name, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Player player = players.get(position);
            holder.bind(player);
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView playerPhotoImageView;
            TextView playerNameTextView;
            ImageView deletePlayerImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                playerPhotoImageView = itemView.findViewById(R.id.playerPhotoImageView);
                playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
                deletePlayerImageView = itemView.findViewById(R.id.deletePlayerImageView);

                deletePlayerImageView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deletePlayer(position);
                    }
                });
            }

            public void bind(Player player) {
                String photoString = player.getPhoto();
                Glide.with(context)
                        .load(Base64.decode(photoString, Base64.DEFAULT))
                        .apply(RequestOptions.circleCropTransform())
                        .into(playerPhotoImageView);

                playerNameTextView.setBackgroundResource(R.drawable.outlineforbutton);
                playerNameTextView.setText(player.getName());
                playerNameTextView.setPadding(20, 20, 20, 20);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Player Name");

            View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_name, null);
            EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
            nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            builder.setView(dialogView)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String name = nameEditText.getText().toString();
                        addNewPlayer(bitmap, name);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void savePlayerData() {
        SharedPreferences sharedPreferences = getSharedPreferences("player_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        editor.putString("player_list", json);
        editor.apply();
    }

    private void loadPlayerData() {
        SharedPreferences sharedPreferences = getSharedPreferences("player_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("player_list", null);
        Type type = new TypeToken<ArrayList<Player>>() {}.getType();
        List<Player> loadedPlayerList = gson.fromJson(json, type);

        if (loadedPlayerList != null) {
            playerList.clear();
            playerList.addAll(loadedPlayerList);
            playerListAdapter.notifyDataSetChanged();
        }
    }
    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}

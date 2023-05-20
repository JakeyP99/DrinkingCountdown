package com.example.countingdowngame;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class WildCardAdapter extends ArrayAdapter<WildCardProbabilities> {
    private final Settings_WildCardChoice mContext;
    private final WildCardMode mMode;
    private WildCardProbabilities[] mProbabilities;

    public WildCardAdapter(WildCardMode mode, Settings_WildCardChoice context, WildCardProbabilities[] probabilities) {
        super(context, R.layout.list_item_wildcard, probabilities);
        mContext = context;
        mProbabilities = probabilities;
        mMode = mode;
    }

    @Override
    public int getCount() {
        return mProbabilities.length;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_wildcard, null);

        Button editButton = view.findViewById(R.id.button_edit_probability);
        TextView textViewWildCard = view.findViewById(R.id.textview_wildcard);
        TextView textViewProbability = view.findViewById(R.id.textview_probability);
        Switch switchWildCard = view.findViewById(R.id.switch_wildcard);

        WildCardProbabilities wildCard = mProbabilities[position];

        textViewWildCard.setText(wildCard.getText());
        textViewProbability.setText(String.valueOf(wildCard.getProbability()));
        switchWildCard.setChecked(wildCard.isEnabled());

        String wildCardText = wildCard.getText();
        setTextViewSizeBasedOnString(textViewWildCard, wildCardText);

        String probabilityText = String.valueOf(wildCard.getProbability());
        setProbabilitySizeBasedOnString(textViewProbability, probabilityText);

        editButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Edit Wildcard");

            LinearLayout layout = new LinearLayout(mContext);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText textInput = new EditText(mContext);
            final EditText probabilityInput = new EditText(mContext);
            probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            probabilityInput.setText(String.valueOf(wildCard.getProbability()));
            layout.addView(probabilityInput);

            if (wildCard.isDeletable()) {
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setText(wildCard.getText());
                layout.addView(textInput);
            } else {
                textInput.setEnabled(false);
            }

            builder.setView(layout);

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            ArrayList<WildCardProbabilities> wildCardList = new ArrayList<>(Arrays.asList(mProbabilities));
            mProbabilities = wildCardList.toArray(new WildCardProbabilities[0]);
            mProbabilities = wildCardList.toArray(new WildCardProbabilities[1]);

            if (wildCard.isDeletable()) {
                builder.setNeutralButton("Delete", (dialog, which) -> {
                    wildCardList.remove(position);
                    notifyDataSetChanged();
                    mContext.saveWildCardProbabilitiesToStorage(mMode, mProbabilities);
                });
            }

            builder.setPositiveButton("OK", (dialog, which) -> {
                int probability = Integer.parseInt(probabilityInput.getText().toString());
                wildCard.setProbability(probability);

                if (wildCard.isDeletable()) {
                    String text = textInput.getText().toString();
                    wildCard.setText(text);
                    textViewWildCard.setText(wildCard.getText());
                }

                textViewProbability.setText(String.valueOf(wildCard.getProbability()));
                setProbabilitySizeBasedOnString(textViewProbability, String.valueOf(wildCard.getProbability()));

                mContext.saveWildCardProbabilitiesToStorage(mMode, mProbabilities);
            });

            builder.show();
        });

        switchWildCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wildCard.setEnabled(isChecked);
            mContext.saveWildCardProbabilitiesToStorage(mMode, mProbabilities);
        });

        return view;
    }

    private void setTextViewSizeBasedOnString(TextView textView, String text) {
        int textSize = 20;
        if (text.length() > 20) {
            textSize = 14;
        } else if (text.length() > 10) {
            textSize = 16;
        }
        textView.setTextSize(textSize);
    }

    private void setProbabilitySizeBasedOnString(TextView textView, String text) {
        int textSize = 18;
        if (text.length() > 3) {
            textSize = 12;
        } else if (text.length() > 0) {
            textSize = 18;
        }
        textView.setTextSize(textSize);
    }
}
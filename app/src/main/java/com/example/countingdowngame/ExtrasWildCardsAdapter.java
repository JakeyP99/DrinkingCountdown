package com.example.countingdowngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
public class ExtrasWildCardsAdapter extends WildCardsAdapter {
    public ExtrasWildCardsAdapter(WildCardHeadings[] extrasWildCards, Context context, WildCardType mode) {
        super("wildCardsKey", extrasWildCards, context, mode);
    }

    @NonNull
    @Override
    public WildCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_wild_cards, parent, false);
        return new ExtrasWildCardsAdapter.ExtrasWildCardViewHolder(view);
    }

    public class ExtrasWildCardViewHolder extends WildCardViewHolder {
        public ExtrasWildCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
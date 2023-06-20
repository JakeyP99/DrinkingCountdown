package com.example.countingdowngame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QuizWildCardsFragment extends Fragment {
    Settings_WildCard_Probabilities[] quizWildCards = {
            new Settings_WildCard_Probabilities("Quiz! Name two famous people with the same initials as yours. If you can't, take 2 drinks, if you can everyone takes 3 drinks.", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Name five countries starting with the letter A. If you can't, take 3 drinks.", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you recite the first 7 digits of pi? If yes, go ahead.", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! In which year was the Great Fire of London?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you name all four fundamental forces in physics?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who wrote the famous novel 'To Kill a Mockingbird'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many bones are there in the human body?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the scientific name for the common cold?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! In which country is the ancient city of Machu Picchu located?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who developed the theory of general relativity?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical formula for glucose?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many symphonies did Ludwig van Beethoven compose?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is credited with inventing the World Wide Web?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest planet in our solar system?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you name all seven colors of the rainbow in order?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest organ in the human body?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who painted the famous artwork 'The Last Supper'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many countries are members of the United Nations?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the capital of Australia?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you name the longest river in the world?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the formula for calculating the area of a circle?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who wrote the famous novel '1984'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many strings does a standard violin have?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical symbol for silver on the periodic table?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who directed the movie 'Pulp Fiction'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many chromosomes are in a human body cell?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the Greek god of the sea?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the national animal of Canada?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many time zones are there in the world?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who wrote the famous novel 'Moby-Dick'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest species of shark?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who composed the famous classical symphony 'Symphony No. 10'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many players are there on a basketball team?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical symbol for iron on the periodic table?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who directed the movie 'The Shawshank Redemption'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many colors are there in a rainbow?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the author of the novel 'The Catcher in the Rye'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the deepest ocean on Earth?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the author of the novel 'The Great Gatsby'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many legs does a spider have?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who directed the movie 'The Godfather'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many sides does a heptagon have?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the current Prime Minister of Japan?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest continent in the world?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the author of the novel 'The Lord of the Rings'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many players are there on a soccer team?", 10, true, true),

    };

    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        QuizWildCardsAdapter adapter = new QuizWildCardsAdapter(quizWildCards, requireContext());
        recyclerView.setAdapter(adapter);

        return view;
    }
}

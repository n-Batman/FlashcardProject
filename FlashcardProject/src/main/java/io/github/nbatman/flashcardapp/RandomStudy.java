package io.github.nbatman.flashcardapp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomStudy implements StudyStrategy {

	@Override
	public List<Flashcard> sortingMethod(FlashcardSet set)
	{
		// TODO Auto-generated method stub
		List<Flashcard> shuffled = new ArrayList<>(set.getCards());
        Collections.shuffle(shuffled);
        return shuffled;
	}

}
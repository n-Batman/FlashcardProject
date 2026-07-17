package io.github.nbatman.flashcardapp;
import java.util.List;

public class SequentialStudyStrategy implements StudyStrategy
{

    @Override
    public List<Flashcard> sortingMethod(FlashcardSet set)
    {
    	return set.getCards();
    }
}


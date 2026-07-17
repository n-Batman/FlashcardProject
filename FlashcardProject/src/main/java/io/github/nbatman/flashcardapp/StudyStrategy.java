package io.github.nbatman.flashcardapp;
import java.util.List;

public interface StudyStrategy
{
	public List<Flashcard>  sortingMethod(FlashcardSet set);
}
//RandomStudyStrategy
//SequentialStudyStrategy
//SpacedRepetitionStrategy

package io.github.nbatman.flashcardapp;
import java.util.List;

public class StudySession
{
	private final List<Flashcard> order;
    private int position = 0;

    public StudySession(FlashcardSet set, StudyStrategy strategy) {
        this.order = strategy.sortingMethod(set);
    }

    public boolean hasNext() {
        return position < order.size();
    }

    public Flashcard next() {
        return order.get(position++);
    }
}

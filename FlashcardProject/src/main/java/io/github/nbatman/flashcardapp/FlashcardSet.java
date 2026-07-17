package io.github.nbatman.flashcardapp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlashcardSet implements Serializable
{
    private List<Flashcard> cards;
    private String name;
    private int id = 0; //and ID of 0 means it has not been given a id by the repository.

    public FlashcardSet(String name) {
        this.cards = new ArrayList<>();
        this.name = name;
    }

    public void addCard(Flashcard card) {
        cards.add(card);
    }

    public Flashcard getRandomCard() {
        Random rand = new Random();
        return cards.get(rand.nextInt(cards.size()));
    }

    public Flashcard getCard(int i)
    {
    	return cards.get(i);
    }

    public List<Flashcard> getCards() {
        return cards;
    }

    public int getSize()
    {
    	return cards.size();
    }

    public String getName()
    {
    	return name;
    }

    public int getId()
    {
    	return id;
    }

    void setId(int nextId)
    {
    	id = nextId;
    }
}


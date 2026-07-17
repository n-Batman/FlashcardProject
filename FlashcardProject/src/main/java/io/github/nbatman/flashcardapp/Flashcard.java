package io.github.nbatman.flashcardapp;

import java.io.Serializable;

public class Flashcard implements Serializable
{
	private String front;
	private String back;

	public Flashcard(String front, String back)
	{
		this.front = front;
		this.back = back;
	}

	public String getFront()
	{
		return front;
	}

	public String getBack()
	{
		return back;
	}

	public void changeFront(String newFront)
	{
		front = newFront;
	}

	public void changeBack(String newBack)
	{
		back = newBack;
	}
}

interface FlashcardFactory
{
	public abstract Flashcard create(String front, String back);
}

class RegularFlashcardFactory implements FlashcardFactory
{
	@Override
	public Flashcard create(String front, String back) {
		// TODO Auto-generated method stub
		return new Flashcard(front, back);
	}
}
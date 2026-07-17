package io.github.nbatman.flashcardapp;


public interface SetRepository
{
	void addSet(FlashcardSet set);
	FlashcardSet getSetById(int productId);
    void updateProduct(FlashcardSet product);
    void deleteProduct(int productId);
}

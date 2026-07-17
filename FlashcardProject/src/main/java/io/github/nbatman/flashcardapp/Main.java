package io.github.nbatman.flashcardapp;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        programStartup();
        FlashcardRepository repository = new FlashcardRepository();

        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" ->
                	{
                		System.out.println("Loading flashcards...");

                		FlashcardSet mySet = repository.getSetById(4);

                		System.out.println(mySet.getName());
                		System.out.println(repository.getAllSets());
                		//StudySession session = new StudySession(mySet, new RandomStudy());
                		//while (session.hasNext()) {
                		   //Flashcard card = session.next();

                		    //some gui option here to show the cards
                		    //System.out.println(card.getFront());
                		    //System.out.println(card.getBack());
                		//}

                	}
                case "2" ->
                {
                    FlashcardSet newSet = setCreation(scanner);
                    repository.addSet(newSet);
                }
                default -> System.out.println("Invalid option. Please enter 1 or 2.");
            }
        }
    }

    private static void programStartup() {
        System.out.println("Program started\n");
        System.out.println("Load a previous set, or make a new one?");
        System.out.println("1. Load a previous set");
        System.out.println("2. Make a new one");
    }

    private static FlashcardSet setCreation(Scanner scanner)
    {
    	System.out.println("What would you like to name the set?");
        String newSetName = scanner.nextLine().trim();
        FlashcardSet newSet = new FlashcardSet(newSetName);
        int i = 1;
    	while (true)
    	{
         System.out.println("What is the info on the front of card " + i);
         String newCardFront = scanner.nextLine().trim();
         System.out.println("What is the info on the back of card " + i);
         String newCardBack = scanner.nextLine().trim();
         i++;

         RegularFlashcardFactory factory = new RegularFlashcardFactory();
         Flashcard newCard = factory.create(newCardFront, newCardBack);

         newSet.addCard(newCard);

         System.out.println("Do you want to exit? y/n");
         if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
			break;
		}
    	}
    	return newSet;
    }

    public void displaySet(FlashcardSet set)
    {
    	for (int i = 0; i < set.getSize(); i++)
    	{
    		Flashcard card = set.getCard(i);
    		System.out.println(card.getFront());
    		System.out.println(card.getBack());
    	}
    }
}

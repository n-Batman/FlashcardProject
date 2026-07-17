package io.github.nbatman.flashcardapp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FlashcardRepository implements SetRepository {
	private final String filename;
	private List<FlashcardSet> sets;

	public FlashcardRepository()
	{
		System.out.println("creating repository");
		File dataDirectory = new File("flashcardset-data");

		if (!dataDirectory.exists()) {
			dataDirectory.mkdir();
		}

		filename = "flashcardset-data/flashcards.dat";
	}

	@Override
	public void addSet(FlashcardSet set) {
		loadSets();

		int newId = getNextId();
	    set.setId(newId);

		sets.add(set);

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename)))
		{
			out.writeObject(sets);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("added " + set.getName());
	}

	@Override
	public FlashcardSet getSetById(int productId) {
		// TODO Auto-generated method stub
		loadSets();

		if (sets == null)
		{
			System.out.println("no sets in file");
			return null;
		}
		for (FlashcardSet set : sets)
	    {
	        if (set.getId() == productId) {
				return set;
			}
	    }
		System.out.println("could not find set by Id");
		return null;
	}

	@Override
	public void updateProduct(FlashcardSet product) {
		loadSets();
		// TODO Auto-generated method stub
		for (int i = 0; i < sets.size(); i++)
	    {
	        if (sets.get(i).getName().equals(product.getName()))
	        {
	        	sets.set(i, product);
	        }
	    }
	}

	@Override
	public void deleteProduct(int productId) {
		loadSets();

		for (FlashcardSet set : sets)
	    {
	        if (set.getId() == productId) {
				sets.remove(set);
			}
	    }
	}

	public List<FlashcardSet> getAllSets()
	{
		return sets;
	}

	private int getNextId() {
	    int maxId = 0;
	    for (FlashcardSet s : sets) {
	        if (s.getId() > maxId) {
				maxId = s.getId();
			}
	    }
	    return maxId + 1;
	}

	@SuppressWarnings("unchecked")
	private void loadSets()
	{
	    File file = new File(filename);

	    if (!file.exists())
	    {
	    	System.out.println("file does not exist");
	        sets = new ArrayList<>();
	        return;
	    }

	    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
	    {
	        Object obj = in.readObject();

	        if (obj instanceof FlashcardSet) //this is the problem.
	        {
	        	sets = new ArrayList<>();
	            sets.add((FlashcardSet) obj);
	        }
	        else if (obj instanceof List<?>)
	        {
	        	sets = (List<FlashcardSet>) obj;
	        } else {
				sets = new ArrayList<>();
			}
	    }
	    catch (IOException | ClassNotFoundException e)
	    {
	        e.printStackTrace();
	        sets = new ArrayList<>();
	    }
	}

}
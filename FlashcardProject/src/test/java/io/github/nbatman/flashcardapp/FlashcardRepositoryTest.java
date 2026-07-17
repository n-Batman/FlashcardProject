package io.github.nbatman.flashcardapp;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FlashcardRepository.
 *
 * ASSUMPTIONS (I don't have FlashcardSet.java / SetRepository.java, only the
 * repository class you posted). I inferred the minimal API used by the
 * repository:
 *
 *   class FlashcardSet implements Serializable {
 *       FlashcardSet(String name) { ... }
 *       int getId();
 *       void setId(int id);
 *       String getName();
 *   }
 *
 * If the real constructor/signatures differ, update the helper method
 * newSet(String) below and everything else should still work.
 *
 * NOTE: FlashcardRepository hardcodes its storage location to
 * "flashcardset-data/flashcards.dat" relative to the working directory —
 * there's no constructor injection for the path. That makes it hard to
 * isolate tests from each other/the real data file, so each test cleans the
 * directory before and after it runs (see setUp/tearDown).
 */
class FlashcardRepositoryTest {

    private static final File DATA_DIR = new File("flashcardset-data");
    private static final File DATA_FILE = new File("flashcardset-data/flashcards.dat");

    private FlashcardRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        deleteDataDir();
        repository = new FlashcardRepository();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteDataDir();
    }

    private void deleteDataDir() throws IOException {
        if (DATA_DIR.exists()) {
            File[] files = DATA_DIR.listFiles();
            if (files != null) {
                for (File f : files) {
                    Files.deleteIfExists(f.toPath());
                }
            }
            Files.deleteIfExists(DATA_DIR.toPath());
        }
    }

    private FlashcardSet newSet(String name) {
        return new FlashcardSet(name);
    }

    // ---------- addSet ----------

    @Test
    void addSet_assignsIncrementingIds() {
        FlashcardSet a = newSet("Spanish Verbs");
        FlashcardSet b = newSet("French Vocab");

        repository.addSet(a);
        repository.addSet(b);

        assertEquals(1, a.getId(), "first set should get id 1");
        assertEquals(2, b.getId(), "second set should get id 2");
    }

    @Test
    void addSet_persistsToDisk_andSurvivesNewRepositoryInstance() {
        FlashcardSet a = newSet("Biology Terms");
        repository.addSet(a);

        // Simulate an app restart: brand new repository instance, same data dir.
        FlashcardRepository reloaded = new FlashcardRepository();
        FlashcardSet found = reloaded.getSetById(a.getId());

        assertNotNull(found, "set should still be found after reloading from disk");
        assertEquals("Biology Terms", found.getName());
    }

    @Test
    void addSet_createsDataDirectoryIfMissing() {
        assertTrue(DATA_DIR.exists() && DATA_DIR.isDirectory(),
                "constructor should create the flashcardset-data directory");

        repository.addSet(newSet("Chemistry"));

        assertTrue(DATA_FILE.exists(), "data file should exist after first addSet");
    }

    // ---------- getSetById ----------

    @Test
    void getSetById_returnsNull_whenFileDoesNotExistYet() {
        // No sets ever added -> loadSets() sees no file -> empty list.
        assertNull(repository.getSetById(1));
    }

    @Test
    void getSetById_returnsNull_whenIdNotFound() {
        repository.addSet(newSet("Kanji"));
        assertNull(repository.getSetById(999));
    }

    @Test
    void getSetById_returnsMatchingSet() {
        FlashcardSet target = newSet("US History");
        repository.addSet(target);

        FlashcardSet found = repository.getSetById(target.getId());

        assertNotNull(found);
        assertEquals(target.getId(), found.getId());
        assertEquals("US History", found.getName());
    }

    // ---------- deleteProduct ----------

    @Test
    void deleteProduct_removesSetFromInMemoryList() {
        FlashcardSet a = newSet("Set A");
        repository.addSet(a);

        repository.deleteProduct(a.getId());

        assertNull(repository.getSetById(a.getId()),
                "getSetById reloads from disk, but deleteProduct never saved -> "
                        + "documenting current behavior below instead");
    }

    @Test
    void deleteProduct_doesNotPersist_currentBehaviorIsABug() {
        // KNOWN ISSUE: deleteProduct() calls loadSets() and mutates the in-memory
        // `sets` list, but never writes it back out. So the deletion is lost
        // the moment anything reloads from disk (e.g. a new repository, or the
        // next call to getSetById/addSet which call loadSets() again).
        FlashcardSet a = newSet("Persisted Anyway");
        repository.addSet(a);

        repository.deleteProduct(a.getId());

        FlashcardRepository reloaded = new FlashcardRepository();
        FlashcardSet stillThere = reloaded.getSetById(a.getId());

        assertNotNull(stillThere,
                "documents that deleteProduct's removal is NOT persisted to disk");
    }

    // ---------- updateProduct ----------

    @Test
    void updateProduct_requiresSetsAlreadyLoaded_orThrows() {
        // KNOWN ISSUE: updateProduct() does not call loadSets() first, unlike
        // every other method. On a freshly constructed repository, `sets` is
        // null, so this throws NullPointerException instead of loading state.
        FlashcardSet a = newSet("Unloaded");
        assertThrows(NullPointerException.class, () -> repository.updateProduct(a));
    }

    @Test
    void updateProduct_matchesByName_notId() {
        FlashcardSet original = newSet("Same Name");
        repository.addSet(original); // this call also populates `sets` via loadSets()

        FlashcardSet replacement = newSet("Same Name");
        replacement.setId(12345); // different id, same name

        repository.updateProduct(replacement);

        // KNOWN ISSUE: matching is done via getName(), not getId(), so a set
        // with an unrelated id but the same name will overwrite the original.
        FlashcardSet result = null;
        for (FlashcardSet s : repository.getAllSets()) {
            if (s.getName().equals("Same Name")) {
                result = s;
            }
        }
        assertNotNull(result);
        assertEquals(12345, result.getId(),
                "update replaced the set purely based on matching name");
    }

    // ---------- loadSets (legacy format) ----------

    @Test
    void loadSets_wrapsLegacySingleObjectFile_inList() throws IOException {
        // Simulate an old data file that stored a single FlashcardSet directly
        // instead of a List<FlashcardSet>, to exercise the backward-compat
        // branch in loadSets().
        FlashcardSet legacy = newSet("Legacy Single Object");
        legacy.setId(7);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(legacy);
        }

        FlashcardSet found = repository.getSetById(7);

        assertNotNull(found, "legacy single-object file should be wrapped into a list");
        assertEquals("Legacy Single Object", found.getName());
    }

    // ---------- getAllSets ----------

    @Test
    void getAllSets_returnsNull_beforeAnyLoadOrAdd() {
        // KNOWN ISSUE: `sets` is only initialized by loadSets(), which is
        // triggered by addSet/getSetById/deleteProduct. A brand new
        // repository that hasn't done any of those yet returns null here
        // instead of an empty list.
        assertNull(repository.getAllSets());
    }

    @Test
    void getAllSets_returnsAllAddedSets() {
        repository.addSet(newSet("One"));
        repository.addSet(newSet("Two"));

        List<FlashcardSet> all = repository.getAllSets();

        assertEquals(2, all.size());
    }
}

package test;


import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import applicationMain.FoundationsMain;
import database.PostDatabase;
import static org.junit.jupiter.api.Assertions.*;

public class ManageThreadTest {

    private PostDatabase postDb;

    /*******
     * <p> Method: setUp </p>
     * 
     * <p> Description: gets a connection to database before testing.</p>
     * 
     */

    @BeforeEach
    void setUp() throws SQLException {
        deleteDatabaseFiles();

        postDb = new PostDatabase();
        postDb.connectToDatabase();

        FoundationsMain.postDatabase = postDb;
    }

    /*******
     * <p> Method: tearDown </p>
     * 
     * <p> Description: eleminates elements and connection after testing.</p>
     * 
     */
    @AfterEach
    void tearDown() {
        if (postDb != null) {
            postDb.closeConnection();
        }
        deleteDatabaseFiles();
    }

    private void deleteDatabaseFiles() {
        String home = System.getProperty("user.home");

        String[] filesToDelete = {
            home + File.separator + "PostDatabase.mv.db",
            home + File.separator + "PostDatabase.trace.db"
        };

        for (String path : filesToDelete) {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /*******
     * <p> Method: testDefaultThreadsExistOnStartup </p>
     * 
     * <p> Description: tests the presence of the default threads.</p>
     * 
     */
    @Test
    void testDefaultThreadsExistOnStartup() {
        List<String> threads = postDb.getAllThreads();

        assertTrue(threads.contains("General"));
        assertTrue(threads.contains("Announcements"));
        assertTrue(threads.contains("Off Topic"));
        assertTrue(threads.contains("Support"));
    }

    /*******
     * <p> Method: testCreateThreadAddsNewThread </p>
     * 
     * <p> Description: tests the creation of new threads.</p>
     * 
     */
    @Test
    void testCreateThreadAddsNewThread() {
        postDb.createThread("Gaming");

        List<String> threads = postDb.getAllThreads();
        assertTrue(threads.contains("Gaming"));
    }

    /*******
     * <p> Method: testCreatingDuplicateThreadsStillShowsDistinctList </p>
     * 
     * <p> Description: tests that creation of duplicate threads does not add additional threads.</p>
     * 
     */
    @Test
    void testCreatingDuplicateThreadsStillShowsDistinctList() {
        postDb.createThread("Sports");
        postDb.createThread("Sports");

        List<String> threads = postDb.getAllThreads();
        assertEquals(1, threads.stream().filter(t -> t.equals("Sports")).count());
    }

    /*******
     * <p> Method: testCreateThreadStoresWhitespaceLiterally </p>
     * 
     * <p> Description: tests that thread names store the literal string provided.</p>
     * 
     */
    @Test
    void testCreateThreadStoresWhitespaceLiterally() {
        postDb.createThread("   Fun Zone   ");

        List<String> threads = postDb.getAllThreads();
        assertTrue(threads.contains("   Fun Zone   "));
    }

    /*******
     * <p> Method: testRenameThreadUpdatesAllPosts </p>
     * 
     * <p> Description: tests that renaming a thread will update it for all posts.</p>
     * 
     */
    @Test
    void testRenameThreadUpdatesAllPosts() {
        postDb.createThread("OldName");
        postDb.createThread("OldName");

        postDb.renameThread("OldName", "NewName");

        List<String> threads = postDb.getAllThreads();
        assertTrue(threads.contains("NewName"));
        assertFalse(threads.contains("OldName"));
    }

    /*******
     * <p> Method: testRenameThreadToExistingThreadMergesThem </p>
     * 
     * <p> Description: tests that renaming a thread to a pre-existing name
     * will merge both threads and all of their posts.</p>
     * 
     */
    @Test
    void testRenameThreadToExistingThreadMergesThem() {
        postDb.createThread("A");
        postDb.createThread("B");

        postDb.renameThread("A", "B");

        List<String> threads = postDb.getAllThreads();

        //default threads + merged "B"
        assertEquals(5, threads.size());
        assertTrue(threads.contains("B"));
        assertFalse(threads.contains("A"));
    }

    /*******
     * <p> Method: testRenameNonexistentThreadDoesNothing </p>
     * 
     * <p> Description: tests that renaming a thread which does not exist does nothing.</p>
     * 
     */
    @Test
    void testRenameNonexistentThreadDoesNothing() {
        postDb.createThread("X");

        postDb.renameThread("DoesNotExist", "NewName");

        List<String> threads = postDb.getAllThreads();

        //default threads + X
        assertEquals(5, threads.size());
        assertTrue(threads.contains("X"));
        assertFalse(threads.contains("NewName"));
    }

    /*******
     * <p> Method: testDeleteThreadRemovesAllPostsInThread </p>
     * 
     * <p> Description: tests that deleting a thread also deletes all posts under that thread.</p>
     * 
     */
    @Test
    void testDeleteThreadRemovesAllPostsInThread() {
        postDb.createThread("DeleteMe");
        postDb.createThread("DeleteMe");
        postDb.createThread("KeepMe");

        postDb.deleteThread("DeleteMe");

        List<String> threads = postDb.getAllThreads();

        //default threads + KeepMe
        assertEquals(5, threads.size());
        assertTrue(threads.contains("KeepMe"));
        assertFalse(threads.contains("DeleteMe"));
    }
    
    /*******
     * <p> Method: testDeleteNonexistentThreadDoesNothing </p>
     * 
     * <p> Description: tests that deleting a nonexistent thread does nothing.</p>
     * 
     */
    @Test
    void testDeleteNonexistentThreadDoesNothing() {
        postDb.createThread("A");

        postDb.deleteThread("DoesNotExist");

        List<String> threads = postDb.getAllThreads();
        assertEquals(5, threads.size());
        assertEquals("A", threads.get(0));
    }

    /*******
     * <p> Method: testThreadNamesAreCaseSensitive </p>
     * 
     * <p> Description: tests that two threads with different capitalization are different threads.</p>
     * 
     */
    @Test
    void testThreadNamesAreCaseSensitive() {
        postDb.createThread("Tech");
        postDb.createThread("tech");

        List<String> threads = postDb.getAllThreads();

        //default threads + Tech + tech
        assertEquals(6, threads.size());
        assertTrue(threads.contains("Tech"));
        assertTrue(threads.contains("tech"));
    }

    /*******
     * <p> Method: testRenameThreadCaseSensitive </p>
     * 
     * <p> Description: tests that renaming a thread keeps case sensitivity.</p>
     * 
     */
    @Test
    void testRenameThreadCaseSensitive() {
        postDb.createThread("Tech");

        postDb.renameThread("tech", "NewName");

        List<String> threads = postDb.getAllThreads();
        assertTrue(threads.contains("Tech"));
        assertFalse(threads.contains("NewName"));
    }

    /*******
     * <p> Method: testDeleteThreadCaseSensitive </p>
     * 
     * <p> Description: tests that deleting a thread keeps case sensitivity.</p>
     * 
     */
    @Test
    void testDeleteThreadCaseSensitive() {
        postDb.createThread("Tech");
        postDb.createThread("tech");

        postDb.deleteThread("Tech");

        List<String> threads = postDb.getAllThreads();

        //default threads + "tech"
        assertEquals(5, threads.size());
        assertTrue(threads.contains("tech"));
        assertFalse(threads.contains("Tech"));
    }

    /*******
     * <p> Method: testRenameThreadToWhitespaceAllowed </p>
     * 
     * <p> Description: tests that renaming a thread allows literal strings.</p>
     * 
     */
    @Test
    void testRenameThreadToWhitespaceAllowed() {
        postDb.createThread("A");

        postDb.renameThread("A", "   ");

        List<String> threads = postDb.getAllThreads();
        assertEquals("   ", threads.get(0));
    }

    /*******
     * <p> Method: testGetAllThreadsReturnsSortedDistinctList </p>
     * 
     * <p> Description: tests the function for returning all threads in a list.</p>
     * 
     */
    @Test
    void testGetAllThreadsReturnsSortedDistinctList() {
        postDb.createThread("Zeta");
        postDb.createThread("Alpha");
        postDb.createThread("Alpha");

        List<String> threads = postDb.getAllThreads();

        //expected: default threads + Alpha + Zeta, sorted alphabetically
        List<String> expected = List.of(
            "Alpha",
            "Announcements",
            "General",
            "Off Topic",
            "Support",
            "Zeta"
        );

        assertEquals(expected, threads);
    }
}

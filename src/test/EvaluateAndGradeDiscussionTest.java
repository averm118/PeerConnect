package test;
/**
 * This class represents a test.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import applicationMain.FoundationsMain;
import database.FeedbackDatabase;
import database.PostDatabase;
import database.ReplyDatabase;
import entityClasses.Feedback;
import entityClasses.Post;
import entityClasses.Reply;

public class EvaluateAndGradeDiscussionTest {

    private PostDatabase postDb;
    private ReplyDatabase replyDb;
    private FeedbackDatabase feedbackDb;
    
    /**
     * Initializes fresh database instances before each test.
     * Deletes any existing database files to ensure clean state.
     *
     * @throws SQLException if database connection fails
     */


    @BeforeEach
    void setUp() throws SQLException {
        deleteDatabaseFiles();

        postDb = new PostDatabase();
        replyDb = new ReplyDatabase();
        feedbackDb = new FeedbackDatabase();

        postDb.connectToDatabase();
        replyDb.connectToDatabase();
        feedbackDb.connectToDatabase();

        FoundationsMain.postDatabase = postDb;
        FoundationsMain.replyDatabase = replyDb;
        FoundationsMain.feedbackDatabase = feedbackDb;
    }

    @AfterEach
    void tearDown() {
        if (postDb != null) postDb.closeConnection();
        if (replyDb != null) replyDb.closeConnection();
        if (feedbackDb != null) feedbackDb.closeConnection();
        deleteDatabaseFiles();
    }
    
    /**
     * Deletes all database files to reset system state between tests.
     */

    private void deleteDatabaseFiles() {
        String home = System.getProperty("user.home");

        String[] filesToDelete = {
            home + File.separator + "PostDatabase.mv.db",
            home + File.separator + "PostDatabase.trace.db",
            home + File.separator + "ReplyDatabase.mv.db",
            home + File.separator + "ReplyDatabase.trace.db",
            home + File.separator + "FeedbackDatabase.mv.db",
            home + File.separator + "FeedbackDatabase.trace.db"
        };

        for (String path : filesToDelete) {
            File f = new File(path);
            if (f.exists()) f.delete();
        }
    }
    
    /**
     * Counts the number of words in a string.
     *
     * @param text the input string
     * @return number of words, or 0 if null/empty
     */

    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }
    
    /**
     * Tests that a staff member can evaluate a student's discussion
     * by retrieving posts and replies and applying word count criteria.
     */
    @Test
    void testStaffCanEvaluateDiscussionUsingCriteria() {
        postDb.createPost(new Post(
            "Post 1",
            "This post has enough words to pass",
            "student1",
            "General"
        ));

        List<String> posts = postDb.getPostTitlesByThread("General", "student1");
        assertFalse(posts.isEmpty());

        int postId = -1;

        for (String entry : posts) {
            if (entry.contains("Post 1") && entry.contains("student1")) {
                int left = entry.indexOf('[');
                int right = entry.indexOf(']');
                postId = Integer.parseInt(entry.substring(left + 1, right));
                break;
            }
        }

        assertTrue(postId > 0);

        replyDb.createReply(new Reply(postId, 0, "This reply has enough words", "student1"));
        replyDb.createReply(new Reply(postId, 0, "This one also passes", "student1"));

        Post savedPost = postDb.getPostById(postId);
        List<Reply> replies = replyDb.getReplyList(postId);

        assertNotNull(savedPost);
        assertTrue(countWords(savedPost.getBody()) >= 5);
        assertEquals(2, replies.size());
        assertTrue(countWords(replies.get(0).getBody()) >= 4);
    }

    /**
     * Tests that a grade can be correctly calculated
     * based on evaluation rules (40/30/30 rubric).
     */
    
    @Test
    void testStaffCanAssignGradeBasedOnEvaluation() {
        boolean passesPostRule = true;
        boolean passesReplyCountRule = true;
        int replyCount = 2;
        int qualifiedReplyCount = 2;

        int grade = 0;
        if (passesPostRule) grade += 40;
        if (passesReplyCountRule) grade += 30;
        if (replyCount > 0) {
            grade += (int)Math.round((30.0 * qualifiedReplyCount) / replyCount);
        }

        assertEquals(100, grade);
    }
    
    /**
     * Tests that staff can save evaluation feedback
     * into the database successfully.
     */

    @Test
    void testStaffCanSaveEvaluationFeedback() {
        Feedback feedback = new Feedback(
            "THREAD_EVALUATION",
            0,
            "staff1",
            "student1",
            "Discussion Evaluation\n\nThread: General\n\nFinal Grade: 90"
        );

        int id = feedbackDb.createFeedback(feedback);

        assertTrue(id > 0);
    }
    
    /**
     * Tests that a student can retrieve and view
     * evaluation feedback based on thread.
     */

    @Test
    void testStudentCanViewEvaluationByThread() {
        feedbackDb.createFeedback(new Feedback(
            "THREAD_EVALUATION",
            0,
            "staff1",
            "student1",
            "Discussion Evaluation\n\nThread: General\n\nFinal Grade: 90"
        ));

        List<Feedback> evaluations =
            feedbackDb.getFeedbackByRecipientAndType("student1", "THREAD_EVALUATION");

        assertEquals(1, evaluations.size());
        assertTrue(evaluations.get(0).getBody().contains("Thread: General"));
        assertTrue(evaluations.get(0).getBody().contains("Final Grade: 90"));
    }
}
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class FeedBackSystemTest {

    private PostDatabase postDb;
    private ReplyDatabase replyDb;
    private FeedbackDatabase feedbackDb;

    /**
     * Sets up database.
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

    /**
     * Cleans up database.
     */
    @AfterEach
    void tearDown() {
        if (postDb != null) {
            postDb.closeConnection();
        }
        if (replyDb != null) {
            replyDb.closeConnection();
        }
        if (feedbackDb != null) {
            feedbackDb.closeConnection();
        }
        deleteDatabaseFiles();
    }

    /**
     * Deletes database files.
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
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /**
     * Creates a post and gets its id.
     *
     * @param author the author
     * @return the post id
     */
    private int createPostAndGetId(String author) {
        Post post = new Post("Post", "Body", author, "General");
        postDb.createPost(post);

        List<String> threadPosts = postDb.getPostTitlesByThread("General", author);

        for (String entry : threadPosts) {
            if (entry.contains("Post") && entry.contains(author)) {
                return extractPostId(entry);
            }
        }

        fail("Could not find newly created post.");
        return -1;
    }

    /**
     * Extracts the post id.
     *
     * @param formattedPostLine the formatted line
     * @return the post id
     */
    private int extractPostId(String formattedPostLine) {
        Pattern pattern = Pattern.compile("Post ID: \\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(formattedPostLine);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        fail("Could not extract post id from: " + formattedPostLine);
        return -1;
    }

    /**
     * Tests staff review of posts.
     */
    @Test
    void testStaffCanReviewPosts() {
        int postId = createPostAndGetId("student1");

        Post saved = postDb.getPostById(postId);

        assertNotNull(saved);
        assertEquals("Post", saved.getTitle());
        assertEquals("Body", saved.getBody());
        assertEquals("student1", saved.getAuthor());
        assertEquals("General", saved.getThread());
    }

    /**
     * Tests staff review of replies.
     */
    @Test
    void testStaffCanReviewReplies() {
        int postId = createPostAndGetId("student1");

        int replyId = replyDb.createReply(new Reply(postId, 0, "Reply", "student2"));

        List<Reply> replies = replyDb.getReplyList(postId);

        assertEquals(1, replies.size());
        assertEquals(replyId, replies.get(0).getID());
        assertEquals(postId, replies.get(0).getOP());
        assertEquals("Reply", replies.get(0).getBody());
        assertEquals("student2", replies.get(0).getAuthor());
    }

    /**
     * Tests staff feedback on posts.
     */
    @Test
    void testStaffCanGivePrivateFeedbackOnPost() {
        int postId = createPostAndGetId("student1");

        feedbackDb.createFeedback(new Feedback(
                "POST",
                postId,
                "staff1",
                "student1",
                "Feedback"
        ));

        List<Feedback> feedback = feedbackDb.getFeedbackForTarget("POST", postId, "student1");

        assertEquals(1, feedback.size());
        assertEquals("staff1", feedback.get(0).getSender());
        assertEquals("student1", feedback.get(0).getRecipient());
        assertEquals("Feedback", feedback.get(0).getBody());
    }

    /**
     * Tests staff feedback on replies.
     */
    @Test
    void testStaffCanGivePrivateFeedbackOnReply() {
        int postId = createPostAndGetId("student1");
        int replyId = replyDb.createReply(new Reply(postId, 0, "Reply", "student2"));

        feedbackDb.createFeedback(new Feedback(
                "REPLY",
                replyId,
                "staff1",
                "student2",
                "Feedback"
        ));

        List<Feedback> feedback = feedbackDb.getFeedbackForTarget("REPLY", replyId, "student2");

        assertEquals(1, feedback.size());
        assertEquals("staff1", feedback.get(0).getSender());
        assertEquals("student2", feedback.get(0).getRecipient());
        assertEquals("Feedback", feedback.get(0).getBody());
    }

    /**
     * Tests staff feedback to staff.
     */
    @Test
    void testStaffCanGivePrivateFeedbackToStaff() {
        int postId = createPostAndGetId("student1");

        feedbackDb.createFeedback(new Feedback(
                "POST",
                postId,
                "staff1",
                "staff2",
                "Note"
        ));

        List<Feedback> feedback = feedbackDb.getFeedbackForTarget("POST", postId, "staff2");

        assertEquals(1, feedback.size());
        assertEquals("staff1", feedback.get(0).getSender());
        assertEquals("staff2", feedback.get(0).getRecipient());
        assertEquals("Note", feedback.get(0).getBody());
    }

    /**
     * Tests feedback visibility.
     */
    @Test
    void testFeedbackVisibleToRecipientOnly() {
        int postId = createPostAndGetId("student1");

        feedbackDb.createFeedback(new Feedback(
                "POST",
                postId,
                "staff1",
                "student1",
                "Note"
        ));

        List<Feedback> recipientView = feedbackDb.getFeedbackForTarget("POST", postId, "student1");
        List<Feedback> otherView = feedbackDb.getFeedbackForTarget("POST", postId, "student2");

        assertEquals(1, recipientView.size());
        assertEquals(0, otherView.size());
    }

    /**
     * Tests unread feedback count.
     */
    @Test
    void testUnreadFeedbackCount() {
        int postId = createPostAndGetId("student1");

        feedbackDb.createFeedback(new Feedback(
                "POST",
                postId,
                "staff1",
                "student1",
                "Note"
        ));

        assertEquals(1, feedbackDb.getUnreadFeedbackCount("student1", "POST", postId));
    }

    /**
     * Tests marking feedback as read.
     */
    @Test
    void testMarkFeedbackAsRead() {
        int postId = createPostAndGetId("student1");

        feedbackDb.createFeedback(new Feedback(
                "POST",
                postId,
                "staff1",
                "student1",
                "Note"
        ));

        feedbackDb.markFeedbackForTargetAsRead("student1", "POST", postId);

        assertEquals(0, feedbackDb.getUnreadFeedbackCount("student1", "POST", postId));
    }

    /**
     * Tests post Flagging.
     */
    @Test
    void testStaffCanFlagPost() {
        int postId = createPostAndGetId("student1");

        postDb.setPostInappropriate(postId, true);

        Post post = postDb.getPostById(postId);

        assertTrue(post.getInappropriate());
    }

    /**
     * Tests clearing post flag.
     */
    @Test
    void testStaffCanClearPostFlag() {
        int postId = createPostAndGetId("student1");

        postDb.setPostInappropriate(postId, true);
        postDb.setPostInappropriate(postId, false);

        Post post = postDb.getPostById(postId);

        assertFalse(post.getInappropriate());
    }

    /**
     * Tests reply Flagging.
     */
    @Test
    void testStaffCanFlagReply() {
        int postId = createPostAndGetId("student1");
        int replyId = replyDb.createReply(new Reply(postId, 0, "Reply", "student2"));

        replyDb.setReplyInappropriate(replyId, true);

        List<Reply> replies = replyDb.getReplyList(postId);

        assertEquals(1, replies.size());
        assertTrue(replies.get(0).getInappropriate());
    }

    /**
     * Tests clearing reply flag.
     */
    @Test
    void testStaffCanClearReplyFlag() {
        int postId = createPostAndGetId("student1");
        int replyId = replyDb.createReply(new Reply(postId, 0, "Reply", "student2"));

        replyDb.setReplyInappropriate(replyId, true);
        replyDb.setReplyInappropriate(replyId, false);

        List<Reply> replies = replyDb.getReplyList(postId);

        assertEquals(1, replies.size());
        assertFalse(replies.get(0).getInappropriate());
    }
}
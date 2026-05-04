package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Post;

/**
 * This class handles database operations for posts.
 * 
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class PostDatabase {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/PostDatabase";

    // Database Credentials
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;
    
	private String currentThread;
	private String currentAuthor;
	private String currentBody;
	private String currentTitle;
	
	/**
	 * This constructor creates a PostDatabase object.
	 */
	public PostDatabase() {}

    /*******
     * <p> Method: connectToDatabase </p>
     * 
     * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
     *		storage.</p>
     *
     * @throws SQLException when the DriverManager is unable to establish a connection
     * 
     */
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
			//statement.execute("DROP ALL OBJECTS");
            createTables();        
            createThread("General");
            createThread("Announcements");
            createThread("Off Topic");
            createThread("Support");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

	
    /*******
     * <p> Method: createTables </p>
     * 
     * <p> Description: Used to create a new instances of the database table used by this class.</p>
     * 
     */
    private void createTables() throws SQLException {
        String postTable = "CREATE TABLE IF NOT EXISTS Posts ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "thread VARCHAR(255), "
                + "title VARCHAR(255), "
                + "body CLOB, "
                + "author VARCHAR(255), "
                + "inappropriate BOOLEAN"
                + ")";
        statement.execute(postTable);
        
        try {
            statement.execute("ALTER TABLE Posts ADD COLUMN inappropriate BOOLEAN");
        } catch (SQLException ignored) {}
        
        String readStatus = "CREATE TABLE IF NOT EXISTS ReadStatus ("
        	    + "username VARCHAR(255) NOT NULL, "
        	    + "post_id INT NOT NULL, "
        	    + "PRIMARY KEY (username, post_id), "
        	    + "FOREIGN KEY (post_id) REFERENCES Posts(id)"
        	    + ")";
        statement.execute(readStatus);
    }

    /**
     * <p> Method: createPost(String title, String body, String author) </p>
     * 
     * <p> Description: Creates a new row in the database using the post parameter. </p>
     * 
     * 
     * @param post the post object to add to the database
   	 * @return the id of the newly created post, or -1 if the post was not created
     */
    public int createPost(Post post) {
    	String query = "INSERT INTO Posts (thread, title, body, author, inappropriate) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        	currentThread = post.getThread();
            pstmt.setString(1, currentThread);
            currentTitle = post.getTitle();
            pstmt.setString(2, currentTitle);
            currentBody = post.getBody();
            pstmt.setString(3, currentBody);
            currentAuthor = post.getAuthor();
            pstmt.setString(4, currentAuthor);
            pstmt.setBoolean(5, post.getInappropriate());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * <p> Method: editPost(String title, String body, String author) </p>
     * 
     * <p> Description: Creates a new row in the database using the post parameter. </p>
     * 
     * @param post the post object containing updated values
     * @return -1 after attempting to update the post
     * 
     */
    public int editPost(Post post) {
    	String query = "UPDATE Posts SET thread = ?, title = ?, body = ?, author = ?, inappropriate = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        	currentThread = post.getThread();
            pstmt.setString(1, currentThread);
            currentTitle = post.getTitle();
            pstmt.setString(2, currentTitle);
            currentBody = post.getBody();
            pstmt.setString(3, currentBody);
            currentAuthor = post.getAuthor();
            pstmt.setString(4, currentAuthor);
            pstmt.setBoolean(5, post.getInappropriate());
            pstmt.setInt(6, post.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This method marks a post as deleted.
     *
     * @param id the id of the post to delete
     */
    public void deletePost(int id) {
        String query = "UPDATE Posts SET title = ?, body = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Original post deleted");
            pstmt.setString(2, "Original post deleted");
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method completely removes a post.
     *
     * @param id the id of the post to remove
     */
    public void deletePostCompletely(int id) {
        String deleteReadStatus = "DELETE FROM ReadStatus WHERE post_id = ?";
        String deletePost = "DELETE FROM Posts WHERE id = ?";

        try (PreparedStatement pstmt1 = connection.prepareStatement(deleteReadStatus);
             PreparedStatement pstmt2 = connection.prepareStatement(deletePost)) {

            pstmt1.setInt(1, id);
            pstmt1.executeUpdate();

            pstmt2.setInt(1, id);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method marks a post as read for a user.
     *
     * @param username the username of the user
     * @param postId the id of the post
     */
    public void markPostAsRead(String username, int postId) {
        String sql = "MERGE INTO ReadStatus (username, post_id) " 
        		   + "KEY (username, post_id) " 
        		   + "VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method checks whether a post is unread for a user.
     *
     * @param username the username of the user
     * @param postId the id of the post
     * @return true if the post is unread, otherwise false
     */
    public boolean isPostUnread(String username, int postId) {
        String sql = "SELECT 1 FROM ReadStatus WHERE username = ? AND post_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, postId);

            ResultSet rs = pstmt.executeQuery();
            return !rs.next(); //unread if no record exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /*******
     *  <p> Method: List getPostByID() </p>
     *  
     *  <P> Description: gets a post object from a given id. </p>
     *  
     * @param id the id of the post
     * @return the post with the given id, or null if no post is found
     */
    public Post getPostById(int id) {
    	String query = "SELECT id, title, body, author, thread, inappropriate FROM Posts WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Post(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("body"),
                    rs.getString("author"),
                    rs.getString("thread"),
                    rs.getBoolean("inappropriate")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // no post found
    }
    
    /*******
     * <p> Method: createThread(String threadName) </p>
     * 
     * <p> Description: Creates thread in the database. </p>
     * 
     * @param threadName the name of the thread
     * @return -1 after attempting to create the thread
     * 
     */
    public int createThread(String threadName) {
        String query = "INSERT INTO Posts (thread) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, threadName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /*******
     *  <p> Method: List getPostTitlesByThread(String threadName, String currentUsername) </p>
     *  
     *  <P> Description: Generate a List of Strings, one for each post in the selected thread. </p>
     *  
     *  @param threadName specifies the name of the thread to search
     *  
     *  @param currentUsername specifies the username of the current user
     *  
     *  @return a list of post titles found in the thread in the database.
     */
    public List<String> getPostTitlesByThread(String threadName, String currentUsername) {
        List<String> posts = new ArrayList<>();

        String query;

        if (threadName.equals("ALL")) {
            query = "SELECT id, title, author FROM Posts ORDER BY id DESC";
        } else {
            query = "SELECT id, title, author FROM Posts WHERE thread = ? ORDER BY id DESC";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            if (!threadName.equals("ALL")) {
                pstmt.setString(1, threadName);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Post p = getPostById(id);
                
                if (p == null) continue;
                
                //skips rendering thread only entries
                if (p.getAuthor() == null) continue;
                
                String title = rs.getString("title");
                String author = rs.getString("author");
                String thread = p.getThread();
                
                int replyCount = applicationMain.FoundationsMain.replyDatabase.getReplyList(id).size();
                int unreadCount = applicationMain.FoundationsMain.replyDatabase.getUnreadReplyCount(
                        currentUsername,
                        id
                );
                
                int unreadFeedbackCount = applicationMain.FoundationsMain.feedbackDatabase.getUnreadFeedbackCount(
                        currentUsername,
                        "POST",
                        id
                );
                String line1 = String.format("%s%s — %s", p.getInappropriate() ? "[FLAGGED] " : "", title, author);
                String line2 = String.format("Post ID: [%d] — Thread: %s — %d replies (%d unread) — %d feedback unread", id, thread, replyCount, unreadCount, unreadFeedbackCount);;

                posts.add(line1 + "\n" + line2);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    
    /*******
     *  <p> Method: List getThreadList() </p>
     *  
     *  <P> Description: Generate a List of Strings, one for each thread in the database,
     *  starting with "ALL" at the start of the list. </p>
     *  
     *  @return a list of threads found in the database.
     */
    public List<String> getThreadList() {
        List<String> threads = new ArrayList<>();
        threads.add("ALL");

        String query = "SELECT DISTINCT thread FROM Posts ORDER BY thread ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                threads.add(rs.getString("thread"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return threads;
    }

    /*******
     *  <p> Method: List getAllThreads() </p>
     *  
     *  <P> Description: Generate a List of Strings, one for each thread in the database,
     *  seperate from list function for selecting thread to apply post. </p>
     *  
     *  @return a list of threads found in the database.
     */
    public List<String> getAllThreads() {
    	List<String> threads = new ArrayList<>();

        String query = "SELECT DISTINCT thread FROM Posts ORDER BY thread ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                threads.add(rs.getString("thread"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return threads;
    }
    
    /*******
     *  <p> Method: List renameThread() </p>
     *  
     *  <P> Description: renames a thread by updating all posts belonging to that thread. </p>
     *  
     */
    public void renameThread(String oldName, String newName) {
        String sql = "UPDATE Posts SET thread = ? WHERE thread = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        deleteThread(oldName);
    }

    /*******
     *  <p> Method: List deleteThread() </p>
     *  
     *  <P> Description:  Deletes a thread by deleting all posts in it.</p>
     *  
     */
    public void deleteThread(String threadName) {
        String deleteReadStatus = 
            "DELETE FROM ReadStatus WHERE post_id IN (SELECT id FROM Posts WHERE TRIM(LOWER(thread)) = TRIM(LOWER(?)))";

        String deletePosts = 
            "DELETE FROM Posts WHERE TRIM(LOWER(thread)) = TRIM(LOWER(?))";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps1 = connection.prepareStatement(deleteReadStatus)) {
                ps1.setString(1, threadName);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = connection.prepareStatement(deletePosts)) {
                ps2.setString(1, threadName);
                ps2.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
    
    /**
     * This method updates whether a post is inappropriate.
     *
     * @param postId the id of the post
     * @param inappropriate whether the post is inappropriate
     */
    public void setPostInappropriate(int postId, boolean inappropriate) {
        String query = "UPDATE Posts SET inappropriate = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, inappropriate);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method closes the database connection.
     */
    public void closeConnection() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException ignored) {}

        try {
            if (connection != null) connection.close();
        } catch (SQLException ignored) {}
    }
}
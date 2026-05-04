package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Feedback;

/**
 * This class handles database operations for feedback.
 * 
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class FeedbackDatabase {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/FeedbackDatabase";

    // Database Credentials
    static final String USER = "sa";
    static final String PASS = "";

    /**
     * This stores the database connection.
     */
    private Connection connection = null;
    
    /**
     * This stores the SQL statement.
     */
    private Statement statement = null;
    
    /**
     * This stores the current feedback type.
     */
    private String currentFeedbackType;
    
    /**
     * This stores the current target id.
     */
    private int currentTargetId;
    
    /**
     * This stores the current sender.
     */
    private String currentSender;
    
    /**
     * This stores the current recipient.
     */
    private String currentRecipient;
    
    /**
     * This stores the current body.
     */
    private String currentBody;
    
    /**
     * This constructor creates a FeedbackDatabase object.
     */
    public FeedbackDatabase() {}

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
            createTables();
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
        String feedbackTable = "CREATE TABLE IF NOT EXISTS Feedback ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "feedbackType VARCHAR(255), "
                + "targetId INT, "
                + "sender VARCHAR(255), "
                + "recipient VARCHAR(255), "
                + "body CLOB)";
        statement.execute(feedbackTable);
        
        String feedbackReadStatusTable = "CREATE TABLE IF NOT EXISTS FeedbackReadStatus ("
                + "username VARCHAR(255), "
                + "feedbackId INT, "
                + "PRIMARY KEY (username, feedbackId))";
        statement.execute(feedbackReadStatusTable);
    }

    /*******
     *  <p> Method: createFeedback(Feedback feedback) </p>
     *  
     *  <P> Description: Creates a new feedback in the database. </p>
     *  
     * @param feedback the feedback to create
     * @return the id of the feedback
     */
    public int createFeedback(Feedback feedback) {
        String query = "INSERT INTO Feedback (feedbackType, targetId, sender, recipient, body) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            currentFeedbackType = feedback.getFeedbackType();
            currentTargetId = feedback.getTargetId();
            currentSender = feedback.getSender();
            currentRecipient = feedback.getRecipient();
            currentBody = feedback.getBody();
            
            pstmt.setString(1, currentFeedbackType);
            pstmt.setInt(2, currentTargetId);
            pstmt.setString(3, currentSender);
            pstmt.setString(4, currentRecipient);
            pstmt.setString(5, currentBody);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                markFeedbackAsRead(currentSender, id);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * Gets all feedback entries for a specific recipient and feedback type.
     *
     * @param recipient the username of the person receiving the feedback
     * @param feedbackType the type of feedback to search for
     * @return a list of matching feedback entries
     */

    public List<Feedback> getFeedbackForTarget(String feedbackType, int targetId, String username) {
        List<Feedback> feedbackList = new ArrayList<>();  // Store all matching feedback entries here
        String query = "SELECT * FROM Feedback WHERE feedbackType = ? AND targetId = ? "
                + "AND (sender = ? OR recipient = ?) ORDER BY id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, feedbackType);
            pstmt.setInt(2, targetId);
            pstmt.setString(3, username);
            pstmt.setString(4, username);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("id"),
                        rs.getString("feedbackType"),
                        rs.getInt("targetId"),
                        rs.getString("sender"),
                        rs.getString("recipient"),
                        rs.getString("body")
                );
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return feedbackList;
    }

    /*******
     *  <p> Method: markFeedbackAsRead(String username, int feedbackId) </p>
     *  
     *  <P> Description: Marks a feedback as read for a user. </p>
     *  
     * @param username the username
     * @param feedbackId the id of the feedback
     */
    public void markFeedbackAsRead(String username, int feedbackId) {
        String query = "MERGE INTO FeedbackReadStatus (username, feedbackId) KEY (username, feedbackId) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, feedbackId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*******
     *  <p> Method: markFeedbackForTargetAsRead(String username, String feedbackType, int targetId) </p>
     *  
     *  <P> Description: Marks all feedback for a target as read for a user. </p>
     *  
     * @param username the username
     * @param feedbackType the type of feedback
     * @param targetId the id of the target
     */
    public void markFeedbackForTargetAsRead(String username, String feedbackType, int targetId) {
        List<Feedback> feedbackList = getFeedbackForTarget(feedbackType, targetId, username);
        
        for (Feedback feedback : feedbackList) {
            markFeedbackAsRead(username, feedback.getId());
        }
    }

    /*******
     *  <p> Method: isFeedbackUnread(String username, int feedbackId) </p>
     *  
     *  <P> Description: Checks if a feedback is unread for a user. </p>
     *  
     * @param username the username
     * @param feedbackId the id of the feedback
     * @return true if unread, otherwise false
     */
    public boolean isFeedbackUnread(String username, int feedbackId) {
        String query = "SELECT * FROM FeedbackReadStatus WHERE username = ? AND feedbackId = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, feedbackId);
            
            ResultSet rs = pstmt.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /*******
     *  <p> Method: getVisibleFeedbackCount(String username, String feedbackType, int targetId) </p>
     *  
     *  <P> Description: Gets the number of visible feedback for a target. </p>
     *  
     * @param username the username
     * @param feedbackType the type of feedback
     * @param targetId the id of the target
     * @return the number of visible feedback
     */
    public int getVisibleFeedbackCount(String username, String feedbackType, int targetId) {
        int count = 0;
        List<Feedback> feedbackList = getFeedbackForTarget(feedbackType, targetId, username);
        
        for (Feedback feedback : feedbackList) {
            count++;
        }
        
        return count;
    }

    /*******
     *  <p> Method: getUnreadFeedbackCount(String username, String feedbackType, int targetId) </p>
     *  
     *  <P> Description: Gets the number of unread feedback for a target. </p>
     *  
     * @param username the username
     * @param feedbackType the type of feedback
     * @param targetId the id of the target
     * @return the number of unread feedback
     */
    public int getUnreadFeedbackCount(String username, String feedbackType, int targetId) {
        int count = 0;
        List<Feedback> feedbackList = getFeedbackForTarget(feedbackType, targetId, username);
        
        for (Feedback feedback : feedbackList) {
            if (!feedback.getSender().equals(username) && isFeedbackUnread(username, feedback.getId())) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Gets all feedback entries for a specific recipient and feedback type.
     *
     * This method searches the Feedback table for rows that match the given
     * recipient and feedback type, then returns them as a list of Feedback objects.
     * The results are ordered from newest to oldest by id.
     *
     * @param recipient the username of the person receiving the feedback
     * @param feedbackType the type of feedback to search for
     * @return a list of matching Feedback objects, or an empty list if none are found
     */

    public List<Feedback> getFeedbackByRecipientAndType(String recipient, String feedbackType) {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM Feedback WHERE recipient = ? AND feedbackType = ? ORDER BY id DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, recipient);
            pstmt.setString(2, feedbackType);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("id"),
                        rs.getString("feedbackType"),
                        rs.getInt("targetId"),
                        rs.getString("sender"),
                        rs.getString("recipient"),
                        rs.getString("body")
                );
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return feedbackList;
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
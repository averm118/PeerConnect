package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entityClasses.Reply;

/**
 * This class handles database operations for replies.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class ReplyDatabase {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/ReplyDatabase";

    // Database Credentials
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;
    
    private int currentID;
    private int currentOP;
    private int currentParent;
	private String currentAuthor;
	private String currentBody;
	
	/**
	 * This constructor creates a ReplyDatabase object.
	 */	
    public ReplyDatabase() {}

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
        String replyTable = "CREATE TABLE IF NOT EXISTS Replies ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "op INT, "
                + "parent INT, "
                + "body CLOB, "
                + "author VARCHAR(255), "
                + "inappropriate BOOLEAN"
                + ")";
        statement.execute(replyTable);
        
        try {
        	statement.execute("ALTER TABLE Replies ADD COLUMN inappropriate BOOLEAN");
        } catch (SQLException ignored) {}
        
        String replyReadStatus = "CREATE TABLE IF NOT EXISTS ReplyReadStatus ("
                + "username VARCHAR(255) NOT NULL, "
                + "reply_id INT NOT NULL, "
                + "PRIMARY KEY (username, reply_id), "
                + "FOREIGN KEY (reply_id) REFERENCES Replies(id)"
                + ")";
        statement.execute(replyReadStatus);
    }

    /*******
     * <p> Method: createReply(String body, String author) </p>
     * 
     * <p> Description: Creates a new row in the database using the reply parameter. </p>
     * 
     * @param reply the reply object to add to the database
     * @return the id of the newly created reply, or -1 if the reply was not created
     * 
     */
    public int createReply(Reply reply) {
    	 String query = "INSERT INTO Replies (op, parent, body, author, inappropriate) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        	currentOP = reply.getOP();
        	pstmt.setInt(1, currentOP);
        	currentParent = reply.getParent();
        	pstmt.setInt(2, currentParent);
            currentBody = reply.getBody();
            pstmt.setString(3, currentBody);
            currentAuthor = reply.getAuthor();
            pstmt.setString(4, currentAuthor);
            pstmt.setBoolean(5, reply.getInappropriate());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {            
            	int newId = rs.getInt(1);
            	//mark the author's own reply as read
            	markReplyAsRead(reply.getAuthor(), newId);
            	return newId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    
    /*******
     * <p> Method: editReply(Reply reply) </p>
     * 
     * <p> Description: edits a row in the database using the reply parameter. </p>
     * 
     * @param reply the reply object containing updated values
     * @return -1 after attempting to update the reply
     * 
     */
    public int editReply(Reply reply) {
    	String query = "UPDATE Replies SET body = ?, inappropriate = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            currentBody = reply.getBody();
            pstmt.setString(1, currentBody);
            pstmt.setBoolean(2, reply.getInappropriate());
            currentID = reply.getID();
            pstmt.setInt(3, currentID);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*******
     *  <p> Method: void deleteReply(int id) </p>
     *  
	 * @param id the id of the reply to delete
     */
    public void deleteReply(int id) {
    	String query = "UPDATE Replies SET body = 'Original reply deleted' WHERE id = ?";

    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setInt(1, id);
    		pstmt.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * This method completely removes a reply.
     *
     * @param id the id of the reply to remove
     */
    public void deleteReplyCompletely(int id) {
    	String deleteReadStatus = "DELETE FROM ReplyReadStatus WHERE reply_id = ?";
    	String deleteReply = "DELETE FROM Replies WHERE id = ?";

    	try (PreparedStatement pstmt1 = connection.prepareStatement(deleteReadStatus);
    		 PreparedStatement pstmt2 = connection.prepareStatement(deleteReply)) {

    		pstmt1.setInt(1, id);
    		pstmt1.executeUpdate();

    		pstmt2.setInt(1, id);
    		pstmt2.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    /*******
     *  <p> Method: void markRepliesAsRead(String username, int op) </p>
     *  
     * This method marks all replies under a post as read for a user.
     *
     * @param username the username of the user
     * @param op the id of the original post
     */
    public void markRepliesAsRead(String username, int op) {
        String sql = "MERGE INTO ReplyReadStatus (username, reply_id) "
        		   + "KEY (username, reply_id) " 
        		   + "SELECT ?, id FROM Replies WHERE op = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, op);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /*******
     *  <p> Method: void markReplyAsRead(String username, int replyId) </p>
     *  
     * This method marks a single reply as read for a user.
     *
     * @param username the username of the user
     * @param replyId the id of the reply
     */
    public void markReplyAsRead(String username, int replyId) {
        String sql = "MERGE INTO ReplyReadStatus (username, reply_id) " 
        		   + "KEY (username, reply_id) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, replyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
    /*******
     *  <p> Method: void isReplyUnread(String username, int replyId) </p>
     *  
     *
     * @param username the username of the user
     * @param replyId the id of the reply
     * @return true if the reply is unread, otherwise false
     */
    public boolean isReplyUnread(String username, int replyId) {
        String sql = "SELECT 1 FROM ReplyReadStatus WHERE username = ? AND reply_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, replyId);

            ResultSet rs = pstmt.executeQuery();
            return !rs.next(); //unread if no record exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * This method gets the number of unread replies for a user under a post.
     *
     * @param username the username of the user
     * @param op the id of the original post
     * @return the number of unread replies
     */
    
    public int getUnreadReplyCount(String username, int op) {
        String sql = "SELECT COUNT(*) FROM Replies r "
                   + "WHERE r.op = ? AND r.id NOT IN ("
                   + "    SELECT reply_id FROM ReplyReadStatus WHERE username = ?"
                   + ")";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, op);
            pstmt.setString(2, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



    /*******
     *  <p> Method: List getReplyList() </p>
     *  
     *  <P> Description: Generate a List of Strings, one for each post in the database. </p>
     *  
     * @param op the id of the original post
     * @return a list of replies for the post
     *  
     */
    public List<Reply> getReplyList(int op) {
        List<Reply> list = new ArrayList<>();

        String query = "SELECT id, parent, body, author, inappropriate FROM Replies WHERE op = ? ORDER BY id";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, op);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
            	Reply r = new Reply(
                        rs.getInt("id"),
                        op,
                        rs.getInt("parent"),
                        rs.getString("body"),
                        rs.getString("author"),
                        rs.getBoolean("inappropriate")
                    );
                    list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    /**
     * This method updates whether a reply is inappropriate.
     *
     * @param replyId the id of the reply
     * @param inappropriate whether the reply is inappropriate
     */
    public void setReplyInappropriate(int replyId, boolean inappropriate) {
        String query = "UPDATE Replies SET inappropriate = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, inappropriate);
            pstmt.setInt(2, replyId);
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
package entityClasses;

/**
 * This class represents private feedback between users
 * Feedback is only visible to the sender and the recipient
 *
 * Feedback can be attached to either a POST or a REPLY
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class Feedback {

    private int id;
    private String feedbackType;
    private int targetId;
    private String sender;
    private String recipient;
    private String body;

    /**
     * This constructor creates an empty Feedback object
     */
    public Feedback() {}

    /**
     * This constructor creates a new Feedback object 
     *
     * @param feedbackType the type of feedback either post or reply
     * @param targetId the ID of the post or reply
     * @param sender the sender username
     * @param recipient the recipient username
     * @param body the feedback message
     */
    public Feedback(String feedbackType, int targetId, String sender, String recipient, String body) {
        this.feedbackType = feedbackType;
        this.targetId = targetId;
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
    }

    /**
     * This constructor creates a Feedback object retrieved from the database.
     *
     * @param id the feedback ID
     * @param feedbackType the type of feedback ("POST" or "REPLY")
     * @param targetId the ID of the target
     * @param sender the sender username
     * @param recipient the recipient username
     * @param body the feedback message
     */
    public Feedback(int id, String feedbackType, int targetId, String sender, String recipient, String body) {
        this.id = id;
        this.feedbackType = feedbackType;
        this.targetId = targetId;
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
    }

    /**
     * Read - Gets the feedback ID.
     *
     * @return the feedback ID
     */
    public int getId() {return id;}

    /**
     * Read - Gets the feedback type.
     *
     * @return "POST" or "REPLY"
     */
    public String getFeedbackType() {return feedbackType;}

    /**
     * Read - Gets the target ID.
     *
     * @return the post or reply ID
     */
    public int getTargetId() {return targetId;}

    /**
     * Read - Gets the sender username.
     *
     * @return the sender
     */
    public String getSender() {return sender;}

    /**
     * Read - Gets the recipient username.
     *
     * @return the recipient
     */
    public String getRecipient() {return recipient;}

    /**
     * Read - Gets the feedback message body.
     *
     * @return the feedback message
     */
    public String getBody() {return body;}

    /**
     * Update - Sets the feedback ID.
     *
     * @param id the feedback ID
     */
    public void setId(int id) {this.id = id;}

    /**
     * Update - Sets the feedback type.
     *
     * @param feedbackType the type ("POST" or "REPLY")
     */
    public void setFeedbackType(String feedbackType) {this.feedbackType = feedbackType;}

    /**
     * Update - Sets the target ID.
     *
     * @param targetId the post or reply ID
     */
    public void setTargetId(int targetId) {this.targetId = targetId;}

    /**
     * Update - Sets the sender username.
     *
     * @param sender the sender
     */
    public void setSender(String sender) {this.sender = sender;}

    /**
     * Update - Sets the recipient username.
     *
     * @param recipient the recipient
     */
    public void setRecipient(String recipient) {this.recipient = recipient;}

    /**
     * Update - Sets the feedback message body.
     *
     * @param body the feedback message
     */
    public void setBody(String body) {this.body = body;}
}
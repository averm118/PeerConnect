package entityClasses;


/**
 * This class represents a post.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class Post {

	/**
	 * These are the private attributes for this entity object
	 */
    private int id;          
    private String title;
    private String body;
    private String author;   
    private String thread;
    private boolean inappropriate;
    /**
     * This constructor creates an empty Post object.
     */
    public Post() {}

    /*****
     * <p> Method: Post(String title, String body, String author) </p>
     * 
     * <p> Create - Description: This constructor is used to establish post entity objects. </p>
     * 
     * @param title the title of the post
     * @param body the body of the post
     * @param author the author of the post
     * @param thread the thread of the post
     *  
     */
    // Constructor to initialize a new Post object with title, body, and author.
    public Post(String title, String body, String author, String thread) {
        this.title = title;
        this.body = body;
        this.author = author;
        this.thread = thread;
        this.inappropriate = false;
    }

    /*****
     * <p> Method: Post(String title, String body, String author) </p>
     * 
     * <p> Create - Description: This constructor is used to establish post entity objects. </p>
     * 
     * @param id the id of the post
     * @param title the title of the post
     * @param body the body of the post
     * @param author the author of the post
     * @param thread the thread of the post
     *  
     */
    public Post(int id, String title, String body, String author, String thread, boolean inappropriate) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.thread = thread;
        this.inappropriate = inappropriate;
    }
    /**
     * Read - This method gets the id of the post.
     *
     * @return the id of the post
     */
    public int getId() {return id;}
    
    /**
     * Read - This method gets the title of the post.
     *
     * @return the title of the post
     */
    public String getTitle() {return title;}
    
    /**
     * Read - This method gets the body of the post.
     *
     * @return the body of the post
     */
    public String getBody() {return body;}
    
    /**
     * Read - This method gets the author of the post.
     *
     * @return the author of the post
     */
    public String getAuthor() {return author;}
    
    /**
     * Read - This method gets the thread of the post.
     *
     * @return the thread of the post
     */
    public String getThread() {return thread;}
    
	/**
	 * Read - This method gets whether the post is inappropriate.
	 * 
	 * @return whether the post is inappropriate
	 */
	public boolean getInappropriate() {return inappropriate;}

    /**
     * Create/Update - This method sets the id of the post.
     *
     * @param id the id of the post
     */    
    public void setId(int id) {this.id = id;}
     
    /**
     * Create/Update - This method sets the title of the post.
     *
     * @param title the title of the post
     */
    public void setTitle(String title) {this.title = title;}
    
    /**
     * Create/Update - This method sets the body of the post.
     *
     * @param body the body of the post
     */
    public void setBody(String body) {this.body = body;}
    
    /**
     * Create/Update - This method sets the author of the post.
     *
     * @param author the author of the post
     */
    public void setAuthor(String author) {this.author = author;}
    
    /**
     * Create/Update - This method sets the thread of the post.
     *
     * @param thread the thread of the post
     */
    public void setThread(String thread) {this.thread = thread;}
    
	/**
	 * Update - This method sets whether the post is inappropriate.
	 * 
	 * @param inappropriate whether the post is inappropriate
	 */
	public void setInappropriate(boolean inappropriate) {this.inappropriate = inappropriate;}
}
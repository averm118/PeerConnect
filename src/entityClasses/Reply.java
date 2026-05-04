package entityClasses;

/**
 * This class represents a reply.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class Reply {

	/*
	 * These are the private attributes for this entity object
	 */
    private int id;         
    private int op;
    private int parent;
    private int depth;
    private String body;
    private String author;   
	private boolean inappropriate;

    /**
     * This constructor creates an empty Reply object.
     */
    public Reply() {}

    /*****
     * <p> Method: Reply(String body, String author) </p>
     * 
     * <p> Create - Description: This constructor is used to establish reply entity objects. </p>
     * 
     * @param op the id of the original post
     * @param parent the id of the parent reply
     * @param body the body of the reply
     * @param author the author of the reply
     *  
     */

    public Reply(int op, int parent, String body, String author) {
        this.op = op;
        this.parent = parent;
        this.body = body;
        this.author = author;
        this.inappropriate = false;
    }

    /*****
     * <p> Method: Reply(int id, String body, String author) </p>
     * 
     * <p> Create - Description: This constructor is used to establish reply entity objects. </p>
     * 
     * @param id the id of the reply
     * @param op the id of the original post
     * @param parent the id of the parent reply
     * @param body the body of the reply
     * @param author the author of the reply
     *  
     */
    public Reply(int id, int op, int parent, String body, String author, boolean inappropriate) {
        this.id = id;
        this.op = op;
        this.parent = parent;
        this.body = body;
        this.author = author;
        this.inappropriate = inappropriate;
    }

    /**
     * Read - This method gets the id of the reply.
     *
     * @return the id of the reply
     */
    public int getID() {return id;}
    
    /**
     * Read - This method gets the original post id of the reply.
     *
     * @return the original post id
     */   
    public int getOP() {return op;}
    
    /**
     * Read - This method gets the parent id of the reply.
     *
     * @return the parent id
     */    
    public int getParent() {return parent;}
    
    /**
     * Read - This method gets the depth of the reply.
     *
     * @return the depth of the reply
     */    
    public int getDepth() {return depth;}
    
    /**
     * Read - This method gets the body of the reply.
     *
     * @return the body of the reply
     */   
    public String getBody() {return body;}
    
    /**
     * Read - This method gets the author of the reply.
     *
     * @return the author of the reply
     */   
    public String getAuthor() {return author;}
    
	/**
	 * Read - This method gets whether the reply is inappropriate.
	 * 
	 * @return whether the reply is inappropriate
	 */
	public boolean getInappropriate() {return inappropriate;}

    /**
     * Create/Update - This method sets the id of the reply.
     *
     * @param id the id of the reply
     */
    public void setID(int id) {this.id = id;}
    
    /**
     * Create/Update - This method sets the original post id of the reply.
     *
     * @param op the original post id
     */
    public void setOP(int op) {this.op = op;}
    
    /**
     * Create/Update - This method sets the parent id of the reply.
     *
     * @param parent the parent id
     */   
    public void setParent(int parent) {this.parent = parent;}
    
    /**
     * Create/Update - This method sets the depth of the reply.
     *
     * @param depth the depth of the reply
     */
    public void setDepth(int depth) {this.depth = depth;}
    
    /**
     * Create/Update - This method sets the body of the reply.
     *
     * @param body the body of the reply
     */
    public void setBody(String body) {this.body = body;}
    
    /**
     * Create/Update - This method sets the author of the reply.
     *
     * @param author the author of the reply
     */
    public void setAuthor(String author) {this.author = author;}
    
	/**
	 * Update - This method sets whether the reply is inappropriate.
	 * 
	 * @param inappropriate whether the reply is inappropriate
	 */
	public void setInappropriate(boolean inappropriate) {this.inappropriate = inappropriate;}
}
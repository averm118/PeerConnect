package entityClasses;

/**
 * Represents a request submitted by a staff member for an admin action.
 *
 * This entity supports both open requests and closed requests. When a closed
 * request is reopened, a new request row is created and linked back to the
 * original closed request through originalRequestId.
 *
 * @author Your Team
 */
public class AdminRequest {

    private int requestId;
    private String requesterUserName;
    private String description;
    private String status;
    private String createdAt;
    private String closedAt;
    private String closedBy;
    private Integer originalRequestId;

    /** Default constructor. */
    public AdminRequest() {}

    /**
     * Full constructor.
     */
    public AdminRequest(int requestId, String requesterUserName, String description,
            String status, String createdAt, String closedAt, String closedBy,
            Integer originalRequestId) {
        this.requestId = requestId;
        this.requesterUserName = requesterUserName;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.closedBy = closedBy;
        this.originalRequestId = originalRequestId;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getRequesterUserName() { return requesterUserName; }
    public void setRequesterUserName(String requesterUserName) { this.requesterUserName = requesterUserName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getClosedAt() { return closedAt; }
    public void setClosedAt(String closedAt) { this.closedAt = closedAt; }

    public String getClosedBy() { return closedBy; }
    public void setClosedBy(String closedBy) { this.closedBy = closedBy; }

    public Integer getOriginalRequestId() { return originalRequestId; }
    public void setOriginalRequestId(Integer originalRequestId) { this.originalRequestId = originalRequestId; }
}

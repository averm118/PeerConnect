package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.AdminRequest;

/**
 * JUnit tests for the Admin Request Management database functions.
 *
 * These tests validate the major TP3 methods added for the Admin Request
 * Management user stories.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */
public class RequestManagementTest {

    private static Database database;

    @BeforeAll
    public static void setup() throws SQLException {
        database = new Database();
        database.connectToDatabase();
    }
    /**
     * Tests the creation of an admin request.
     *
     * Verifies that a valid request ID is generated and that
     * the request appears in the list of open requests.
     */
    @Test
    public void testCreateAdminRequest() {
        int requestId = database.createAdminRequest("staffUser", "Please reset access for a student.", null);
        List<AdminRequest> openRequests = database.getOpenAdminRequests();

        assertTrue(requestId > 0);
        assertTrue(openRequests.stream().anyMatch(r -> r.getRequestId() == requestId));
    }
    /**
     * Tests closing an admin request.
     *
     * Verifies that the request status is set to CLOSED,
     * the closing admin is recorded correctly, and the
     * request appears in the list of closed requests.
     */
    @Test
    public void testCloseAdminRequest() {
        int requestId = database.createAdminRequest("staffUser", "Please remove a duplicate post.", null);
        database.closeAdminRequest(requestId, "adminUser", "Closing for test.");

        List<AdminRequest> closedRequests = database.getClosedAdminRequests();
        AdminRequest found = closedRequests.stream()
                .filter(r -> r.getRequestId() == requestId)
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals("CLOSED", found.getStatus());
        assertEquals("adminUser", found.getClosedBy());
    }
    /**
     * Tests reopening a closed admin request.
     *
     * Verifies that reopening creates a new request with OPEN status
     * and that it correctly references the original request ID.
     */
    @Test
    public void testReopenAdminRequestCreatesLinkedRequest() {
        int originalId = database.createAdminRequest("staffUser", "Need admin to change a role.", null);
        database.closeAdminRequest(originalId, "adminUser", "Closing original request.");

        int reopenedId = database.reopenAdminRequest(originalId, "staffUser", "Need admin to change the role urgently.");
        AdminRequest reopened = database.getAdminRequestById(reopenedId);

        assertNotNull(reopened);
        assertEquals("OPEN", reopened.getStatus());
        assertEquals(originalId, reopened.getOriginalRequestId());
    }
    /**
     * Tests updating the description of a reopened admin request.
     *
     * Verifies that the description is successfully updated
     * and stored in the database.
     */
    @Test
    public void testUpdateReopenedRequestDescription() {
        int originalId = database.createAdminRequest("staffUser", "Old description.", null);
        database.closeAdminRequest(originalId, "adminUser", "Closing before reopen.");
        int reopenedId = database.reopenAdminRequest(originalId, "staffUser", "Reopened description.");

        database.updateAdminRequestDescription(reopenedId, "Updated reopened description.");
        AdminRequest reopened = database.getAdminRequestById(reopenedId);

        assertEquals("Updated reopened description.", reopened.getDescription());
    }
}

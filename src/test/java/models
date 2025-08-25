package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ActivityLogTest {

    private ActivityLog activityLog;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        activityLog = new ActivityLog(testDate, "User logged in");
    }

    @Test
    void testGetActivity() {
        assertEquals("User logged in", activityLog.getActivity(), "The activity description should be 'User logged in'.");
    }

    @Test
    void testSetActivity() {
        activityLog.setActivity("User logged out");
        assertEquals("User logged out", activityLog.getActivity(), "The activity description should be updated to 'User logged out'.");
    }

    @Test
    void testGetDate() {
        assertEquals(testDate, activityLog.getDate(), "The activity date should match the test date.");
    }

    @Test
    void testSetDate() {
        Date newDate = new Date();
        activityLog.setDate(newDate);
        assertEquals(newDate, activityLog.getDate(), "The activity date should be updated to the new date.");
    }
}

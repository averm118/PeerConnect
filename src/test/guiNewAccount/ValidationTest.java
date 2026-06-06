package guiNewAccount;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import UserNameRecognizer.UserNameRecognizer;
import guiChangePassword.ModelChangePassword;

public class ValidationTest {
    @Test
    void testUsernameTooSmallFails() {
        String result = UserNameRecognizer.checkForValidUserName("abc");
        assertFalse(result.isEmpty(), "Username < 4 chars should fail");
    }

    @Test
    void testUsernameTooLargeFails() {
        String longName = "a".repeat(33);
        String result = UserNameRecognizer.checkForValidUserName(longName);
        assertFalse(result.isEmpty(), "Username > 32 chars should fail");
    }

    @Test
    void testUsernameNotStartingWithLetterFails() {
        String result = UserNameRecognizer.checkForValidUserName("1abc");
        assertFalse(result.isEmpty(), "Username must start with a letter");
    }

    @Test
    void testUsernameWithInvalidCharactersFails() {
        String result = UserNameRecognizer.checkForValidUserName("abc$def");
        assertFalse(result.isEmpty(), "Username with invalid characters should fail");
    }

    @Test
    void testUsernameEndingWithSpecialCharacterFails() {
        String result = UserNameRecognizer.checkForValidUserName("valid_");
        assertFalse(result.isEmpty(), "Username ending with special char should fail");
    }

    @Test
    void testValidUsernamesSucceed() {
        assertTrue(UserNameRecognizer.checkForValidUserName("Alice").isEmpty());
        assertTrue(UserNameRecognizer.checkForValidUserName("Bob_Smith").isEmpty());
        assertTrue(UserNameRecognizer.checkForValidUserName("john.doe99").isEmpty());
    }

    @Test
    void testPasswordTooSmallFails() {
        String result = ModelChangePassword.evaluatePassword("Ab1!");
        assertFalse(result.isEmpty(), "Password < 8 chars should fail");
    }

    @Test
    void testPasswordTooLargeFails() {
        String longPass = "A1!" + "a".repeat(20);
        String result = ModelChangePassword.evaluatePassword(longPass);
        assertFalse(result.isEmpty(), "Password > 16 chars should fail");
    }

    @Test
    void testPasswordMissingUppercaseFails() {
        String result = ModelChangePassword.evaluatePassword("abc123!@#");
        assertFalse(result.isEmpty(), "Missing uppercase should fail");
    }

    @Test
    void testPasswordMissingLowercaseFails() {
        String result = ModelChangePassword.evaluatePassword("ABC123!@#");
        assertFalse(result.isEmpty(), "Missing lowercase should fail");
    }

    @Test
    void testPasswordMissingNumberFails() {
        String result = ModelChangePassword.evaluatePassword("Abcdef!@#");
        assertFalse(result.isEmpty(), "Missing number should fail");
    }

    @Test
    void testPasswordMissingSpecialCharacterFails() {
        String result = ModelChangePassword.evaluatePassword("Abcdef123");
        assertFalse(result.isEmpty(), "Missing special character should fail");
    }

    @Test
    void testValidPasswordsSucceed() {
        assertTrue(ModelChangePassword.evaluatePassword("Abcdef1!").isEmpty());
        assertTrue(ModelChangePassword.evaluatePassword("GoodPass9$").isEmpty());
        assertTrue(ModelChangePassword.evaluatePassword("XyZ12345@").isEmpty());
    }
}

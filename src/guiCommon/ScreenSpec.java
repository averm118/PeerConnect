package guiCommon;

import entityClasses.User;

/** Immutable metadata used by the shared shell to render page chrome. */
public record ScreenSpec(String title, String subtitle, User user, String roleName, String iconLiteral) {
    public static ScreenSpec of(String title, String subtitle, User user, String roleName, String iconLiteral) {
        return new ScreenSpec(title, subtitle, user, roleName, iconLiteral);
    }
}

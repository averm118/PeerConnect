package guiCommon;

/** Describes a command that should be rendered as a consistent PeerConnect action button. */
public record ActionSpec(String label, String iconLiteral, Runnable action, String... styleClasses) {
    public static ActionSpec of(String label, String iconLiteral, Runnable action, String... styleClasses) {
        return new ActionSpec(label, iconLiteral, action, styleClasses);
    }
}

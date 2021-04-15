package ca.bcit.android_client;

public enum PayloadValues {
    PROTOCOL_VERSION(1),
    TIC_TAC_TOE_ID(1),
    ROCK_PAPER_SCISSORS_ID(2),
    TEAM_X(1),
    TEAM_O(2),
    WIN(1),
    TIE(2),
    LOSS(3),
    MOVE_TOP_LEFT(0),
    MOVE_TOP(1),
    MOVE_TOP_RIGHT(2),
    MOVE_LEFT(3),
    MOVE_MIDDLE(4),
    MOVE_RIGHT(5),
    MOVE_BOTTOM_LEFT(6),
    MOVE_BOTTOM(7),
    MOVE_BOTTOM_RIGHT(8),
    MOVE_ROCK(1),
    MOVE_PAPER(2),
    MOVE_SCISSORS(3);
    private final int val;
    PayloadValues(int v) { val = v; }
    public int getVal() { return val; }
}

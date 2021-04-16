package ca.bcit.android_client;

public enum ResponseContexts {
    START_GAME(1),
    MOVE_MADE(2),
    END_OF_GAME(3),
    OPPONENT_DISCONNECTED(4),
    CONFIRMATION(1),
    INFORMATION(2),
    META_ACTION(3),
    GAME_ACTION(4);
    private final int val;
    ResponseContexts(int v) { val = v; }
    public int getVal() { return val; }
}

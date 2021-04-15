package ca.bcit.android_client;

public enum RequestTypes {
    CONFIRMATION(1),
    INFORMATION(2),
    META_ACTION(3),
    GAME_ACTION(4);
    private final int val;
    RequestTypes(int v) { val = v; }
    public int getVal() { return val; }
}

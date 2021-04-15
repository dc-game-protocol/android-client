package ca.bcit.android_client;

public enum RequestContexts {
    CONFIRM_RULESET(1),
    MAKE_MOVE(1),
    QUIT(1);
    private final int val;
    RequestContexts(int v) { val = v; }
    public int getVal() { return val; }
}

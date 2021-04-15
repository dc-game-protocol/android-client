package ca.bcit.android_client;

public enum ResponseTypes {
    SUCCESS(10),
    UPDATE(20),
    INVALID_REQUEST(30),
    INVALID_UID(31),
    INVALID_TYPE(32),
    INVALID_CONTEXT(33),
    INVALID_PAYLOAD(34),
    SERVER_ERROR(40),
    INVALID_ACTION(50),
    ACTION_OUT_OF_TURN(51);
    private final int val;
    ResponseTypes(int v) { val = v; }
    public int getVal() { return val; }
}

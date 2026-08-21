package com.internal.netatlas.normalize.model;

public class InterfaceRecord {
    private final String name;
    private final String description;
    private final String adminStatus;
    private final String operStatus;
    private final String macAddress;
    private final int speed;

    public InterfaceRecord(String name, String description, String adminStatus, String operStatus, String macAddress, int speed) {
        this.name = name;
        this.description = description;
        this.adminStatus = adminStatus;
        this.operStatus = operStatus;
        this.macAddress = macAddress;
        this.speed = speed;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAdminStatus() { return adminStatus; }
    public String getOperStatus() { return operStatus; }
    public String getMacAddress() { return macAddress; }
    public int getSpeed() { return speed; }
}

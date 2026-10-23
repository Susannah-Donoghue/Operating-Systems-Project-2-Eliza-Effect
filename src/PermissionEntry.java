public class PermissionEntry {
    private String domainName;
    private boolean canRead;
    private boolean canWrite;
    private boolean canSwitch;

    public PermissionEntry(String domainName, boolean canRead,
                           boolean canWrite, boolean canSwitch) {
        this.domainName = domainName;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canSwitch = canSwitch;
    }

    public String getDomainName() {
        return domainName;
    }

    public boolean canRead() {
        return canRead;
    }

    public boolean canWrite() {
        return canWrite;
    }

    public boolean canSwitch() {
        return canSwitch;
    }
}
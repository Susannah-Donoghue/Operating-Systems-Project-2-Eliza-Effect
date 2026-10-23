import java.util.ArrayList;
import java.util.List;

public class ObjectACL {
    private String objectName;
    private boolean fileObject;
    private List<PermissionEntry> entries;

    public ObjectACL(String objectName, boolean fileObject) {
        this.objectName = objectName;
        this.fileObject = fileObject;
        entries = new ArrayList<>();
    }

    public String getObjectName() {
        return objectName;
    }

    public void addEntry(String domainName, boolean canRead,
                         boolean canWrite, boolean canSwitch) {
        entries.add(new PermissionEntry(
                domainName, canRead, canWrite, canSwitch));
    }

    public boolean isAllowed(String domainName, String operation) {
        for (PermissionEntry entry : entries) {
            if (entry.getDomainName().equals(domainName)) {
                if (operation.equals("READ")) {
                    return entry.canRead();
                }

                if (operation.equals("WRITE")) {
                    return entry.canWrite();
                }

                if (operation.equals("SWITCH")) {
                    return !fileObject && entry.canSwitch();
                }
            }
        }

        return false;
    }


    public String format() {
        String result = objectName + " --> ";

        boolean first = true;

        for (PermissionEntry entry : entries) {
            if (!first) {
                result += ", ";
            }

            result += entry.getDomainName() + ":";

            if (entry.canSwitch()) {
                result += "allow";
            } else if (entry.canRead() && entry.canWrite()) {
                result += "R/W";
            } else if (entry.canRead()) {
                result += "R";
            } else if (entry.canWrite()) {
                result += "W";
            }

            first = false;
        }

        if (first) {
            result += "(no permissions)";
        }

        return result;
    }

}
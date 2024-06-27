package records;

import java.util.ArrayList;
import java.util.List;

public record IpAdressess(String ip, String port, List<String> logs) {
    public IpAdressess(String ip, String port) {
        this(ip, port, new ArrayList<>());
    }
}

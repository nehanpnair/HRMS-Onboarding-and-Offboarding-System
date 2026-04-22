package gui;

import model.model.Employee;

// Abstract Product
interface Asset {
    void allocate(Employee emp);
}

// Concrete Products
class StandardAsset implements Asset {
    @Override
    public void allocate(Employee emp) {
        System.out.println("FACTORY PATTERN: Allocated Standard Office Assets (Desktop, Desk) to " + emp.getName());
    }
}

class RemoteAsset implements Asset {
    @Override
    public void allocate(Employee emp) {
        System.out.println("FACTORY PATTERN: Allocated Remote Working Assets (Laptop, VPN Token) to " + emp.getName());
    }
}

// Factory
public class AssetFactory {
    public static Asset createAsset(String type) {
        if ("Remote".equalsIgnoreCase(type)) {
            return new RemoteAsset();
        } else {
            return new StandardAsset();
        }
    }
}

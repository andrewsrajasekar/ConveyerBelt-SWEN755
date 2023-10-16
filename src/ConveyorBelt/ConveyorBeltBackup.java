package ConveyorBelt;

public class ConveyorBeltBackup extends ConveyorBelt {

    public ConveyorBeltBackup(int id) {
        super(id);
    }

    public void activate(int productCount) {
        super.setProductCount(productCount);
        super.setStatus(true);
    }

    @Override
    public String toString() {
        return "ConveyorBelt(backup):" + super.getBeltId() + "(" + super.getThreshold() + ")";
    }

    public static void main(String[] args) {
        ConveyorBeltBackup belt = new ConveyorBeltBackup(1);
        belt.start();
    }
}

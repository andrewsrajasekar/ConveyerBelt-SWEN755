package ConveyorBelt;

public class ConveyorBeltBackup extends ConveyorBelt {

    public ConveyorBeltBackup(int id) {
        super(id);
    }

    public void activate(int productCount) {
        super.setProductCount(productCount);
        super.setStatus(true);
        super.log("Starting with product count: " + super.getProductCount());
    }

    @Override
    public String toString() {
        return "ConveyorBelt[backup], id:" + super.getBeltId() + "(" + super.getThreshold() + ")";
    }

    public static void main(String[] args) {
        ConveyorBeltBackup belt = new ConveyorBeltBackup(1);
        belt.start();
    }
}

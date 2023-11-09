package hotel.management.system;

public class Room {
    private int id, numberBed, capacity, price;
    private String number, type, status, note;

    public Room() {
        
    }

    public Room(int id, String number,  String type, int numberBed, int capacity, int price, String status, String note) {
        this.id = id;
        this.numberBed = numberBed;
        this.capacity = capacity;
        this.price = price;
        this.number = number;
        this.type = type;
        this.status = status;
        this.note = note;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberBed() {
        return numberBed;
    }

    public void setNumberBed(int numberBed) {
        this.numberBed = numberBed;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    @Override
    public String toString() {
        return id + " "
                +  number + " "
                + type + " "
                + numberBed + " "
                + capacity + " "
                + price + " "
                + status + " "
                + note;
    }
}

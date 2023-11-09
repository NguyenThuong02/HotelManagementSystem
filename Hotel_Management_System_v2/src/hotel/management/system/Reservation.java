package hotel.management.system;

import java.util.Date;

public class Reservation {
    private int id, customerId, roomId;
    private Date reservationDate, checkIn, checkOut;
    private int deposit;
    
    public Reservation() {
        
    }

    public Reservation(int id, int customerId, int roomId, Date reservationDate, Date checkIn, Date checkOut, int deposit) {
        this.id = id;
        this.customerId = customerId;
        this.roomId = roomId;
        this.reservationDate = reservationDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.deposit = deposit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }
}

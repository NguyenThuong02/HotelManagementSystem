package hotel.management.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class ConnectDB {
    private Connection conn = null;
    
    public ConnectDB(String user, String password) {
        String url =
            "jdbc:sqlserver://localhost:1433;"
            + "database=Hotel;"
            + String.format("user=%s;", user)
            + String.format("password=%s;", password)
            + "encrypt=true;trustServerCertificate=true;sslProtocol=TLSv1.2";
        try {
            this.conn =  DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static Connection getConnection(String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url =
            "jdbc:sqlserver://localhost:1433;"
            + "database=Hotel;"
            + String.format("user=%s;", user)
            + String.format("password=%s;", password)
            + "encrypt=true;trustServerCertificate=true;sslProtocol=TLSv1.2";
        return DriverManager.getConnection(url);
    }
    
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public boolean fullBackup() {
        try {
            String sqlQuery = "BACKUP DATABASE [Hotel] " +
                        "TO  DISK = N'C:\\Program Files\\Microsoft SQL Server\\MSSQL15.SQLEXPRESS\\MSSQL\\DATA\\Hotel.bak' " +
                        "WITH NOFORMAT, NOINIT,  NAME = N'Hotel-Full Database Backup', SKIP, NOREWIND, NOUNLOAD, NO_COMPRESSION,  STATS = 10";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    
    public boolean fullRestore() {
        try {
            String sqlQuery = "RESTORE DATABASE [Hotel] FILE = N'Hotel' " +
                    "FROM  DISK = N'C:\\Program Files\\Microsoft SQL Server\\MSSQL15.SQLEXPRESS\\MSSQL\\DATA\\Hotel.bak'";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    
    // Rooms Query
    public ArrayList<Room> selectQueryRooms(String query) {
        ArrayList<Room> rooms = new ArrayList<>();
        String sqlQuery = "SELECT "
                + "Rooms.*, "
                + "RoomTypes.Room_Type_Name, "
                + "RoomStatus.Room_Status_Name "
                + "FROM Rooms, RoomTypes, RoomStatus"
                + " WHERE (Rooms.Room_Type = RoomTypes.Room_Type_Id) AND (Rooms.Status = RoomStatus.Room_Status_Id) ";
        
        if(!query.isEmpty()) {
            sqlQuery += query;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Room room = new Room(
                        rs.getInt("Room_Id"), 
                        rs.getString("Room_Number"), 
                        rs.getString("Room_Type_Name"), 
                        rs.getInt("Number_Bed"), 
                        rs.getInt("Capacity"), 
                        rs.getInt("Price"), 
                        rs.getString("Room_Status_Name"),
                        rs.getString("Note")
                );
                rooms.add(room);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return rooms;
    }
    
    public ArrayList<Integer> getAllRoomPrice() {
        ArrayList<Integer> prices = new ArrayList<>();
        try {
            String sqlQuery = "SELECT Rooms.Price FROM Rooms";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                prices.add(rs.getInt(1));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return prices;
    }
    
    public int insertRoom(Room room) {
        try {
            if(!fullBackup()) {
                return -1;
            }
            String sqlQuery = "SELECT ISNULL(MAX(Room_Id) + 1, 0) FROM Rooms";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int newRoomId = rs.getInt(1);
                room.setId(newRoomId);
                sqlQuery = "INSERT Rooms (Room_Number, Room_Type, Number_Bed, Capacity, Price, Status, Note) "
                        + "VALUES ("
                        + "N'" + room.getNumber() + "', "
                        + getRoomTypeIdByRoomTypeName(room.getType()) + ", "
                        + room.getNumberBed() + ", "
                        + room.getCapacity() + ", "
                        + room.getPrice() + ", "
                        + getRoomStatusIdByRoomStatusName(room.getStatus()) + ", "
                        + "N'" + room.getNote() + "'"
                        + ")";
                ps = conn.prepareStatement(sqlQuery);
                ps.execute();
                return newRoomId;
            }
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return -1;
    }
    
    public boolean updateRoomQuery(Room room) {
        try {
            if(!fullBackup()) {
                return false;
            }
            int roomTypeId = getRoomTypeIdByRoomTypeName(room.getType());
            if(roomTypeId == -1) {
                roomTypeId = insertRoomType(room.getType());
            }
            int roomStatusId = getRoomStatusIdByRoomStatusName(room.getStatus());
            if(roomStatusId == -1) {
                roomStatusId = insertRoomStatus(room.getStatus());
            }
            String sqlQuery = "UPDATE Rooms SET "
                    + "Room_Type = " + roomTypeId + ", "
                    + "Number_Bed = " + room.getNumberBed() + ", "
                    + "Capacity = " + room.getCapacity() + ", "
                    + "Price = " + room.getPrice() + ", "
                    + "Status = " + roomStatusId + ", "
                    + "Note = N'" + room.getNote()+ "'"
                    + " WHERE Room_Id = " + room.getId();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return false;
    }
    
    public boolean deleteRoomQuery(Room room) {
        try {
            String sqlQuery = "DELETE FROM Rooms WHERE Room_Id = " + room.getId();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    
    public String getRoomNumber(String query) {
        try {
            String sqlQuery = "SELECT Rooms.Room_Number FROM Rooms "
                    + "WHERE (1 = 1) " + query;
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "ERROR";
    }
    
    public int getRoomId(String query) {
        try {
            String sqlQuery = "SELECT Rooms.Room_Id FROM Rooms "
                    + "WHERE (1 = 1) " + query;
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    
    // RoomTypes Query
    public int insertRoomType(String roomTypeName) {
        try {
            if(!fullBackup()) {
                return -1;
            }
            String sqlQuery = "SELECT ISNULL(MAX(Room_Type_Id) + 1, 0) FROM RoomTypes";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int newRoomTypeId = rs.getInt(1);
                sqlQuery = "INSERT RoomTypes (Room_Type_Name)"
                    + " VALUES (N'" + roomTypeName + "')";
                ps = conn.prepareStatement(sqlQuery);
                ps.execute();
                return newRoomTypeId;
            }
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return -1;
    }
    
    public int getRoomTypeIdByRoomTypeName(String roomTypeName) {
        String sqlQuery = "SELECT RoomTypes.Room_Type_Id FROM RoomTypes WHERE ("
                + "Room_Type_Name = N'" + roomTypeName + "')";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    
    // Room Status Query
    public int insertRoomStatus(String status) {
        try {
            if(!fullBackup()) {
                return -1;
            }
            String sqlQuery = "SELECT ISNULL(MAX(Room_Status_Id) + 1, 0) FROM RoomStatus";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int newRoomStatusId = rs.getInt(1);
                sqlQuery = "INSERT RoomStatus (Room_Status_Name)"
                    + " VALUES (N'" + status + "')";
                ps = conn.prepareStatement(sqlQuery);
                ps.execute();
                return newRoomStatusId;
            }
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return -1;
    }
    
    public int getRoomStatusIdByRoomStatusName(String roomStatusName) {
        String sqlQuery = "SELECT RoomStatus.Room_Status_Id FROM RoomStatus WHERE ("
                + "Room_Status_Name = N'" + roomStatusName + "')";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    
    // Customers Query
    public ArrayList<Customer> selectCustomerQuery(String query) {
        ArrayList<Customer> list = new ArrayList<>();
        try {
            String sqlQuery = "SELECT Customers.*, "
                    + "Genders.Gender_Name "
                    + "FROM Customers, Genders "
                    + "WHERE (Customers.Gender = Genders.Gender_Id) " + query;
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Customer cust = new Customer(
                        rs.getInt("Customer_Id"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Gender_Name"),
                        rs.getDate("DOB"),
                        rs.getString("Address"),
                        rs.getString("Contact"),
                        rs.getString("Id_Proof")
                );
                list.add(cust);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
    
    public int searchCustomer(Customer cust) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String sqlQuery = "SELECT Customers.Customer_Id FROM Customers "
                    + "WHERE (First_Name = N'" + cust.getFirstName() + "') AND "
                    + "(Last_Name = N'" + cust.getLastName() + "') AND "
                    + "(DOB = CONVERT(date, '" + formatter.format(cust.getDob()) + "', 103)) AND "
                    + "(Address = N'" + cust.getAddress() + "') AND "
                    + "(Contact = N'" + cust.getContact() + "') AND "
                    + "(Id_Proof = N'" + cust.getIdProof() + "')";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    
    public int insertCustomerQuery(Customer cust) {
        try {
            if(!fullBackup()) {
                return -1;
            }
            String sqlQuery = "SELECT ISNULL(MAX(Customer_Id) + 1, 0) FROM Customers";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                int newCustomerId = rs.getInt(1);
                cust.setId(newCustomerId);
                int genderId = getGenderIdByGenderName(cust.getGender());
                sqlQuery = "INSERT Customers (First_Name, Last_Name, Gender, DOB, Address, Contact, Id_Proof) "
                        + "VALUES ("
                        + "N'" + cust.getFirstName() + "', "
                        + "N'" + cust.getLastName() + "', "
                        + genderId + ", "
                        + "CONVERT(date, '" + formatter.format(cust.getDob()) + "', 103), "
                        + "N'" + cust.getAddress() + "', "
                        + "N'" + cust.getContact() + "', "
                        + "N'" + cust.getIdProof() + "')";
                ps = conn.prepareStatement(sqlQuery);
                ps.execute();
                return newCustomerId;
            }
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return -1;
    }
    
    public boolean deleteCustomerQuery(Customer cust) {
        try {
            String sqlQuery = "DELETE FROM Customers WHERE Customer_Id = " + cust.getId();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    
    public boolean updateCustomerQuery(Customer cust) {
        try {
            if(!fullBackup()) {
                return false;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String sqlQuery = "UPDATE Customers SET "
                    + "First_Name = N'" + cust.getFirstName() + "', "
                    + "Last_Name = N'" + cust.getLastName() + "', "
                    + "Gender = " + getGenderIdByGenderName(cust.getGender()) + ", "
                    + "DOB = CONVERT(date, '" + formatter.format(cust.getDob()) + "', 103), "
                    + "Address = N'" + cust.getAddress() + "', "
                    + "Contact = N'" + cust.getContact() + "', "
                    + "Id_Proof = N'" + cust.getIdProof() + "' "
                    + "WHERE Customer_Id = " + cust.getId();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return false;
    }
    
    // Gender Query
    public int getGenderIdByGenderName(String genderName) {
        try {
            String sqlQuery = "SELECT Genders.Gender_Id FROM Genders "
                    + "WHERE Gender_Name = N'" + genderName + "'";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }
    
    // Reservations
    public ArrayList<Reservation> selectReservationsQuery(String query) {
        ArrayList<Reservation> re = new ArrayList<>();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String sqlQuery = "SELECT * FROM Reservations "
                    + "WHERE (1 = 1) " + query;
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("Reservation_Id"));
                r.setCustomerId(rs.getInt("Customer_Id"));
                r.setRoomId(rs.getInt("Room_Id"));
                r.setReservationDate(rs.getDate("Reservation_Date"));
                r.setCheckIn(rs.getDate("Check_In_Date"));
                r.setCheckOut(rs.getDate("Check_Out_Date"));
                r.setDeposit(rs.getInt("Deposit"));
                re.add(r);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return re;
    }
    
    public int insertReservation(Reservation r) {
        int newId = -1;
        fullBackup();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String sqlQuery = "SELECT ISNULL(MAX(Reservation_Id) + 1, 0) FROM Reservations";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                newId = rs.getInt(1);
                sqlQuery = "INSERT Reservations ("
                        + "Customer_Id, "
                        + "Room_Id, "
                        + "Reservation_Date, "
                        + "Check_In_Date, "
                        + "Check_Out_Date, "
                        + "Deposit) "
                        + "VALUES (" + r.getCustomerId() + ", "
                        + r.getRoomId() + ", "
                        + "CONVERT(date, '" + formatter.format(r.getReservationDate()) + "', 103), "
                        + "CONVERT(datetime, '" + formatter.format(r.getCheckIn()) + "', 103), "
                        + "CONVERT(datetime, '" + formatter.format(r.getCheckOut()) + "', 103), "
                        + r.getDeposit() + ")";
                ps = conn.prepareStatement(sqlQuery);
                ps.execute();
                sqlQuery = "UPDATE Rooms SET "
                        + "Status = 2 "
                        + "WHERE Room_Id = " + r.getRoomId();
                ps = conn.prepareStatement(sqlQuery);
                ps.execute();
                return newId;
            }
        } catch (Exception e) {
            fullRestore();
            System.out.println(e);
        }
        return -1;
    }
    
    public boolean deleteReservation(Reservation reservation) {
        try {
            fullBackup();
            String sqlQuery = "DELETE Reservations WHERE "
                    + "Reservation_Id = " + reservation.getId();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            sqlQuery = "UPDATE Rooms SET "
                    + "Status = 1 "
                    + "WHERE Room_Id = " + reservation.getRoomId();
            ps = conn.prepareStatement(sqlQuery);
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            fullRestore();
        }return false;
    }
    
    @Override
    public void finalize() {
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

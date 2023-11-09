package hotel.management.system;

import java.util.Date;


public class Customer {
    private int id;
    private String firstName, lastName, gender, address, contact, idProof;
    private Date dob;
    
    public Customer() {
        
    }

    public Customer(int id, String firstName, String lastName, String gender, Date dob, String address, String contact, String idProof) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.contact = contact;
        this.idProof = idProof;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    @Override
    public String toString() {
        return id + " "
                + firstName + " "
                + lastName + " "
                + gender + " "
                + dob + " "
                + address + " "
                + idProof + " "
                + contact;
    }
}

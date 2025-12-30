// Improved UI + UX version
// Logic unchanged, only layout, styling, dialogs, and navigation improved

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

interface Rentable {
    double calculateCost(int hours);
}

class Bike implements Rentable {
    int bikeId;
    String model;
    double pricePerHour;
    boolean isAvailable = true;

    Bike(int bikeId, String model, double pricePerHour) {
        this.bikeId = bikeId;
        this.model = model;
        this.pricePerHour = pricePerHour;
    }

    public double calculateCost(int hours) {
        return hours * pricePerHour;
    }

    public String getType() { return "Standard"; }
}

class ElectricBike extends Bike {
    ElectricBike(int id, String model, double price) { super(id, model, price); }
    public double calculateCost(int h) { return h * pricePerHour * 1.2; }
    public String getType() { return "Electric"; }
}

class MountainBike extends Bike {
    MountainBike(int id, String model, double price) { super(id, model, price); }
    public double calculateCost(int h) { return h * pricePerHour * 1.1; }
    public String getType() { return "Mountain"; }
}

class Booking {
    int bookingId, hours;
    String user;
    String phone;
    Bike bike;
    double total;

    Booking(int id, String user, String phone, Bike bike, int h) {
        this.bookingId = id;
        this.user = user;
        this.phone = phone;
        this.bike = bike;
        this.hours = h;
        this.total = bike.calculateCost(h);
    }
}


public class BikeRentalProject2_ImprovedUI extends JFrame {

    ArrayList<Bike> bikes = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    int bikeId = 1, bookingId = 1;

    CardLayout card = new CardLayout();
    JPanel centerPanel = new JPanel(card);

    DefaultTableModel bikeModel = new DefaultTableModel(new String[]{"ID","Model","Price/hr","Available","Type"},0);
    DefaultTableModel bookingModel = new DefaultTableModel(new String[]{"ID","User","Phone","Bike","Hours","Total"},0);

    public BikeRentalProject2_ImprovedUI() {
        setTitle("Bike Rental System");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(header(), BorderLayout.NORTH);
        add(sideMenu(), BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        centerPanel.add(dashboard(), "home");
        centerPanel.add(tablePanel(new JTable(bikeModel)), "bikes");
        centerPanel.add(tablePanel(new JTable(bookingModel)), "bookings");

        setVisible(true);
    }

    JPanel header() {
        JLabel title = new JLabel("Bike Rental Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(30,144,255));
        title.setForeground(Color.WHITE);
        p.add(title);
        return p;
    }

    JPanel sideMenu() {
        JPanel p = new JPanel(new GridLayout(6,1,10,10));
        p.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        p.add(menuBtn("Dashboard", e -> card.show(centerPanel,"home")));
        p.add(menuBtn("Add Bike", e -> addBikeDialog()));
        p.add(menuBtn("View Bikes", e -> { refreshBikes(); card.show(centerPanel,"bikes"); }));
        p.add(menuBtn("Book Bike", e -> bookBikeDialog()));
        p.add(menuBtn("View Bookings", e -> { refreshBookings(); card.show(centerPanel,"bookings"); }));
        p.add(menuBtn("Cancel Booking", e -> cancelDialog()));

        return p;
    }

    JButton menuBtn(String text, java.awt.event.ActionListener a) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.addActionListener(a);
        return b;
    }

    JPanel dashboard() {
        JLabel l = new JLabel("Welcome! Use the menu to manage rentals", JLabel.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JPanel p = new JPanel(new BorderLayout());
        p.add(l);
        return p;
    }

    JPanel tablePanel(JTable t) {
        t.setRowHeight(25);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        return new JPanel(new BorderLayout()){{ add(new JScrollPane(t)); }};
    }

    void addBikeDialog() {
        JTextField model = new JTextField();
        JTextField price = new JTextField();
        JComboBox<String> type = new JComboBox<>(new String[]{"Standard","Electric","Mountain"});

        Object[] f = {"Model",model,"Price/hr",price,"Type",type};
        if (JOptionPane.showConfirmDialog(this,f,"Add Bike",JOptionPane.OK_CANCEL_OPTION)==0) {
            double p = Double.parseDouble(price.getText());
            Bike b = switch(type.getSelectedItem().toString()){
                case "Electric" -> new ElectricBike(bikeId++,model.getText(),p);
                case "Mountain" -> new MountainBike(bikeId++,model.getText(),p);
                default -> new Bike(bikeId++,model.getText(),p);
            };
            bikes.add(b);
            JOptionPane.showMessageDialog(this,"Bike Added Successfully");
        }
    }

    void bookBikeDialog() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Bike ID"));
        for(Bike b: bikes) if(b.bikeId==id && b.isAvailable){
            String u = JOptionPane.showInputDialog("User Name");
            String ph = JOptionPane.showInputDialog("Phone Number");
            int h = Integer.parseInt(JOptionPane.showInputDialog("Hours"));
            bookings.add(new Booking(bookingId++,u,ph,b,h));
            b.isAvailable=false;
            JOptionPane.showMessageDialog(this,"Booked! Total ₹"+b.calculateCost(h));
            return;
        }
        JOptionPane.showMessageDialog(this,"Bike not available");
    }

    void cancelDialog() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Booking ID"));
        for(Booking b: bookings) if(b.bookingId==id){
            b.bike.isAvailable=true;
            bookings.remove(b);
            JOptionPane.showMessageDialog(this,"Cancelled");
            return;
        }
        JOptionPane.showMessageDialog(this,"Not Found");
    }

    void refreshBikes(){
        bikeModel.setRowCount(0);
        for(Bike b: bikes)
            bikeModel.addRow(new Object[]{b.bikeId,b.model,b.pricePerHour,b.isAvailable?"YES":"NO",b.getType()});
    }

    void refreshBookings(){
        bookingModel.setRowCount(0);
        for(Booking b: bookings)
            bookingModel.addRow(new Object[]{b.bookingId,b.user,b.phone,b.bike.model,b.hours,b.total});
    }

    public static void main(String[] args) {
        new BikeRentalProject2_ImprovedUI();
    }
}

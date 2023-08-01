package org.example;

import java.io.PrintStream;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static final String APARTMENTS_CONNECTION = "jdbc:mysql://localhost:3306/apartments?serverTimezone=Europe/Kiev";
    static final String APARTMENTS_USER = "root";
    static final String APARTMENTS_PASWORD = "password";
    static Connection conn;

    public Main() {}

    public static void main (String[]args){
        Scanner sc = new Scanner(System.in);

        try{
            try{
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/apartments?serverTimezone=Europe/Kiev", "root", "254551FhnmjV!");
                initDB();

                while (true){
                    System.out.println("1: add apartments");
                    System.out.println("2: add random apartments");
                    System.out.println("3: delete apartments");
                    System.out.println("4: change apartments price");
                    System.out.println("5: view all apartments");
                    System.out.println("6: selection by parameters - BETA"); // !!!
                    System.out.print("->");
                    switch (sc.nextLine()){
                        case "1":
                            addApartments(sc);
                            break;
                        case "2":
                            insertRandomApartments(sc);
                            break;
                        case "3":
                            deleteApartments(sc);
                            break;
                        case "4":
                            changeApartmentsPrice(sc);
                            break;
                        case "5":
                            viewAllApartments(sc);
                            break;
                        case "6":
                            selectionApartments(sc);
                            break;
                    }
                }
            } finally {
                sc.close();
                if (conn != null){
                    conn.close();
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    private static void initDB () throws SQLException {
        Statement st = conn.createStatement();
        try{
            st.execute("DROP TABLE IF EXISTS Apartments");
            st.execute("CREATE TABLE Apartments (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                                                    "district VARCHAR(20), " +
                                                    "address VARCHAR(25) , " +
                                                    "area INT, " +
                                                    "Rooms INT, " +
                                                    "price INT)");
        } finally {
            st.close();
        }
    }
    private static void addApartments(Scanner sc) throws SQLException{
        System.out.print("Enter district apartments: ");
        String district = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter area: ");
        String areaString = sc.nextLine();
        int area =Integer.parseInt(areaString);
        System.out.print("Enter numbers of rooms: ");
        String numberOfRoomsString = sc.nextLine();
        int numbersOfRooms =Integer.parseInt(numberOfRoomsString);
        System.out.print("Enter price: ");
        String priceString = sc.nextLine();
        int price =Integer.parseInt(priceString);

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district, address, area, Rooms, price) VALUES (?, ?, ?, ?, ?)");


        try{
            ps.setString(1,district);
            ps.setString(2, address);
            ps.setInt(3, area);
            ps.setInt(4, numbersOfRooms);
            ps.setInt(5, price);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }
    private static void insertRandomApartments (Scanner sc) throws SQLException{
        System.out.print("Enter apartments: ");
        String countString = sc.nextLine();
        int count = Integer.parseInt(countString);
        Random rwd = new Random();
        conn.setAutoCommit(false);

        try{
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Apartments (district, address, area, Rooms, price) VALUES (?, ?, ?, ?, ?)");
            try{
                for (int i = 0; i < count; i++) {
                    ps.setString(1,"district " + i);
                    ps.setString(2, "address " + i );
                    ps.setInt(3, rwd.nextInt(120));
                    ps.setInt(4, rwd.nextInt(4));
                    ps.setInt(5, rwd.nextInt(100_000));
                    ps.executeUpdate();
                }
                conn.commit();
            } finally {
                ps.close();
            }
        } catch (Exception e2){
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }
    }
    private static void deleteApartments (Scanner sc) throws SQLException{
        System.out.print("Enter address for delete apartments: ");
        String address = sc.nextLine();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM Apartments WHERE address = ?");

        try{
            ps.setString(1, address);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }
    private static void changeApartmentsPrice(Scanner sc) throws SQLException{
        System.out.print("Enter address apartments: ");
        String address = sc.nextLine();
        System.out.print("Enter new price: ");
        String priceString = sc.nextLine();
        int price = Integer.parseInt(priceString);

        PreparedStatement ps = conn.prepareStatement("UPDATE Apartments SET price = ? WHERE address = ?");

        try{
            ps.setInt(1, price);
            ps.setString(2, address);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }
    private static void viewAllApartments (Scanner sc) throws SQLException{
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments");
        try{
            ResultSet rs = ps.executeQuery();
            try{
                ResultSetMetaData md = rs.getMetaData();

                PrintStream var10000;
                String var10001;
               // int i;
                for (int i = 1 ; i <= md.getColumnCount() ; ++i) {
                    var10000 = System.out;
                    var10001 = md.getColumnName(i);
                    var10000.print(var10001 + "\t\t");
                }
                System.out.println();

                while ( rs.next()){
                    for (int j = 1; j <= md.getColumnCount();++j) {
                        var10000 = System.out;
                        var10001 = rs.getString(j);
                        var10000.print(var10001 + "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }

    private static void viewChoseApartments (String condition) throws SQLException{
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Apartments"+ " WHERE " + condition);
        try{
            ResultSet rs = ps.executeQuery();
            try{
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1 ; i <= md.getColumnCount() ; i++)
                    System.out.print(md.getColumnName(i) + "\t\t");
                    System.out.println();

                while ( rs.next()){

                    for (int i = 1; i <= md.getColumnCount();i++) {
                        System.out.print(rs.getString(i)+ "\t\t");
                    }
                    System.out.println();
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.close();
        }
    }
    private static void selectionApartments (Scanner sc) throws SQLException{

        System.out.println("1: Choose from district.");
        System.out.println("2: Choose from address.");
        System.out.println("3: Choose from area.");
        System.out.println("4: Choose from rooms.");
        System.out.println("5: Choose from price.");
        System.out.print("Enter choice ->");


        switch (sc.nextLine()){
            case "1":
                System.out.print("Please enter what you search: ");
                String cond =  "district = '" + sc.nextLine()+"'";
                System.out.println();
                viewChoseApartments(cond);
                System.out.println();

                break;
            case "2":
                System.out.print("Please enter what you search: ");
                String cond2 =  "address = '" + sc.nextLine()+"'";
                System.out.println();
                viewChoseApartments(cond2);
                System.out.println();
                break;
            case "3":
                System.out.print("Please enter what you search: ");
                String cond3 =  "area = '" + sc.nextLine()+"'";
                System.out.println();
                viewChoseApartments(cond3);
                System.out.println();
                break;

            case "4":
                System.out.print("Please enter what you search: ");
                String cond4 =  "Rooms = '" + sc.nextLine()+"'";
                System.out.println();
                viewChoseApartments(cond4);
                System.out.println();
                break;

            case "5":
                System.out.print("Please enter what you search: ");
                String cond5 =  "price = '" + sc.nextLine()+"'";
                System.out.println();
                viewChoseApartments(cond5);
                System.out.println();
                break;

        }
    }
}

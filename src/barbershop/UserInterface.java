package barbershop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserInterface {
    private final Connection con;
    private final Statement stmt;
    private ResultSet rs;
    private String sqlCommand;
    private PreparedStatement pstmt;

    public UserInterface(Connection con) throws SQLException {
        this.con = con;
        this.stmt = con.createStatement();
    }

    public void start() throws Exception {
        out("**************************************************");
        out("**********Barbershop Admin Control Panel**********");
        out("**************************************************");
        out("Type 0 for help");
        String command;
        do {
            command = in();
            try {
                switch (command) {
                    case "0":
                        out("Enter the symbol of command:");
                        out("1 - Employ a new master");
                        out("2 - Sack a master");
                        out("3 - Add a new customer");
                        out("4 - Update customer details");
                        out("5 - Register a new booking");
                        out("6 - look for customers");
                        out("7 - view customer's upcoming bookings");
                        out("8 - Add discount");
                        out("9 - Remove discount");
                        out("10 - Update discount");
                        out("11 - Add service");
                        out("12 - Exclude service");
                        out("13 - Update service");
                        out("0 - this help message");
                        out("q - Quit");
                        out("**************************************************");
                        break;
                    case "1":
                        addMaster();
                        break;
                    case "2":
                        removeMaster();
                        break;
                    case "3":
                        addCustomer();
                        break;
                    case "4":
                        updateCustomer();
                        break;
                    case "5":
                        addBooking();
                        break;
                    case "6":
                        searchCustomer();
                        break;
                    case "7":
                        viewUpcomingBookings();
                        break;
                    case "8":
                        addDiscount();
                        break;
                    case "9":
                        removeDiscount();
                        break;
                    case "10":
                        updateDiscount();
                        break;
                    case "11":
                        addService();
                        break;
                    case "12":
                        removeService();
                        break;
                    case "13":
                        updateService();
                        break;
                    case "q":
                        break;
                    default:
                        System.out.println("Unknown command");
                        break;
                }
            } catch (SQLWarning w) {
                out("******SQLWarning: " + w.getMessage());
                out(w.getMessage());
            } catch (SQLException s) {
                out("******SQLException: " + s);
                out(s.getMessage());
            } catch (IOException i) {
                out("******IOException: " + i);
                out(i.getMessage());
            } catch (Exception e) {
                out("******Exception: " + e);
                out(e.getMessage());
            }
        }
        while (!command.equals("q"));
    }

    private void addMaster() throws SQLException, IOException {
        out("Adding new master");
        String name = in("Name");
        String sername = in("Sername");
        String id = in("National ID");
        String employmentDate = in("Employment date");
        String qualification = in("qualification");
        sqlCommand = "insert into master values (default, ?, ?, ?, ?, ?);";
        pstmt = con.prepareStatement(sqlCommand);
        setString(1, id);
        setString(2, name);
        setString(3, sername);
        setString(4, qualification);
        setDate(5, employmentDate);
        printSuccess(pstmt.executeUpdate());
    }

    private void removeMaster() throws SQLException, IOException {
        out("Enter ID of master you want to sack from the table below");
        out("Id     Name     Sername");
        rs = stmt.executeQuery("select id, name, sername from master");
        while (rs.next()) {
            out(rs.getInt("id") + "     " + rs.getString("name") + "     " + rs.getString("sername"));
        }
        int id = Integer.parseInt(in());
        printSuccess(stmt.executeUpdate("delete from master where id = " + id + ";"));
    }

    private void addCustomer() throws SQLException, IOException {
        out("Adding new customer");
        String name = in("Name");
        String phone = in("Phone");
        String email = in("Email");
        boolean vip = true;
        String vipSetter = in("Is VIP? Type 0 if no and anything for yes");
        if (vipSetter.equals("0"))
            vip = false;
        sqlCommand = "insert into customer values (default, ?, ?, ?, ?);";
        pstmt = con.prepareStatement(sqlCommand);
        setString(1, name);
        setString(2, phone);
        setString(3, email);
        pstmt.setBoolean(4, vip);
        printSuccess(pstmt.executeUpdate());
    }

    private void updateCustomer() throws SQLException, IOException {
        out("Enter id of customer you want to update information of");
        out("Id     Name     Phone     Email");
        rs = stmt.executeQuery("select id, name, phone, email from customer;");
        while (rs.next()) {
            out(rs.getInt("id") + "     " + rs.getString("name") + "     " + rs.getString("phone") + "     " + rs.getString("email"));
        }
        int id = Integer.parseInt(in());
        rs = stmt.executeQuery("select * from customer where id = " + id + ";");
        String name = in("Enter new name");
        String phone = in("Enter new phone");
        String email = in("Enter new email");
        boolean vip = true;
        String vipSetter = in("Is VIP? Type 0 if no and anything for yes");
        if (vipSetter.equals("0")) {
            vip = false;
        }
        out("[Optional]: enter code from discount list to set it to this customer, otherwise leave empty");
        rs = stmt.executeQuery("select code from discount;");
        while (rs.next()) {
            out(rs.getString(1));
        }
        String code = in();
        String from = in("Active from");
        String to = in("Expires on");
        sqlCommand = "update customer set name=?, phone=?, email=?, vip=? where id=" + id + ";";
        pstmt = con.prepareStatement(sqlCommand);
        setString(1, name);
        setString(2, phone);
        setString(3, email);
        if (vip)
            setString(4, "true");
        else
            setString(5, "false");

        if (!code.equals("")) {
            stmt.executeUpdate("insert into customer_discount values ('" + code + "', " + id + ", '" + from + "', '" + to + "');");
        }
        printSuccess(pstmt.executeUpdate());
    }

    private void addBooking() throws SQLException, IOException {
        out("Registering a new booking");
        String date = in("Date");
        String time = in("Time");
        out("Choose master by typing his/her ID from the table below:");
        out("Id     Name     Sername");
        rs = stmt.executeQuery("select id, name, sername from master");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2) + "     " + rs.getString(3));
        }
        int masterId = Integer.parseInt(in());
        out("Choose customer by typing his/her ID from the table below:");
        out("Id     Name     Phone     Email");
        rs = stmt.executeQuery("select id, name, phone, email from customer");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2) + "     " + rs.getString(3) + "     " + rs.getString(4));
        }
        int customerId = Integer.parseInt(in());
        out("Choose discount code from the list of selected customer discounts or leave empty line");
        out("Code     Expiration date");
        rs = stmt.executeQuery("select code, expiration from customer_discount where id=" + customerId + ";");
        while (rs.next()) {
            out(rs.getString(1) + "     " + rs.getString(2));
        }
        String code = in();
        out("Enter 1 or more service IDs (when finished, leave blank line)");
        out("ID     Name     Price");
        rs = stmt.executeQuery("select * from service;");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2) + "     " + rs.getDouble(3));
        }
        List<Integer> serviceIds = new ArrayList<>();
        String idBuffer;
        do {
            idBuffer = in();
            if (!idBuffer.equals("")) {
                serviceIds.add(Integer.parseInt(idBuffer));
            }
        }
        while (!idBuffer.equals(""));
        try {
            con.setAutoCommit(false);
            sqlCommand = "insert into booking values (default, ?, '" + time + "', " + masterId + ", " + customerId + ", ?);";
            pstmt = con.prepareStatement(sqlCommand);
            setDate(1, date);
            setString(2, code);
            pstmt.executeUpdate();
            rs = stmt.executeQuery("select id from booking where date='" + date + "' and time='" + time + "' and master_id=" + masterId + " and customer_id=" + customerId + ";");
            int bookingId = -1;
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }
            if (serviceIds.size() == 0) {
                out("No service ID provided, operation failed");
                con.rollback();
            } else {
                for (Integer serviceId : serviceIds) {
                    stmt.executeUpdate("insert into customer_service values (" + bookingId + ", " + serviceId + ");");
                }
                con.commit();
                rs = stmt.executeQuery("select * from calculate_booking_price(" + bookingId + ");");
                if (rs.next()) {
                    double initialPrice = rs.getDouble(1);
                    double finalPrice = rs.getDouble(2);
                    System.out.print("Order has been successfully created. Total price is " + initialPrice + " EUR.");
                    if (initialPrice == finalPrice) {
                        out("");
                    } else {
                        out(" Final price with discount is " + finalPrice + " EUR.");
                    }
                } else {
                    // should never get there
                    out("Perhaps order was created successfully, however it is unable to get price information.");
                }
            }
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    private void searchCustomer() throws SQLException, IOException {
        out("Searching for customer");
        String name = in("Name");
        String phone = in("Phone");
        String email = in("Email");
        rs = stmt.executeQuery("select * from customer where name like '" + name + "' or phone like '" + phone + "' or email like '" + email + "';");
        out("Search results:");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2) + "     " + rs.getString(3) + "     " + rs.getString(4));
        }
        out("\r\n-----End of results-----");
    }

    private void viewUpcomingBookings() throws SQLException, IOException {
        out("Customer upcoming bookings review");
        out("Select customer ID:");
        out("Id     Name     Phone     Email");
        rs = stmt.executeQuery("select id, name, phone, email from customer");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2) + "     " + rs.getString(3) + "     " + rs.getString(4));
        }
        int customerId = Integer.parseInt(in());
        rs = stmt.executeQuery("select date, time from booking where customer_id=" + customerId + " and date >= current_date;");
        out("Date     Time");
        while (rs.next()) {
            out(rs.getString(1) + "     " + rs.getString(2));
        }
    }

    private void addDiscount() throws SQLException, IOException {
        out("Adding new discount");
        String code = in("Code");
        double value = Double.parseDouble(in("Value"));
        boolean isFixed = true;
        String fixSetter = in("Is value fixed? Press 0 for no, anything for yes");
        if (fixSetter.equals("0")) {
            isFixed = false;
        }
        printSuccess(stmt.executeUpdate("insert into discount values ('" + code + "', " + value + ", " + isFixed + ");"));
    }

    private void updateDiscount() throws SQLException, IOException {
        out("Type code of discount you want to update:");
        rs = stmt.executeQuery("select code from discount;");
        while (rs.next()) {
            out(rs.getString(1));
        }
        String code = in();
        double value = Double.parseDouble(in("Value"));
        boolean isFixed = true;
        String fixSetter = in("Is value fixed? Press 0 for no, anything for yes");
        if (fixSetter.equals("0")) {
            isFixed = false;
        }
        printSuccess(stmt.executeUpdate("update discount set value=" + value + ", is_fixed=" + isFixed + " where code='" + code + "';"));
    }

    private void removeDiscount() throws SQLException, IOException {
        out("Type code of discount you want to remove:");
        rs = stmt.executeQuery("select code from discount;");
        while (rs.next()) {
            out(rs.getString(1));
        }
        String code = in();
        printSuccess(stmt.executeUpdate("delete from discount where code='" + code + "';"));
    }

    private void addService() throws SQLException, IOException {
        out("Adding new service");
        String name = in("Name");
        double price = Double.parseDouble(in("Price"));
        int duration = Integer.parseInt(in("Duration (in minutes)"));
        printSuccess(stmt.executeUpdate("insert into service values(default, '" + name + "', " + price + ", '" + duration + "m');"));
    }

    private void removeService() throws SQLException, IOException {
        out("Type ID of service you want to remove:");
        out("ID     Name");
        rs = stmt.executeQuery("select id, name from service;");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2));
        }
        int id = Integer.parseInt(in());
        printSuccess(stmt.executeUpdate("delete from service where id=" + id + ";"));
    }

    private void updateService() throws SQLException, IOException {
        out("Type ID of service you want to update:");
        out("ID     Name");
        rs = stmt.executeQuery("select id, name from service;");
        while (rs.next()) {
            out(rs.getInt(1) + "     " + rs.getString(2));
        }
        int id = Integer.parseInt(in());
        String name = in("Name");
        double price = Double.parseDouble(in("Price"));
        int duration = Integer.parseInt(in("Duration (in minutes)"));
        printSuccess(stmt.executeUpdate("update service set name='" + name + "', price=" + price + ", duration='" + duration + "m' where id=" + id + ";"));
    }

    private void printSuccess(int rows) {
        System.out.print(rows + " row");
        if (rows != 1) {
            System.out.print("s");
        }
        out(" affected");
        System.out.println("Operation was completed successfully!");
    }

    private String in(String hint) throws IOException {
        System.out.print(hint + ": ");
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    // overload: without hint
    private String in() throws IOException {
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    private void out(String text) {
        System.out.println(text);
    }

    private void setString(int position, String value) throws SQLException {
        if (value.equals("")) {
            pstmt.setNull(position, Types.NULL);
        } else {
            pstmt.setString(position, value);
        }
    }

    private void setDate(int position, String value) throws SQLException {
        if (value.equals("")) {
            pstmt.setNull(position, Types.NULL);
        } else {
            pstmt.setDate(position, java.sql.Date.valueOf(value));
        }
    }

}
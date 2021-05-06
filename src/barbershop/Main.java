package barbershop;

import java.sql.DriverManager;

public class Main {
    /**
     * @author Vytautas Leveris, VU MIF PS 2 k., 5 gr.
     */
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            UserInterface uI = new UserInterface(DriverManager.getConnection("jdbc:postgresql://pgsql3.mif/studentu", "vyle6357", "Hermis501"));
            uI.start();
        } catch (Exception e) {
            System.out.println("Connection error occured!");
            System.out.println(e);
        }
    }

}
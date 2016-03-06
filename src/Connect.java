/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author mattgaitan
 */

public class Connect {
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private String name;
    private String price;
    Vector colNames = new Vector();
    Vector data = new Vector();
    private String junk;

    public Connect(){
        try{
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@//delphi.cs.csubak.edu:1521/dbs01.cs.csubak", "winter342", "c3m4p2s");
            st = con.createStatement();
            st.setFetchSize(5000);
        }
        catch(ClassNotFoundException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    public DefaultTableModel getTransaction(){
        try{   
            rs = st.executeQuery("Select * from JGMG_view_transaction");
            while(rs.next()){
                Vector v2 = new Vector();
                for(int i = 1; i < 10; i++){
                    if(8 != i){
                        v2.addElement(rs.getString(i));
                    }
                    else
                        junk = rs.getString(i);      
                }
                data.addElement(v2);
            }
            colNames.addElement("EmployeeID");
            colNames.addElement("CustomerID");
            colNames.addElement("SaleID");
            colNames.addElement("ProductID");
            colNames.addElement("Title");
            colNames.addElement("Quantity");
            colNames.addElement("Sale Date");
            colNames.addElement("Total");
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return new DefaultTableModel(data, colNames);
    }
    
    public Vector getCustomers(){
        String temp;
        Vector cust = new Vector();

        try{
            rs = st.executeQuery("Select fname, lname from JGMG_Customer Order by lname asc");
            while(rs.next()){
                temp = rs.getString(1) + " " + rs.getString(2);
                cust.addElement(temp);
            }
            
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return cust;
    }
    
    public Vector getTitles(){
        Vector title = new Vector();
        try{
            rs = st.executeQuery("Select Title from JGMG_Product");
            while(rs.next()){
                title.addElement(rs.getString(1));
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return title;
    }
    
    public String getPrice(String Title){
        String currentPrice = null;
        Boolean found = false;
        try{
            rs = st.executeQuery("Select Title, currentPrice from JGMG_Product");
            while(rs.next() && !found){
                if(rs.getString(1).compareTo(Title) == 0){
                    currentPrice = rs.getString(2);
                    found = true;
                }
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return currentPrice;
    }
}

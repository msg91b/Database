/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.*;
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
    Vector colNames = new Vector();
    Vector data = new Vector();

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
    
    public void completeTransaction(String employeeID, String customerID, String saleID, String productID, String Title, String Quantity, String Total){
        try{
          System.out.println("Insert into JGMG_view_Transaction Values("+ employeeID + ", " + customerID + ", " + saleID + ", " + productID + ", '" + Title + "', " + Quantity + ", " + "trunc(sysdate)" + ", " + Total +")");
            st.executeUpdate("Insert into JGMG_view_Transaction Values("+ employeeID + ", " + customerID + ", " + saleID + ", " + productID + ", '" + Title + "', " + Quantity + ", " + "trunc(sysdate)" + ", " + Total +")");
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        
    }
    
    public boolean employeeLogin(String employeeID){
        boolean ret = false;
        try{
            rs = st.executeQuery("Select employeeID from JGMG_Employee where employeeID = " + employeeID);
            if(rs.next()){
                ret = true;
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return ret;
    }
    
    public String getCustomerID(String fName, String lName){
        String customerID = null;
        try{
            rs = st.executeQuery("Select customerID from JGMG_Customer where fName = '" + fName + "' and lName = '" + lName + "'");
            if(rs.next()){
                customerID = rs.getString(1);
            }                 
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }   
        return customerID;
    }
    
    public String getProductID(String Title){
        String productID = null;
        try{
            rs = st.executeQuery("Select productID from JGMG_Product where Title = '" + Title + "'");
            if(rs.next()){
                productID = rs.getString(1);
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return productID;
    }
    
    public String getNextSaleID(){
        String saleID = null;
        try{
            rs = st.executeQuery("Select saleID from JGMG_SALE WHERE rownum = 1 order by saleID desc");
            if(rs.next()){
                saleID = "" + (Integer.parseInt(rs.getString(1)) + 1);
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return saleID;
    }
    
    public String getEmployeeName(String employeeID){
        String employeeName = null;
        try{
            rs = st.executeQuery("Select efname, elname from JGMG_Employee where employeeID = '" + employeeID + "'");
            if(rs.next()){
                employeeName = rs.getString(1) + " " + rs.getString(2);
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return employeeName;
    }
    
    public DefaultTableModel getAllCustomerData(){
        Vector customer = new Vector();
        Vector column = new Vector();
        try{
            rs = st.executeQuery("Select * from JGMG_Customer order by customerID asc");
        
            while(rs.next()){
                Vector temp = new Vector();
                for(int i = 1; i < 11; i++){
                    temp.addElement(rs.getString(i));
                }
                customer.addElement(temp);
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex + "here?");
        }
        
        column.addElement("customerID");
        column.addElement("First Name");
        column.addElement("Last Name");
        column.addElement("Street Number");
        column.addElement("Street Name");
        column.addElement("City");
        column.addElement("State");
        column.addElement("Zip");
        column.addElement("Email Address");
        column.addElement("Phone Number");
            
        DefaultTableModel model = new DefaultTableModel(customer, column);

        return model;
    }
    
    public DefaultTableModel getCustomerSearch(String searchFor){
        DefaultTableModel model = getAllCustomerData();
        Vector v1 = new Vector();
        Vector v2 = new Vector();
        boolean check;
        String temp;
        for(int row = 0; row < model.getRowCount(); row++){
            check = true;
            for(int col = 0; col < model.getColumnCount() && check; col++){
                if(model.getValueAt(row, col) != null){
                    temp = (String) model.getValueAt(row, col);
                    
                    int searchLength = searchFor.length();
                    if(temp.length() >= searchLength){
                        if(temp.substring(0, searchLength).compareTo(searchFor) == 0){
                            v1.addElement(model.getDataVector().elementAt(row));
                            check = false;
                        }
                    }
                }
            }
        }
        v2.addElement("customerID");
        v2.addElement("First Name");
        v2.addElement("Last Name");
        v2.addElement("Street Number");
        v2.addElement("Street Name");
        v2.addElement("City");
        v2.addElement("State");
        v2.addElement("Zip");
        v2.addElement("Email Address");
        v2.addElement("Phone Number");

    DefaultTableModel newModel = new DefaultTableModel(v1,v2);
    return newModel;  
    }
   
}


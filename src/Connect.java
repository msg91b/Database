/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.math.BigDecimal;
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
                    if(i == 2 || i == 4){
                        temp.addElement(rs.getString(i) + " " + rs.getString(i+1));
                        i++;
                    }
                    else{
                        temp.addElement(rs.getString(i));
                    }
                }
                customer.addElement(temp);
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        
        column.addElement("customerID");
        column.addElement("Name");
        column.addElement("Address");
        column.addElement("City");
        column.addElement("State");
        column.addElement("Zip");
        column.addElement("Email");
        column.addElement("Phone Number");
            
        DefaultTableModel model = new DefaultTableModel(customer, column){
            boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false
        };

            @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
        };

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
        v2.addElement("Name");
        v2.addElement("Address");
        v2.addElement("City");
        v2.addElement("State");
        v2.addElement("Zip");
        v2.addElement("Email");
        v2.addElement("Phone Number");

    DefaultTableModel newModel = new DefaultTableModel(v1,v2){
            boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false, false, false
        };

            @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }
        };
    return newModel;  
    }
    
    public BigDecimal getNextCustomerID(){
        BigDecimal nextID = null;
        try{
            rs = st.executeQuery("Select customerID from JGMG_Customer WHERE rownum = 1 order by CustomerID desc");
            if(rs.next()){
                nextID = BigDecimal.valueOf(Long.parseLong(rs.getString(1))+1);
            }
        }
        catch(SQLException ex){
            System.out.println("Error:" + ex);
        }
        return nextID;
    }
    
    public void addCustomer(String n, String a, String c, String s, String z, String e, String p){
        String[] flname = n.split(" ");
        String[] address = a.split(" ");
        String streetName = "";
        
        System.out.println(getNextCustomerID());
        BigDecimal streetNum = BigDecimal.valueOf(Long.parseLong(address[0]) + 1);
        // Concatonate streetName back into a string
        for(int i = 1; i < address.length; i++){ // need to start at 1 because first element will be street num
            if(i == address.length-1){
                streetName += address[i];
            }
            else{
                streetName += address[i] + " ";
            }
        }
        
        BigDecimal zip = BigDecimal.valueOf(Long.parseLong(z));
        BigDecimal phone = null;
        if(p.compareTo("") != 0){
            phone = BigDecimal.valueOf(Long.parseLong(p));
        }   
        
        System.out.println(streetName);
       try{
        PreparedStatement na = con.prepareStatement("Insert into JGMG_Customer Values (?,?,?,?,?,?,?,?,?,?)");
        na.setBigDecimal(1, getNextCustomerID());
        na.setString(2, flname[0]);
        na.setString(3, flname[1]);
        na.setBigDecimal(4, streetNum);
        na.setString(5, streetName);
        na.setString(6, c);
        na.setString(7, s);
        na.setBigDecimal(8, zip);
        na.setString(9, e);
        na.setBigDecimal(10, phone);
        
        na.executeUpdate();
       }
       catch(SQLException ex){
           System.out.println("Error: " + ex);
       }
    }
   
    public DefaultTableModel getSelectedSales(String name){
        String[] flname = name.split(" ");
        String customerID = getCustomerID(flname[0], flname[1]);
        Vector data = new Vector();
        Vector col = new Vector();
        try{
            PreparedStatement na = con.prepareStatement("Select unique saleID, \"Sale Date\", Total From JGMG_view_transaction where customerID = ? ORDER BY saleID");
            na.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(customerID)));
            rs = na.executeQuery();
            
            while(rs.next()){
                Vector temp = new Vector();
                //System.out.println(rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
                temp.addElement(rs.getString(1));
                temp.addElement(rs.getString(2).substring(0, 10));
                temp.addElement(rs.getString(3));
                data.addElement(temp);
                }
            
            
            col.addElement("saleID");
            col.addElement("Sale Date");
            col.addElement("Total");
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        
        DefaultTableModel model = new DefaultTableModel(data, col);
        return model;
    }
}


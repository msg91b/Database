/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;

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
        
        cust.addElement("");

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
                        if(temp.contains(searchFor) == true){
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
    
    public void addCustomer(String fn, String ln, String a, String c, String s, String z, String e, String p){
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
        na.setString(2, fn);
        na.setString(3, ln);
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
        
        DefaultTableModel model = new DefaultTableModel(data, col){boolean[] canEdit = new boolean [] {
        false, false, false
    };

        @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
    }};
        return model;
    }
    
    public DefaultTableModel getAllOrdersData() {
        Vector data = new Vector();
        Vector supp = new Vector();
        try {
            rs = st.executeQuery("SELECT unique orderID, supplierID, cDate FROM JGMG_view_Orders ORDER BY OrderID asc");
            while(rs.next()) {
                String out;
                Vector temp = new Vector();
                for(int i=1; i < 4; i++) {
                    if(i==3){
                        out = rs.getString(i).substring(0, 10);
                    }
                    else{
                        out = rs.getString(i);
                    }
                    temp.addElement(out);
                }
                supp.addElement(temp);
            }
        }catch(SQLException ex) {
            System.out.println("Error: " + ex);
        }
        data.addElement("OrderID");
        data.addElement("SupplierID");
        data.addElement("Order Date");
        DefaultTableModel model = new DefaultTableModel(supp, data){boolean[] canEdit = new boolean [] {
        false, false, false
    };

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
    }};
        return model;
    }
    
    public Vector getOrders() {
        String temp;
        Vector supp = new Vector();
        try {
            rs = st.executeQuery("SELECT * FROM JGMG_view_Orders ORDER BY OrderID asc");
            while(rs.next()) {
                temp = rs.getString(1)+ " " + rs.getString(2) + " " + rs.getString(3)+ " " + rs.getString(4) + " " + rs.getString(5)+ " " + rs.getString(6) + " " + rs.getString(7);
                supp.addElement(temp);
            }
        }catch(SQLException ex) {
            System.out.println("Error: " + ex);
        }
        return supp;
    }
    
    public DefaultTableModel getOrdersSearch(String searchOrder) {
        DefaultTableModel model = getAllOrdersData();
        Vector v1 = new Vector();
        Vector v2 = new Vector();
        boolean check;
        String temp;
        for(int row = 0; row < model.getRowCount(); row++){
            check = true;
            for(int col = 0; col < model.getColumnCount() && check; col++){
                if(model.getValueAt(row, col) != null){
                    temp = (String) model.getValueAt(row, col);
                    
                    int searchLength = searchOrder.length();
                    if(temp.length() >= searchLength){
                        if(temp.substring(0, searchLength).compareTo(searchOrder) == 0){
                            v1.addElement(model.getDataVector().elementAt(row));
                            check = false;
                        }
                    }
                }
            }
        }
        v2.addElement("OrderID");
        v2.addElement("SupplierID");
        v2.addElement("Order Date");

    DefaultTableModel newModel = new DefaultTableModel(v1,v2){boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false
    };

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
    }};
    return newModel;  
    }
    
    public void generateSaleReciept(int s){
        try{
            HashMap m = new HashMap();
            String report = "saleReport.jrxml";
            JasperReport jR = JasperCompileManager.compileReport(getClass().getResourceAsStream(report));
            m.put("saleID", s);
            JasperPrint jP = JasperFillManager.fillReport(jR, m, con);
            JasperViewer.viewReport(jP, false);
            }
            catch(Exception ex){
                System.out.println("Error: " + ex);
            }
    }
    
    public String latestCustomer(){
        String name = null;

        try{
            rs = st.executeQuery("SELECT fname, lname from JGMG_Customer where rownum = 1 order by customerID desc");
            if(rs.next()){
                name = rs.getString(1) + " " + rs.getString(2);
            }
                    
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return name;
    }
    
    public BigDecimal getNextItemID(){
        BigDecimal nextItemID = null;
        try{
            rs = st.executeQuery("Select * from JGMG_Item where rownum = 1 order by itemID desc");
            if(rs.next()){
                nextItemID = BigDecimal.valueOf(Long.parseLong(rs.getString(1)) + 1);
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return nextItemID;
    }
    
    public void completeRequest(String t, String q, String n){
        String[] flname = n.split(" ");
        try{
            PreparedStatement ps = con.prepareStatement("Insert into JGMG_view_requests Values(?,?,?,?,trunc(sysdate))");
            ps.setBigDecimal(1, getNextItemID());
            ps.setBigDecimal(2, BigDecimal.valueOf(Long.parseLong(getCustomerID(flname[0], flname[1])) + 1));
            ps.setString(3, t);
            ps.setBigDecimal(4, BigDecimal.valueOf(Long.parseLong(q)));
            
            ps.executeUpdate();
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
    }
    
    public void generateOrderInvoice(int orderID){
        try{
            HashMap m = new HashMap();
            String report = "orderInvoice.jrxml";
            JasperReport jR = JasperCompileManager.compileReport(getClass().getResourceAsStream(report));
            m.put("orderID", orderID);
            JasperPrint jP = JasperFillManager.fillReport(jR, m, con);
            JasperViewer.viewReport(jP, false);
        }
        catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }
    
    public Vector getSuppliers(){
        Vector suppliers = new Vector();
        try{
        rs = st.executeQuery("Select SUPPLIERID from JGMG_Supplier order by supplierID asc");
        while(rs.next()){
            suppliers.addElement(rs.getString(1));
        }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return suppliers;
    }
    
    public BigDecimal getNextOrderID(){
        BigDecimal orderID = null;
        try{
            rs = st.executeQuery("Select orderID from JGMG_Order where rownum = 1 order by orderID desc");
            if(rs.next()){
                orderID = BigDecimal.valueOf(Long.parseLong(rs.getString(1)) + 1);
            }
            System.out.println(orderID);
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return orderID;
    }
    
    public void addOrder(BigDecimal o, String s, String p, String t, String q){
        try{
            
            PreparedStatement ps = con.prepareStatement("Insert into JGMG_view_orders values(?,?,?,?,?,trunc(sysdate))");
            ps.setBigDecimal(1, o);
            ps.setBigDecimal(2, BigDecimal.valueOf(Long.parseLong(s)));
            ps.setBigDecimal(3, BigDecimal.valueOf(Long.parseLong(p)));
            ps.setString(4, t);
            ps.setBigDecimal(5, BigDecimal.valueOf(Long.parseLong(q)));
            System.out.println("Insert into JGMG_view_orders values(" + o + ", " + s + ", " + p + ", " + t + ", " + q + ", " + "trunc(sysdate)" +")");
            
            ps.executeQuery();
            
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
    }
    
    public String[] customerData(String customerID){
        String[] custData = new String[9];
        try{
            rs = st.executeQuery("select * from JGMG_Customer where customerID = " + Integer.parseInt(customerID));
            if(rs.next()){
                custData[0] = rs.getString(1);//id
                custData[1] = rs.getString(2);//fname
                custData[2] = rs.getString(3);//lanme
                custData[3] = rs.getString(4) + " " + rs.getString(5);//streetnum + streetname
                custData[4] = rs.getString(6);//city
                custData[5] = rs.getString(7);//state
                custData[6] = rs.getString(8);//zip
                custData[7] = rs.getString(9);//email
                custData[8] = rs.getString(10);//phone

                
            }
        }
        catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
        return custData;
    }
    
    public void updateCustomer(String id, String fn, String ln, String a, String c, String s, String z, String e, String p){
        String[] address = a.split(" ");
        String streetName = "";
        
        //System.out.println(getNextCustomerID());
        BigDecimal streetNum = BigDecimal.valueOf(Long.parseLong(address[0]));
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
        PreparedStatement na = con.prepareStatement("Update JGMG_Customer set fname = ?, lname = ?, "
                + "streetNum = ?, streetName = ?, city = ?, state = ?, zip = ?, email = ?, phonenumber = ? where customerID = ?");
        na.setString(1, fn);
        na.setString(2, ln);
        na.setBigDecimal(3, streetNum);
        na.setString(4, streetName);
        na.setString(5, c);
        na.setString(6, s);
        na.setBigDecimal(7, zip);
        na.setString(8, e);
        na.setBigDecimal(9, phone);
        na.setBigDecimal(10, BigDecimal.valueOf(Long.parseLong(id)));
    

        na.executeUpdate();
       }
       catch(SQLException ex){
           System.out.println("Error: " + ex);
       }
    }
    
}


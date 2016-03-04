/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.*;
/**
 *
 * @author mattgaitan
 */

public class Connect {
    private Connection con;
    private Statement st;

    public Connect(){

        try{
        Class.forName("oracle.jdbc.OracleDriver");
        con = DriverManager.getConnection("jdbc:oracle:thin:@//delphi.cs.csubak.edu:1521/dbs01.cs.csubak", "winter342", "c3m4p2s");
        st = con.createStatement();
        }
        catch(ClassNotFoundException | SQLException ex){
            System.out.println(ex);
        }
    }
    
    public void getTransaction(){
        
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Process;

import ConnectDB.OracleConnection;
import Model.Person;
import Model.Supply;
import Model.SupplyTableModel;
import View.ChangeValue;
import View.CharityScreen;
import java.awt.Font;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Tran Nhat Sinh
 */
public class SupplyController {

    private Connection con;

    //Ham lay cac yeu tiep te cua da gui cua nguoi dung do 
    public ArrayList<HashMap> getSupply(int idper) {
        try
        {
            con = OracleConnection.getOracleConnection();
            String sql = "select idsup,name,created,needfood,neednecess,needequip,status,detail\n"
                    + "from supply s\n"
                    + "left join charity p\n"
                    + "on s.idchar=p.idchar\n"
                    + "where idper=" + idper;
            ArrayList<HashMap> list = new ArrayList<HashMap>();
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next())
            {
                HashMap<String, String> sup = new HashMap<String, String>();
                sup.put("idsup", rs.getString(1));
                sup.put("name", rs.getString(2));
                sup.put("created", ChangeValue.DateToString(rs.getDate(3)));
                sup.put("needfood", Integer.toString(rs.getInt(4)));
                sup.put("neednecess", Integer.toString(rs.getInt(5)));
                sup.put("needequip", Integer.toString(rs.getInt(6)));
                sup.put("status", Integer.toString(rs.getInt(7)));
                sup.put("detail", rs.getString(8));
                list.add(sup);
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex)
        {
            ex.printStackTrace();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new ArrayList<HashMap>();
    }
    
    
    
    

    //Ham lay cac yeu cau tiep te da mo cua nguoi dung do
    public ArrayList<HashMap> getSupplyHaveStatus1(int idper) {
        try
        {
            con = OracleConnection.getOracleConnection();
            String sql = "select idsup,name,created,needfood,neednecess,needequip,status,detail\n"
                    + "from supply s\n"
                    + "left join charity p\n"
                    + "on s.idchar=p.idchar\n"
                    + "where status=1 and\n"
                    + "idper= " + idper;
            ArrayList<HashMap> list = new ArrayList<HashMap>();
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next())
            {
                HashMap<String, String> sup = new HashMap<String, String>();
                sup.put("idsup", rs.getString(1));
                sup.put("name", rs.getString(2));
                sup.put("created", ChangeValue.DateToString(rs.getDate(3)));
                sup.put("needfood", Integer.toString(rs.getInt(4)));
                sup.put("neednecess", Integer.toString(rs.getInt(5)));
                sup.put("needequip", Integer.toString(rs.getInt(6)));
                sup.put("status", Integer.toString(rs.getInt(7)));
                sup.put("detail", rs.getString(8));
                list.add(sup);
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex)
        {
            ex.printStackTrace();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new ArrayList<HashMap>();
    }

    public static String getNextValueSupply() {
        String id = "";
        try
        {
            Connection con = OracleConnection.getOracleConnection();
            String sql1 = "SELECT IDSUP.NEXTVAL  s FROM DUAL";
            Statement statement1 = con.createStatement();
            ResultSet rs1 = statement1.executeQuery(sql1);

            if (rs1.next())
            {
                id = rs1.getString("s");
            }

            String sql2 = "ALTER SEQUENCE IDSUP INCREMENT BY -1";
            Statement statement2 = con.createStatement();
            ResultSet rs2 = statement2.executeQuery(sql2);

            String sql3 = "SELECT IDSUP.NEXTVAL FROM DUAL";
            Statement statement3 = con.createStatement();
            ResultSet rs3 = statement3.executeQuery(sql3);

            String sql4 = "ALTER SEQUENCE IDSUP INCREMENT BY 1";
            Statement statement4 = con.createStatement();
            ResultSet rs4 = statement4.executeQuery(sql4);

            rs1.close();
            rs2.close();
            rs3.close();
            rs4.close();
            con.close();
            return id;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return id;
    }

    public static int AddSupply(Supply Supply) {
        try
        {
            Connection con = OracleConnection.getOracleConnection();
            String sql = "{CALL PROC_INSERT_SUPPLY(?,?,?,?,?)}";
            CallableStatement ps = con.prepareCall(sql);

            ps.setInt(1, Supply.getIdper());
            ps.setInt(2, Supply.getNeedfood());
            ps.setInt(3, Supply.getNeednecess());
            ps.setInt(4, Supply.getNeedequip());
            ps.setString(5, Supply.getDetail());
            //ps.setInt(6,Supply.getStatus());

            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException sqlex)
        {
            if (sqlex.getErrorCode() == 1400)
            {
                JOptionPane.showMessageDialog(null, "Kh??ng ???????c ????? tr???ng c??c mi???n gi?? tr??? b???t bu???c!",
                        "L???i!", JOptionPane.WARNING_MESSAGE);
            }
            else if (sqlex.getErrorCode() == 2290)
            {
                JOptionPane.showMessageDialog(null, "Kh??ng ???????c ????? tr???ng c??c mi???n gi?? tr??? b???t bu???c!",
                        "L???i!", JOptionPane.WARNING_MESSAGE);
            }else if (sqlex.getErrorCode() == 20500)
            {
                JOptionPane.showMessageDialog(null, "Y??u c???u v??? l????ng th???c ch??a ???????c ho??n th??nh",
                        "L???i!", JOptionPane.WARNING_MESSAGE);
            } else if (sqlex.getErrorCode() == 20501)
            {
                JOptionPane.showMessageDialog(null, "Y??u c???u v??? nhu y???u ph???m ch??a ???????c ho??n th??nh",
                        "L???i!", JOptionPane.WARNING_MESSAGE);
            } else if (sqlex.getErrorCode() == 20502)
            {
                JOptionPane.showMessageDialog(null, "Y??u c???u v??? v???t d???ng ch??a ???????c ho??n th??nh",
                        "L???i!", JOptionPane.WARNING_MESSAGE);
            } else if (sqlex.getErrorCode() == 20010)
            {
                JOptionPane.showMessageDialog(null, "Ng?????i d??ng kh??ng t???n t???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            }

            return 1;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    public static int DeleteSupply(int Idsup) {
        try
        {
            Connection con = OracleConnection.getOracleConnection();
            String sql = "{CALL PROC_DELETE_SUPPLY(?)}";
            CallableStatement ps = con.prepareCall(sql);

            ps.setInt(1, Idsup);

            ps.execute();

            ps.close();
            con.close();
        } catch (SQLException sqlex) {
            if (sqlex.getErrorCode() == 20152) {
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u kh??ng h???p l???, kh??ng th??? x??a!",
                        "C???nh b??o!", JOptionPane.ERROR_MESSAGE);
            }else if (sqlex.getErrorCode() == 20153) {
                JOptionPane.showMessageDialog(null, "Vui l??ng ch???n m???t y??u c???u tr?????c khi x??a!",
                        "C???nh b??o!", JOptionPane.WARNING_MESSAGE);
            } 
            return 1;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return 0;
    }


    public static int UpdateSupply(Supply Supply) {
        try {
            Connection con = OracleConnection.getOracleConnection();
            String sql = "{CALL PROC_UPDATE_SUPPLY(?,?,?,?,?)}";
            CallableStatement ps = con.prepareCall(sql);
                      
            ps.setInt(1, Supply.getIdsup());
            ps.setInt(2, Supply.getNeedfood());
            ps.setInt(3, Supply.getNeednecess());
            ps.setInt(4, Supply.getNeedequip());
            ps.setString(5,Supply.getDetail());
            //ps.setInt(6,Supply.getStatus());
            
            ps.execute();
            
            ps.close();
            con.close();
        } catch (SQLException sqlex) {
            if (sqlex.getErrorCode() == 20142){
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u kh??ng h???p l???!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);}
//            else if (sqlex.getErrorCode() == 1407){
//                JOptionPane.showMessageDialog(null, "Vui l??ng ch???n t???i thi???u m???t danh m???c c???n ti???p t???!",
//                        "L???i!", JOptionPane.WARNING_MESSAGE);}
            else if (sqlex.getErrorCode() == 20081){
                JOptionPane.showMessageDialog(null, "Y??u c???u ti???p t??? n??y kh??ng c??n t???n t???i",
                        "L???i!", JOptionPane.ERROR_MESSAGE);}
            else if (sqlex.getErrorCode() == 2290){
                JOptionPane.showMessageDialog(null, "Vui l??ng ch???n t???i thi???u m???t danh m???c c???n ti???p t???!",
                        "L???i!", JOptionPane.WARNING_MESSAGE);}
            return 1;
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
            return 1;
        }
        return 0;       
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    ////Controller cho charity///////////////////////
    
    
    //Ham lay tat ca yeu tiep te
    public  ArrayList<HashMap> getSupplyCharity()
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select idsup,name,gender,phone,province,created,needfood,neednecess,needequip,detail\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=2 ";
            ArrayList<HashMap> list= new ArrayList<HashMap>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> sup= new HashMap<String,String>();
                sup.put("idsup",rs.getString(1));
                sup.put("name",rs.getString(2));
                sup.put("gender",Integer.toString(rs.getInt(3)));
                sup.put("phone",rs.getString(4));
                sup.put("province",rs.getString(5));
                sup.put("created",ChangeValue.DateToString(rs.getDate(6)));
                sup.put("needfood",Integer.toString(rs.getInt(7)));
                sup.put("neednecess",Integer.toString(rs.getInt(8)));
                sup.put("needequip",Integer.toString(rs.getInt(9)));
                sup.put("detail",rs.getString(10));
                list.add(sup);
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<HashMap>();
    }
    
    //Ph???n n??y l?? demo PHANTOM READ
    public  void getSupplyTransaction(JTable SupplyTable,DefaultTableModel SupplyTableModel,ArrayList<HashMap> SupplyList)
    {
        try {
            Connection con = OracleConnection.getOracleConnection();
            int i = -1;
            i = Connection.TRANSACTION_READ_COMMITTED;
//            i = Connection.TRANSACTION_SERIALIZABLE;
            con.setAutoCommit(false);
            con.setTransactionIsolation(i);
            SupplyTable.getSelectionModel().clearSelection();
            SupplyTableModel=getSupplyModelTransaction(con, SupplyList);
            SupplyTable.setModel(SupplyTableModel);
            setSupplyTableSize(SupplyTable);
            //Th??ng b??o ???? xong l???n 1
            System.out.println("1");
            //H??m 1 thread m???i v?? th???c hi???n 2 giao t??c trong 
            //Ph???n n??y l?? x??? l?? tr?????ng h???p phantom read
            new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
//                                SwingUtilities.invokeLater(new Runnable() {
//                                    @Override
//                                    public void run()  {
                    try {
                        //Ngu 10 giay
                        Thread.sleep(10000);
                        //Bat dau thuc hien lan 2
                        System.out.println("2");
                        SupplyTable.getSelectionModel().clearSelection();
                        SupplyTable.setModel(getSupplyModelTransaction(con, SupplyList));
                        setSupplyTableSize(SupplyTable);
                        con.commit();
                    } catch (SQLException ex) {
                        Logger.getLogger(CharityScreen.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CharityScreen.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(CharityScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                                    }
//                                });
                    return null;
                }
            }.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CharityScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CharityScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CharityScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //H??m n??y d??ng ????? set d??? li???u cho model b???ng trong khi th???c hi???n transaction
    public static DefaultTableModel getSupplyModelTransaction (Connection con,ArrayList<HashMap> list) 
    throws SQLException,UnsupportedOperationException,Exception
    {
            String sql = "select idsup,name,gender,phone,province,created,needfood,neednecess,needequip,detail\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=2 ";
            list.clear();
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> sup= new HashMap<String,String>();
                sup.put("idsup",rs.getString(1));
                sup.put("name",rs.getString(2));
                sup.put("gender",Integer.toString(rs.getInt(3)));
                sup.put("phone",rs.getString(4));
                sup.put("province",rs.getString(5));
                sup.put("created",ChangeValue.DateToString(rs.getDate(6)));
                sup.put("needfood",Integer.toString(rs.getInt(7)));
                sup.put("neednecess",Integer.toString(rs.getInt(8)));
                sup.put("needequip",Integer.toString(rs.getInt(9)));
                sup.put("detail",rs.getString(10));
                list.add(sup);
            }
            DefaultTableModel SupplyTableModel= new SupplyTableModel().setSupplyTableCharity(list);
            return SupplyTableModel;
    }
    
    //H??m l???y t???t c??? y??u c???u t?? v???n theo t???nh/th??nh ph??? ????
    public ArrayList<HashMap> getSupplyByProvince(String province)
    {
        try{
            con =OracleConnection.getOracleConnection();
            String sql = "select idsup,name,gender,phone,province,created,needfood,neednecess,needequip,detail\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=2 and \n"
                    + "province= '"+province+"'";
            ArrayList<HashMap> list= new ArrayList<>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> sup= new HashMap<>();
                sup.put("idsup",rs.getString(1));
                sup.put("name",rs.getString(2));
                sup.put("gender",Integer.toString(rs.getInt(3)));
                sup.put("phone",rs.getString(4));
                sup.put("province",rs.getString(5));
                sup.put("created",ChangeValue.DateToString(rs.getDate(6)));
                sup.put("needfood",Integer.toString(rs.getInt(7)));
                sup.put("neednecess",Integer.toString(rs.getInt(8)));
                sup.put("needequip",Integer.toString(rs.getInt(9)));
                sup.put("detail",rs.getString(10));
                list.add(sup);
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    //H??m n??y d??ng ????? l???y t???t c??? t???nh th??nh ph??? m?? c?? ng?????i mu???n ti???p t???
    public ArrayList<String> getProvinceFromSup()
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select distinct province\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=2 \n";
            ArrayList<String> list= new ArrayList<>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        }catch (SQLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<String>();
    }
    
    //H??m g???i th??? t???c ch???p nh???n y??u c???u t?? v???n
    public int AcceptSupply(String idchar,String idsup)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String CallProc = "{call PROC_ACCEPT_SUPPLY(?,?)}";
            CallableStatement callSt=con.prepareCall(CallProc);
            callSt.setString(1,idchar);
            callSt.setString(2,idsup);
            callSt.execute();
            callSt.close();
            con.close();
            return 1;
        }catch (SQLException sqle) {
            if (sqle.getErrorCode() == 20212)
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u n??y kh??ng h???p l??? ????? nh???n, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            else if (sqle.getErrorCode() == 20213)
                JOptionPane.showMessageDialog(null, "Kh??ng ????? ??i???u ki???n ch???p nh???n y??u c???u n??y!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            else if (sqle.getErrorCode() == 20214)
                JOptionPane.showMessageDialog(null, "Y??u c???u n??y kh??ng c??n t???n t???i, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            //sqle.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    //Ph???n n??y l?? ch???nh s???a c???t v?? v?? header cho b???ng v?? kh??ng th??? l???y
    // h??m t??? b??n m??n h??nh trung t??m qua ???????c
    private void setSupplyTableSize(JTable SupplyTable)
    {
        //column
        SupplyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        SupplyTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        SupplyTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        SupplyTable.getColumnModel().getColumn(3).setPreferredWidth(40);
       
        //header
        TableCellRenderer rendererFromHeader = SupplyTable.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JTableHeader header=SupplyTable.getTableHeader();
        Font headerFont = new Font("Segoe", Font.PLAIN, 14);
        header.setFont(headerFont);
    }
    
    //=======================================================================
    //H??m cho m??n h??nh tiep te
    //H??m l???y t???t c??? y??u c???u t?? v???n ???? nh???n cua 1 trung t??m
    public  ArrayList<HashMap> getWaitSupply(String idchar)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select idsup,name,gender,phone,province,created,needfood,neednecess,needequip,detail\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=3 and \n"
                    + "idchar= "+idchar;
            ArrayList<HashMap> list= new ArrayList<HashMap>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> sup= new HashMap<String,String>();
                sup.put("idsup",rs.getString(1));
                sup.put("name",rs.getString(2));
                sup.put("gender",Integer.toString(rs.getInt(3)));
                sup.put("phone",rs.getString(4));
                sup.put("province",rs.getString(5));
                sup.put("created",ChangeValue.DateToString(rs.getDate(6)));
                sup.put("needfood",Integer.toString(rs.getInt(7)));
                sup.put("neednecess",Integer.toString(rs.getInt(8)));
                sup.put("needequip",Integer.toString(rs.getInt(9)));
                sup.put("detail",rs.getString(10));
                list.add(sup);
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<HashMap>();
    }
    
    //H??m l???y t???t c??? y??u c???u t?? v???n ??ang ch??? theo t???nh/th??nh ph??? ????
    public ArrayList<HashMap> getWaitSupplyByProvince(String idchar,String province)
    {
        try{
            con =OracleConnection.getOracleConnection();
            String sql = "select idsup,name,gender,phone,province,created,needfood,neednecess,needequip,detail\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=3 and \n"
                    + "province= '"+province+"' and\n"
                    + "idchar= "+idchar;
            ArrayList<HashMap> list= new ArrayList<>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> sup= new HashMap<>();
                sup.put("idsup",rs.getString(1));
                sup.put("name",rs.getString(2));
                sup.put("gender",Integer.toString(rs.getInt(3)));
                sup.put("phone",rs.getString(4));
                sup.put("province",rs.getString(5));
                sup.put("created",ChangeValue.DateToString(rs.getDate(6)));
                sup.put("needfood",Integer.toString(rs.getInt(7)));
                sup.put("neednecess",Integer.toString(rs.getInt(8)));
                sup.put("needequip",Integer.toString(rs.getInt(9)));
                sup.put("detail",rs.getString(10));
                list.add(sup);
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    //H??m n??y d??ng ????? l???y t???t c??? t???nh th??nh ph??? m?? c???a ng?????i mu???n t?? v???n
    public ArrayList<String> getProvinceFromWaitSup(String idchar)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select distinct province\n"
                    + "from supply s\n"
                    + "join person p\n"
                    + "on s.idper=p.idper\n"
                    + "where s.status=3 and \n"
                    + "idchar= "+idchar;
            ArrayList<String> list= new ArrayList<>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            rs.close();
            stat.close();
            con.close();
            return list;
        }catch (SQLException ex) {
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<String>();
    }
    
    //H??m g???i th??? t???c ch???p nh???n y??u c???u ti???p t???
    public int FinishSupply(int idsup)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String CallProc = "{call PROC_FINISH_SUPPLY(?)}";
            CallableStatement callSt=con.prepareCall(CallProc);;
            callSt.setInt(1, idsup);
            callSt.execute();
            callSt.close();
            con.close();
            return 1;
        }catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }catch (SQLException sqle) {
            if (sqle.getErrorCode() == 20232)
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u n??y kh??ng h???p l??? ????? nh???n, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            else if (sqle.getErrorCode() == 20232)
                JOptionPane.showMessageDialog(null, "Y??u c???u n??y kh??ng c??n t???n t???i, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    
    //H??m g???i th??? t???c h???y y??u c???u ti???p t???
    public int CancelSupply(String idchar,String idsup)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String CallProc = "{call PROC_CANCEL_SUPPLY(?,?)}";
            CallableStatement callSt=con.prepareCall(CallProc);;
            callSt.setString(1,idchar);
            callSt.setString(2,idsup);
            callSt.execute();
            callSt.close();
            con.close();
            return 1;
        }catch (SQLException sqle) {
            if (sqle.getErrorCode() == 20222)
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u n??y kh??ng h???p l??? ????? nh???n, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            else if (sqle.getErrorCode() == 20212)
                JOptionPane.showMessageDialog(null, "Y??u c???u n??y kh??ng c??n t???n t???i, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}

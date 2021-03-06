/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Process;

import ConnectDB.OracleConnection;
import Model.AdvisoryTableModel;
import View.ChangeValue;
import View.DoctorScreen;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;
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
 * @author Luong Nguyen Thanh Nhan
 */
public class AdvisoryController {
    private Connection con;
    
    //Ham lay tat ca yeu tu van
    public  ArrayList<HashMap> getAdvisory()
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select idad, name,gender,phone,province,created,yearbirth,height,weight,pastmedicalhistory,detail\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=1 ";
            ArrayList<HashMap> list= new ArrayList<HashMap>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> ad= new HashMap<String,String>();
                ad.put("idad",rs.getString(1));
                ad.put("name",rs.getString(2));
                ad.put("gender",Integer.toString(rs.getInt(3)));
                ad.put("phone",rs.getString(4));
                ad.put("province",rs.getString(5));
                ad.put("created",ChangeValue.DateToString(rs.getDate(6)));
                ad.put("yearbirth",Integer.toString(rs.getInt(7)));
                ad.put("height",Integer.toString(rs.getInt(8)));
                ad.put("weight",Integer.toString(rs.getInt(9)));
                ad.put("pastmedicalhistory",rs.getString(10));
                ad.put("detail",rs.getString(11));
                list.add(ad);
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
    public  void getAdvisoryTransaction(JTable AdvisoryTable,DefaultTableModel AdvisoryTableModel,ArrayList<HashMap> AdvisoryList)
    {
        try {
            Connection con = OracleConnection.getOracleConnection();
            int i = -1;
            i = Connection.TRANSACTION_READ_COMMITTED;
//            i = Connection.TRANSACTION_SERIALIZABLE;
            con.setAutoCommit(false);
            con.setTransactionIsolation(i);
            AdvisoryTable.getSelectionModel().clearSelection();
            AdvisoryTableModel=getAdvisoryModelTransaction(con, AdvisoryList);
            AdvisoryTable.setModel(AdvisoryTableModel);
            setAdvisoryTableSize(AdvisoryTable);
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
                        Thread.sleep(30000);
                        //Bat dau thuc hien lan 2
                        System.out.println("2");
                        AdvisoryTable.getSelectionModel().clearSelection();
                        AdvisoryTable.setModel(getAdvisoryModelTransaction(con, AdvisoryList));
                        setAdvisoryTableSize(AdvisoryTable);
                        con.commit();
                    } catch (SQLException ex) {
                        Logger.getLogger(DoctorScreen.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DoctorScreen.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(DoctorScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                                    }
//                                });
                    return null;
                }
            }.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DoctorScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DoctorScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DoctorScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //H??m n??y d??ng ????? set d??? li???u cho model b???ng trong khi th???c hi???n transaction
    public static DefaultTableModel getAdvisoryModelTransaction (Connection con,ArrayList<HashMap> list) 
    throws SQLException,UnsupportedOperationException,Exception
    {
            String sql = "select idad, name,gender,phone,province,created,yearbirth,height,weight,pastmedicalhistory,detail\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=1 ";
            list.clear();
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> ad= new HashMap<String,String>();
                ad.put("idad",rs.getString(1));
                ad.put("name",rs.getString(2));
                ad.put("gender",Integer.toString(rs.getInt(3)));
                ad.put("phone",rs.getString(4));
                ad.put("province",rs.getString(5));
                ad.put("created",ChangeValue.DateToString(rs.getDate(6)));
                ad.put("yearbirth",Integer.toString(rs.getInt(7)));
                ad.put("height",Integer.toString(rs.getInt(8)));
                ad.put("weight",Integer.toString(rs.getInt(9)));
                ad.put("pastmedicalhistory",rs.getString(10));
                ad.put("detail",rs.getString(11));
                list.add(ad);
            }
            DefaultTableModel AdvisoryTableModel= new AdvisoryTableModel().setAdvisoryTable(list);
            return AdvisoryTableModel;
    }
    
    //H??m l???y t???t c??? y??u c???u t?? v???n theo t???nh/th??nh ph??? ????
    public ArrayList<HashMap> getAdvisoryByProvince(String province)
    {
        try{
            con =OracleConnection.getOracleConnection();
            String sql = "select idad, name,gender,phone,province,created,yearbirth,height,weight,pastmedicalhistory,detail\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=1 and \n"
                    + "province= '"+province+"'";
            ArrayList<HashMap> list= new ArrayList<>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> ad= new HashMap<>();
                ad.put("idad",rs.getString(1));
                ad.put("name",rs.getString(2));
                ad.put("gender",Integer.toString(rs.getInt(3)));
                ad.put("phone",rs.getString(4));
                ad.put("province",rs.getString(5));
                ad.put("created",ChangeValue.DateToString(rs.getDate(6)));
                ad.put("yearbirth",Integer.toString(rs.getInt(7)));
                ad.put("height",Integer.toString(rs.getInt(8)));
                ad.put("weight",Integer.toString(rs.getInt(9)));
                ad.put("pastmedicalhistory",rs.getString(10));
                ad.put("detail",rs.getString(11));
                list.add(ad);
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
    
    //H??m n??y d??ng ????? l???y t???t c??? t???nh th??nh ph??? m?? c?? ng?????i mu???n t?? v???n
    public ArrayList<String> getProvinceFromAd()
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select distinct province\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=1 \n";
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
    public int AcceptAdvisory(String iddoc,String idad)
    {
        try{
            con = OracleConnection.getOracleConnection();
            con.setAutoCommit(false);
            String CallProc = "{call PROC_ACCEPT_ADVISORY(?,?)}";
            CallableStatement callSt=con.prepareCall(CallProc);;
            callSt.setString(1,iddoc);
            callSt.setString(2,idad);
            callSt.execute();
            callSt.close();
            con.commit();
            con.close();
            return 1;
        }catch (SQLException sqle) {
            if (sqle.getErrorCode() == 20241)
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u n??y kh??ng h???p l??? ????? nh???n, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            else if (sqle.getErrorCode() == 20242)
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
    
    //DEMO DEADLOCK
    public int AcceptDLAdvisory(String iddoc,String idad1,String idad2)
    {
        try{
            con = OracleConnection.getOracleConnection();
            con.setAutoCommit(false);
            String CallProc1 = "{call PROC_ACCEPT_ADVISORY(?,?)}";
            CallableStatement callSt1=con.prepareCall(CallProc1);;
            callSt1.setString(1,iddoc);
            callSt1.setString(2,idad1);
            callSt1.execute();
            System.err.println("2");
            Thread.sleep(5000);
            
            String CallProc2 = "{call PROC_ACCEPT_ADVISORY(?,?)}";
            CallableStatement callSt2=con.prepareCall(CallProc2);;
            callSt2.setString(1,iddoc);
            callSt2.setString(2,idad2);
            //try{
                callSt2.execute();
            //}
//            catch (SQLException sqle) {
//                if (sqle.getErrorCode() == 60) {
//                    JOptionPane.showMessageDialog(null, "???? c?? Deadlock x???y ra!",
//                        "L???i!", JOptionPane.ERROR_MESSAGE);
//                    con.rollback();
//                    sqle.printStackTrace();
//                }
//            }
            callSt1.close();
            callSt2.close();
            con.commit();
            con.close();
            return 1;
        }catch (SQLException sqle) {
            if (sqle.getErrorCode() == 20241)
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u n??y kh??ng h???p l??? ????? nh???n, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            if (sqle.getErrorCode() == 20242)
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
    
    //Ph???n n??y l?? ch???nh s???a c???t v?? v?? header cho b???ng v?? kh??ng th??? l???y
    // h??m t??? b??n m??n h??nh b??c s?? qua ???????c
    private void setAdvisoryTableSize(JTable AdvisoryTable)
    {
        //column
        AdvisoryTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        AdvisoryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        AdvisoryTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        AdvisoryTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        AdvisoryTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        AdvisoryTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        //header
        TableCellRenderer rendererFromHeader = AdvisoryTable.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JTableHeader header=AdvisoryTable.getTableHeader();
        Font headerFont = new Font("Segoe", Font.PLAIN, 14);
        header.setFont(headerFont);
    }
    
    //=======================================================================
    //H??m cho m??n h??nh t?? v???n b???nh nh??n
    //H??m l???y t???t c??? y??u c???u t?? v???n ???? nh???n cua 1 b??c s??
    public  ArrayList<HashMap> getWaitAdvisory(String iddoc)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select idad, name,gender,phone,province,created,yearbirth,height,weight,pastmedicalhistory,detail\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=2 and \n"
                    + "iddoc= "+iddoc;
            ArrayList<HashMap> list= new ArrayList<HashMap>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> ad= new HashMap<String,String>();
                ad.put("idad",rs.getString(1));
                ad.put("name",rs.getString(2));
                ad.put("gender",Integer.toString(rs.getInt(3)));
                ad.put("phone",rs.getString(4));
                ad.put("province",rs.getString(5));
                ad.put("created",ChangeValue.DateToString(rs.getDate(6)));
                ad.put("yearbirth",Integer.toString(rs.getInt(7)));
                ad.put("height",Integer.toString(rs.getInt(8)));
                ad.put("weight",Integer.toString(rs.getInt(9)));
                ad.put("pastmedicalhistory",rs.getString(10));
                ad.put("detail",rs.getString(11));
                list.add(ad);
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
    public ArrayList<HashMap> getWaitAdvisoryByProvince(String iddoc,String province)
    {
        try{
            con =OracleConnection.getOracleConnection();
            String sql = "select idad, name,gender,phone,province,created,yearbirth,height,weight,pastmedicalhistory,detail\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=2 and \n"
                    + "province= '"+province+"' and\n"
                    + "iddoc= "+iddoc;
            ArrayList<HashMap> list= new ArrayList<>(); 
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                HashMap<String,String> ad= new HashMap<>();
                ad.put("idad",rs.getString(1));
                ad.put("name",rs.getString(2));
                ad.put("gender",Integer.toString(rs.getInt(3)));
                ad.put("phone",rs.getString(4));
                ad.put("province",rs.getString(5));
                ad.put("created",ChangeValue.DateToString(rs.getDate(6)));
                ad.put("yearbirth",Integer.toString(rs.getInt(7)));
                ad.put("height",Integer.toString(rs.getInt(8)));
                ad.put("weight",Integer.toString(rs.getInt(9)));
                ad.put("pastmedicalhistory",rs.getString(10));
                ad.put("detail",rs.getString(11));
                list.add(ad);
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
    public ArrayList<String> getProvinceFromWaitAd(String iddoc)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String sql = "select distinct province\n"
                    + "from advisory a\n"
                    + "join person p\n"
                    + "on a.idper=p.idper\n"
                    + "where a.status=2 and \n"
                    + "iddoc= "+iddoc;
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
    public int FinishAdvisory(int idad)
    {
        try{
            con = OracleConnection.getOracleConnection();
            String CallProc = "{call PROC_FINISH_ADVISORY(?)}";
            CallableStatement callSt=con.prepareCall(CallProc);;
            callSt.setInt(1, idad);
            callSt.execute();
            callSt.close();
            con.close();
            return 1;
        }catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }catch (SQLException sqle) {
            if (sqle.getErrorCode() == 20241)
                JOptionPane.showMessageDialog(null, "Tr???ng th??i y??u c???u n??y kh??ng h???p l??? ????? nh???n, vui l??ng t???i l???i!",
                        "L???i!", JOptionPane.ERROR_MESSAGE);
            else if (sqle.getErrorCode() == 20242)
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
}

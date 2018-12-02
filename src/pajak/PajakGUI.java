package pajak;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.awt.*;
import java.text.*;
import java.util.*;

public class PajakGUI extends javax.swing.JFrame {

    Connection con;
    Statement stat;
    ResultSet rs;
    
    DefaultTableModel tableModel;
    NumberFormat nf = NumberFormat.getInstance();
    Object[] object = new Object[7];
    String Nama, Status, Id;
    int Jmlanak = 0;
    double gajiPokok = 0;
    double tunjanganFungsional = 0;
    double gajipokok = 0;
    double tunsuamiistri = 0;
    double tunanak = 0;
    double tunfungsional = 0;
    double tunberas;
    double pengkotor = 0;
    double biayajabatan = 0;
    double iuranpensiun = 0;
    double pengurangan = 0;
    double pengnettobulan = 0;
    double pengnettotahun = 0;
    double ptkp = 0;
    double pkp = 0;
    double persenpkp = 0;
    double pphtahun = 0;
    double pphbulan = 0;
    
    String namaUser;
    int jmlanakUser = 0;
    String statusUser;
    double gajipokokUser = 0;
    double tunfungsionalUser = 0;
    
    boolean count = false;
    
    public PajakGUI() {
        initComponents();
        connect();
        tableModel = (DefaultTableModel) pphTable.getModel();
        showData();
        
        this.getContentPane().setBackground(new Color(0,204,102));
        jmlanakSpinner.setEnabled(false);
        GajiPokok.setText("");
        TunSuamiIstri.setText("");
        TunAnak.setText("");
        TunFungsional.setText("");
        TunBeras.setText("");
        PengKotor.setText("");
        BiayaJabatan.setText("");
        IuranPensiun.setText("");
        TotalPengurangan.setText("");
        PengNettoBulan.setText("");
        PengNettoTahun.setText("");
        Ptkp.setText("");
        Pkp.setText("");
        PphTahun.setText("");
        PphBulan.setText("");
        
        namaTxt.setEnabled(false);
        statusCombox.setEnabled(false);
        jmlanakSpinner.setEnabled(false);
        gajipokokTxt.setEnabled(false);
        tunfungsionalTxt.setEnabled(false);
    }

    public void connect()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pajak_db", "root", "");
            stat = con.createStatement();
        } catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal");
        }
    }
    
    public void showData()
    {
        try
        {
            rs = stat.executeQuery("SELECT * FROM pph");
            
            while(rs.next())
            {
                object[0] = rs.getString("nama");
                object[1] = rs.getString("gaji_pokok");
                object[2] = rs.getString("peng_kotor_perbulan");
                object[3] = rs.getString("peng_netto_perbulan");
                object[4] = rs.getString("ptkp");
                object[5] = rs.getString("pkp");
                object[6] = rs.getString("pph_perbulan");
                
                tableModel.addRow(object);
            }
        } catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, "Data gagal diambil");
        }
    }
    
    public void masuk(String idName)
    {
        Id = idName;
        getDataProfile();
        getDataPph();
        
        if(count)
        {
            hitungFromDb();
            editPphBtn.setText("Edit");
            gajipokokTxt.setEnabled(false);
            tunfungsionalTxt.setEnabled(false);
        } else
        {
            editPphBtn.setText("Simpan");
            gajipokokTxt.setEnabled(true);
            tunfungsionalTxt.setEnabled(true);
        }
    }
    
    public void getDataProfile()
    { 
        try
        {
            rs = stat.executeQuery("SELECT * FROM user_profile WHERE id = '" + Id + "'");
            
            if(rs.next())
            {
                namaUser = rs.getString("nama");
                jmlanakUser = rs.getInt("jml_anak");
                statusUser = rs.getString("status");
                
                namaTxt.setText(namaUser);
                statusCombox.setSelectedItem(statusUser);
                jmlanakSpinner.setValue(jmlanakUser);
                jmlanakSpinner.setEnabled(false);
            }
        } catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, ex);
        }
    }
    
    public void getDataPph()
    {
        try
        {
            rs = stat.executeQuery("SELECT * FROM pph WHERE id = '" + Id + "'");
            
            if(rs.next())
            {
                count = true;
                gajipokokUser = rs.getDouble("gaji_pokok");
                tunfungsionalUser = rs.getDouble("tunjangan_fungsional");
                int gajip = (int) gajipokokUser;
                int tunfung = (int) tunfungsionalUser;
                
                gajipokokTxt.setText(String.valueOf(gajip));
                tunfungsionalTxt.setText(String.valueOf(tunfung));
            } else
            {
                count = false;
                JOptionPane.showMessageDialog(this, "Masukkan data penghasilan");
            }
        } catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, ex);
        }
    }
    
    public void hitungFromDb()
    {
        tunberas = 290000;
        
        if(statusUser.equals("Kawin"))
        {
            tunsuamiistri = ((double) 10/100) * gajipokokUser;
            tunanak = 2 * ((double) 2/100) * gajipokokUser;

            if(jmlanakUser == 0)
            {
                ptkp = 26325000;
            } else if(jmlanakUser == 1)
            {
                ptkp = 28350000;
            } else if(jmlanakUser == 2)
            {
                ptkp = 30375000;
            } else if(jmlanakUser == 3)
            {
                ptkp = 32400000;
            }
        } else
        {
            tunsuamiistri = 0;
            tunanak = 0;
            ptkp = 24300000;
        }
        
        pengkotor = gajipokokUser + tunsuamiistri + tunanak + tunfungsionalUser + tunberas;

        biayajabatan = ((double) 5/100) * pengkotor;
        iuranpensiun = ((double) 4.75/100) * (gajipokokUser + tunsuamiistri + tunanak);

        pengurangan = biayajabatan + iuranpensiun;
        pengnettobulan = pengkotor - (biayajabatan + iuranpensiun);
        pengnettotahun = pengnettobulan * 12;
        pkp = pengnettotahun - ptkp;

        if(pkp >= 0 && pkp <= 50000000)
        {
            persenpkp = (double) 5/100;
        } else if(pkp > 50000000 && pkp <= 250000000)
        {
            persenpkp = (double) 15/100;
        } else if(pkp > 250000000 && pkp <= 500000000)
        {
            persenpkp = (double) 25/100;
        } else if(pkp > 500000000)
        {
            persenpkp = (double) 30/100;
        }

        pphtahun = persenpkp * pkp;
        pphbulan = pphtahun / 12;

        GajiPokok.setText("Rp " + nf.format(gajipokokUser));
        TunSuamiIstri.setText("Rp " + nf.format(tunsuamiistri));
        TunAnak.setText("Rp " + nf.format(tunanak));
        TunFungsional.setText("Rp " + nf.format(tunfungsionalUser));
        TunBeras.setText("Rp " + nf.format(tunberas));
        PengKotor.setText("Rp " + nf.format(pengkotor));
        BiayaJabatan.setText("Rp " + nf.format(biayajabatan));
        IuranPensiun.setText("Rp " + nf.format(iuranpensiun));
        TotalPengurangan.setText("Rp " + nf.format(pengurangan));
        PengNettoBulan.setText("Rp " + nf.format(pengnettobulan));
        PengNettoTahun.setText("Rp " + nf.format(pengnettotahun));
        Ptkp.setText("Rp " + nf.format(ptkp));
        Pkp.setText("Rp " + nf.format(pkp));
        PphTahun.setText("Rp " + nf.format(pphtahun));
        PphBulan.setText("Rp " + nf.format(pphbulan));
    }
    
    public void hitung()
    {
        tunberas = 290000;
        
        if(!Nama.isEmpty() || Status.equals("-- Pilih --") || gajiPokok != 0)
        {
            if(Status.equals("Kawin"))
            {
                tunsuamiistri = ((double) 10/100) * gajiPokok;
                tunanak = 2 * ((double) 2/100) * gajiPokok;

                if(jmlanakUser == 0)
                {
                    ptkp = 26325000;
                } else if(jmlanakUser == 1)
                {
                    ptkp = 28350000;
                } else if(jmlanakUser == 2)
                {
                    ptkp = 30375000;
                } else if(jmlanakUser == 3)
                {
                    ptkp = 32400000;
                }
            } else
            {
                tunsuamiistri = 0;
                tunanak = 0;
                ptkp = 24300000;
            }

            pengkotor = gajiPokok + tunsuamiistri + tunanak + tunjanganFungsional + tunberas;

            biayajabatan = ((double) 5/100) * pengkotor;
            iuranpensiun = ((double) 4.75/100) * (gajiPokok + tunsuamiistri + tunanak);

            pengurangan = biayajabatan + iuranpensiun;
            pengnettobulan = pengkotor - (biayajabatan + iuranpensiun);
            pengnettotahun = pengnettobulan * 12;
            pkp = pengnettotahun - ptkp;

            if(pkp >= 0 && pkp <= 50000000)
            {
                persenpkp = (double) 5/100;
            } else if(pkp > 50000000 && pkp <= 250000000)
            {
                persenpkp = (double) 15/100;
            } else if(pkp > 250000000 && pkp <= 500000000)
            {
                persenpkp = (double) 25/100;
            } else if(pkp > 500000000)
            {
                persenpkp = (double) 30/100;
            }

            pphtahun = persenpkp * pkp;
            pphbulan = pphtahun / 12;

            GajiPokok.setText("Rp " + nf.format(gajiPokok));
            TunSuamiIstri.setText("Rp " + nf.format(tunsuamiistri));
            TunAnak.setText("Rp " + nf.format(tunanak));
            TunFungsional.setText("Rp " + nf.format(tunjanganFungsional));
            TunBeras.setText("Rp " + nf.format(tunberas));
            PengKotor.setText("Rp " + nf.format(pengkotor));
            BiayaJabatan.setText("Rp " + nf.format(biayajabatan));
            IuranPensiun.setText("Rp " + nf.format(iuranpensiun));
            TotalPengurangan.setText("Rp " + nf.format(pengurangan));
            PengNettoBulan.setText("Rp " + nf.format(pengnettobulan));
            PengNettoTahun.setText("Rp " + nf.format(pengnettotahun));
            Ptkp.setText("Rp " + nf.format(ptkp));
            Pkp.setText("Rp " + nf.format(pkp));
            PphTahun.setText("Rp " + nf.format(pphtahun));
            PphBulan.setText("Rp " + nf.format(pphbulan));
            
            gajipokokTxt.setEnabled(false);
            tunfungsionalTxt.setEnabled(false);

            tableModel.setRowCount(0);
            showData();
        } else
        {
            JOptionPane.showMessageDialog(this, "Isi data dengan lengkap!");
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pajakPenghasilan = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        namaTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        statusCombox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jmlanakSpinner = new javax.swing.JSpinner();
        batalProfilBtn = new javax.swing.JButton();
        editProfilBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        gajipokokTxt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tunfungsionalTxt = new javax.swing.JTextField();
        editPphBtn = new javax.swing.JButton();
        batalPphBtn = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        GajiPokok = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        TunSuamiIstri = new javax.swing.JLabel();
        TunAnak = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        TunFungsional = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        TunBeras = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        PengKotor = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        BiayaJabatan = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        IuranPensiun = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        TotalPengurangan = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        PengNettoBulan = new javax.swing.JLabel();
        PengNettoTahun = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        Ptkp = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        PphTahun = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        PphBulan = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        Pkp = new javax.swing.JLabel();
        keluarBtn = new javax.swing.JButton();
        hapusPph = new javax.swing.JButton();
        tabelPph = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pphTable = new javax.swing.JTable();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 13)); // NOI18N

        pajakPenghasilan.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Profil", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans Unicode", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel1.setText("Nama");

        namaTxt.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel2.setText("Status");

        statusCombox.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        statusCombox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Belum Kawin", "Kawin" }));
        statusCombox.setLightWeightPopupEnabled(false);
        statusCombox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusComboxActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel3.setText("Jumlah Anak");

        jmlanakSpinner.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jmlanakSpinner.setEnabled(false);

        batalProfilBtn.setBackground(new java.awt.Color(255, 51, 51));
        batalProfilBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        batalProfilBtn.setForeground(new java.awt.Color(255, 255, 255));
        batalProfilBtn.setText("Batal");
        batalProfilBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalProfilBtnActionPerformed(evt);
            }
        });

        editProfilBtn.setBackground(new java.awt.Color(51, 153, 255));
        editProfilBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        editProfilBtn.setForeground(new java.awt.Color(255, 255, 255));
        editProfilBtn.setText("Edit");
        editProfilBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editProfilBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(statusCombox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 200, Short.MAX_VALUE)
                            .addComponent(jmlanakSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(namaTxt)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(batalProfilBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(editProfilBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(namaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusCombox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jmlanakSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editProfilBtn)
                    .addComponent(batalProfilBtn))
                .addContainerGap())
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Penghasilan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans Unicode", 1, 12))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel4.setText("Gaji Pokok");

        gajipokokTxt.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel5.setText("Tunjangan Fungsional");

        tunfungsionalTxt.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N

        editPphBtn.setBackground(new java.awt.Color(51, 153, 255));
        editPphBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        editPphBtn.setForeground(new java.awt.Color(255, 255, 255));
        editPphBtn.setText("Edit");
        editPphBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPphBtnActionPerformed(evt);
            }
        });

        batalPphBtn.setBackground(new java.awt.Color(255, 51, 51));
        batalPphBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        batalPphBtn.setForeground(new java.awt.Color(255, 255, 255));
        batalPphBtn.setText("Batal");
        batalPphBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalPphBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tunfungsionalTxt)
                            .addComponent(gajipokokTxt)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(batalPphBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(editPphBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(gajipokokTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tunfungsionalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editPphBtn)
                    .addComponent(batalPphBtn))
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pajak Penghasilan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Sans Unicode", 1, 12))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel7.setText("Gaji Pokok");

        GajiPokok.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        GajiPokok.setText("test");

        jLabel9.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel9.setText("Tunjangan Suami/Istri");

        TunSuamiIstri.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        TunSuamiIstri.setText("test");

        TunAnak.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        TunAnak.setText("test");

        jLabel12.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel12.setText("Tunjangan Anak");

        jLabel13.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel13.setText("Tunjangan Fungsional");

        TunFungsional.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        TunFungsional.setText("test");

        jLabel15.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel15.setText("Tunjangan Beras");

        TunBeras.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        TunBeras.setText("test");

        jLabel17.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel17.setText("Penghasilan Kotor Perbulan");

        PengKotor.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        PengKotor.setText("test");

        jLabel19.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel19.setText("Biaya Jabatan");

        BiayaJabatan.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        BiayaJabatan.setText("test");

        jLabel21.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        jLabel21.setText("Iuran Pensiun");

        IuranPensiun.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        IuranPensiun.setText("test");

        jLabel23.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel23.setText("Total Pengurangan");

        TotalPengurangan.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        TotalPengurangan.setText("test");

        jLabel25.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel25.setText("Penghasilan Netto Perbulan");

        PengNettoBulan.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        PengNettoBulan.setText("test");

        PengNettoTahun.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        PengNettoTahun.setText("test");

        jLabel28.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel28.setText("Penghasilan Netto Pertahun");

        jLabel29.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel29.setText("PTKP");

        Ptkp.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        Ptkp.setText("test");

        jLabel31.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel31.setText("PPh Setahun");

        PphTahun.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        PphTahun.setText("test");

        jLabel33.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel33.setText("PPh Sebulan");

        PphBulan.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        PphBulan.setText("test");

        jLabel30.setFont(new java.awt.Font("Lucida Sans Unicode", 1, 12)); // NOI18N
        jLabel30.setText("PKP");

        Pkp.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        Pkp.setText("test");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(GajiPokok, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TunSuamiIstri, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TunAnak, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TunFungsional, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TunBeras, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PengKotor, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(BiayaJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(IuranPensiun, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(TotalPengurangan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(PengNettoBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(PengNettoTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(Ptkp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(PphTahun, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(PphBulan, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(36, 36, 36)
                        .addComponent(Pkp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(GajiPokok))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(TunSuamiIstri))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(TunAnak))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(TunFungsional))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(TunBeras))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(PengKotor))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(BiayaJabatan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(IuranPensiun))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(TotalPengurangan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(PengNettoBulan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(PengNettoTahun))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(Ptkp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(Pkp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(PphTahun))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(PphBulan))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        keluarBtn.setBackground(new java.awt.Color(255, 51, 51));
        keluarBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        keluarBtn.setForeground(new java.awt.Color(255, 255, 255));
        keluarBtn.setText("KELUAR");
        keluarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keluarBtnActionPerformed(evt);
            }
        });

        hapusPph.setBackground(new java.awt.Color(255, 51, 51));
        hapusPph.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        hapusPph.setForeground(new java.awt.Color(255, 255, 255));
        hapusPph.setText("Hapus Data Pph");
        hapusPph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusPphActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pajakPenghasilanLayout = new javax.swing.GroupLayout(pajakPenghasilan);
        pajakPenghasilan.setLayout(pajakPenghasilanLayout);
        pajakPenghasilanLayout.setHorizontalGroup(
            pajakPenghasilanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pajakPenghasilanLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(pajakPenghasilanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pajakPenghasilanLayout.createSequentialGroup()
                        .addGroup(pajakPenghasilanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(73, 73, 73)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(hapusPph))
                .addGap(18, 18, 18)
                .addComponent(keluarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        pajakPenghasilanLayout.setVerticalGroup(
            pajakPenghasilanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pajakPenghasilanLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(pajakPenghasilanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pajakPenghasilanLayout.createSequentialGroup()
                        .addGroup(pajakPenghasilanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pajakPenghasilanLayout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(keluarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hapusPph)
                .addGap(55, 55, 55))
        );

        jTabbedPane1.addTab("Pajak Penghasilan", pajakPenghasilan);
        pajakPenghasilan.getAccessibleContext().setAccessibleName("Pajak Penghasilan");

        tabelPph.setBackground(new java.awt.Color(255, 255, 255));

        pphTable.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 13)); // NOI18N
        pphTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "Gaji Pokok", "Peng. Kotor", "Peng. Netto Perbulan", "PTKP", "PKP", "PPh Perbulan"
            }
        ));
        jScrollPane1.setViewportView(pphTable);

        javax.swing.GroupLayout tabelPphLayout = new javax.swing.GroupLayout(tabelPph);
        tabelPph.setLayout(tabelPphLayout);
        tabelPphLayout.setHorizontalGroup(
            tabelPphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelPphLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
                .addContainerGap())
        );
        tabelPphLayout.setVerticalGroup(
            tabelPphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabelPphLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tabel Pajak Penghasilan", tabelPph);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 994, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 688, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editPphBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPphBtnActionPerformed
        if(editPphBtn.getText().equals("Edit"))
        {
            editPphBtn.setText("Simpan");
            gajipokokTxt.setEnabled(true);
            tunfungsionalTxt.setEnabled(true);
        } else if(editPphBtn.getText().equals("Simpan"))
        {
            try
            {
                Nama = namaTxt.getText();
                Status = statusCombox.getSelectedItem().toString();
                Jmlanak = (int) jmlanakSpinner.getValue();
                gajiPokok =  Integer.parseInt(gajipokokTxt.getText());
                tunjanganFungsional = Integer.parseInt(tunfungsionalTxt.getText());
                tunberas = 290000;
                
                if(!count)
                {
                    hitung();
                    stat.executeUpdate("INSERT INTO pph values('" + Id + "', '" + Nama + "', '" + Status + "', '" + Jmlanak + "', '" + gajiPokok + "', '" + tunjanganFungsional + "', '" + pengkotor + "', '" + pengurangan + "', "
                        + "'" + pengnettobulan + "', '" + pengnettotahun + "', '" + ptkp + "', '" + pkp + "', '" + pphtahun + "', '" + pphbulan + "')");
                    JOptionPane.showMessageDialog(this, "Data Pph Berhasil dimasukkan");
                    getDataPph();
                    tableModel.setRowCount(0);
                    showData();
                } else
                {
                    hitung();
                    stat.executeUpdate("UPDATE pph SET nama = '" + Nama + "', status = '" + Status + "', jml_anak = '" + Jmlanak + "', gaji_pokok = '" + gajiPokok + "', tunjangan_fungsional = '" + tunjanganFungsional + "', peng_kotor_perbulan = '" + pengkotor + "', total_pengurangan = '" + pengurangan + "', "
                        + " peng_netto_perbulan = '" + pengnettobulan + "', peng_netto_pertahun = '" + pengnettotahun + "', ptkp = '" + ptkp + "', pkp = '" + pkp + "', pph_pertahun = '" + pphtahun + "', pph_perbulan = '" + pphbulan + "' WHERE id = '" + Id + "'");
                    JOptionPane.showMessageDialog(this, "Data Pph Berhasil disunting");
                    tableModel.setRowCount(0);
                    showData();
                }
                
                editPphBtn.setText("Edit");
                editProfilBtn.setText("Edit");
                namaTxt.setEnabled(false);
                statusCombox.setEnabled(false);
                jmlanakSpinner.setEnabled(false);
                gajipokokTxt.setEnabled(false);
                tunfungsionalTxt.setEnabled(false);
            } catch(Exception ex)
            {
                JOptionPane.showMessageDialog(this, ex);
            }
        }
    }//GEN-LAST:event_editPphBtnActionPerformed

    private void batalPphBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalPphBtnActionPerformed
        getDataPph();
        gajipokokTxt.setEnabled(false);
        tunfungsionalTxt.setEnabled(false);
    }//GEN-LAST:event_batalPphBtnActionPerformed

    private void statusComboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusComboxActionPerformed
        if(statusCombox.getSelectedItem().toString().equals("Kawin"))
        {
            jmlanakSpinner.setEnabled(true);
        } else
        {
            jmlanakSpinner.setValue(0);
            jmlanakSpinner.setEnabled(false);
        }
    }//GEN-LAST:event_statusComboxActionPerformed

    private void batalProfilBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalProfilBtnActionPerformed
        getDataProfile();
        editProfilBtn.setText("Edit");
        namaTxt.setEnabled(false);
        statusCombox.setEnabled(false);
        jmlanakSpinner.setEnabled(false);
    }//GEN-LAST:event_batalProfilBtnActionPerformed

    private void editProfilBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editProfilBtnActionPerformed
        if(editProfilBtn.getText().equals("Edit"))
        {
            editProfilBtn.setText("Simpan");
            namaTxt.setEnabled(true);
            statusCombox.setEnabled(true);
            jmlanakSpinner.setEnabled(true);
        } else if(editProfilBtn.getText().equals("Simpan"))
        {
            try
            {
                Nama = namaTxt.getText();
                Status = statusCombox.getSelectedItem().toString();
                Jmlanak = (int) jmlanakSpinner.getValue();
                gajiPokok =  Integer.parseInt(gajipokokTxt.getText());
                tunjanganFungsional = Integer.parseInt(tunfungsionalTxt.getText());
                
                if(!count)
                {
                    hitung();
                    stat.executeUpdate("INSERT INTO pph values('" + Id + "', '" + Nama + "', '" + Status + "', '" + Jmlanak + "', '" + gajiPokok + "', '" + tunjanganFungsional + "', '" + pengkotor + "', '" + pengurangan + "', "
                        + "'" + pengnettobulan + "', '" + pengnettotahun + "', '" + ptkp + "', '" + pkp + "', '" + pphtahun + "', '" + pphbulan + "')");
                    getDataPph();
                    tableModel.setRowCount(0);
                    showData();
                } else
                {
                    stat.executeUpdate("UPDATE user_profile SET nama = '" + Nama + "', status = '" + Status + "', jml_anak = '" + Jmlanak + "' WHERE id = '" + Id + "'");
                    hitung();
                    stat.executeUpdate("UPDATE pph SET nama = '" + Nama + "', status = '" + Status + "', jml_anak = '" + Jmlanak + "', gaji_pokok = '" + gajiPokok + "', tunjangan_fungsional = '" + tunjanganFungsional + "', peng_kotor_perbulan = '" + pengkotor + "', total_pengurangan = '" + pengurangan + "', "
                        + " peng_netto_perbulan = '" + pengnettobulan + "', peng_netto_pertahun = '" + pengnettotahun + "', ptkp = '" + ptkp + "', pkp = '" + pkp + "', pph_pertahun = '" + pphtahun + "', pph_perbulan = '" + pphbulan + "' WHERE id = '" + Id + "'");
                    JOptionPane.showMessageDialog(this, "Profil berhasil disunting");
                    tableModel.setRowCount(0);
                    showData();
                }
                
                editProfilBtn.setText("Edit");
                editPphBtn.setText("Edit");
                namaTxt.setEnabled(false);
                statusCombox.setEnabled(false);
                jmlanakSpinner.setEnabled(false);
                gajipokokTxt.setEnabled(false);
                tunfungsionalTxt.setEnabled(false);
            } catch(Exception ex)
            {
                JOptionPane.showMessageDialog(this, ex);
            }
        }
        
    }//GEN-LAST:event_editProfilBtnActionPerformed

    private void keluarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keluarBtnActionPerformed
        this.dispose();
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
    }//GEN-LAST:event_keluarBtnActionPerformed

    private void hapusPphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusPphActionPerformed
        try
        {
            stat.executeUpdate("DELETE FROM pph WHERE id = '" + Id + "'");
            JOptionPane.showMessageDialog(this, "Data Pph Telah dihapus");
            gajipokokTxt.setText("");
            tunfungsionalTxt.setText("");
            gajipokokTxt.setEnabled(true);
            tunfungsionalTxt.setEnabled(true);
            
            GajiPokok.setText("");
            TunSuamiIstri.setText("");
            TunAnak.setText("");
            TunFungsional.setText("");
            TunBeras.setText("");
            PengKotor.setText("");
            BiayaJabatan.setText("");
            IuranPensiun.setText("");
            TotalPengurangan.setText("");
            PengNettoBulan.setText("");
            PengNettoTahun.setText("");
            Ptkp.setText("");
            Pkp.setText("");
            PphTahun.setText("");
            PphBulan.setText("");
            getDataPph();
            tableModel.setRowCount(0);
            showData();
        } catch(Exception ex)
        {
            JOptionPane.showMessageDialog(this, ex);
        }
    }//GEN-LAST:event_hapusPphActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PajakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PajakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PajakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PajakGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PajakGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BiayaJabatan;
    private javax.swing.JLabel GajiPokok;
    private javax.swing.JLabel IuranPensiun;
    private javax.swing.JLabel PengKotor;
    private javax.swing.JLabel PengNettoBulan;
    private javax.swing.JLabel PengNettoTahun;
    private javax.swing.JLabel Pkp;
    private javax.swing.JLabel PphBulan;
    private javax.swing.JLabel PphTahun;
    private javax.swing.JLabel Ptkp;
    private javax.swing.JLabel TotalPengurangan;
    private javax.swing.JLabel TunAnak;
    private javax.swing.JLabel TunBeras;
    private javax.swing.JLabel TunFungsional;
    private javax.swing.JLabel TunSuamiIstri;
    private javax.swing.JButton batalPphBtn;
    private javax.swing.JButton batalProfilBtn;
    private javax.swing.JButton editPphBtn;
    private javax.swing.JButton editProfilBtn;
    private javax.swing.JTextField gajipokokTxt;
    private javax.swing.JButton hapusPph;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JSpinner jmlanakSpinner;
    private javax.swing.JButton keluarBtn;
    private javax.swing.JTextField namaTxt;
    private javax.swing.JPanel pajakPenghasilan;
    private javax.swing.JTable pphTable;
    private javax.swing.JComboBox<String> statusCombox;
    private javax.swing.JPanel tabelPph;
    private javax.swing.JTextField tunfungsionalTxt;
    // End of variables declaration//GEN-END:variables
}

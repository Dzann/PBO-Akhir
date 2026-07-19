package view;

import config.DBConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class LaporanForm extends JFrame {
    private JTable tabelLaporan;
    private DefaultTableModel model;
    private JLabel lblTotalOmset;

    public LaporanForm() {
        setTitle("Laporan Penjualan (View & Function Terintegrasi)");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        model = new DefaultTableModel(new String[]{"Faktur ID", "Waktu Transaksi", "Pelanggan", "Nama Barang", "Kuantitas", "Subtotal"}, 0);
        tabelLaporan = new JTable(model);
        add(new JScrollPane(tabelLaporan), BorderLayout.CENTER);

        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalOmset = new JLabel("Mengalkulasi Total Omset...");
        pnlStatus.add(lblTotalOmset);
        add(pnlStatus, BorderLayout.SOUTH);

        bacaDataDariView();
        hitungTotalDariFunction();
    }

    // MEMBACA VIEW DATABASE
    private void bacaDataDariView() {
        model.setRowCount(0);
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM view_laporan_penjualan")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id_transaksi"),
                    rs.getTimestamp("tgl_transaksi"),
                    rs.getString("nama_pelanggan"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah"),
                    rs.getInt("subtotal")
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // MEMANGGIL DATABASE FUNCTION
    private void hitungTotalDariFunction() {
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT func_total_pendapatan() AS omset")) {
            
            if (rs.next()) {
                lblTotalOmset.setText("Total Seluruh Akumulasi Omset Pendapatan (Dari DB Function): Rp. " + rs.getInt("omset"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
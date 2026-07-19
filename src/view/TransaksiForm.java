package view;

import config.DBConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TransaksiForm extends JFrame {
    private JComboBox<String> cbPelanggan, cbBarang;
    private JTextField txtJumlah, txtTotal;
    private JTable tabelKeranjang;
    private DefaultTableModel model;
    private int currentIdTransaksi = -1;
    private int totalBayar = 0;

    public TransaksiForm() {
        setTitle("Form Transaksi Kasir");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlForm.add(new JLabel("Pilih Pelanggan:")); cbPelanggan = new JComboBox<>(); pnlForm.add(cbPelanggan);
        pnlForm.add(new JLabel("Pilih Barang:")); cbBarang = new JComboBox<>(); pnlForm.add(cbBarang);
        pnlForm.add(new JLabel("Jumlah Beli:")); txtJumlah = new JTextField(); pnlForm.add(txtJumlah);

        JButton btnMulai = new JButton("Mulai Transaksi Baru");
        JButton btnTambah = new JButton("Masukkan Keranjang");
        JPanel pnlAction = new JPanel(); pnlAction.add(btnMulai); pnlAction.add(btnTambah);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.CENTER); pnlTop.add(pnlAction, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Nama Barang", "Jumlah Beli", "Subtotal"}, 0);
        tabelKeranjang = new JTable(model);
        add(new JScrollPane(tabelKeranjang), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTotal = new JTextField(12); txtTotal.setEditable(false);
        pnlBottom.add(new JLabel("Total Transaksi: Rp.")); pnlBottom.add(txtTotal);
        add(pnlBottom, BorderLayout.SOUTH);

        isiComboBox();

        // IMPLEMENTASI STORED PROCEDURE (SP)
        btnMulai.addActionListener(e -> {
            try (Connection conn = DBConfig.getConnection();
                 CallableStatement cs = conn.prepareCall("{call sp_tambah_transaksi(?, ?)}")) {
                
                String itemPelanggan = cbPelanggan.getSelectedItem().toString();
                int idPelanggan = Integer.parseInt(itemPelanggan.split(" - ")[0]);
                
                cs.setInt(1, idPelanggan);
                cs.registerOutParameter(2, Types.INTEGER);
                cs.execute();
                
                currentIdTransaksi = cs.getInt(2);
                totalBayar = 0;
                txtTotal.setText("0");
                model.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Transaksi Baru Dimulai! ID Faktur: " + currentIdTransaksi);
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        // IMPLEMENTASI DETAIL TRANSAKSI + DB TRIGGER OTOMATIS CUT STOK
        btnTambah.addActionListener(e -> {
            if (currentIdTransaksi == -1) {
                JOptionPane.showMessageDialog(this, "Harap klik 'Mulai Transaksi Baru' terlebih dahulu!");
                return;
            }
            try (Connection conn = DBConfig.getConnection()) {
                String itemBarang = cbBarang.getSelectedItem().toString();
                int idBarang = Integer.parseInt(itemBarang.split(" - ")[0]);
                String namaBarang = itemBarang.split(" - ")[1];
                int hargaBarang = Integer.parseInt(itemBarang.split(" - ")[2]);
                int qty = Integer.parseInt(txtJumlah.getText());
                int subtotal = hargaBarang * qty;

                // Memasukkan item belanja ke detail. Sistem DB Trigger 'trig_kurangi_stok' langsung aktif.
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO tbl_detail_transaksi(id_transaksi, id_barang, jumlah, subtotal) VALUES(?,?,?,?)")) {
                    ps.setInt(1, currentIdTransaksi); ps.setInt(2, idBarang); ps.setInt(3, qty); ps.setInt(4, subtotal);
                    ps.executeUpdate();
                }

                // Kalkulasi total bayar berjalan
                totalBayar += subtotal;
                try (PreparedStatement ps2 = conn.prepareStatement("UPDATE tbl_transaksi SET total_bayar=? WHERE id_transaksi=?")) {
                    ps2.setInt(1, totalBayar); ps2.setInt(2, currentIdTransaksi);
                    ps2.executeUpdate();
                }

                model.addRow(new Object[]{namaBarang, qty, subtotal});
                txtTotal.setText(String.valueOf(totalBayar));
                txtJumlah.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal menambahkan item: " + ex.getMessage()); }
        });
    }

    private void isiComboBox() {
        try (Connection conn = DBConfig.getConnection(); Statement st = conn.createStatement()) {
            ResultSet rsPel = st.executeQuery("SELECT id_pelanggan, nama_pelanggan FROM tbl_pelanggan");
            while (rsPel.next()) cbPelanggan.addItem(rsPel.getInt(1) + " - " + rsPel.getString(2));
            
            ResultSet rsBrg = st.executeQuery("SELECT id_barang, nama_barang, harga FROM tbl_barang");
            while (rsBrg.next()) cbBarang.addItem(rsBrg.getInt(1) + " - " + rsBrg.getString(2) + " - " + rsBrg.getInt(3));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
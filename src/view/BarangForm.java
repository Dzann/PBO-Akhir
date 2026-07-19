package view;

import config.DBConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BarangForm extends JFrame {
    private JTextField txtNama, txtHarga, txtStok, txtCari;
    private JTable tabelBarang;
    private DefaultTableModel model;
    private int idTerpilih = -1;

    public BarangForm() {
        setTitle("CRUD & Pencarian Data Barang");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlForm.add(new JLabel("Nama Barang:")); txtNama = new JTextField(); pnlForm.add(txtNama);
        pnlForm.add(new JLabel("Harga:")); txtHarga = new JTextField(); pnlForm.add(txtHarga);
        pnlForm.add(new JLabel("Stok:")); txtStok = new JTextField(); pnlForm.add(txtStok);

        JButton btnAdd = new JButton("Simpan");
        JButton btnEdit = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JPanel pnlAction = new JPanel(); pnlAction.add(btnAdd); pnlAction.add(btnEdit); pnlAction.add(btnDelete);

        JPanel pnlCari = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCari = new JTextField(15); JButton btnCari = new JButton("Cari Nama");
        pnlCari.add(txtCari); pnlCari.add(btnCari);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.NORTH); pnlTop.add(pnlAction, BorderLayout.CENTER); pnlTop.add(pnlCari, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID Barang", "Nama Barang", "Harga", "Stok"}, 0);
        tabelBarang = new JTable(model);
        add(new JScrollPane(tabelBarang), BorderLayout.CENTER);

        tabelBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelBarang.getSelectedRow();
                if(row != -1){
                    idTerpilih = Integer.parseInt(model.getValueAt(row, 0).toString());
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtHarga.setText(model.getValueAt(row, 2).toString());
                    txtStok.setText(model.getValueAt(row, 3).toString());
                }
            }
        });

        btnAdd.addActionListener(e -> execute("INSERT INTO tbl_barang(nama_barang,harga,stok) VALUES(?,?,?)", txtNama.getText(), txtHarga.getText(), txtStok.getText()));
        btnEdit.addActionListener(e -> {
            if(idTerpilih == -1) return;
            execute("UPDATE tbl_barang SET nama_barang=?, harga=?, stok=? WHERE id_barang=?", txtNama.getText(), txtHarga.getText(), txtStok.getText(), idTerpilih);
        });
        btnDelete.addActionListener(e -> {
            if(idTerpilih == -1) return;
            execute("DELETE FROM tbl_barang WHERE id_barang=?", idTerpilih);
        });
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        loadData("");
    }

    private void loadData(String search) {
        model.setRowCount(0);
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM tbl_barang WHERE nama_barang LIKE ?")) {
            ps.setString(1, "%" + search + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4)});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void execute(String query, Object... params) {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Aksi Berhasil!");
            txtNama.setText(""); txtHarga.setText(""); txtStok.setText(""); idTerpilih = -1;
            loadData("");
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }
}
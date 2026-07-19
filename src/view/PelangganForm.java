package view;

import config.DBConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PelangganForm extends JFrame {
    private JTextField txtNama, txtTelp, txtAlamat, txtCari;
    private JTable tabelPelanggan;
    private DefaultTableModel model;
    private int idTerpilih = -1;

    public PelangganForm() {
        setTitle("CRUD & Pencarian Data Pelanggan");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlForm.add(new JLabel("Nama Pelanggan:")); txtNama = new JTextField(); pnlForm.add(txtNama);
        pnlForm.add(new JLabel("Telepon:")); txtTelp = new JTextField(); pnlForm.add(txtTelp);
        pnlForm.add(new JLabel("Alamat:")); txtAlamat = new JTextField(); pnlForm.add(txtAlamat);

        JButton btnAdd = new JButton("Simpan");
        JButton btnEdit = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JPanel pnlAction = new JPanel(); pnlAction.add(btnAdd); pnlAction.add(btnEdit); pnlAction.add(btnDelete);

        JPanel pnlCari = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtCari = new JTextField(15); JButton btnCari = new JButton("Cari Pelanggan");
        pnlCari.add(txtCari); pnlCari.add(btnCari);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.NORTH); pnlTop.add(pnlAction, BorderLayout.CENTER); pnlTop.add(pnlCari, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Nama", "Telepon", "Alamat"}, 0);
        tabelPelanggan = new JTable(model);
        add(new JScrollPane(tabelPelanggan), BorderLayout.CENTER);

        tabelPelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelPelanggan.getSelectedRow();
                if(row != -1){
                    idTerpilih = Integer.parseInt(model.getValueAt(row, 0).toString());
                    txtNama.setText(model.getValueAt(row, 1).toString());
                    txtTelp.setText(model.getValueAt(row, 2).toString());
                    txtAlamat.setText(model.getValueAt(row, 3).toString());
                }
            }
        });

        btnAdd.addActionListener(e -> execute("INSERT INTO tbl_pelanggan(nama_pelanggan,telepon,alamat) VALUES(?,?,?)", txtNama.getText(), txtTelp.getText(), txtAlamat.getText()));
        btnEdit.addActionListener(e -> {
            if(idTerpilih == -1) return;
            execute("UPDATE tbl_pelanggan SET nama_pelanggan=?, telepon=?, alamat=? WHERE id_pelanggan=?", txtNama.getText(), txtTelp.getText(), txtAlamat.getText(), idTerpilih);
        });
        btnDelete.addActionListener(e -> {
            if(idTerpilih == -1) return;
            execute("DELETE FROM tbl_pelanggan WHERE id_pelanggan=?", idTerpilih);
        });
        btnCari.addActionListener(e -> loadData(txtCari.getText()));

        loadData("");
    }

    private void loadData(String search) {
        model.setRowCount(0);
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM tbl_pelanggan WHERE nama_pelanggan LIKE ?")) {
            ps.setString(1, "%" + search + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void execute(String query, Object... params) {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Pelanggan Diperbarui!");
            txtNama.setText(""); txtTelp.setText(""); txtAlamat.setText(""); idTerpilih = -1;
            loadData("");
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }
}
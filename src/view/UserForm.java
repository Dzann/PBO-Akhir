package view;

import config.DBConfig;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserForm extends JFrame {
    private JTextField txtUsername, txtPassword;
    private JComboBox<String> cbRole;
    private JTable tabelUser;
    private DefaultTableModel model;
    private int idTerpilih = -1;

    public UserForm() {
        setTitle("Manajemen Data User");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlForm.add(new JLabel("Username:")); txtUsername = new JTextField(); pnlForm.add(txtUsername);
        pnlForm.add(new JLabel("Password:")); txtPassword = new JTextField(); pnlForm.add(txtPassword);
        pnlForm.add(new JLabel("Role:")); cbRole = new JComboBox<>(new String[]{"Admin", "Kasir"}); pnlForm.add(cbRole);

        JButton btnSave = new JButton("Simpan");
        JButton btnEdit = new JButton("Ubah");
        JButton btnDelete = new JButton("Hapus");
        JPanel pnlAction = new JPanel(); pnlAction.add(btnSave); pnlAction.add(btnEdit); pnlAction.add(btnDelete);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlAction, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0);
        tabelUser = new JTable(model);
        add(new JScrollPane(tabelUser), BorderLayout.CENTER);

        tabelUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tabelUser.getSelectedRow();
                if(row != -1){
                    idTerpilih = Integer.parseInt(model.getValueAt(row, 0).toString());
                    txtUsername.setText(model.getValueAt(row, 1).toString());
                    cbRole.setSelectedItem(model.getValueAt(row, 2).toString());
                }
            }
        });

        btnSave.addActionListener(e -> executeUpdate("INSERT INTO tbl_user(username,password,role) VALUES(?,?,?)", txtUsername.getText(), txtPassword.getText(), cbRole.getSelectedItem().toString()));
        btnEdit.addActionListener(e -> {
            if (idTerpilih == -1) return;
            executeUpdate("UPDATE tbl_user SET username=?, password=?, role=? WHERE id_user=?", txtUsername.getText(), txtPassword.getText(), cbRole.getSelectedItem().toString(), idTerpilih);
        });
        btnDelete.addActionListener(e -> {
            if (idTerpilih == -1) return;
            executeUpdate("DELETE FROM tbl_user WHERE id_user=?", idTerpilih);
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_user, username, role FROM tbl_user")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void executeUpdate(String query, Object... params) {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Aksi CRUD User Berhasil!");
            txtUsername.setText(""); txtPassword.setText(""); idTerpilih = -1;
            loadData();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage()); }
    }
}
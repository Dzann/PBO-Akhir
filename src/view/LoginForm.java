package view;

import config.DBConfig;
import javax.swing.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("Login Sistem Penjualan");
        setSize(320, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(20, 30, 80, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(110, 30, 160, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(20, 70, 80, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(110, 70, 160, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(110, 120, 90, 25);
        add(btnLogin);

        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            try (Connection conn = DBConfig.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM tbl_user WHERE username=? AND password=?")) {
                
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Berhasil! Role: " + rs.getString("role"));
                    new MainMenuForm().setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error Database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
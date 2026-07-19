package view;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm extends JFrame {
    public MainMenuForm() {
        setTitle("Dashboard Menu Utama");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        JButton btnUser = new JButton("CRUD User");
        JButton btnBarang = new JButton("CRUD Barang & Cari");
        JButton btnPelanggan = new JButton("CRUD Pelanggan & Cari");
        JButton btnTransaksi = new JButton("Transaksi Penjualan");
        JButton btnLaporan = new JButton("Laporan Penjualan");
        JButton btnLogout = new JButton("Logout");

        add(btnUser); add(btnBarang); add(btnPelanggan);
        add(btnTransaksi); add(btnLaporan); add(btnLogout);

        btnUser.addActionListener(e -> new UserForm().setVisible(true));
        btnBarang.addActionListener(e -> new BarangForm().setVisible(true));
        btnPelanggan.addActionListener(e -> new PelangganForm().setVisible(true));
        btnTransaksi.addActionListener(e -> new TransaksiForm().setVisible(true));
        btnLaporan.addActionListener(e -> new LaporanForm().setVisible(true));
        btnLogout.addActionListener(e -> {
            new LoginForm().setVisible(true);
            this.dispose();
        });
    }
}
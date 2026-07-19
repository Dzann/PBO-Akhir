package model;

public class Pelanggan {
    private int idPelanggan;
    private String telepon;
    private String alamat;

    public Pelanggan(int idPelanggan, String telepon, String alamat) {
        this.idPelanggan = idPelanggan;
        this.telepon = telepon;
        this.alamat = alamat;
    }

    public int getIdPelanggan() {
        return idPelanggan;
    }
    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getTelepon() {
        return telepon;
    }
    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

}

// Kelas abstrak Pengguna: induk dari Penyewa dan PemilikKos
public abstract class Pengguna {
    protected String nama;
    protected String noTelepon;

    public Pengguna(String nama, String noTelepon) {
        this.nama = nama;
        this.noTelepon = noTelepon;
    }

    public String getNama()       { return nama; }
    public String getNoTelepon()  { return noTelepon; }

    // Setiap subclass wajib implementasi
    public abstract String getPeran();

    @Override
    public String toString() {
        return getPeran() + ": " + nama + " (" + noTelepon + ")";
    }
}

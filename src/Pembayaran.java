// Kelas Pembayaran: merekam satu transaksi pembayaran yang dilakukan penyewa
public class Pembayaran {
    private String idPenyewa;
    private double jumlah;
    private String keterangan;

    public Pembayaran(String idPenyewa, double jumlah, String keterangan) {
        this.idPenyewa   = idPenyewa;
        this.jumlah      = jumlah;
        this.keterangan  = keterangan;
    }

    public String getId()          { return idPenyewa; }
    public double getJumlah()      { return jumlah; }
    public String getKeterangan()  { return keterangan; }

    @Override
    public String toString() {
        return "[Pembayaran] " + idPenyewa + " | Rp " + (long)jumlah + " | " + keterangan;
    }
}

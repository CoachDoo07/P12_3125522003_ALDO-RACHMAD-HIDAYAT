// Kelas Tagihan: mencatat jumlah tagihan dan status pelunasan
// Mendukung serialisasi ke/dari String (format CSV) untuk penyimpanan file
public class Tagihan {
    private double jumlahTagihan;
    private double jumlahDibayar;
    private boolean lunas;

    public Tagihan(double jumlahTagihan) {
        this.jumlahTagihan = jumlahTagihan;
        this.jumlahDibayar = 0;
        this.lunas = false;
    }

    public double getJumlahTagihan() { return jumlahTagihan; }
    public double getJumlahDibayar() { return jumlahDibayar; }
    public boolean isLunas()          { return lunas; }

    public void bayar(double jumlah) throws PembayaranTidakCukupException {
        double sisa = jumlahTagihan - jumlahDibayar;
        if (jumlah < sisa)
            throw new PembayaranTidakCukupException(
                "Pembayaran kurang Rp " + (long)(sisa - jumlah) +
                " (tagihan Rp " + (long)jumlahTagihan +
                ", dibayar Rp " + (long)jumlah + ")");
        jumlahDibayar += jumlah;
        lunas = true;
    }

    public String getStatusTagihan() {
        return lunas ? "Lunas" : "Belum Lunas (sisa Rp " + (long)(jumlahTagihan - jumlahDibayar) + ")";
    }

    // ── Serialisasi CSV: "jumlahTagihan|jumlahDibayar|lunas" ────
    public String keCSV() {
        return jumlahTagihan + "|" + jumlahDibayar + "|" + lunas;
    }

    public static Tagihan dariCSV(String baris) {
        String[] bagian = baris.split("\\|");
        Tagihan t = new Tagihan(Double.parseDouble(bagian[0]));
        t.jumlahDibayar = Double.parseDouble(bagian[1]);
        t.lunas = Boolean.parseBoolean(bagian[2]);
        return t;
    }

    @Override
    public String toString() {
        return "Tagihan: Rp " + (long)jumlahTagihan + " | " + getStatusTagihan();
    }
}

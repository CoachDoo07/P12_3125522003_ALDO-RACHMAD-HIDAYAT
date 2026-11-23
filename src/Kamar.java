// Kelas Kamar: merepresentasikan satu unit kamar kos
// Mendukung serialisasi ke/dari String (format CSV) untuk penyimpanan file
public class Kamar implements DapatDicekStatus {
    private String nomorKamar;
    private String tipe;        // "Single" / "Double"
    private double hargaSewa;   // per bulan
    private boolean terisi;

    public Kamar(String nomorKamar, String tipe, double hargaSewa) {
        if (nomorKamar == null || nomorKamar.isBlank())
            throw new IllegalArgumentException("Nomor kamar tidak boleh kosong.");
        if (hargaSewa <= 0)
            throw new IllegalArgumentException("Harga sewa harus lebih dari 0, bukan: " + hargaSewa);
        this.nomorKamar = nomorKamar.trim();
        this.tipe = tipe;
        this.hargaSewa = hargaSewa;
        this.terisi = false;
    }

    // ── Getter ──────────────────────────────────────────────────
    public String  getNomorKamar() { return nomorKamar; }
    public String  getTipe()       { return tipe; }
    public double  getHargaSewa()  { return hargaSewa; }
    public boolean isTerisi()      { return terisi; }

    // ── Setter (dipanggil oleh Penyewa saat check-in/check-out) ─
    public void setTerisi(boolean terisi) { this.terisi = terisi; }

    @Override
    public String getStatus() {
        return terisi ? "Terisi" : "Tersedia";
    }

    // ── Serialisasi CSV: "nomorKamar|tipe|hargaSewa|terisi" ─────
    public String keCSV() {
        return nomorKamar + "|" + tipe + "|" + hargaSewa + "|" + terisi;
    }

    public static Kamar dariCSV(String baris) {
        String[] bagian = baris.split("\\|");
        Kamar k = new Kamar(bagian[0], bagian[1], Double.parseDouble(bagian[2]));
        k.setTerisi(Boolean.parseBoolean(bagian[3]));
        return k;
    }

    @Override
    public String toString() {
        return nomorKamar + " (" + tipe + ", " + getStatus() + ") seharga Rp " + (long) hargaSewa;
    }
}

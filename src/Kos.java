import java.util.ArrayList;
import java.util.List;

// Kelas Kos: mengelola koleksi kamar dan penyewa
// P12: ditambah kemampuan simpan/muat data ke/dari file teks (via SiKosFileManager)
public class Kos {
    private String namaKos;
    private List<Kamar>   daftarKamar;
    private List<Penyewa> daftarPenyewa;

    public Kos(String namaKos) {
        this.namaKos       = namaKos;
        this.daftarKamar   = new ArrayList<>();
        this.daftarPenyewa = new ArrayList<>();
    }

    public String getNamaKos()           { return namaKos; }
    public List<Kamar> getDaftarKamar()  { return daftarKamar; }
    public List<Penyewa> getDaftarPenyewa() { return daftarPenyewa; }

    // ── Operasi Kamar ────────────────────────────────────────────
    public void tambahKamar(Kamar kamar) {
        if (cariKamar(kamar.getNomorKamar()) != null)
            throw new IllegalArgumentException(
                "Nomor kamar " + kamar.getNomorKamar() + " sudah terdaftar di " + namaKos);
        daftarKamar.add(kamar);
    }

    public Kamar cariKamar(String nomorKamar) {
        for (Kamar k : daftarKamar)
            if (k.getNomorKamar().equalsIgnoreCase(nomorKamar)) return k;
        return null;
    }

    public boolean hapusKamar(String nomorKamar) {
        Kamar k = cariKamar(nomorKamar);
        if (k == null) return false;
        if (k.isTerisi())
            throw new IllegalStateException(
                "Kamar " + nomorKamar + " sedang terisi, tidak dapat dihapus!");
        return daftarKamar.remove(k);
    }

    public void tampilkanDaftarKamar() {
        System.out.println("=== Daftar Kamar di " + namaKos + " ===");
        if (daftarKamar.isEmpty()) {
            System.out.println("Belum ada kamar terdaftar di " + namaKos + ".");
            return;
        }
        for (Kamar k : daftarKamar) System.out.println("  - " + k);
    }

    // ── Operasi Penyewa ──────────────────────────────────────────
    public Penyewa daftarkanPenyewa(String id, String nama, String noTelepon, String nomorKamar)
            throws KamarTidakTersediaException {
        if (cariPenyewa(id) != null)
            throw new IllegalArgumentException(
                "ID Penyewa " + id + " sudah terdaftar di " + namaKos);
        Kamar kamar = cariKamar(nomorKamar);
        if (kamar == null)
            throw new IllegalArgumentException("Kamar " + nomorKamar + " tidak ditemukan.");
        Penyewa p = new Penyewa(id, nama, noTelepon, kamar);
        daftarPenyewa.add(p);
        return p;
    }

    public Penyewa cariPenyewa(String idPenyewa) {
        for (Penyewa p : daftarPenyewa)
            if (p.getIdPenyewa().equalsIgnoreCase(idPenyewa)) return p;
        return null;
    }

    public boolean hapusPenyewa(String idPenyewa) {
        Penyewa p = cariPenyewa(idPenyewa);
        if (p == null) return false;
        if (p.isAktif())
            throw new IllegalStateException(
                "Penyewa " + idPenyewa + " masih aktif menempati kamar " +
                p.getKamarDitempati().getNomorKamar() + ", selesaikan check-out terlebih dahulu!");
        return daftarPenyewa.remove(p);
    }

    public void tampilkanDaftarPenyewa() {
        System.out.println("=== Daftar Penyewa di " + namaKos + " ===");
        if (daftarPenyewa.isEmpty()) {
            System.out.println("Belum ada penyewa terdaftar di " + namaKos + ".");
            return;
        }
        for (Penyewa p : daftarPenyewa) System.out.println("  - " + p);
    }

    // ── Statistik ────────────────────────────────────────────────
    public double hitungPotensiPendapatan() {
        double total = 0;
        for (Kamar k : daftarKamar) total += k.getHargaSewa();
        return total;
    }

    public int jumlahKamarTersedia() {
        int count = 0;
        for (Kamar k : daftarKamar) if (!k.isTerisi()) count++;
        return count;
    }
}

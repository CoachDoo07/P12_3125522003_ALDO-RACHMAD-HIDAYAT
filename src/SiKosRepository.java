import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * SiKosRepository — P12: Penyimpanan dan Pembacaan Data ke File
 *
 * Bertanggung jawab menyimpan dan memuat data Kamar ke/dari file teks
 * menggunakan java.nio.file (NIO2). Memisahkan tanggung jawab file I/O
 * dari class Kos (prinsip Single Responsibility).
 *
 * Format file kamar.txt  : nomorKamar|tipe|hargaSewa|terisi
 *   Contoh:
 *     K01|Single|500000.0|true
 *     K02|Double|750000.0|false
 *
 * Aturan keamanan:
 *   - simpan() memvalidasi semua data SEBELUM penulisan dimulai.
 *   - muat() menampung hasil ke objek sementara; collection lama BARU
 *     diganti setelah SELURUH file valid (rollback otomatis jika gagal).
 *   - File tidak ditemukan → FileNotFoundException (subkelas IOException).
 *   - Format baris salah / data tidak valid → IOException dengan pesan baris.
 */
public class SiKosRepository {

    // ── SIMPAN data kamar ke file ────────────────────────────────
    public static void simpanKamar(List<Kamar> daftarKamar, Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (Kamar k : daftarKamar) {
            String nomor = k.getNomorKamar();
            String tipe  = k.getTipe();
            // Validasi: nilai tidak boleh mengandung separator |
            if (nomor.contains("|") || tipe.contains("|"))
                throw new IllegalArgumentException(
                    "Data kamar " + nomor + " mengandung karakter terlarang '|'.");
            baris.add(nomor + "|" + tipe + "|" + k.getHargaSewa() + "|" + k.isTerisi());
        }
        // Files.write membuat file jika belum ada, menimpa jika sudah ada
        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    // ── MUAT data kamar dari file — rollback jika ada baris invalid ──
    public static void muatKamar(List<Kamar> daftarKamar, Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        // Tampung hasil ke daftar sementara
        List<Kamar> sementara = new ArrayList<>();
        for (int i = 0; i < baris.size(); i++) {
            String b = baris.get(i);
            if (b.isBlank()) continue;
            // split("\\|", -1) mempertahankan kolom kosong agar dapat divalidasi
            String[] bagian = b.split("\\|", -1);
            if (bagian.length != 4) {
                throw new IOException(
                    "Format salah pada baris " + (i + 1) + ": butuh 4 kolom, dapat " + bagian.length);
            }
            try {
                double harga  = Double.parseDouble(bagian[2]);
                boolean terisi = Boolean.parseBoolean(bagian[3]);
                Kamar k = new Kamar(bagian[0], bagian[1], harga);
                k.setTerisi(terisi);
                sementara.add(k);
            } catch (IllegalArgumentException e) {
                throw new IOException(
                    "Data tidak valid pada baris " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        // Ganti collection HANYA setelah semua baris dinyatakan valid
        daftarKamar.clear();
        daftarKamar.addAll(sementara);
    }

    // ── Cek keberadaan file ──────────────────────────────────────
    public static boolean fileAda(Path lokasi) {
        return Files.exists(lokasi);
    }
}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main.java — SiKos P12: Penyimpanan dan Pembacaan Data ke File
 *
 * Mahasiswa  : Aldo Rachmad Hidayat
 * NRP        : 3125522003
 * Prodi      : D3 Teknik Informatika — PENS PSDKU Sumenep
 * Pertemuan  : 12 — Penyimpanan dan Pembacaan Data ke File
 *
 * Skenario pengujian (T1–T14):
 *   T1–T3  : Siapkan data kamar, daftarkan penyewa
 *   T4     : Simpan data kamar ke file
 *   T5     : Simulasi restart — buat objek Kos baru (data di memori hilang)
 *   T6     : Muat kamar dari file → data kembali
 *   T7     : Verifikasi data setelah dimuat
 *   T8     : Muat file yang tidak ada → IOException, collection lama aman
 *   T9     : Simpan ulang setelah tambah kamar baru
 *   T10    : Muat ulang → verifikasi kamar baru ada
 *   T11    : Muat file dengan format baris salah → IOException, rollback
 *   T12    : Muat file dengan data tidak valid → IOException, rollback
 *   T13    : Muat file kosong → collection menjadi kosong (valid)
 *   T14    : Laporan akhir
 */
public class Main {

    static final Path FILE_KAMAR = Paths.get("kamar.txt");

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("  SiKos P12 \u2014 Penyimpanan dan Pembacaan Data");
        System.out.println("  Aldo Rachmad Hidayat | NRP: 3125522003");
        System.out.println("=================================================");

        PemilikKos pemilik = new PemilikKos("Haidar", "0812-3456-7890");
        Kos kos = new Kos("Kos Sejahtera");

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 1. SIAPKAN DATA AWAL");

        // [T1] Tambah kamar
        System.out.println("\n[T1] Tambah Kamar K01 (Single, 500000) dan K02 (Double, 750000)");
        try {
            kos.tambahKamar(new Kamar("K01", "Single", 500_000));
            kos.tambahKamar(new Kamar("K02", "Double", 750_000));
            kos.tambahKamar(new Kamar("K03", "Single", 500_000));
            System.out.println("  Hasil : BERHASIL \u2013 K01, K02, K03 ditambahkan.");
            System.out.println("  State : Total kamar di Kos = " + kos.getDaftarKamar().size());
        } catch (Exception e) {
            System.out.println("  Hasil : GAGAL \u2013 " + e.getMessage());
        }

        // [T2] Daftarkan penyewa di K01
        System.out.println("\n[T2] Daftarkan Penyewa Budi (P01) menempati K01");
        try {
            kos.daftarkanPenyewa("P01", "Budi", "0811-1111-1111", "K01");
            System.out.println("  Hasil : BERHASIL \u2013 Budi terdaftar, K01 = Terisi.");
            System.out.println("  State : Total penyewa = " + kos.getDaftarPenyewa().size()
                + " | K01 = " + kos.cariKamar("K01").getStatus());
        } catch (Exception e) {
            System.out.println("  Hasil : GAGAL \u2013 " + e.getMessage());
        }

        // [T3] Tampilkan data awal
        System.out.println("\n[T3] Tampilkan data kamar sebelum disimpan");
        kos.tampilkanDaftarKamar();

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 2. SIMPAN DATA KE FILE");

        // [T4] Simpan kamar ke file
        System.out.println("\n[T4] Simpan data kamar ke \"" + FILE_KAMAR + "\"");
        try {
            SiKosRepository.simpanKamar(kos.getDaftarKamar(), FILE_KAMAR);
            System.out.println("  Hasil : BERHASIL \u2013 Data kamar tersimpan ke " + FILE_KAMAR);
            System.out.println("  State : File ada = " + SiKosRepository.fileAda(FILE_KAMAR));
        } catch (IOException e) {
            System.out.println("  Hasil : GAGAL (IOException) \u2013 " + e.getMessage());
        }

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 3. SIMULASI RESTART PROGRAM");

        // [T5] Buat objek Kos baru (simulasi program ditutup dan dibuka ulang)
        System.out.println("\n[T5] Buat objek Kos baru (data di memori hilang)");
        kos = new Kos("Kos Sejahtera");
        System.out.println("  Hasil : Data di memori dikosongkan (simulasi restart).");
        System.out.println("  State : Kamar = " + kos.getDaftarKamar().size()
            + " | Penyewa = " + kos.getDaftarPenyewa().size());

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 4. MUAT DATA DARI FILE");

        // [T6] Muat kamar dari file
        System.out.println("\n[T6] Muat data kamar dari \"" + FILE_KAMAR + "\"");
        try {
            SiKosRepository.muatKamar(kos.getDaftarKamar(), FILE_KAMAR);
            System.out.println("  Hasil : BERHASIL \u2013 Data kamar dimuat dari file.");
            System.out.println("  State : Total kamar dimuat = " + kos.getDaftarKamar().size());
        } catch (IOException e) {
            System.out.println("  Hasil : GAGAL (IOException) \u2013 " + e.getMessage());
        }

        // [T7] Verifikasi data setelah dimuat
        System.out.println("\n[T7] Verifikasi K01 (Terisi) dan K02 (Tersedia) setelah dimuat");
        kos.tampilkanDaftarKamar();
        Kamar k01 = kos.cariKamar("K01");
        System.out.println("  State : K01 = " + (k01 != null ? k01.getStatus() : "tidak ditemukan")
            + " | K02 = " + (kos.cariKamar("K02") != null
                ? kos.cariKamar("K02").getStatus() : "tidak ditemukan"));

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 5. PENANGANAN KESALAHAN FILE I/O");

        // [T8] Muat file yang tidak ada → IOException, data lama tetap aman
        System.out.println("\n[T8] Muat dari file yang tidak ada (data_kosong.txt)");
        int jumlahSebelum = kos.getDaftarKamar().size();
        try {
            SiKosRepository.muatKamar(kos.getDaftarKamar(), Paths.get("data_kosong.txt"));
            System.out.println("  Hasil : (tidak seharusnya sampai di sini)");
        } catch (IOException e) {
            System.out.println("  Hasil : DITOLAK (IOException) \u2013 " + e.getClass().getSimpleName()
                + ": " + e.getMessage());
            System.out.println("  State : Collection lama tetap aman, kamar = "
                + kos.getDaftarKamar().size() + " (rollback: " + jumlahSebelum + " \u2192 " + kos.getDaftarKamar().size() + ")");
        }

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 6. UPDATE DAN SIMPAN ULANG");

        // [T9] Tambah K04, simpan ulang
        System.out.println("\n[T9] Tambah Kamar K04 (VIP, 1200000) dan simpan ulang");
        try {
            kos.tambahKamar(new Kamar("K04", "VIP", 1_200_000));
            SiKosRepository.simpanKamar(kos.getDaftarKamar(), FILE_KAMAR);
            System.out.println("  Hasil : BERHASIL \u2013 K04 ditambah, file diperbarui.");
            System.out.println("  State : Total kamar = " + kos.getDaftarKamar().size());
        } catch (Exception e) {
            System.out.println("  Hasil : GAGAL \u2013 " + e.getMessage());
        }

        // [T10] Reset dan muat ulang → verifikasi K04 ada
        System.out.println("\n[T10] Reset memori, muat ulang \u2014 verifikasi K04 tersimpan");
        Kos kosVerif = new Kos("Kos Sejahtera");
        try {
            SiKosRepository.muatKamar(kosVerif.getDaftarKamar(), FILE_KAMAR);
            Kamar k04 = kosVerif.cariKamar("K04");
            System.out.println("  Hasil : " + (k04 != null
                ? "BERHASIL \u2013 K04 ada: " + k04 : "GAGAL \u2013 K04 tidak ditemukan"));
            System.out.println("  State : Total kamar dimuat = " + kosVerif.getDaftarKamar().size());
        } catch (IOException e) {
            System.out.println("  Hasil : GAGAL (IOException) \u2013 " + e.getMessage());
        }

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 7. VALIDASI ROLLBACK PADA MUAT GAGAL");

        // [T11] Muat file dengan format baris salah → rollback
        System.out.println("\n[T11] Buat file rusak (format salah) lalu coba muat");
        Kos kosPre = new Kos("Kos Sejahtera");
        try {
            SiKosRepository.muatKamar(kosPre.getDaftarKamar(), FILE_KAMAR);
            System.out.println("  Pra-kondisi: " + kosPre.getDaftarKamar().size() + " kamar di memori.");
        } catch (IOException e) {
            System.out.println("  Gagal pra-kondisi: " + e.getMessage());
        }
        // Tulis file rusak (hanya 2 kolom, butuh 4)
        try {
            java.nio.file.Files.writeString(Paths.get("kamar_rusak.txt"),
                "K01|Single\nK02|Double|750000.0|false\n",
                StandardCharsets.UTF_8);
            int sebelum = kosPre.getDaftarKamar().size();
            SiKosRepository.muatKamar(kosPre.getDaftarKamar(), Paths.get("kamar_rusak.txt"));
            System.out.println("  Hasil : (tidak seharusnya sampai di sini)");
        } catch (IOException e) {
            System.out.println("  Hasil : DITOLAK (IOException) \u2013 " + e.getMessage());
            System.out.println("  State : Collection tetap = " + kosPre.getDaftarKamar().size()
                + " kamar (rollback berhasil, data lama dipertahankan)");
        }

        // [T12] Muat file dengan data tidak valid (harga negatif) → rollback
        System.out.println("\n[T12] Buat file dengan harga kamar tidak valid (\u22120) lalu coba muat");
        try {
            java.nio.file.Files.writeString(Paths.get("kamar_invalid.txt"),
                "K01|Single|-100|false\n",
                StandardCharsets.UTF_8);
            int sebelum = kosPre.getDaftarKamar().size();
            SiKosRepository.muatKamar(kosPre.getDaftarKamar(), Paths.get("kamar_invalid.txt"));
            System.out.println("  Hasil : (tidak seharusnya sampai di sini)");
        } catch (IOException e) {
            System.out.println("  Hasil : DITOLAK (IOException) \u2013 " + e.getMessage());
            System.out.println("  State : Collection tetap = " + kosPre.getDaftarKamar().size()
                + " kamar (rollback berhasil)");
        }

        // [T13] Muat file kosong → collection menjadi kosong (valid)
        System.out.println("\n[T13] Muat file kosong (valid: collection menjadi kosong)");
        try {
            java.nio.file.Files.writeString(Paths.get("kamar_kosong.txt"), "",
                StandardCharsets.UTF_8);
            SiKosRepository.muatKamar(kosPre.getDaftarKamar(), Paths.get("kamar_kosong.txt"));
            System.out.println("  Hasil : BERHASIL \u2013 File kosong dianggap collection kosong yang valid.");
            System.out.println("  State : Total kamar = " + kosPre.getDaftarKamar().size());
        } catch (IOException e) {
            System.out.println("  Hasil : GAGAL \u2013 " + e.getMessage());
        }

        // ═══════════════════════════════════════════════════════
        System.out.println("\n>>> 8. LAPORAN AKHIR");

        // [T14] Laporan akhir
        System.out.println("\n[T14] Laporan akhir setelah semua operasi file");
        pemilik.tampilkanLaporan(kos);

        // Bersihkan file percobaan
        try {
            java.nio.file.Files.deleteIfExists(Paths.get("kamar_rusak.txt"));
            java.nio.file.Files.deleteIfExists(Paths.get("kamar_invalid.txt"));
            java.nio.file.Files.deleteIfExists(Paths.get("kamar_kosong.txt"));
            java.nio.file.Files.deleteIfExists(FILE_KAMAR);
        } catch (IOException ignore) {}
    }
}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Latihan P12 — Penyimpanan dan Pembacaan Data ke File
 *
 * Study kasus: class Buku + Perpustakaan dengan fitur simpan/muat ke file teks.
 * Mengikuti modul P12 (java.nio.file, NIO2):
 *   - Format file: kodeBuku|judul  (satu buku per baris, separator |)
 *   - simpan(Path): tulis seluruh collection ke file
 *   - muat(Path): baca file → validasi → ganti collection HANYA jika semua valid
 *   - Kegagalan muat TIDAK menghapus collection sebelumnya
 *   - Main menerima argumen CLI: "simpan" atau "muat"
 *
 * Mahasiswa  : Aldo Rachmad Hidayat
 * NRP        : 3125522003
 */

// ── Kelas Buku ──────────────────────────────────────────────────────────────
class Buku {
    private String kode;
    private String judul;

    public Buku(String kode, String judul) {
        // Validasi: kode dan judul wajib diisi
        if (kode == null || kode.isBlank())
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        if (judul == null || judul.isBlank())
            throw new IllegalArgumentException("Kode dan judul wajib diisi.");
        // Validasi: tidak boleh mengandung | atau newline
        if (kode.contains("|") || kode.contains("\n") || kode.contains("\r"))
            throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
        if (judul.contains("|") || judul.contains("\n") || judul.contains("\r"))
            throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
        this.kode  = kode.trim();
        this.judul = judul.trim();
    }

    public String getKode()  { return kode; }
    public String getJudul() { return judul; }

    @Override
    public String toString() { return kode + " \u2013 " + judul; }
}

// ── Kelas Perpustakaan ───────────────────────────────────────────────────────
class Perpustakaan {
    private List<Buku> daftarBuku = new ArrayList<>();

    // Tambah buku — tolak null, kode kosong, kode duplikat
    public void tambahBuku(Buku buku) {
        if (buku == null)
            throw new IllegalArgumentException("Objek buku tidak boleh null.");
        for (Buku b : daftarBuku) {
            if (b.getKode().equalsIgnoreCase(buku.getKode()))
                throw new IllegalArgumentException("Kode buku sudah terdaftar: " + buku.getKode());
        }
        daftarBuku.add(buku);
    }

    public void tampilkanBuku() {
        if (daftarBuku.isEmpty()) {
            System.out.println("Koleksi masih kosong.");
            return;
        }
        for (Buku b : daftarBuku) System.out.println(b);
        System.out.println("Total buku: " + daftarBuku.size());
    }

    public int jumlahBuku() { return daftarBuku.size(); }

    // ── LANGKAH 2: Method simpan ─────────────────────────────────
    public void simpan(Path lokasi) throws IOException {
        List<String> baris = new ArrayList<>();
        for (Buku buku : daftarBuku) {
            String kode  = buku.getKode();
            String judul = buku.getJudul();
            // Seluruh data diperiksa sebelum penulisan dimulai
            if (kode.contains("|") || judul.contains("|"))
                throw new IllegalArgumentException("Kode/judul mengandung pemisah terlarang.");
            baris.add(kode + "|" + judul);
        }
        // Files.write membuat file jika belum ada, menimpa jika sudah ada
        Files.write(lokasi, baris, StandardCharsets.UTF_8);
    }

    // ── LANGKAH 3: Method muat ───────────────────────────────────
    public void muat(Path lokasi) throws IOException {
        List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        // Tampung hasil pembacaan di objek sementara
        Perpustakaan sementara = new Perpustakaan();
        for (int i = 0; i < baris.size(); i++) {
            String b = baris.get(i);
            if (b.isBlank()) continue;
            // split("\\|", 2) memisahkan data dan mempertahankan kolom kosong agar dapat divalidasi
            String[] bagian = b.split("\\|", 2);
            if (bagian.length != 2) {
                throw new IOException("Format salah pada baris " + (i + 1));
            }
            try {
                sementara.tambahBuku(new Buku(bagian[0], bagian[1]));
            } catch (IllegalArgumentException e) {
                throw new IOException("Data tidak valid pada baris " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        // Ganti collection HANYA setelah semua baris valid
        daftarBuku.clear();
        daftarBuku.addAll(sementara.daftarBuku);
    }
}

// ── Main — LANGKAH 4: Menerima argumen CLI ───────────────────────────────────
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Gunakan: java Main simpan  atau  java Main muat");
            return;
        }

        Perpustakaan perpustakaan = new Perpustakaan();
        Path lokasi = Paths.get("buku.txt");

        try {
            if (args[0].equals("simpan")) {
                // Mode simpan: tambah dua buku lalu simpan ke file
                perpustakaan.tambahBuku(new Buku("B001", "Dasar Java"));
                perpustakaan.tambahBuku(new Buku("B002", "Konsep OOP"));
                perpustakaan.simpan(lokasi);
                System.out.println("Data berhasil disimpan.");
            } else if (args[0].equals("muat")) {
                // Mode muat: baca dari file lalu tampilkan
                perpustakaan.muat(lokasi);
                perpustakaan.tampilkanBuku();
            } else {
                System.out.println("Perintah tidak dikenal: " + args[0]);
                System.out.println("Gunakan: java Main simpan  atau  java Main muat");
            }
        } catch (IOException e) {
            System.out.println("Operasi file gagal: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Data ditolak: " + e.getMessage());
        }
    }
}

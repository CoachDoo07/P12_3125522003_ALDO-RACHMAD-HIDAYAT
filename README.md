# P12 — Penyimpanan dan Pembacaan Data ke File

**Mata Kuliah**: Praktikum Pemrograman Berorientasi Objek  
**Pertemuan**: 12  
**Topik**: Penyimpanan dan Pembacaan Data ke File  
**Mahasiswa**: Aldo Rachmad Hidayat  
**NRP**: 3125522003  
**Program Studi**: D3 Teknik Informatika — PENS PSDKU Sumenep

---

## Deskripsi

Pada Pertemuan 12, aplikasi **SiKos** (Tugas Proyek P11) dikembangkan dengan menambahkan kemampuan **persistensi data** agar data kamar tidak hilang saat program ditutup. File I/O menggunakan **java.nio.file (NIO2)** dengan format teks satu baris per objek. Prinsip utama: data hasil pembacaan file **harus divalidasi dulu** sebelum menggantikan collection yang sedang digunakan — jika gagal, collection lama dipertahankan.

Prasyarat: Collection pada P11 dan exception handling pada P10.

---

## Konsep Utama

| Komponen | Fungsi |
|----------|--------|
| `Path` | Merepresentasikan lokasi file |
| `Files.write(Path, List<String>, Charset)` | Menulis data ke file; membuat baru jika belum ada, menimpa jika sudah ada |
| `Files.readAllLines(Path, Charset)` | Membaca seluruh baris file ke `List<String>` |
| `Paths.get(String)` | Mengonversi String path ke objek `Path` |
| `StandardCharsets.UTF_8` | Encoding lintas platform |
| `Files.exists(Path)` | Cek keberadaan file sebelum dibaca |
| `IOException` | Menyampaikan kegagalan operasi baca/tulis |

**Prinsip utama:** data hasil pembacaan ditampung di objek *sementara* dahulu. Collection lama baru diganti setelah **seluruh file selesai divalidasi** (rollback otomatis jika ada baris yang gagal).

---

## Struktur Folder

```
P12_3125522003_ALDO RACHMAD HIDAYAT/
├── src/
│   ├── DapatDicekStatus.java          # Interface
│   ├── KamarTidakTersediaException.java
│   ├── PembayaranTidakCukupException.java
│   ├── TagihanBelumLunasException.java
│   ├── Pengguna.java                  # Abstract class
│   ├── Kamar.java
│   ├── Tagihan.java
│   ├── Pembayaran.java
│   ├── Penyewa.java
│   ├── Kos.java
│   ├── SiKosRepository.java           # ★ Kelas baru: File I/O kamar
│   ├── PemilikKos.java
│   └── Main.java                      # 14 skenario pengujian
├── latihan/
│   └── Main.java                      # Buku + Perpustakaan (simpan/muat via CLI)
├── README.md
├── README.pdf
└── UML-SiKos-P12.pdf
```

---

## Kelas Baru: SiKosRepository

Memisahkan tanggung jawab file I/O dari class `Kos` (prinsip Single Responsibility).

**Format file `kamar.txt`:**
```
K01|Single|500000.0|true
K02|Double|750000.0|false
K03|Single|500000.0|false
```

```java
// Simpan: validasi semua data sebelum penulisan dimulai
public static void simpanKamar(List<Kamar> daftarKamar, Path lokasi) throws IOException {
    List<String> baris = new ArrayList<>();
    for (Kamar k : daftarKamar)
        baris.add(k.getNomorKamar() + "|" + k.getTipe() + "|"
                  + k.getHargaSewa() + "|" + k.isTerisi());
    Files.write(lokasi, baris, StandardCharsets.UTF_8);
}

// Muat: tampung ke sementara, ganti collection HANYA jika semua valid
public static void muatKamar(List<Kamar> daftarKamar, Path lokasi) throws IOException {
    List<String> baris = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
    List<Kamar> sementara = new ArrayList<>();
    for (int i = 0; i < baris.size(); i++) {
        String[] bagian = baris.get(i).split("\\|", -1);
        if (bagian.length != 4)
            throw new IOException("Format salah pada baris " + (i + 1));
        try {
            Kamar k = new Kamar(bagian[0], bagian[1], Double.parseDouble(bagian[2]));
            k.setTerisi(Boolean.parseBoolean(bagian[3]));
            sementara.add(k);
        } catch (IllegalArgumentException e) {
            throw new IOException("Data tidak valid pada baris " + (i + 1) + ": " + e.getMessage(), e);
        }
    }
    // Ganti collection HANYA setelah semua baris valid
    daftarKamar.clear();
    daftarKamar.addAll(sementara);
}
```

---

## Latihan: Perpustakaan (Langkah-Langkah Modul)

Mengembangkan program Perpustakaan dari P11 dengan dua fitur baru:

| Method | Deskripsi |
|--------|-----------|
| `simpan(Path)` | Tulis koleksi buku ke file (format: `kode\|judul`) |
| `muat(Path)` | Baca file → validasi → ganti koleksi jika semua valid |

**Format file `buku.txt`:**
```
B001|Dasar Java
B002|Konsep OOP
```

**Cara menjalankan:**
```bash
cd latihan
javac Main.java

java Main simpan     # Simpan dua buku ke buku.txt
java Main muat       # Baca buku.txt dan tampilkan
```

**Hasil yang diharapkan (muat):**
```
B001 – Dasar Java
B002 – Konsep OOP
Total buku: 2
```

---

## Tabel Pengujian — src/Main.java (T1–T14)

| No | Skenario | Input | Hasil yang Diharapkan | Status |
|----|----------|-------|-----------------------|--------|
| T1 | Tambah K01, K02, K03 | Kamar baru | BERHASIL, total kamar = 3 | ✓ |
| T2 | Daftarkan Penyewa Budi (P01) di K01 | id, nama, kamar | BERHASIL, K01 = Terisi | ✓ |
| T3 | Tampilkan data kamar sebelum disimpan | — | Daftar 3 kamar tampil | ✓ |
| T4 | Simpan data kamar ke `kamar.txt` | FILE_KAMAR | BERHASIL, file ada = true | ✓ |
| T5 | Simulasi restart (objek Kos baru) | — | Kamar=0, Penyewa=0 | ✓ |
| T6 | Muat data kamar dari `kamar.txt` | FILE_KAMAR | BERHASIL, 3 kamar dimuat | ✓ |
| T7 | Verifikasi K01=Terisi, K02=Tersedia | — | Status sesuai data yang disimpan | ✓ |
| T8 | Muat file yang tidak ada | "data_kosong.txt" | IOException, collection lama tetap | ✓ |
| T9 | Tambah K04 (VIP), simpan ulang | Kamar baru + file | BERHASIL, 4 kamar tersimpan | ✓ |
| T10 | Reset & muat ulang → K04 ada | — | K04 ditemukan dari file | ✓ |
| T11 | Muat file format salah (2 kolom) | kamar_rusak.txt | IOException + rollback, tetap 4 kamar | ✓ |
| T12 | Muat file harga negatif (-100) | kamar_invalid.txt | IOException + rollback, tetap 4 kamar | ✓ |
| T13 | Muat file kosong | kamar_kosong.txt | BERHASIL, collection jadi kosong (valid) | ✓ |
| T14 | Laporan akhir | — | Rekapitulasi tampil | ✓ |

---

## Tabel Pengujian — latihan/Main.java

| Perintah | Skenario | Hasil yang Diharapkan | Status |
|----------|----------|-----------------------|--------|
| `java Main simpan` | Simpan B001 dan B002 ke buku.txt | "Data berhasil disimpan." | ✓ |
| `java Main muat` | Baca buku.txt, tampilkan buku | B001 dan B002 tampil, Total: 2 | ✓ |
| `java Main` | Tanpa argumen | Pesan petunjuk penggunaan | ✓ |
| `java Main muat` (file tidak ada) | File buku.txt dihapus dulu | "Operasi file gagal: buku.txt" | ✓ |

---

## Cara Menjalankan

### src/Main.java
```bash
cd src
javac -encoding UTF-8 *.java
java -cp . Main
```

### latihan/Main.java
```bash
cd latihan
javac -encoding UTF-8 Main.java
java -cp . Main simpan
java -cp . Main muat
```

---

## Pertanyaan Evaluasi

**1. Apa perbedaan data di collection dan data di file?**  
Data di collection ada di memori RAM dan hilang saat program ditutup. Data di file tersimpan di media penyimpanan permanen (disk) dan tetap ada setelah program berhenti. File bersifat persisten sedangkan collection bersifat volatile.

**2. Mengapa data dari file tetap harus divalidasi?**  
File bisa saja rusak, diubah manual, atau berformat tidak sesuai. Jika data langsung dipakai tanpa validasi, objek yang dibentuk bisa punya state tidak valid (misal harga negatif, kode kosong) yang menyebabkan error saat runtime.

**3. Apa risiko mengosongkan collection sebelum seluruh file selesai diperiksa?**  
Jika terjadi error di tengah pembacaan, collection sudah telanjur dikosongkan sehingga data lama ikut hilang. Solusinya: tampung hasil ke objek *sementara* dan ganti collection lama **hanya setelah semua baris dinyatakan valid**.

**4. Mengapa fitur simpan harus dipanggil kembali setelah data diubah?**  
File tidak memperbarui dirinya sendiri secara otomatis. Setiap perubahan di collection (tambah/hapus/ubah) hanya ada di memori. Memanggil `simpan()` kembali memastikan file selalu mencerminkan kondisi collection terkini.

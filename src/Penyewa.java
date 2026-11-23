// Kelas Penyewa: merepresentasikan penyewa yang menempati kamar kos
// Mendukung serialisasi ke/dari String (format CSV) untuk penyimpanan file
public class Penyewa extends Pengguna {
    private String idPenyewa;
    private Kamar kamarDitempati;
    private Tagihan tagihan;
    private boolean aktif;

    public Penyewa(String idPenyewa, String nama, String noTelepon, Kamar kamar)
            throws KamarTidakTersediaException {
        super(nama, noTelepon);
        if (kamar.isTerisi())
            throw new KamarTidakTersediaException(
                "Kamar " + kamar.getNomorKamar() + " sudah terisi, tidak dapat dihuni.");
        this.idPenyewa      = idPenyewa;
        this.kamarDitempati = kamar;
        this.tagihan        = new Tagihan(kamar.getHargaSewa());
        this.aktif          = true;
        kamar.setTerisi(true);
    }

    // ── Getter ──────────────────────────────────────────────────
    public String  getIdPenyewa()       { return idPenyewa; }
    public Kamar   getKamarDitempati()  { return kamarDitempati; }
    public Tagihan getTagihan()         { return tagihan; }
    public boolean isAktif()            { return aktif; }

    @Override
    public String getPeran() { return "Penyewa"; }

    // ── Bayar tagihan ────────────────────────────────────────────
    public Pembayaran bayarTagihan(double jumlah) throws PembayaranTidakCukupException {
        tagihan.bayar(jumlah);
        return new Pembayaran(idPenyewa, jumlah, "Pembayaran sewa kamar " + kamarDitempati.getNomorKamar());
    }

    // ── Check-out ────────────────────────────────────────────────
    public void checkOut() throws TagihanBelumLunasException {
        if (!tagihan.isLunas())
            throw new TagihanBelumLunasException(
                "Tagihan " + idPenyewa + " belum lunas. Selesaikan pembayaran sebelum check-out.");
        aktif = false;
        kamarDitempati.setTerisi(false);
    }

    // ── Serialisasi CSV: "idPenyewa|nama|noTelepon|nomorKamar|tagihanCSV|aktif" ──
    public String keCSV() {
        return idPenyewa + "|" + nama + "|" + noTelepon + "|" +
               kamarDitempati.getNomorKamar() + "|" + tagihan.keCSV() + "|" + aktif;
    }

    @Override
    public String toString() {
        return idPenyewa + " – " + nama + " (Menempati: " + kamarDitempati.getNomorKamar() + ")";
    }
}

# Jawaban Formulir Keamanan Data (Data safety) — Play Console

Menu: Play Console → Noto → Kebijakan dan program → Konten aplikasi → Keamanan data.

Dasar jawaban (sudah dicek di kode):
- Manifest gabungan tidak memuat izin `INTERNET`, jadi Noto tidak bisa mengirim data ke luar perangkat.
- Tidak ada SDK iklan, analitik, atau crash reporting.
- `allowBackup="false"` dan aturan ekstraksi data mengecualikan semua data (cadangan cloud dan pindah perangkat).

Menurut definisi Google, data yang hanya diproses di perangkat **tidak** dihitung sebagai "dikumpulkan".

## Bagian 1: Pengumpulan dan keamanan data

| Pertanyaan | Jawaban |
| --- | --- |
| Apakah aplikasi mengumpulkan atau membagikan jenis data pengguna yang wajib dilaporkan? | **Tidak** |

Kalau dijawab "Tidak", bagian jenis data dilewati otomatis.

## Bagian 2: Ringkasan yang tampil di toko

Hasilnya di halaman toko:
- "Tidak ada data yang dibagikan kepada pihak ketiga"
- "Tidak ada data yang dikumpulkan"

## Catatan

- Kalau nanti menambah analitik, crash report, sinkronisasi, atau izin internet, formulir ini **wajib** diperbarui sebelum rilis versi itu.
- URL kebijakan privasi (wajib karena ada izin sensitif dan Aksesibilitas):
  `https://lollyeahhh.github.io/NOTO-MOBILE-APP/rilis/kebijakan-privasi` (setelah GitHub Pages dinyalakan, lihat `langkah-rilis.md`).

## Konten aplikasi lain (di menu yang sama)

| Bagian | Jawaban yang disarankan |
| --- | --- |
| Kebijakan privasi | URL di atas |
| Akses aplikasi | Semua fitur bisa diakses tanpa login. Tidak perlu akun uji. |
| Iklan | Tidak berisi iklan |
| Rating konten | Isi kuesioner: kategori "Utilitas/Produktivitas", jawab "Tidak" untuk semua konten sensitif. Hasil yang diharapkan: semua umur (3+ / Everyone). |
| Target audiens | 18+ (atau 13+). Jangan pilih kelompok di bawah 13 tahun, supaya tidak masuk aturan Families. |
| Aplikasi berita | Bukan |
| Aplikasi pemerintah | Bukan |
| Fitur keuangan | Tidak ada |
| Kesehatan | Tidak ada (Noto bukan aplikasi kesehatan) |
| Izin foreground service | Lihat `deklarasi-izin.md` |
| Aksesibilitas | Lihat `deklarasi-izin.md` |

# Skenario Uji NOTO

Dijalankan semua saat QA (Tahap 9). Tiap tahap baru menambah skenarionya di sini.
Perangkat: **E** = emulator 393 dp, **X** = HP Xiaomi. Bahasa: uji di **ID** dan **EN** bila ada teks.

Kolom hasil diisi saat QA: ✅ lolos, ❌ gagal (tulis catatan), – tidak berlaku.

## A. Splash dan navigasi (Tahap 2)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| A1 | Buka aplikasi dari launcher | Splash 7 frame ±5,6 detik sama dengan prototype FigCraft, lalu Beranda | E, X | |
| A2 | Tekan tiap tab Bottom Nav | Pindah Beranda/Blokir/Statistik/Setelan, pill aktif sesuai desain | E | |
| A3 | Tombol back sistem di tab selain Beranda | Kembali sesuai riwayat, aplikasi tidak keluar mendadak | E, X | |
| A4 | Putar HP / ganti ke mode gelap sistem | Tampilan tetap terang, tidak rusak | E | |

## B. Aturan: tambah, edit, hapus (Tahap 3–4)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| B1 | Blokir kosong → "+" | Form Aturan Baru terbuka, tombol Simpan nonaktif | E | |
| B2 | Pilih aplikasi → Pilih N Aplikasi | Kembali ke form, Sheet Atur Jadwal terbuka otomatis | E | |
| B3 | Template "Jam Kerja", Simpan Jadwal, Kuota 1j 30m, Simpan Aturan tanpa nama | Aturan tersimpan bernama "Blokir"; aturan kedua tanpa nama jadi "Blokir 1" | E | |
| B4 | Nama aturan sama dengan aturan lain | Ditolak dengan pesan sesuai desain | E | |
| B5 | Jadwal lewat tengah malam (22.00–05.00) | Bar 24 jam jadi dua blok di kedua ujung, status benar sebelum dan sesudah tengah malam | E | |
| B6 | Detail → Edit → ubah aplikasi/jadwal/kuota → Simpan Perubahan | Perubahan tampil di Detail, Daftar, Beranda | E | |
| B7 | Detail → toggle Aturan aktif mati | Badge "Nonaktif", panel info "Aturan sedang dijeda…", kartu pindah ke seksi Nonaktif | E | |
| B8 | Detail → Hapus aturan → Ya, hapus | Dissolve ke Daftar, aturan hilang; bila terakhir → Blokir Kosong | E | |
| B9 | Edit → Hapus aturan ini | Sama dengan B8 | E | |
| B10 | Tutup paksa aplikasi lalu buka lagi | Semua aturan dan setelan masih ada | E, X | |

## C. Pilih Aplikasi (Tahap 5)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| C1 | Buka Pilih Aplikasi | Semua aplikasi ber-ikon di launcher tampil (kecuali NOTO), tampilan Kategori aktif | E, X | |
| C2 | Cek kelompok | Sosial Media / Hiburan / Lainnya, isi tiap kelompok urut A–Z, kelompok kosong tidak tampil | X | |
| C3 | Tekan A–Z | Satu kelompok "Semua aplikasi" urut A–Z | E, X | |
| C4 | "Pilih semua" di satu kelompok | Hanya isi kelompok itu tercentang, jumlah di Bilah Aksi bertambah | E | |
| C5 | Cari "insta" / "INSTA" | Hasil sama (tidak beda huruf besar/kecil) | X | |
| C6 | Cari nama yang tidak ada | Layar "Yahh Nggak Ketemu", chip tetap tampil | E | |
| C7 | 1 aplikasi dipilih (EN) | "1 app selected", "Choose 1 App" | E | |
| C8 | Pasang aplikasi baru saat NOTO terbuka, lalu buka Pilih Aplikasi | Aplikasi baru muncul | X | |
| C9 | Aplikasi tanpa ikon / ikon gagal dimuat | Logo Cadangan (kotak sage + ikon empat kotak) | X | |
| C10 | Copot aplikasi yang ada di aturan | Kartu dan Detail menampilkan ikon cadangan dan nama paket; Edit → Pilih → aplikasi itu hilang dari aturan | X | |
| C11 | Kartu Blokir, Beranda, Detail | Ikon dan nama aplikasi asli tampil | E, X | |

## D. Setelan (Tahap 3–4)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| D1 | Ubah tiap setelan Notifikasi & Aturan Pembatasan, tutup paksa, buka lagi | Nilai tetap | E | |
| D2 | Tentang → Kirim masukan | Aplikasi email terbuka ke alamat pengembang | X | |
| D3 | Ganti bahasa sistem ke English | Semua teks berbahasa Inggris yang wajar, tidak ada teks Indonesia tersisa | E | |

## E. Izin dan pelacakan pemakaian (Tahap 6)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| E1 | Instal baru, buat aturan pertama | Sheet izin Akses penggunaan muncul sesuai desain | E, X | |
| E2 | Izinkan Akses penggunaan di Setelan Android, kembali | Baris izin di Setelan berstatus aktif | E, X | |
| E3 | Cabut Akses penggunaan | Baris izin berstatus belum diizinkan, pelacakan berhenti tanpa crash | E, X | |
| E4 | Izin Notifikasi (Android 13+) | Dialog sistem muncul; ditolak tidak membuat crash | E, X | |
| E5 | Pemantau berjalan | Notifikasi tetap NOTO tampil sesuai desain | E, X | |
| E6 | Buka aplikasi di aturan selama 2 menit di dalam jadwal | Sisa kuota berkurang ±2 menit, "Dibuka 1 kali" | E, X | |
| E7 | Buka aplikasi yang sama di luar jadwal | Kuota tidak berkurang | E, X | |
| E8 | Sisa kuota ≤ nilai "Pengingat sisa kuota" | Status "Hampir Habis" (mustard) | E | |
| E9 | Kuota habis | Status "Time Out" | E | |
| E10 | Jendela jadwal berikutnya dimulai | Kuota penuh lagi tanpa membuka NOTO | E, X | |
| E11 | Restart HP | Pemantau menyala lagi otomatis | X | |
| E12 | Biarkan HP terkunci 1 jam | Pemantau tidak menghabiskan baterai mencolok; waktu layar mati tidak dihitung | X | |
| E13 | Halaman optimasi baterai | Terbuka dari sheet izin | X | |

## G. Izin penguncian (Tahap 7A)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| G1 | Simpan aturan saat izin Tampil di atas belum ada | Sheet "Izinkan Noto tampil di atas aplikasi" muncul setelah sheet Baterai | E, X | |
| G2 | Ketuk "Buka Setelan" | Halaman "Tampil di atas aplikasi lain" untuk Noto terbuka; setelah dinyalakan, baris Setelan jadi "Aktif" | E, X | |
| G3 | Simpan aturan pertama kali di Xiaomi/Oppo/vivo/dll. | Sheet "Satu langkah lagi untuk perangkatmu" muncul sekali saja | X | |
| G4 | Ketuk "Buka Info Aplikasi" di Xiaomi | Editor izin MIUI (atau Info Aplikasi) Noto terbuka | X | |
| G5 | Simpan aturan lagi setelah G3 | Sheet perangkat tidak muncul lagi; baris "Izin tambahan perangkat" tetap "Atur" | X | |
| G6 | Emulator / Samsung / Pixel | Sheet dan baris "Izin tambahan perangkat" tidak ada | E | |

## H. Layar Terkunci (Tahap 7B)

| No | Langkah | Hasil yang diharapkan | Perangkat | Hasil |
|---|---|---|---|---|
| H1 | Pakai aplikasi di aturan sampai kuota habis, di dalam jadwal | Layar Terkunci muncul ≤ 2 detik, isi sesuai aturan (nama, kuota, aplikasi, jam bebas) | E, X | |
| H2 | Tombol back di layar Terkunci | Ke layar utama Android, bukan ke aplikasi yang dikunci | E, X | |
| H3 | "Kembali ke layar utama" | Ke layar utama Android | E, X | |
| H4 | "Lihat statistik hari ini" | Noto terbuka di tab Statistik | E, X | |
| H5 | Buka lagi aplikasi yang dikunci | Layar Terkunci muncul lagi | E, X | |
| H6 | Matikan "Latihan napas" / "Tampilkan sisa waktu" | Panel Napas / badge "Bebas lagi dalam …" hilang | E | |
| H7 | Di luar jadwal | Aplikasi bebas, layar Terkunci tidak muncul | E, X | |
| H8 | Izin Tampil di atas dicabut | Layar Terkunci tidak muncul, status tetap Time Out, tanpa crash | E, X | |

## F. Otomatis (dijalankan tiap build)

`./gradlew testDebugUnitTest`: format waktu, jadwal, nama otomatis, saring aplikasi, kategori, hitung kuota.

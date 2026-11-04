# NOTO: Panduan untuk AI Coding Agent

File ini dibaca oleh Claude Code (`CLAUDE.md`) dan Codex (`AGENTS.md`). Isinya sama, simpan salinannya dengan kedua nama di akar proyek.

Bahasa komunikasi dengan pemilik proyek: **Bahasa Indonesia**.

---

## 1. Aturan utama: tanya dulu, kerjakan kemudian

Pemilik proyek ingin tahu dan menyetujui setiap langkah. **Jangan mengubah file, menjalankan perintah yang mengubah sesuatu, atau mengambil keputusan teknis sebelum rencananya disetujui.**

### Alur wajib untuk setiap tugas

1. **Pahami tugas.** Baca file yang relevan terlebih dahulu. Jangan menebak isi file.
2. **Ajukan rencana** dengan format di bagian 2, lalu **berhenti dan tunggu persetujuan**.
3. **Kerjakan hanya yang disetujui.** Kalau di tengah jalan perlu menambah atau mengubah rencana, berhenti dan tanya lagi.
4. **Laporkan hasil** dengan format di bagian 3.

### Yang boleh dilakukan tanpa bertanya
- Membaca file, mencari kode, dan menjelaskan isi kode
- Menjalankan perintah baca-saja, misalnya `git status`, `git diff`, `git log`
- Menjawab pertanyaan

### Yang HARUS ditanyakan lebih dulu
- Membuat, mengubah, memindah, atau menghapus file apa pun
- Menambah atau mengubah dependensi (file Gradle, `libs.versions.toml`)
- Mengubah `AndroidManifest.xml`, izin (permission), atau konfigurasi build
- Mengubah struktur folder atau nama package
- Menjalankan build, instalasi ke HP, atau perintah `adb`
- Perintah git yang mengubah riwayat: `commit`, `push`, `reset`, `rebase`, `checkout` yang membuang perubahan
- Memilih satu pendekatan teknis ketika ada lebih dari satu pilihan yang masuk akal

---

## 2. Format rencana (sebelum mengerjakan)

Tulis dalam bentuk berikut, singkat dan jelas:

```
RENCANA

Tujuan:
  (satu kalimat: apa yang ingin dicapai)

Yang akan dibuat:
  - path/file.kt : (fungsi file ini)

Yang akan diubah:
  - path/file.kt : (bagian apa, dan kenapa)

Keputusan yang akan kuambil:
  - (keputusan 1) : (alasan, serta alternatifnya)

Langkah:
  1. ...
  2. ...

Risiko / hal yang perlu diperhatikan:
  - ...

Pertanyaan untuk kamu:
  - ...

Lanjut? (ya / ubah / batal)
```

Aturan tambahan:
- Kalau ada keputusan dengan beberapa pilihan, **sebutkan minimal dua opsi**, beri rekomendasi, dan biarkan pemilik proyek memilih.
- Pisahkan **fakta** (sudah dicek di kode) dari **asumsi** (belum dipastikan).
- Ukuran tugas kecil dan bertahap lebih baik daripada satu perubahan besar. Kalau tugasnya terlalu besar, usulkan pemecahannya.

---

## 3. Format laporan (setelah mengerjakan)

```
SELESAI

Dibuat    : (daftar file)
Diubah    : (daftar file + ringkasan perubahan)
Dihapus   : (daftar file, kalau ada)
Dijalankan: (perintah yang dipakai dan hasilnya)
Belum diuji / belum yakin: (jujur kalau ada)
Saran langkah berikutnya: (maksimal tiga)
```

Kalau build atau tes gagal, **tampilkan pesan error apa adanya**, jangan disembunyikan, lalu usulkan perbaikan dan minta persetujuan sebelum mengubah.

---

## 4. Kalau ada yang tidak jelas: bertanya

- **Bertanya lebih baik daripada menebak.** Kalau permintaan ambigu, bertentangan dengan dokumen ini, atau butuh informasi yang tidak ada di proyek (desain, teks, perilaku yang diharapkan), tanyakan.
- Ajukan pertanyaan yang spesifik dan mudah dijawab, sebaiknya dengan pilihan jawaban. Jangan memberi pertanyaan terbuka yang terlalu luas.
- Kumpulkan beberapa pertanyaan sekaligus dalam satu pesan, jangan bertanya satu-satu berulang kali.
- Jangan mengisi kekosongan dengan keputusan sendiri diam-diam. Kalau terpaksa berasumsi, tulis asumsinya dengan jelas dan minta konfirmasi.
- Kalau pemilik proyek terlihat salah paham atau memilih pendekatan yang berisiko, sampaikan kekhawatiranmu dengan jelas, jangan hanya menurut.

---

## 5. Tentang proyek

**NOTO** adalah aplikasi Android untuk membatasi penggunaan aplikasi tertentu dengan **jadwal** dan **kuota waktu layar (screen time)**.

**Perilaku inti:**
- Pengguna memilih aplikasi dan menyusun aturan, misalnya aturan "Game".
- Di dalam jendela jadwal (misalnya 07.00-21.00), pemakaian aplikasi dihitung terhadap kuota.
- Kalau kuota habis, aplikasi **terkunci sampai jadwal berikutnya**, dan layar pemblokir ditampilkan.
- **Di luar jendela jadwal, aplikasi bebas dipakai tanpa batas** dan tidak ada penghitungan.
- Kuota di-reset otomatis saat jendela jadwal dimulai lagi.

**Fitur versi 1:** pemilihan aplikasi, manajemen jadwal, kuota screen time, pelacakan pemakaian real-time, penguncian otomatis, reset kuota otomatis, akses tanpa batas di luar jadwal, statistik pemakaian, notifikasi dan pengingat, aturan pembatasan kustom.

**Di luar cakupan versi 1** (jangan dikerjakan tanpa diminta): pemblokiran website, pemblokiran notifikasi aplikasi lain, `AccessibilityService`, akun pengguna, sinkronisasi cloud, iklan atau langganan.

---

## 6. Teknologi

| Hal | Ketentuan |
|---|---|
| Bahasa | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Build | Gradle Kotlin DSL (`.kts`) |
| Min SDK | API 26 (Android 8.0) |
| Penyimpanan | Room atau DataStore (tanyakan dulu sebelum memilih) |
| Deteksi aplikasi di layar | `UsageStatsManager` (izin Usage Access) |
| Pemblokiran | Layar pemblokir (Activity atau overlay) |
| Layanan latar belakang | Foreground service |
| Penjadwalan | `AlarmManager` / `WorkManager` |
| Perangkat uji | HP Android sungguhan (bukan emulator), termasuk HP Xiaomi |

**Jangan menambah pustaka baru tanpa persetujuan.** Jelaskan dulu kenapa dibutuhkan dan apa alternatifnya.

---

## 7. Struktur folder

Di dalam `app/src/main/java/<package>/`:

```
MainActivity.kt
ui/
  theme/        Color.kt, Type.kt, Theme.kt
  navigation/   NavGraph.kt, BottomBar.kt
  home/         Beranda
  blocks/       Daftar blokir, buat/edit aturan, pilih aplikasi
  stats/        Statistik
  settings/     Setelan, status izin
  permission/   Bottom sheet izin
data/           Room: entitas, DAO, repository
service/        Pemantau aplikasi, layar pemblokir
util/           Format waktu dan helper
```

Kalau perlu menyimpang dari struktur ini, tanyakan dulu.

---

## 8. Desain

**Sumber kebenaran desain adalah file Figma**, bukan dokumen ini:
`https://www.figma.com/design/RGq2ZCSnPEm1tZ5qgcsqS8/NOTO-APP` (halaman "Noto Mobile App", frame 393 x 852).

Frame yang sudah ada: `Beranda Awal` (kosong, 99:110), `Beranda Isi` (4:2), `block` (107:113, belum berisi), `stats` (156:153, 187:472, 203:285; tiga versi, tanyakan mana yang final), dan komponen `nav` (56:468). Nilai di bawah diambil dari frame-frame itu. **Kalau ada beda dengan Figma, ikuti Figma dan tanyakan.**

### Tipografi
- Font: **Poppins** (Regular, Medium, SemiBold, Bold). Satu keluarga font saja.
- Status bar di Figma memakai SF Pro, itu hanya mockup iPhone dan **bukan** font aplikasi.

| Peran | Gaya | Ukuran |
|---|---|---|
| Label navbar | Medium | 12 |
| Judul bagian ("Blokir Aktif") | Regular | 12 |
| Judul kartu ("Sosmed") | SemiBold | 12 |
| Subjudul kartu | Regular | 10 |
| Judul bagian di Statistik | SemiBold | 15 |
| Nama aplikasi di daftar | SemiBold | 13 |
| Judul layar kosong | SemiBold | 16 |
| Angka besar (ringkasan) | Bold | 24 |
| Angka sangat besar ("4h 13m") | Medium | 30 |
| Teks kecil, label chart | Regular / Medium / SemiBold | 9-11 |

Satuan `sp` di Compose dipetakan langsung dari angka px di Figma.

### Palet warna (dari Figma)

| Nama di Figma | Hex | Dipakai untuk |
|---|---|---|
| Putih | `#FFFFFF` | Latar layar, teks pada elemen sage |
| Color | `#F2F4EE` | Header, navbar, kartu di Beranda |
| Sage lebih muda | `#6E8F76` | Elemen aktif (pil tab aktif) |
| Sage | `#5B7B6A` | Aksen di Statistik (ikon, bar, kartu ringkasan) |
| Sage tua banget | `#1F2B25` | Teks utama, logo, ikon |
| On Surface / High Emphasis | `#212121` | Ikon |
| Icons Color | `#292929` | Garis ikon |
| (tanpa nama) | `#424843` | Teks sekunder dan label kecil |
| (tanpa nama) | `#42654E` | Angka dan teks aksen hijau |
| (tanpa nama) | `#2B4E39` | Teks pada chip "Stabil" |
| (tanpa nama) | `#EDEEE9`, `#F3F4EF` | Permukaan kartu di Statistik |
| (tanpa nama) | `#DFE5D7` | Garis pemisah dan latar track |
| (tanpa nama) | `#C8DFDB` | Track progres |
| (tanpa nama) | `#ABBAAE` | Batang chart |
| (tanpa nama) | `#C4ECCF` | Latar chip/badge |
| (tanpa nama) | `#3368A0` | Penanda "32% turun" |

Warna logo aplikasi (Instagram, TikTok, dll.) memakai warna aslinya masing-masing dan bukan bagian palet NOTO.

**Belum ditentukan di Figma, tanyakan sebelum memakai:** warna status kuota (aman, hampir habis, terkunci), warna peringatan izin, warna tombol utama, dan **mode gelap** (belum ada desainnya, jangan dikerjakan).

### Bentuk dan jarak
- Frame 393 x 852. Margin kiri-kanan sekitar 16-17 dp.
- Header: tinggi 114 (termasuk status bar 59), latar `#F2F4EE`, logo NOTO di kiri pada y = 74.
- Navbar: 4 tab (Beranda, Blokir, Statistik, Setelan), latar `#F2F4EE`, tinggi isi sekitar 80 ditambah area home indicator 34.
- Kartu di Beranda: lebar 361, tinggi 124, radius 11. Kartu di Statistik: radius 16. Item daftar: radius 12. Pil dan chip: radius penuh.
- Isi kartu Beranda: judul + subjudul di kiri atas, ikon blokir dan menu titik tiga di kanan atas, deretan logo aplikasi 27 x 27 dengan jarak 5 di bagian bawah.

**Jangan mengarang warna, ukuran, atau gaya di luar Figma.** Kalau butuh yang belum ada, tanyakan.

---

## 9. Bahasa dan teks

- Bahasa aplikasi: **Indonesia**, gaya santai tapi rapi, memakai "kamu" (bukan "Anda").
- **Semua teks yang tampil ke pengguna wajib ada di `strings.xml`**, tidak boleh ditulis langsung di kode.
  - `res/values/strings.xml` : bahasa Inggris (default)
  - `res/values-in/strings.xml` : bahasa Indonesia
- Format jam: `07.00` (pakai titik). Format durasi: `1j 30m`.
- Contoh nada: "Kuota habis. Dibuka lagi besok pukul 07.00", "Istirahat dulu, ya."

---

## 10. Aturan kode

- Tulis kode yang sederhana dan mudah dibaca. Pemilik proyek masih belajar, jadi **utamakan kejelasan daripada trik**.
- Beri komentar singkat berbahasa Indonesia pada bagian yang rumit (izin, service, logika kuota dan jadwal).
- Satu file, satu tanggung jawab. Pisahkan UI dari logika.
- Gunakan `@Preview` untuk komponen UI agar bisa dicek di Android Studio.
- Jangan memasukkan kunci rahasia, kata sandi, atau data pribadi ke kode atau file ini.
- Jangan menghapus atau menulis ulang kode yang sudah berfungsi kecuali diminta. **Ubah seminimal mungkin**, dan jelaskan bagian mana yang diubah.
- Jelaskan istilah Android yang belum umum (misalnya *foreground service*, *overlay*) dengan singkat saat pertama kali dipakai.

---

## 11. Perizinan (perlu perhatian khusus)

Izin yang direncanakan untuk versi 1:

1. Akses Pemakaian (Usage Access)
2. Tampil di atas aplikasi lain (Overlay)
3. Notifikasi
4. Abaikan optimasi baterai
5. Alarm tepat waktu (exact alarm)
6. Khusus perangkat Xiaomi: izin tampil jendela pop-up di latar belakang

Perubahan apa pun pada izin atau `AndroidManifest.xml` **wajib dibahas dulu**. Jangan meminta izin di luar daftar ini.

---

## 12. Git

- Satu perubahan logis, satu commit. Pesan commit singkat dan jelas.
- **Jangan melakukan commit atau push tanpa persetujuan.**
- Jangan memasukkan `.idea/`, `local.properties`, `build/`, dan `.gradle/` ke repositori.
- Jangan mengerjakan file yang sama bersamaan dengan agent lain. Kalau terlihat ada perubahan yang bukan buatanmu di `git status`, tanyakan dulu.

---

## 13. Urutan pengerjaan yang disepakati

1. Tema: warna (`Color.kt`), font (`Type.kt`), `Theme.kt`
2. Kerangka navigasi: navbar 4 tab dengan layar kosong
3. Layar Beranda dengan data contoh
4. Layar Blokir: daftar, buat/edit aturan, pilih aplikasi, jadwal, kuota
5. Bottom sheet izin dan banner izin
6. Penyimpanan data (Room/DataStore)
7. Deteksi aplikasi, penghitung kuota, dan layar pemblokir
8. Statistik dan notifikasi
9. Pengujian di HP, termasuk penanganan baterai dan Xiaomi

Kerjakan **satu tahap per satu waktu**, dan minta persetujuan sebelum pindah ke tahap berikutnya.

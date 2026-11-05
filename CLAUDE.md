# NOTO: Panduan untuk AI Coding Agent

File ini dibaca oleh Claude Code (`CLAUDE.md`) dan Codex (`AGENTS.md`). Isinya sama, simpan salinannya dengan kedua nama di akar proyek.

Bahasa komunikasi dengan pemilik proyek: **Bahasa Indonesia**.

---

## 0. ATURAN MUTLAK: desain FigCraft adalah acuan

- **Desain dan animasi prototype di FigCraft adalah acuan mutlak.** Semua layar, state, komponen, teks, warna, ukuran, ikon, font, dan animasi prototype (termasuk splash 7 frame ±5,6 detik, smart animate, dan transisi sheet) harus **direplikasi persis** di aplikasi.
- Tidak boleh ada yang dihapus, diganti, disederhanakan, atau "diperbaiki" tanpa persetujuan pemilik proyek.
- Kalau ada bagian yang tidak bisa direplikasi persis di Android, **sebutkan dan tanyakan dulu**.
- Kalau kode dan desain bertentangan, **desain yang menang**.
- Pola lama di layar Beranda dan Statistik direplikasi apa adanya. Jangan dirapikan menjadi komponen baru di tampilan.
- **Satu-satunya pengecualian:** StatusBar dan HomeIndicator bergaya iOS di desain hanyalah mockup. Di aplikasi, pakai system bar Android asli (edge-to-edge). Ruang tingginya (59 dan 34) tetap dipakai sebagai acuan jarak.

### Verifikasi visual per layar
Setelah tiap layar dibuat:
1. Ambil screenshot frame aslinya dari FigCraft.
2. Ambil screenshot hasilnya di emulator.
3. Laporkan perbedaannya (ukuran, warna, jarak, teks).
4. Perbaiki sampai sama sebelum tahap dinyatakan selesai.

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
- Membaca desain di FigCraft (`inspect_document`, `get_node`, `screenshot`). **Jangan pernah mengubah file desain.**
- Menjawab pertanyaan

### Yang HARUS ditanyakan lebih dulu
- Membuat, mengubah, memindah, atau menghapus file apa pun
- Menambah atau mengubah dependensi (file Gradle, `libs.versions.toml`)
- Mengubah `AndroidManifest.xml`, izin (permission), atau konfigurasi build
- Mengubah struktur folder atau nama package
- Menjalankan build, instalasi ke HP/emulator, atau perintah `adb`
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
Beda dengan desain: (hasil perbandingan screenshot FigCraft vs emulator)
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

**Model aturan (dari desain):**
- Satu aturan berlaku untuk **sekelompok aplikasi** (misalnya "Sosmed") dengan **kuota bersama**.
- Isi aturan: nama, aplikasi, **hari berlaku (Sen–Min)**, **jam terbatas** (mulai–selesai), dan **kuota** dengan satu nilai yang sama untuk semua hari aktif.
- Status aturan: **Berlaku**, **Hampir Habis**, **Time Out**, **Di Luar Jadwal**, dan **Nonaktif** (dijeda pengguna).
- Saat time out **tidak ada tombol tambahan waktu**.

**Fitur versi 1:** semua yang ada di desain FigCraft, yaitu:
- pemilihan aplikasi, manajemen jadwal, dan kuota screen time
- pelacakan pemakaian real-time, penguncian otomatis, dan reset kuota otomatis
- akses tanpa batas di luar jadwal
- statistik (Screen Time, Minggu Ini, Bulan Ini)
- notifikasi dan pengingat (pengingat sisa kuota, kuota habis, jadwal, ringkasan harian/mingguan, jam tenang)
- aturan pembatasan kustom (bawaan aturan baru, jeda sebelum mematikan, kunci edit saat time out, latihan napas, tampilkan sisa waktu)
- splash animasi
- `AccessibilityService` (baris "Aksesibilitas" ada di desain Setelan). **Sebelum menulis kodenya, jelaskan dulu implikasi kebijakan Google Play dan alternatifnya.**

**Di luar cakupan versi 1** (jangan dikerjakan tanpa diminta): pemblokiran website, pemblokiran notifikasi aplikasi lain, akun pengguna, sinkronisasi cloud, iklan atau langganan, dan mode gelap.

---

## 6. Teknologi

| Hal | Ketentuan |
|---|---|
| Bahasa | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Arsitektur | MVVM |
| Build | Gradle Kotlin DSL (`.kts`) |
| Package | `id.noto.app` |
| Min SDK | API 26 (Android 8.0) |
| Penyimpanan | **Room** untuk aturan dan pemakaian, **DataStore** untuk setelan |
| Deteksi aplikasi di layar | `UsageStatsManager` (izin Usage Access), ditambah `AccessibilityService` (lihat §5) |
| Pemblokiran | Layar pemblokir (Activity atau overlay, tanyakan saat tahapnya tiba) |
| Layanan latar belakang | Foreground service |
| Penjadwalan | `AlarmManager` / `WorkManager` |
| Font | Poppins sebagai file `.ttf` di `res/font` |
| Ikon | Diekspor dari FigCraft ke vector drawable (`res/drawable`) |
| Pengecekan tampilan | Emulator dengan layar selebar 393 dp, dibandingkan dengan screenshot FigCraft |
| Pengujian fungsi | HP Android sungguhan, termasuk HP Xiaomi |

**Jangan menambah pustaka baru tanpa persetujuan.** Jelaskan dulu kenapa dibutuhkan dan apa alternatifnya.

---

## 7. Struktur folder

Di dalam `app/src/main/java/id/noto/app/`:

```
MainActivity.kt
ui/
  theme/        Color.kt, Type.kt, Theme.kt
  components/   Komponen yang dipakai ulang (sesuai komponen FigCraft)
  navigation/   NavGraph.kt, BottomBar.kt
  splash/       Animasi splash
  home/         Beranda
  blocks/       Daftar blokir, detail, buat/edit aturan, pilih aplikasi, sheet, layar terkunci
  stats/        Statistik
  settings/     Setelan, status izin
  permission/   Bottom sheet izin (menunggu desain)
data/           Room (entitas, DAO, repository) dan DataStore
service/        Pemantau aplikasi, layar pemblokir
util/           Format waktu dan helper
```

Kalau perlu menyimpang dari struktur ini, tanyakan dulu.

---

## 8. Desain

**Sumber kebenaran desain adalah file FigCraft** `D:\desain-claude\NOTO APP.figcraft` (Page 1, dibaca lewat MCP `figcraft-app`), bukan dokumen ini. Nilai di bawah diambil dari file itu dan dari `D:\desain-claude\CLAUDE.md`. **Kalau ada beda, ikuti FigCraft dan tanyakan.**

`D:\desain-claude\CLAUDE.md` berisi aturan kerja untuk **mendesain** di FigCraft (variabel `$var`, `style.apply`, `batch`, tata letak kanvas, dan sebagainya). Aturan kerja itu tidak berlaku untuk coding. Yang dipakai dari dokumen itu hanya **nilai desainnya**: warna, tipografi, spasi, komponen, teks, model aturan, dan timing animasi.

### Layar
Ada 21 layar ditambah 7 frame splash:
- **Beranda:** Kosong, Terisi.
- **Blokir:** Kosong, Daftar, Detail Aturan, Tambah Aturan, Tambah Aturan Terisi, Pilih Aplikasi, Sheet Atur Jadwal, Sheet Atur Kuota, Edit Aturan, Sheet Hapus Aturan, Terkunci Time Out.
- **Statistik:** Screen Time, Minggu Ini, Bulan Ini.
- **Setelan:** Utama, Notifikasi & Pengingat, Aturan Pembatasan, Sheet Jeda Mematikan.
- **Splash:** 1 Jam, 2a/2b/2c Putar, 3 Mengecil, 4 Wordmark, 5 Tagline.

Detail tiap layar, urutan dan timing splash, serta transisinya ada di `D:\desain-claude\CLAUDE.md` bagian "Layar" dan "Prototype".

### Tipografi
Satu keluarga font: **Poppins**. Ukuran dalam `sp` sama dengan angka px di FigCraft.

| Style | Ukuran/Line height | Ketebalan |
|---|---|---|
| `NOTO/Angka Hero` | 24/32 | Bold |
| `NOTO/Judul Kartu` | 16/24 | SemiBold |
| `NOTO/Judul Item` | 14/20 | SemiBold |
| `NOTO/Label` | 12/16 | Medium |
| `NOTO/Teks` | 12/16 | Regular |
| `NOTO/Kecil Label` | 10/14 | Medium |
| `NOTO/Kecil Teks` | 10/14 | Regular |
| `NOTO/Kecil Tebal` | 10/14 | SemiBold |
| `NOTO/Eyebrow` | 10/14, KAPITAL, +0.5px | SemiBold |

Layar dengan pola lama (Beranda, Statistik) bisa memakai ukuran di luar tabel ini. Untuk layar itu, **ikuti nilai aktual di FigCraft** (§0).

### Warna (berdasarkan peran)

| Peran | Nama variabel | Hex |
|---|---|---|
| Latar layar | Putih | `#FFFFFF` |
| Permukaan: kartu, header, nav, tab, latar layar Terkunci | Color | `#F2F4EE` |
| Primer: tab/segmen/chip aktif, tombol utama, batang grafik, angka aksen | Hijau sage | `#5B7B6A` |
| Primer muda: pill nav aktif, progress kuota, blok "Terbatas", badge Aktif | sage lebih muda | `#6E8F76` |
| Teks utama, ikon, scrim (opacity 40%) | Sage tua banget | `#1F2B25` |
| Teks sekunder, keterangan | sagenya ga nyage | `#424843` |
| Garis pemisah, track, garis input/chip, blok "Bebas" | sage bocil | `#DFE5D7` |
| Data pasif, toggle mati, placeholder, progress time out | sage pucet | `#ABBAAE` |
| Latar badge info, track progress, panel info | Biru pucet | `#C8DFDB` |
| Teks dan ikon info, badge tren | Biru semu | `#3368A0` |
| Peringatan "hampir habis" (satu-satunya pemakaian) | Mustard | `#E6C15A` |

- Teks di atas Primer atau Primer muda memakai Putih. Teks di atas Mustard memakai Teks utama.
- **Hindari merah**, termasuk untuk aksi hapus.
- Hex langsung hanya untuk: logo merek aplikasi, efek blur dekoratif `#C4ECCF`, dan nilai aktual lain yang memang ada di FigCraft.
- Mode gelap tidak ada desainnya, jadi jangan dikerjakan.

### Bentuk dan jarak
- Frame lebar 393. Margin layar 16, jadi lebar konten 361.
- Kelipatan spasi: 4 / 8 / 12 / 16 / 24.
- Radius:
  - 16 untuk kartu, tombol besar, dan sudut atas bottom sheet
  - 12 untuk tombol utama, input, item di dalam kartu, Tab Periode, dan pill nav
  - 9999 (penuh) untuk badge, chip, toggle, checkbox, dan track
  - 2 untuk ujung batang grafik
- Tanpa garis tepi dan tanpa bayangan. Kedalaman cukup dari kontras Permukaan di atas latar putih.
- Header tinggi 114 (termasuk ruang status bar 59). Bottom Nav tinggi 114 (berisi nav 50 dan ruang HomeIndicator 34; posisi rinci ambil dari FigCraft).
- Nilai rinci tiap komponen ada di FigCraft. Ambil dari sana, jangan mengarang.

### Ikon dan logo
- Ikon dan logo diekspor dari FigCraft sebagai vector drawable di `res/drawable` (`ic_*.xml`, `logo_*.xml`). Gradien (misalnya logo Instagram) didukung karena minSdk 26 (gradien vector butuh API 24+). Kalau ekspor vector memberi peringatan, pakai PNG/WebP hasil ekspor FigCraft, jangan disederhanakan.
- **Logo pihak ketiga (`logo_*.xml`: Instagram, TikTok, YouTube, Discord, Pinterest, Gmail, dan lainnya) hanya data contoh untuk Preview dan data dummy. Hapus bersama data contohnya sebelum rilis.** Selama pengembangan tetap di `src/main/res/drawable` (jangan dipindah ke source set debug, karena kode di `src/main` merujuknya).
- Di aplikasi nyata, ikon tiap aplikasi diambil dari sistem lewat `PackageManager` (Tahap 5). Komponen Item Aplikasi memakai kotak, ukuran, dan radius persis seperti desain, dengan isi ikon dari sistem.
- Ikon cadangan bila ikon sistem gagal dimuat **belum ada di desain**. Jangan dibuat sendiri (lihat Pertanyaan terbuka di §13).

### Lisensi aset
- Font Poppins berlisensi SIL OFL 1.1. Teks lisensinya ada di `app/src/main/assets/Poppins-OFL.txt` (ikut masuk APK) dan salinannya di `licenses/Poppins-OFL.txt`.

**Jangan mengarang warna, ukuran, atau gaya di luar FigCraft.** Kalau butuh yang belum ada, tanyakan.

---

## 9. Bahasa dan teks

- Bahasa aplikasi: **Indonesia**, gaya santai tapi rapi, memakai "kamu" (bukan "Anda"). Teks persis seperti di FigCraft.
- **Semua teks yang tampil ke pengguna wajib ada di `strings.xml`**, tidak boleh ditulis langsung di kode.
  - `res/values/strings.xml` : bahasa Inggris (default)
  - `res/values-in/strings.xml` : bahasa Indonesia
- Format jam: `07.00`, rentang `07.00–21.00`.
- Format durasi: `2j 15m`, `45m`, dan `1j` di sumbu grafik. **Jangan pakai `h`.**
- Hari disingkat: Sen, Sel, Rab, Kam, Jum, Sab, Min. Detik ditulis lengkap ("10 detik").
- Contoh nada: "Saatnya time out sejenak", "Tarik napas sebentar, yuk", "Yahh Kosong".

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

1. Akses Pemakaian (Usage Access). Sudah ada di desain Setelan.
2. Aksesibilitas (`AccessibilityService`). Sudah ada di desain Setelan; lihat catatan Google Play di §5.
3. Tampil di atas aplikasi lain (Overlay)
4. Notifikasi
5. Abaikan optimasi baterai
6. Alarm tepat waktu (exact alarm)
7. Khusus perangkat Xiaomi: izin tampil jendela pop-up di latar belakang

- **Izin 3–7 belum ada desainnya.** Jangan membuat UI-nya sendiri. Kumpulkan daftar izin yang benar-benar dibutuhkan, lalu serahkan ke pemilik proyek untuk didesain di FigCraft sebelum tahap izin dimulai.
- Perubahan apa pun pada izin atau `AndroidManifest.xml` **wajib dibahas dulu**. Jangan meminta izin di luar daftar ini.

---

## 12. Git

- Satu perubahan logis, satu commit. Pesan commit singkat dan jelas.
- **Jangan melakukan commit atau push tanpa persetujuan.**
- Jangan memasukkan `.idea/`, `local.properties`, `build/`, `.gradle/`, dan `.omc/` ke repositori.
- Jangan mengerjakan file yang sama bersamaan dengan agent lain. Kalau terlihat ada perubahan yang bukan buatanmu di `git status`, tanyakan dulu.

### Commit dan push
- Jangan menambahkan Co-Authored-By, tanda tangan, footer "Generated with Claude Code", Claude-Session, atau atribusi apa pun ke pesan commit dan deskripsi pull request.
- Jangan pernah menjalankan `git push`. Pengguna yang push sendiri.
- Jangan commit kecuali diminta. Jika diminta, pakai identitas Git yang sudah ada di komputer ini, dan jangan mengubah `git config`.
- Sebelum menyatakan suatu tahap selesai, jalankan `git log --grep="Co-Authored-By" --oneline` dan laporkan hasilnya.

---

## 13. Urutan pengerjaan yang disepakati

1. **Design system:** warna, font Poppins, text style, `Theme.kt`, ikon vector drawable
2. **UI statis dan navigasi:** komponen dasar, Header, Bottom Nav 4 tab, splash animasi, dan transisi
3. **Semua layar dengan data contoh:** Beranda, Blokir, Statistik, Setelan (dipecah per tab)
4. **Database:** Room untuk aturan dan pemakaian, DataStore untuk setelan
5. **Pemilihan aplikasi dan jadwal** yang berfungsi
6. **Pelacakan pemakaian:** izin (setelah desainnya ada), foreground service, penghitung kuota
7. **Penguncian:** layar Terkunci, reset kuota otomatis, Aksesibilitas
8. **Notifikasi dan setelan** yang berfungsi, statistik dengan data asli
9. **Pengujian di HP**, termasuk penanganan baterai dan Xiaomi

Kerjakan **satu tahap per satu waktu**, dan minta persetujuan sebelum pindah ke tahap berikutnya. Setiap tahap yang menyentuh tampilan baru dianggap selesai setelah lolos verifikasi visual (§0).

### Pertanyaan terbuka (menunggu desain atau keputusan pemilik proyek)
- **Sebelum Tahap 5:** desain ikon cadangan bila ikon aplikasi dari sistem gagal dimuat.
- **Sebelum Tahap 6:** desain UI izin 3–7 (§11).
- **Statistik - Bulan Ini:** ikon di chip "32% turun" (node 520) kosong di FigCraft. Sedang dicek pemilik proyek, jangan diubah.
- **Nav lama (gerigi terisi)** di Beranda - Terisi, Beranda - Kosong, Blokir - Kosong, dan tiga layar Statistik direplikasi apa adanya. Penyeragaman diputuskan nanti di FigCraft.

# Langkah Rilis Noto (untuk pemilik proyek)

Urutan dari kode siap sampai Noto ada di Play Store dan GitHub Releases.

## 1. Keystore (sekali saja)

1. Android Studio → **Build → Generate Signed App Bundle or APK…** → Android App Bundle → **Create new…**
2. Simpan di luar folder proyek, misalnya `D:\Pribadi\kunci\noto.jks`.
3. Catat kata sandi keystore, alias (misalnya `noto`), dan kata sandi alias di pengelola kata sandi.
4. **Cadangkan file `.jks`** (flashdisk atau cloud pribadi). Kalau hilang, pembaruan di luar Play App Signing tidak bisa ditandatangani lagi.

Jangan pernah memasukkan `.jks` atau kata sandinya ke repo.

## 2. Versi

Di `app/build.gradle.kts`:
- `versionName`: 0.1, 0.2, … (yang dilihat pengguna).
- `versionCode`: 1, 2, 3, … **wajib naik setiap kali mengunggah ke Play**, walaupun versionName sama.

## 3. Membuat file rilis

Android Studio → **Build → Generate Signed App Bundle or APK…**
- **Android App Bundle (.aab)** → untuk Play Store. Hasil: `app/release/app-release.aab`.
- **APK** → untuk GitHub Releases dan pasang langsung. Hasil: `app/release/app-release.apk`.

Pilih varian **release** dan keystore dari langkah 1.

Sebelum diunggah, pasang APK release di emulator dan Xiaomi, lalu jalankan skenario utama di `docs/skenario-uji.md`.

## 4. Kebijakan privasi online (GitHub Pages)

1. Push repo ke GitHub (kamu sendiri).
2. GitHub → repo → **Settings → Pages** → Source: *Deploy from a branch* → Branch: `main`, folder `/docs` → Save.
3. Tunggu ±1 menit, lalu buka `https://lollyeahhh.github.io/NOTO-MOBILE-APP/rilis/kebijakan-privasi`.
4. Pakai URL itu di Play Console.

## 5. GitHub Releases (tombol unduh di README)

1. GitHub → repo → **Releases → Draft a new release**.
2. Tag: `v0.1`, judul: `Noto v0.1`.
3. Isi catatan rilis (salin dari bagian Rilis di README).
4. Lampirkan `app-release.apk`, ganti namanya jadi `noto-v0.1.apk`.
5. **Publish release**. Tombol unduh di README langsung mengarah ke rilis terbaru.

## 6. Play Console

1. Buat akun developer (sekali bayar) di play.google.com/console.
2. **Create app** → nama Noto, bahasa Indonesia, Aplikasi, Gratis.
3. Isi **Konten aplikasi**: lihat `keamanan-data.md` dan `deklarasi-izin.md`.
4. Isi **Listing toko**: lihat `listing-toko.md`.
5. **Pengujian tertutup** dulu. Untuk akun developer pribadi yang baru, Play biasanya mewajibkan pengujian tertutup dengan sejumlah penguji selama beberapa hari sebelum boleh ke Produksi (cek syarat terbaru di Play Console).
6. Unggah `app-release.aab` ke jalur pengujian, kirim untuk ditinjau.
7. Setelah syarat terpenuhi, promosikan ke **Produksi**.

## 7. Setelah rilis

- Perbarui README (versi, tanggal, tombol Google Play).
- Versi berikutnya: naikkan `versionCode` dan `versionName`, perbarui teks "Catatan rilis" di Setelan → Tentang Noto.

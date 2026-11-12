# Deklarasi Izin untuk Play Console

Teks di bawah ditulis dalam bahasa Inggris karena formulir Play ditinjau dalam bahasa Inggris. Salin apa adanya, lalu sesuaikan kalau ada yang berubah di aplikasi.

---

## 1. Accessibility API (Layanan Aksesibilitas)

Menu: Konten aplikasi → Accessibility API (muncul setelah AAB berisi `AccessibilityService` diunggah).

**Fakta di kode** (`res/xml/aksesibilitas.xml`, `service/PengunciAksesibilitas.kt`):
- Hanya menerima `typeWindowStateChanged` (jendela berganti).
- `canRetrieveWindowContent="false"`: tidak bisa membaca isi layar.
- Tidak melakukan aksi (klik, ketik, gestur) atas nama pengguna. Hanya membuka layar time out milik Noto.
- Opsional: Noto tetap berfungsi tanpa layanan ini (memakai Usage Access + overlay).
- Ada pengungkapan jelas (prominent disclosure) di Noto sebelum pengguna diarahkan ke Setelan Android: sheet "Kunci aplikasi lebih cepat".

**Apakah aplikasi adalah alat aksesibilitas (IsAccessibilityTool)?** Tidak.

**Isian "Describe the core functionality of your app"**

> Noto is a screen time manager. The user chooses apps and creates rules with active days, a restricted time window, and a time quota. Inside the window, time spent in those apps counts toward the quota; when it runs out, those apps are locked with a time out screen until the window ends. Outside the window, apps are free to use.

**Isian "Explain how your app uses the Accessibility API"**

> Noto's Accessibility Service is optional and is used only to detect which app has just come to the foreground (TYPE_WINDOW_STATE_CHANGED, package name only). When the user opens an app whose quota has run out, Noto immediately shows its own time out screen. The service has canRetrieveWindowContent set to false, so it cannot read screen content, text, passwords, or messages. It performs no actions on the user's behalf and makes no autonomous decisions: it only enforces limits the user created. All data stays on the device; the app has no internet permission. Before the user is sent to system settings, Noto shows an in-app disclosure explaining exactly this, and the user must agree.

**Isian "Why is this API necessary" (kalau ditanya)**

> Without it, Noto can only detect app switches by polling Usage Stats, which can let a blocked app stay usable for a few seconds and is less reliable on some devices that delay usage events. The accessibility event lets the user's own limit take effect immediately. The feature is optional and the app works without it.

### Naskah video demo Aksesibilitas (± 60–90 detik)

Rekam layar perangkat (atau emulator) dalam satu video tanpa potongan, unggah ke YouTube sebagai **Tidak publik (Unlisted)**, lalu tempel tautannya di formulir.

1. Buka Noto. Tampilkan Beranda dengan satu aturan, misalnya "Sosmed" berisi YouTube.
2. Buka Setelan → Aksesibilitas "Izinkan". Tahan 3 detik di sheet "Kunci aplikasi lebih cepat" supaya teks pengungkapan terbaca.
3. Ketuk tombol setuju → masuk Setelan Android → Aksesibilitas → Noto → nyalakan → setujui dialog sistem.
4. Kembali ke Noto: baris Aksesibilitas sekarang "Aktif".
5. Tunjukkan aturan yang kuotanya sudah habis (status Time Out).
6. Buka YouTube dari layar utama → layar time out Noto langsung muncul.
7. Ketuk "Kembali ke layar utama" di layar time out.
8. (Opsional) Matikan Aksesibilitas → tunjukkan Noto tetap mengunci lewat overlay.

Supaya cepat: isi data kuota hampir habis dulu (lihat `docs/skenario-uji.md`, skenario H dan L).

---

## 2. Foreground service `specialUse`

Menu: Konten aplikasi → Izin foreground service.

**Fakta di kode**: `PemantauService`, `android:foregroundServiceType="specialUse"`, subtype di manifest:
"Tracks which app is on screen to enforce the user's own screen time limits."
Notifikasi tetap: "Noto sedang berjalan — Pemakaian aplikasi dihitung sesuai jadwal aturanmu."

**Jenis tugas yang dipilih**: Special use (Lainnya).

**Isian deskripsi**

> Noto runs a foreground service while Usage Access is granted. Every 2 seconds (only while the screen is on) it checks which app is in the foreground (via UsageStatsManager), records per-app usage for the in-app statistics, adds the time to the matching rule's quota, sends quota reminders, and shows the time out screen when the quota runs out. The work must continue while the user is in other apps, which is the entire purpose of the app, so it cannot be deferred with WorkManager. None of the standard foreground service types (dataSync, mediaPlayback, location, health, etc.) describe this use. The user sees a persistent notification ("Noto is running") the whole time, and the service stops if Usage Access is revoked.

**Dampak bagi pengguna kalau tugas ditunda/dihentikan sistem**

> Usage would not be counted and apps would not be locked when the user's quota runs out, so the user's own limits would silently stop working.

### Naskah video demo foreground service (± 30–45 detik)

1. Buka Noto, tunjukkan satu aturan aktif.
2. Tarik panel notifikasi: tunjukkan notifikasi "Noto sedang berjalan".
3. Buka aplikasi yang dibatasi, pakai sebentar, kembali ke Noto → sisa kuota berkurang.
4. Pakai sampai kuota habis (atau siapkan data hampir habis) → layar time out muncul.

> Catatan (dicek di kode): layanan berjalan terus selama Usage Access diberikan, termasuk saat tidak ada aturan, karena juga mencatat pemakaian untuk Statistik. Layanan berhenti kalau Usage Access dicabut.

---

## 3. Izin lain (tanpa formulir khusus)

| Izin | Catatan |
| --- | --- |
| `PACKAGE_USAGE_STATS` | Tidak ada formulir Play. Diberikan pengguna sendiri lewat Setelan Android. |
| `SYSTEM_ALERT_WINDOW` | Tidak ada formulir Play. Dipakai untuk menampilkan layar time out. |
| `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED` | Izin biasa. |
| `QUERY_ALL_PACKAGES` | **Tidak dipakai.** Noto memakai `<queries>` untuk aplikasi peluncur saja, jadi tidak perlu deklarasi. |

## Risiko

Google bisa menolak pemakaian Aksesibilitas untuk aplikasi non-alat-aksesibilitas. Kalau ditolak: hapus `PengunciAksesibilitas` dari manifest pada versi berikutnya (fitur ini opsional), lalu kirim ulang. Noto tetap berfungsi dengan Usage Access + overlay.

# Noto - Notice Your Time.

> ✨ **Noto Wektu, Noto Urip.** *Menata waktu, menata kehidupan.*
> 

Noto adalah aplikasi Android untuk membantu pengguna mengatur penggunaan aplikasi melalui **jadwal akses** dan **kuota screen time**, tanpa harus memblokir aplikasi sepanjang waktu.

| Properti | Detail |
| --- | --- |
| 📱 Platform | Android |
| 💻 Bahasa | Kotlin |
| 🎨 UI | Jetpack Compose |
| 🚧 Status | In Development |
| 📄 Lisensi | Ditentukan pada tahap pengembangan |

## 📌 Latar Belakang

Pada malam tanggal 3 Oktober 2026, saya mencari aplikasi yang bisa memblokir aplikasi lain. Namun, tidak ada yang benar-benar sesuai dengan kebutuhan saya: membatasi aplikasi **hanya pada jam tertentu** dengan **kuota waktu**, bukan memblokirnya sepanjang hari.

Dari situ, Noto dibuat sebagai aplikasi sendiri dengan fitur yang memang sesuai dengan yang saya inginkan.

---

## 💡 Tentang Noto

Masalah penggunaan smartphone sering kali bukan karena aplikasi tertentu harus dihindari, tetapi karena **waktu penggunaannya tidak terkontrol**.

Noto berbeda dari app blocker biasa. Pengguna tetap bisa memakai aplikasi yang diinginkan, tetapi selama periode tertentu aplikasi itu punya kuota waktu. Saat kuota habis, aplikasi dikunci sampai periode pembatasan berakhir. Di luar periode itu, aplikasi bebas digunakan tanpa batas.

## 🌱 Filosofi Nama

Nama **Noto** punya dua makna sekaligus. Yang pertama dari akronim bahasa Inggris, yang kedua dari bahasa Jawa. Keduanya saling melengkapi.

### 🔤 1. Notice Your Time Out

**Noto** adalah singkatan dari **N**otice y**O**ur **T**ime **O**ut.

*Notice* berarti menyadari atau memperhatikan. *Time out* berarti jeda, yaitu saat untuk berhenti sejenak.

Digabung, **Notice Your Time Out** mengajak pengguna untuk menyadari ke mana waktunya pergi dan kapan waktunya berhenti sejenak. Ketika kuota habis dan aplikasi terkunci, itu bukan hukuman, melainkan pengingat bahwa sekarang saatnya *time out*.

### 🪔 2. Noto dalam Bahasa Jawa

Dalam bahasa Jawa, *“noto”* berarti **menata** atau **mengatur**.

> ✨ **Noto wektu, noto urip.** *Menata waktu, menata kehidupan.*
> 

Maknanya: menata kehidupan yang dimulai dari hal kecil yaitu mengatur screen time. Mengatur screen time bukan cuma soal mengurangi pemakaian smartphone, tetapi tentang cara seseorang menata waktu dan kebiasaannya sehari-hari.

### ☯️ Dua Makna, Satu Nama

| Asal | Makna | Peran |
| --- | --- | --- |
| **Notice Your Time Out** (Inggris) | Menyadari waktu dan kapan harus berhenti | 👁️ *Kesadaran* |
| **Noto** (Jawa) | Menata kehidupan dengan mengatur screen time | 🛠️ *Tindakan* |

> 💬 Tanpa disadari, nama **Noto** membawa dua arti sekaligus: **sadari** waktumu, lalu **tata** hidupmu.
> 

## 🎯 Tujuan Proyek

**Tujuan pribadi**

- Sebagai portofolio.
- Punya aplikasi sendiri dengan fitur yang sesuai kebutuhan pribadi.

**Tujuan produk**

- Membantu pengguna mengontrol penggunaan aplikasi tertentu.
- Batas penggunaan yang lebih fleksibel dibanding daily app limit biasa.
- Memisahkan waktu penggunaan terbatas dan waktu penggunaan bebas.
- Membantu membangun kebiasaan smartphone yang lebih terkontrol.
- Pengalaman screen time management yang sederhana dan mudah dipahami.

## ⚙️ Fitur Utama

### 1️⃣ App Selection

Pilih aplikasi yang ingin dikontrol, misalnya Instagram, TikTok, YouTube, X, atau Mobile Games. Setiap aplikasi punya konfigurasi sendiri.

### 2️⃣ Scheduled Restriction

Tentukan periode saat pembatasan berlaku. Di luar periode itu, pembatasan tidak aktif.

### 3️⃣ Screen Time Quota

Tentukan jumlah waktu yang tersedia selama periode pembatasan. Waktu **hanya dihitung ketika aplikasi sedang dipakai**.

### 4️⃣ Real-Time Screen Time Tracking

Penggunaan aplikasi dihitung otomatis secara real-time. Informasi yang ditampilkan:

- Total penggunaan
- Sisa kuota
- Waktu penggunaan saat ini
- Status pembatasan

### 5️⃣ Automatic App Lock

Saat kuota habis, aplikasi otomatis dikunci dan tidak bisa dibuka selama periode pembatasan masih berlangsung.

### 6️⃣ Automatic Quota Reset

Saat periode pembatasan berikutnya dimulai, kuota kembali ke nilai awal.

### 7️⃣ Unlimited Access Outside Restriction

Di luar periode pembatasan, aplikasi bebas digunakan tanpa kuota.

## 🧭 Core Concept

```
mermaid
flowchart TD
    A[Select App] --> B["Set Schedule<br>07.00 – 21.00"]
    B --> C["Set Time Quota<br>2 Hours"]
    C --> D[Track Usage]
    D --> E{Quota}
    E -->|"Quota > 0"| F[App Open]
    E -->|"Quota = 0"| G[App Locked]
    F --> H[Schedule End]
    G --> H
    H --> I[Unlimited]
    I --> J[Next Schedule]
    J --> K[Reset Quota]

```

---

## 👥 Target Pengguna

Noto cocok untuk pengguna smartphone yang:

- Merasa terlalu banyak menghabiskan waktu di aplikasi tertentu.
- Ingin mengurangi doomscrolling tanpa menghapus aplikasi.
- Butuh batas penggunaan berdasarkan waktu.
- Ingin pembagian waktu bebas dan waktu terbatas yang jelas.
- Ingin kebiasaan penggunaan smartphone yang lebih teratur.

---

## 📚 Contoh Use Case

| Skenario | Aplikasi | Jadwal | Kuota |
| --- | --- | --- | --- |
| 🎓 **Student** | Instagram | 07.00 – 21.00 | 1 jam |
| 🎮 **Gaming** | Mobile Game | 08.00 – 20.00 | 2 jam |
| 🧘 **Digital Wellbeing** | Aplikasi hiburan | 07.00 – 21.00 | Terbatas (bebas 21.00 – 07.00) |
- **Student:** Instagram tetap bisa dipakai saat dibutuhkan, tetapi setelah 1 jam aplikasi dikunci sampai periode selesai.
- **Gaming:** Setelah 2 jam bermain, game tidak bisa dibuka lagi sampai periode pembatasan selesai.
- **Digital Wellbeing:** Aplikasi hiburan dibatasi di siang hari, tetapi bebas dipakai di malam hari.

## 🧩 Prinsip Desain

| Prinsip | Penjelasan |
| --- | --- |
| 🤝 **Control, Not Punishment** | Noto tidak menghukum pengguna. Tujuannya memberi kontrol atas waktu. |
| 🌊 **Flexible, Not Absolute** | Tidak perlu memblokir aplikasi seharian. Pembatasan bisa diterapkan di periode tertentu saja. |
| 👀 **Awareness Before Restriction** | Pengguna bisa melihat bagaimana waktunya digunakan sebelum aplikasi dibatasi. |

---

## 🛠️ Teknologi

| Komponen | Teknologi |
| --- | --- |
| Platform | Android |
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Local Storage | Room |
| App Usage Tracking | Android UsageStats API |
| App Restriction | Android Accessibility / Device APIs (sesuai kebutuhan implementasi) |
| Notification | Android Notification API |

> ⚠️ Teknologi dapat berubah selama pengembangan, tergantung kebutuhan teknis dan keterbatasan Android.
>

---

## 🌅 Visi

Noto bukan sekadar aplikasi untuk memblokir aplikasi lain.

Noto ingin membantu pengguna membangun hubungan yang lebih sehat dengan teknologi, dengan menata **kapan** teknologi digunakan, **berapa lama** digunakan, dan **kapan waktunya berhenti**.

> ✨ **Noto wektu, noto urip.***Menata waktu, menata kehidupan.*
> 

---

## 📄 Lisensi

Lisensi akan ditentukan pada tahap pengembangan.

### Adios

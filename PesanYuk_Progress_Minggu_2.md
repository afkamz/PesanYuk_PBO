# Laporan Progress Minggu 2 – Implementasi Konsep OOP
## Proyek: PesanYuk (Restaurant Point of Sale)

| Parameter | Detail |
|---|---|
| Mata Kuliah | Praktikum Pemrograman Berorientasi Objek (PBO) |
| Program Studi | D3 Teknik Informatika PSDKU Lamongan - PENS |
| Bahasa Pemrograman | Java (OpenJDK 26 / Standard Java 8+) |
| Tahap | **Minggu 2: Implementasi Konsep OOP & Logika Utama Aplikasi** |

---

## 1. Ringkasan Aktivitas Minggu 2

Pada Minggu 2 ini, seluruh perancangan pada dokumen `PesanYuk_Analisis_dan_Desain_Sistem.md` telah berhasil direalisasikan ke dalam kode Java murni dengan menerapkan seluruh prinsip dasar OOP:

1. **Membuat struktur project Java** yang modular berbasis package `com.pesanyuk.*`.
2. **Membuat class dan object** untuk memodelkan seluruh entitas restoran.
3. **Mengimplementasikan constructor dan encapsulation** untuk validasi data dan perlindungan integritas objek.
4. **Mengimplementasikan inheritance** pada hierarki menu restoran.
5. **Mengimplementasikan polymorphism** pada perhitungan harga dan interface pembayaran.
6. **Mengimplementasikan abstraction** dengan abstract class dan interface.
7. **Mengembangkan logika utama aplikasi** mencakup alokasi meja otomatis (*Best-Fit Allocation*), manajemen menu, kalkulasi subtotal/pajak 10%/total, transaksi, custom exception handling, serta persistensi file.

---

## 2. Struktur Direktori Project

```
project pbo/
├── bin/                             # Direktori hasil kompilasi bytecode (.class)
├── data/                            # File penyimpanan data persisten
│   ├── meja.txt                     # Data meja (nomor;kapasitas;terisi)
│   ├── menu.txt                     # Data menu (id;kategori;nama;harga;atribut)
│   └── transaksi.txt                # Data riwayat transaksi
├── src/
│   └── com/
│       └── pesanyuk/
│           ├── exception/           # Custom Exceptions
│           │   ├── InputTidakValidException.java
│           │   ├── MenuNotFoundException.java
│           │   ├── TidakAdaMejaException.java
│           │   └── UangKurangException.java
│           ├── model/               # Entitas Bisnis & Polimorfisme
│           │   ├── MenuItem.java            (abstract class)
│           │   ├── Makanan.java             (extends MenuItem)
│           │   ├── Minuman.java             (extends MenuItem)
│           │   ├── Meja.java
│           │   ├── OrderItem.java
│           │   ├── Order.java
│           │   ├── Transaksi.java
│           │   ├── Pembayaran.java          (interface)
│           │   ├── PembayaranTunai.java     (implements Pembayaran)
│           │   └── PembayaranQRIS.java      (implements Pembayaran)
│           ├── manager/             # Logika Bisnis Utama
│           │   ├── MenuManager.java
│           │   ├── MejaManager.java
│           │   └── OrderManager.java
│           ├── util/                # Utilitas I/O File
│           │   └── FileHandler.java
│           └── Main.java            # Driver class demonstrasi & pengujian mandiri
├── PesanYuk_Analisis_dan_Desain_Sistem.md
└── PesanYuk_Progress_Minggu_2.md
```

---

## 3. Rincian Implementasi Konsep OOP

### 3.1 Abstraction
- **`MenuItem` (Abstract Class):**
  Mendefinisikan atribut umum menu (`id`, `nama`, `harga`) serta mendeklarasikan method abstrak yang wajib diimplementasikan oleh setiap jenis menu:
  - `public abstract double hitungHarga(int qty);`
  - `public abstract String getKategori();`
  - `public abstract String getAtributKhusus();`
- **`Pembayaran` (Interface):**
  Menyediakan kontrak standar pemrosesan pembayaran tanpa memedulikan mekanisme detailnya:
  - `boolean bayar(double total) throws UangKurangException;`
  - `String getNamaMetode();`
  - `String getDetailPembayaran();`

### 3.2 Inheritance
- **`Makanan extends MenuItem`**:
  Mewarisi atribut dan method dari `MenuItem`, serta menambahkan atribut khusus `tingkatPedas` ("Tidak Pedas", "Sedang", "Pedas", "Ekstra Pedas"). Menggunakan pemanggilan konstruktor induk `super(id, nama, harga)`.
- **`Minuman extends MenuItem`**:
  Mewarisi atribut dan method dari `MenuItem`, serta menambahkan atribut khusus `ukuran` ("S", "M", "L").

### 3.3 Polymorphism
- **Polymorphism via Method Overriding:**
  - Pada `Makanan`: `hitungHarga(int qty)` menghitung `harga * qty`.
  - Pada `Minuman`: `hitungHarga(int qty)` memperhitungkan tambahan biaya ukuran (`(harga + getTambahanUkuran()) * qty`). Ukuran L dikenakan tambahan Rp 3.000.
- **Polymorphism via Interface:**
  - `OrderManager.prosesBayar(Order order, Pembayaran pembayaran)` dapat menerima objek `PembayaranTunai` maupun `PembayaranQRIS` secara transparan.
  - Pada `PembayaranTunai`, dilakukan validasi apakah uang yang diserahkan mencukupi dan menghitung uang kembalian.
  - Pada `PembayaranQRIS`, digenerate kode referensi unik dan diverifikasi secara otomatis.

### 3.4 Encapsulation & Constructor
- Seluruh variabel instans bertipe `private`.
- Akses dan modifikasi dilakukan secara terkontrol melalui getter dan setter dengan validasi aturan bisnis:
  - Harga tidak boleh negatif (`harga < 0`).
  - Kuantitas pesanan harus lebih besar dari 0 (`qty > 0`).
  - Jumlah tamu minimal 1 orang.
  - Nomor meja dan kapasitas meja harus positif.
- Jika data tidak valid, konstruktor maupun setter akan melempar `InputTidakValidException`.

### 3.5 Logika Utama Aplikasi
- **Alokasi Meja Otomatis (Best-Fit Allocation):**
  Metode `alokasiMeja(int jumlahTamu)` pada `MejaManager` mencari meja kosong berkapasitas $\ge$ `jumlahTamu` dan memilih meja dengan kapasitas terkecil yang mencukupi untuk mengoptimalkan kapasitas restoran. Jika penuh, dilempar `TidakAdaMejaException`.
- **Kalkulasi Biaya:**
  `Order` menghitung subtotal seluruh `OrderItem`, pajak restoran 10% (`hitungPajak()`), dan total akhir (`hitungTotal()`).
- **Siklus Hidup Meja:**
  Meja otomatis berstatus `terisi = true` saat pesanan dibuat, dan kembali dibebaskan (`terisi = false`) saat transaksi berhasil dibayar atau dibatalkan.
- **Pencetakan Struk:**
  `Transaksi.generateStruk()` memformat struk pembayaran profesional berisi rincian item, subtotal, pajak, total, dan metode bayar.
- **Persistensi File (Persiapan Minggu 3):**
  `FileHandler` mengimplementasikan penyimpanan dan pembacaan data teks pada `data/menu.txt`, `data/meja.txt`, dan `data/transaksi.txt`.

---

## 4. Cara Menjalankan Program

Jalankan perintah berikut pada terminal:

```bash
# 1. Kompilasi seluruh file Java ke folder bin/
javac -d bin $(find src -name "*.java")

# 2. Jalankan driver class Main
java -cp bin com.pesanyuk.Main
```

---

## 5. Hasil Pengujian Eksekusi Program

Berikut cuplikan log eksekusi terminal saat program `Main` dijalankan:

```text
======================================================================
     PESANYUK POS - DEMO IMPLEMENTASI KONSEP OOP (MINGGU 2)
======================================================================

>>> [1] INISIALISASI MANAGER & DATA AWAL
Daftar Meja Terdaftar:
  - Meja #1 (Kapasitas: 2 orang) [KOSONG]
  - Meja #2 (Kapasitas: 2 orang) [KOSONG]
  - Meja #3 (Kapasitas: 4 orang) [KOSONG]
  - Meja #4 (Kapasitas: 4 orang) [KOSONG]
  - Meja #5 (Kapasitas: 4 orang) [KOSONG]
  - Meja #6 (Kapasitas: 6 orang) [KOSONG]
  - Meja #7 (Kapasitas: 8 orang) [KOSONG]

Daftar Menu Awal:
  - [M001] Makanan: Nasi Goreng Spesial (Pedas: Sedang) - Rp 22,000
  - [M002] Makanan: Ayam Bakar Madu (Pedas: Biasa) - Rp 28,000
  - [M003] Makanan: Mie Goreng Seafood (Pedas: Pedas) - Rp 25,000
  - [M004] Makanan: Kwetiau Sapi (Pedas: Sedang) - Rp 27,000
  - [M005] Makanan: Kentang Goreng (Snack) (Pedas: Biasa) - Rp 15,000
  - [D001] Minuman: Es Teh Manis (Ukuran: M) - Rp 5,000 (Tambahan: Rp 0)
  - [D002] Minuman: Es Jeruk Peras (Ukuran: M) - Rp 8,000 (Tambahan: Rp 0)
  - [D003] Minuman: Kopi Susu Gula Aren (Ukuran: L) - Rp 18,000 (Tambahan: Rp 3,000)
  - [D004] Minuman: Matcha Latte (Ukuran: L) - Rp 20,000 (Tambahan: Rp 3,000)
  - [D005] Minuman: Air Mineral (Ukuran: M) - Rp 4,000 (Tambahan: Rp 0)

>>> [2] PENGUJIAN INHERITANCE & ABSTRACTION
Menu berhasil ditambahkan melalui relasi induk MenuItem:
  - Makanan subclass: [M006] Makanan: Sate Ayam Madura (Pedas: Sedang) - Rp 30,000
  - Minuman subclass: [D006] Minuman: Jus Alpukat Kocok (Ukuran: L) - Rp 15,000 (Tambahan: Rp 3,000)

>>> [3] PENGUJIAN POLYMORPHISM (HITUNG HARGA)
  * Makanan (Sate Ayam Madura x 3): Rp 90,000
  * Minuman Ukuran L (Jus Alpukat Kocok x 3): Rp 54,000 (termasuk biaya ukuran)

>>> [4] LOGIKA ALOKASI MEJA OTOMATIS (BEST-FIT)
Skenario 1: Tamu 3 orang datang (Harus memilih meja kapasitas 4 terdekat)
  -> Meja dialokasikan untuk 3 tamu: Meja #3 (Kapasitas: 4)

Skenario 2: Tamu 5 orang datang (Harus memilih meja kapasitas 6)
  -> Meja dialokasikan untuk 5 tamu: Meja #6 (Kapasitas: 6)

>>> [5] PEMESANAN MENU PADA ORDER 1
Daftar Item pada ORD-001:
  - Nasi Goreng Spesial x2 = Rp 44,000
  - Es Teh Manis x2 = Rp 10,000
  - Jus Alpukat Kocok x1 = Rp 18,000
Subtotal : Rp 72,000
Pajak 10%: Rp 7,200
Total    : Rp 79,200

>>> [6] PENGUJIAN EXCEPTION HANDLING (UANG KURANG)
Mencoba membayar Rp 20,000 untuk tagihan Rp 79,200...
  [SUKSES DITANGKAP] Uang tunai tidak cukup! Kurang: Rp 59,200 (Diterima: Rp 20,000, Total: Rp 79,200)

>>> [7] PEMBAYARAN TUNAI BERHASIL & CETAK STRUK
Transaksi berhasil diproses!
Status Meja #3 setelah transaksi: KOSONG (Otomatis Dibebaskan)

--- STRUK TRANSAKSI TUNAI ---
========================================
               PESANYUK POS             
       Restoran Cepat Saji & Cafe       
========================================
No. Transaksi : TRX-20261116-001
Waktu         : 2026-11-16 12:39:38
No. Meja      : #3 (3 Tamu)
----------------------------------------
Item                   Qty      Subtotal
----------------------------------------
Nasi Goreng Spesial...   2  Rp     44,000
Es Teh Manis (M)         2  Rp     10,000
Jus Alpukat Kocok (L)    1  Rp     18,000
----------------------------------------
Subtotal                   Rp     72,000
Pajak Restoran (10%)       Rp      7,200
TOTAL BAYAR                Rp     79,200
----------------------------------------
Pembayaran    : Metode: Tunai | Diterima: Rp 100,000 | Kembalian: Rp 20,800
========================================
     Terima Kasih Atas Kunjungan Anda!  
========================================

>>> [8] PEMESANAN ORDER 2 & PEMBAYARAN QRIS
Total Order 2: Rp 161,700
Transaksi QRIS berhasil diproses!

--- STRUK TRANSAKSI QRIS ---
========================================
               PESANYUK POS             
       Restoran Cepat Saji & Cafe       
========================================
No. Transaksi : TRX-20261116-002
Waktu         : 2026-11-16 12:39:38
No. Meja      : #6 (5 Tamu)
----------------------------------------
Item                   Qty      Subtotal
----------------------------------------
Ayam Bakar Madu [Bi...   3  Rp     84,000
Kopi Susu Gula Aren...   3  Rp     63,000
----------------------------------------
Subtotal                   Rp    147,000
Pajak Restoran (10%)       Rp     14,700
TOTAL BAYAR                Rp    161,700
----------------------------------------
Pembayaran    : Metode: QRIS | Ref: QRIS-11CFD96B | Status: LUNAS (Verified)
========================================
     Terima Kasih Atas Kunjungan Anda!  
========================================

>>> [9] PENGUJIAN PENYIMPANAN DATA PERSISTEN (FILE HANDLING)
Berhasil menyimpan data menu.txt, meja.txt, dan transaksi.txt ke folder data/.

======================================================================
     SELURUH AKTIVITAS MINGGU 2 BERHASIL DIIMPLEMENTASIKAN & DIUJI!   
======================================================================
```

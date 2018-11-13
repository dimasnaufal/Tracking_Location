# Tracking_Location
Last known Location ; reverse Geocoding ; Tracking (Update Location)

# Hasil

# 1
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/first.gif "siji")
# 2
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/second.gif "loro")
# 3
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/third.gif "telu")
# 4
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/fourth.gif "papat")


# 1.	Cara mendeteksi lokasi dari last known location 
-	Menambah/buat variabel bertipe location & FusedLocationProviderClient
-	Menginisiasi variabel FusedLocation tersebut pada onCreate
-	Tambahkan code di bawah pada method getLocation()
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/satu.png "pertama")
 

# 2.	Cara mengkonversi alamat dari geocode koordinat langitude-longitude 
-	Menambahkan class baru untuk mendapatkan alamat task yg mengextend AsyncTask, yang mana menggunakan 2 parameter :
-	Tipe Params untuk passing parameters ke method doInBackground(). 
Pada aplikasi yang dibuat, params ini berupa Location object.
-	Tipe Results untuk mendapatkan hasil dari method onPostExecute(). 
Untuk aplikasi ini, result yang dihasilkan adalah data alamat berupa String.

# 3.	Cara meminta update lokasi secara kontinu 
-	Menggunakan Method LocationRequest dan LocationCallback, yang mana memproses hasil dari LocationCallback yang berupa geocode, dikonversi ke alamat dengan menggunakan class DapatkanAlamatTask().
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/dua.png "kedua")
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/tiga.png "ketiga")
 

 
-	dan ditampilkan dengan code 
![alt_text](https://github.com/dimasnaufal/Tracking_Location/blob/master/app/src/main/res/drawable/empat.png "keempat")
 

# Announcer

> Otomatik sohbet duyuruları ve `/duyuru` ile ekran ortasında anlık duyuru gönderen eklenti.

![Minecraft](https://img.shields.io/badge/Minecraft-1.8--1.21-brightgreen)
![Java](https://img.shields.io/badge/Java-8-orange)
![License](https://img.shields.io/badge/License-MIT-blue)

Belirli aralıklarla sohbete otomatik duyuru gönderir. Ayrıca yetkililer `/duyuru <mesaj>` ile
tüm oyunculara **ekranın ortasında** anlık duyuru atabilir. Tek jar **1.8 – 1.21** arası çalışır.

## ✨ Özellikler

- ⏱️ Ayarlanabilir süre aralığı (saniye)
- 🔀 Sıralı veya rastgele gönderim
- 📝 Çok satırlı duyuru desteği + özelleştirilebilir prefix
- 📣 **`/duyuru <mesaj>`** → herkese ekran ortasında title duyuru (`Yetkili : mesaj`)
- 🎨 Duyuru etiketi (`Yetkili`) ve renkler configden ayarlanabilir

## 📥 Kurulum

1. `Announcer.jar` dosyasını indir.
2. Sunucunun `plugins/` klasörüne at.
3. Sunucuyu yeniden başlat.
4. `plugins/Announcer/config.yml` dosyasından duyuruları düzenle.

## 🎮 Komutlar

| Komut | Açıklama | Yetki |
|-------|----------|-------|
| `/duyuru <mesaj>` | Ekran ortasında anlık duyuru gönderir | `announcer.say` |
| `/announcer reload` | Configi yeniden yükler | `announcer.admin` |
| `/announcer next` | Sıradaki otomatik duyuruyu hemen gönderir | `announcer.admin` |

**Alias:** `/duyuru` · `/ac`

## 🔑 Yetkiler

| Yetki | Açıklama | Varsayılan |
|-------|----------|------------|
| `announcer.say` | `/duyuru <mesaj>` ile anlık duyuru atar | op |
| `announcer.admin` | reload ve next | op |

## ⚙️ Ayarlar (config.yml)

| Anahtar | Açıklama |
|---------|----------|
| `announcer.interval` | Otomatik duyurular arası süre (saniye) |
| `announcer.random` | `true` = rastgele, `false` = sırayla |
| `announcer.prefix` | Otomatik duyuruların başına eklenir |
| `announcer.messages` | Duyuru listesi (her biri bir/birden çok satır) |
| `announcer.say.title` | `/duyuru` üst satır etiketi (`Yetkili`) |
| `announcer.say.subtitle` | `/duyuru` alt satır (`%message%`) |
| `announcer.say.fade-in/stay/fade-out` | Title süreleri (tick) |

`%player%` (gönderen) ve `%message%` (yazılan mesaj) yer tutucuları `say` içinde kullanılabilir.

## 🛠️ Derleme

```bash
mvn clean package
```

Çıktı: `target/Announcer.jar`

> 1.8 uyumluluğu için Java 8 hedefiyle derlenir. En sorunsuz derleme **JDK 8, 11 veya 17** iledir.

## 📄 Lisans

MIT — dilediğin gibi kullan, değiştir, dağıt.

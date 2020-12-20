# WebCrawler :card_index_dividers:

## Content
- [Descriere aplicație](#Introducere)
- [Argumente](#Parametrii)
- [Configuratie default](#ConfigDefault)
- [Limbaj de programare](#Limbaj)
- [Realizatori](#Echipa)



## Introducere
Acest proiect conține o aplicație ce se ocupă cu descărcarea paginilor web primite la input. Utilitarul primește la input un fișier cu un număr varabil de URL-uri, iar de la acest URL-uri, descarcă recursiv atât paginile respective, cât și refințele lor până la un nivel maxim de recusivitate setat implicit sau de către utilizator.
<br />
Alte functionalități oferite de aplicație sunt:
-rularea multithreading
<br />
-autodetectare site-uri invalide/ site-uri excluse prin robots.txt
<br />
-crearea unui sitemap pentru fiecare pagină descarcată unde sunt reprezentate sub forma arborescentă toate fișierele descarcate
<br />
-cautarea în cadrul paginilor descărcate dupa cuvinte cheie
<br />
-filtrarea datelor
<br />

## Servicii
- [x] crawl furnizează funcția de crawl a aplicație.
- [x] search caută în cadrul directorului dorit anumite fișiere conform  parametrilor doriți.
- [x] sitemap accesează funcționalitatea de sitemap.

## Parametrii
- [x] -in path -setează calea de intrare. Poate fi un fișier sau un folder.
- [x] -config file -calea către fișierul de configurare.
- [x] -url address -setează url-ul dorit.
- [x] -keyword word -argument folosit pentru a căuta după un cuvânt cheie.
- [x] -out file -setează fișierul de output.
- [x] -pattern regex -argument folosit pentru a căuta după o expresie regulată.
- [x] -maxsize val -argument folosit pentru a căuta date până într-o anumită dimensiune (bytes).
- [x] -extensions extensions list -listă de extensii folosite pentru a căuta fișierele dorite.

## Utilizarea utilitarului
./program serviciu parametrii

## Exemple de parametrii
- search -in testFolder -maxsize 200000 -extensions pdf txt
- search -in testFolder -maxsize 19000 -extensions txt
- search -in testFolder/images -maxsize 200000 -extensions jpg
- sitemap -in dbcenter.ro -out sitemap.txt -config Conf.cfg
- sitemap -in dbcenter.ro -out sitemap.txt
- crawl -in url.txt
- crawl -in url.txt -config Conf.cfg

## ConfigDefault
- instance.threadsNumber = 4
- instance.delay = 20
- instance.depthLevel = 4
- instance.logFile =log.txt
- instance.logLevel = 3
- instance.targetDirectory = out
- instance.configFileLocation = null
- instance.robots = true;
- instance.configFileLocation = config.cfg

## Exemplu fișier de configurare

Config.cfg

```json
threadsNumber=3
delay=20
depthLevel=3
logFile=P:\log.txt
targetDirectory=aleluia
logLevel=3
robots=true
```

## Limbaj
:memo: Java -> SDK 15

## Echipa

:trollface: Ghenea Claudiu-Florentin -Șăf
<br />
:trollface: Florea Vlad-George
<br />
:trollface:	Neacșu Cristinel-Gabriel
<br />
:trollface: Hariga George-Codrin
<br />
:trollface: Panțucu Flavius-Marian
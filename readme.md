# WebCrawler :card_index_dividers:

## Content
- [Descriere aplicație](#Introducere)
- [Argumente](#Parametrii aplicației)
- [Configuratie default](#Parametrii presetati)
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

## Parametrii aplicației
###
- [x] -in setează fișierul de intrare pentru îndeplinirea functionalității de download
- [x] -config setează fișierul de configurare
- [x] -url trimite un url la input pentru descărcare
- [x] -keywords filtrează dupa cuvinte cheie
- [x] -crawl furnizează funcția de crawl a aplicație
- [x] -search caută în cadrul informatiilor accesate anumite pattern-uri
- [x] -sitemap accesează funcționalitatea de sitemap
- [x] -out setează fișierul de out pentru stocarea datelor de log
- [x] -pattern search după o anumită sintaxă
- [x] -maxsize setează dimensiunea maximă a datelor
- [x] -extensions filtrează după extensie

## Parametrii presetati
-threadsNumber = 4
<br />
-instance.delay = 20
<br />
-instance.depthLevel = 4
<br />
-instance.logFile =log.txt
<br />
-instance.logLevel = 3
<br />
-instance.targetDirectory =out
<br />

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
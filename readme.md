# WebCrawler :card_index_dividers:

Content
- [Introducere](#Descriere aplicație)
- [Modul de rulare](#Modul de rulare)
- [Comenzi aplicatie](#Comenzile disponibile)
- [Fisierul de configurare](#Conținutul fișierului de input)
- [Realizatori](#Echipa)
- [Limbaj](#Limbaj de programare)



## Introducere
Acest proiect conține o aplicație ce se ocupă cu descărcarea paginilor web primite la input. Utilitarul primește la input un fișier cu un număr varabil de URL-uri, iar de la acest URL-uri, descarcă recursiv atât paginile respective, cât și refințele lor până la un nivel maxim de recusivitate setat implicit sau de către utilizator.
Alte functionalități oferite de aplicație sunt:
-rularea multithreading
-autodetectare site-uri invalide/ site-uri excluse prin robots.txt
-crearea unui sitemap pentru fiecare pagină descarcată unde sunt reprezentate sub forma arborescentă toate fișierele descarcate
-cautarea în cadrul paginilor descărcate dupa cuvinte cheie
-filtrarea datelor


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
- [x] -extensons filtrează după extensie

## Formatul presetat al fișierului de configurare
-threadsNumber = 4
-instance.delay = 20
-instance.depthLevel = 4
-instance.logFile =log.txt
-instance.logLevel = 3;
-instance.targetDirectory = "out";

## Limbaj
:memo: Java -> SDK 15

## Echipa

:trollface: Ghenea Claudiu-Florentin
<br />
:trollface: Florea Vlad-George
<br />
:trollface:	Neacșu Cristinel-Gabriel
<br />
:trollface: Hariga George-Codrin
<br />
:trollface: Panțucu Flavius-Marian
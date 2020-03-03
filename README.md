# Lucyna

Projekt tworzony w ramach zaliczenia przedmiotu Programowanie Obiektowe na Wydziale MIM UW. Projekt składa się z dwóch części : Indeksera i Wyszukiwarki. Indekser ma za zadanie obserwować zmiany w plikach i aktualizować indeks plików, wspomagający wyszukiwanie wzorców. Wyszukiwarka służy do wyszukiwania tekstu w monitorowanych przez indekser plikach. Szczegóły i opcje wyszukiwania opisane są niżej.

# Kompilacja

Indexer kompilujemy poleceniem javac na wszystkich plikach w folderze Indexer. Analogicznie dla folderu Searcher kompilujemy wyszukiwarkę.

# Uruchamianie indexera

Aby wyszukiwarka działała poprawnie w tle musi działać indekser. Uruchamiamy go wtedy bez argumentów. Można uruchomić z następującymi opcjami :
* --purge - czyści indeks, po wykonaniu żadne pliki nie są monitorowane
* --add "ścieżka" - dodaje do monitorowanych plików wszystkie pliki dostępne pod ścieżką
* --rm "ścieżka" - usuwa z monitorowanych plików wszystkie pliki dostępne pod ścieżką
* --reindex - buduje indeks od nowa
* --list - wyświetla listę plików monitorowanych

# Korzystanie z wyszukiwarki

Dostępne są następujące polecenia :
* "fraza" - wyszukuje frazę w monitorowanych plikach zgodnie z ustawioną konfiguracją
* %lang pl/en - ustawia język wyszukiwania na polski/angielski
* %details on/off - włącza lub wyłącza pokazywanie fragmentów plików z wyszukiwanymi frazami
* %limit "liczba" - ustawia limit wyników wyszukiwań na "liczba"
* %color on/off - włącza lub wyłącza wyróżnianie znalezionych słów w pokazywanych fragmentach, widoczne tylko po włączeniu details
* %term - ustawienie trybu wyszukiwarki na wyszukiwanie pojedynczych słów
* %phrase - ustawienie trybu wyszukiwarki na wyszukiwanie fraz
* %fuzzy - ustawienie trybu wyszukiwarki na wyszukiwanie niedokładne, tzn. wyszukiwane będą również słowa podobne do wpisanego

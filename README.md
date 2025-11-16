#  Flood-Simulation (Wersja 1.0)

##  Opis Projektu

Pierwsza wersja aplikacji rozproszonej symulującej zjawiska powodziowe 
i zarządzanie zbiornikami retencyjnymi.
System wykorzystuje komunikację **RMI**,
gdzie wiele procesów JVM wymienia dane w czasie rzeczywistym.

##  Stos Technologiczny
* **Język:** Java
* **Komunikacja:** RMI
* **Współbieżność:** `volatile`, `synchronized`

##  Uruchomienie Projektu


### Kroki

1.  **Klonowanie:**
    ```bash
    git clone [ADRES URL REPOZYTORIUM]
    ```
2.  **Konfiguracja Projektu w IDE (IntelliJ IDEA):**
   
    a. **Dodanie Zależności JAR:** Przejdź do **Project Structure** 
    (**File** > **Project Structure** > **Modules** > **Dependencies**). 
    Dodaj plik JAR (znajdujący się w katalogu `libs/`)
    do zależności modułu projektu.
    
    b. **Oznaczenie Źródeł:** W oknie projektu, kliknij prawym przyciskiem myszy na główny katalog z kodem Java i wybierz **Mark Directory as** > **Sources Root**.
 


3. **Uruchomienie Węzła Głównego (Serwera RMI):**
    Uruchom główny kontroler systemu, który inicjalizuje i udostępnia zdalne obiekty.
    * Uruchom klasę **`Tailor`** 


4.  **Uruchomienie Węzłów Klienckich (GUI):**
    Po uruchomieniu `Tailor`, uruchom dowolną z poniższych klas interfejsu
    użytkownika. Każda z nich stanowi osobny proces kliencki.
    * Uruchom klasę **`GuiRiver`**
    * Uruchom klasę **`GuiBasin`**
    * Uruchom klasę **`GuiEnv`**
    * Uruchom klasę **`GuiCC`**

***



### Kontynuacja (Flood-SimulationV2)

Projekt jest obecnie refaktoryzowany (jako **Flood-SimulationV2**)
w celu migracji z RMI na nowoczesną architekturę opartą
na **Spring Boot i REST API**, co ma zwiększyć skalowalność systemu.
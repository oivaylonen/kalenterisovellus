# Projektin seuranta

### 19.2. (Aika-arvio 10h)
- Alustavat rakenteet Category ja Event luokille
- Alustava toteutus CalendarData-oliolle
  - Valittiin Buffer toteutus List rakenteen sijaan
  - Poistaminen ja lisääminen mahdollista
- Koodin organisointi "package mycalendar" rakenteella
- Filehandlerille mahdollisuus tallentaa eventtejä .ics tiedostomuotoon ja ladata tietoja .ics tiedostoista
- Apuobjekti (ICSUtil) FileHandlerille selkeyttämään tiedostorakennetta
- Sbt consolissa testattu funktioiden toiminta oikeilla syötteillä. 
  - Virhetilanteiden käsittelyä ei huomioitu vielä tässä vaiheessa
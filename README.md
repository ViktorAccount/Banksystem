##Docker
Projektet kräver att Docker är installerat. Första gången det startas körs docker compose up --build.

När du står i mappen SOA_tjanst och ska köra testerna måste du först starta
om container-tjänsten med docker compose restart java-service. 
Därefter körs commandot mvn verify för att exekvera alla tester och hämta rapport för code coverage.

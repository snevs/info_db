Fisiere necesare pentru testarea aplicatiei web in Flask, HTML, CSS, care se executa in container Docker.
Contine si o baza de date Oracle care se executa tot intr-un container Docker.

- docker rmi web --force; docker rm web --force; docker build -t web .
- docker run -d -p 1522:1521 -v oracle-volume:/opt/oracle/XE21CFULL/oradata svcosti/infodb

  Sau:
- docker compose up -> va citi din fisierul docker-compose.yml si va executa doua containere

Baza de date va rula pe portul 1521
Aplicatia web va rula pe portul 4443

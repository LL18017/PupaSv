#Imagen pecreada de liberty docker
#docker run --name liberty_app -p 9080:9080 -e DB_HOST=db16 -e DB_NAME=TipicoSV -e DB_PORT=5432 -e DB_USER=postgres -e DB_PASSWORD=12345 --network tpi_pupa_network --rm  liberty_app:latest
#docker build -t liberty_app .

FROM openliberty/open-liberty:latest

COPY server.xml /config/

COPY ./postgresql-42.7.5.jar /config/lib/

# Exponer los puertos típicos de Open Liberty
EXPOSE 9080 9443
COPY ./target/PupaSv-1.0-SNAPSHOT.war /liberty/usr/servers/defaultServer/dropins/

CMD ["server", "run", "defaultServer"]



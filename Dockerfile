#Imagen pecreada de liberty docker
FROM ubuntu:24.04
USER root

# Actualiza e intsalcion de paquetes necesarios
RUN apt-get update && \
    apt-get install -y \
    wget \
    unzip \
    gettext \
  tzdata \
    && rm -rf /var/lib/apt/lists/*

# Establece la zona horaria sin interacción
RUN echo "America/El_Salvador" > /etc/timezone && \
    dpkg-reconfigure -f noninteractive tzdata
#CARPETAS DE TRABAJO
RUN mkdir /usr/local/openLiberty

##DESCRAGA DE LIBERTY Y POSGREST
#RUN wget -O /usr/local/openLiberty/liberty.zip https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/release/25.0.0.2/openliberty-jakartaee10-25.0.0.2.zip
#COPY ./postgresql-42.7.5.jar  /usr/local/postgresql-42.7.5.jar
#
##descarga de JDK
#RUN apt update -y
#RUN echo "America/El_Salvador" > /etc/timezone && \
#    dpkg-reconfigure -f noninteractive tzdata
#
#RUN apt install openjdk-21-jdk -y
#
##CARPETAS DE TRABAJO Y MOVER ARCHIVOS
#
#RUN mkdir openLiberty
#RUN unzip /usr/local/openLiberty/liberty.zip -d /usr/local/openLiberty/
#RUN chmod -R 755 /usr/local/openLiberty/wlp
#COPY ./postgresql-42.7.5.jar  /usr/local/postgresql-42.7.5.jar
#RUN mkdir -p /usr/local/openLiberty/wlp/usr/servers/defaultServer/apps /usr/local/openLiberty/wlp/usr/servers/defaultServer/config
#COPY ./server.xml /usr/local/openLiberty/wlp/usr/servers/defaultServer
## Establece variables de entorno para la base de datos (pueden ser sobrescritas en docker run)
#ENV DB_HOST=db16
#ENV DB_NAME=TipicosSv
#ENV DB_PORT=5432
#ENV DB_USER=postgres
#ENV DB_PASSWORD=abc123
#
#
## Expone los puertos HTTP y HTTPS de Open Liberty
#EXPOSE 9080 9443
#
#
## Usa el servidor predeterminado de Open Liberty
#CMD ["/usr/local/openLiberty/wlp/bin/server", "run", "defaultServer"]


FROM openliberty/open-liberty:latest

COPY server.xml /config/

COPY ./postgresql-42.7.5.jar /config/lib/

# Exponer los puertos típicos de Open Liberty
EXPOSE 9080 9443
COPY ./target/PupaSv-1.0-SNAPSHOT.war /liberty/usr/servers/defaultServer/dropins/

CMD ["server", "run", "defaultServer"]



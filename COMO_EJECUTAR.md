## Pasos para ejecutar la aplicación
1. Primero ejecutar todos los msvc
2. luego acceder a la carpeta
# cd msvc-web-semantica\target\apache-jena-fuseki-6.0.0
3. Ejecutar el .jar
# java -jar .\fuseki-server.jar
4. Abrir en el navegador este link
# http://localhost:3030/
5. En el link crear el dataset (new dataset)
# Dataset name: atencion_medica
# Dataset type: Persistent (TDB2) – dataset will persist across Fuseki restarts
6. Subir el archivo .ttl
# select file, selecionar el archivo .ttl([atencion-medica.ttl](msvc-web-semantica/src/main/resources/ontology/atencion-medica.ttl))
# En la columna actions seleccionar (upload now)
7. Ejecutar Frontend
# En un terminal ingresar a la carpeta [nova-frontend](nova-frontend)
# cd nova-frontend
8. Ejecutar el codgio
# npm run dev (si hay errores con el frontend primer ejecutar npm run build y luego de eso npm run dev)
### GRACIAS
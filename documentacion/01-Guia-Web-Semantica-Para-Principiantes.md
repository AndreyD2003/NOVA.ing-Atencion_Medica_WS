# Guía de Implementación de Web Semántica (Para Principiantes)

Esta guía está pensada para alguien que empieza desde cero y quiere entender e implementar la Web Semántica paso a paso.

---

## 1. Fundamentos conceptuales

### 1.1 ¿Qué es la Web Semántica?

- La web tradicional está pensada para humanos: páginas HTML, texto, imágenes.
- Los ordenadores ven texto, pero no entienden bien el significado de las cosas.
- La Web Semántica añade una **capa de significado estructurado** a los datos para que:
  - Las máquinas sepan qué es una persona, un libro, una película, un paciente, etc.
  - Se puedan relacionar datos de diferentes fuentes automáticamente.

**Analogía sencilla**

Imagina muchas bibliotecas en distintas ciudades. Cada una tiene sus propios catálogos y forma de organizar los libros. La Web Semántica sería como:

- Un **idioma común** que todas las bibliotecas aceptan para describir libros, autores, temas.
- Un **mapa de relaciones** para saber qué libros se relacionan aunque estén en ciudades diferentes.

### 1.2 Tripletas RDF: sujeto–predicado–objeto

La Web Semántica representa la información como **tripletas**:

- **Sujeto**: de quién se habla.
- **Predicado**: qué propiedad o relación.
- **Objeto**: el valor o el otro recurso.

Ejemplos:

- `Juan` — `tieneEdad` — `30`
- `Libro_1` — `tieneAutor` — `Autor_1`
- `Pelicula_X` — `tieneActor` — `Actor_Y`

Visualmente, esto forma un **grafo**: nodos conectados por aristas.

### 1.3 Ontologías

Una **ontología** es un modelo conceptual que define:

- **Clases** (tipos de cosas): `Persona`, `Libro`, `Pelicula`, `Paciente`.
- **Propiedades de datos**: atributos como `nombre`, `fechaNacimiento`, `titulo`.
- **Propiedades de objeto**: relaciones entre cosas, como `tieneAutor`, `tieneAmigo`.
- **Restricciones**: reglas (por ejemplo, una persona tiene exactamente una fecha de nacimiento).

Es parecido a un **modelo de datos** de una base de datos, pero:

- Está pensado para ser usado y compartido globalmente.
- Trabaja con grafos en lugar de tablas.

### 1.4 Linked Data (Datos Enlazados)

**Linked Data** significa publicar datos RDF en la web siguiendo unos principios:

- Cada cosa importante tiene una **URI** (una dirección única, como una URL).
- Esa URI es accesible por HTTP.
- Cuando alguien consulta esa URI, obtiene datos RDF sobre esa cosa.
- Esos datos incluyen enlaces a otras URIs, incluso de otros sitios web.

Ejemplo:

- `http://example.org/libros/1` describe un libro.
- El campo autor puede enlazar a `http://dbpedia.org/resource/J._R._R._Tolkien`, que describe al autor.

**Diferencia con la web tradicional**

- Web tradicional: enlaza páginas (HTML).
- Web Semántica: enlaza **cosas** (personas, libros, organizaciones, etc.).

---

## 2. Preparación del entorno

### 2.1 Herramientas recomendadas

Para empezar de forma cómoda, necesitarás:

- **Editor de ontologías**:
  - Protégé (gratuito, muy usado).
- **Triplestore (base de datos para RDF)**:
  - Apache Jena Fuseki (ligero y fácil para empezar).
  - o GraphDB Free (interfaz muy amigable).
- **Framework RDF (opcional, si quieres programar)**:
  - Apache Jena (Java).
  - o Eclipse RDF4J (Java).
- **Editor de texto**:
  - VS Code, Notepad++, Sublime, etc.
- **Java** (si usarás Jena o RDF4J):
  - JDK 17 o similar.

### 2.2 Instalación de Protégé

1. Ve a: <https://protege.stanford.edu>
2. Descarga Protégé Desktop para tu sistema:
   - Windows: instalador `.exe` o `.zip`.
   - macOS: `.dmg`.
   - Linux: `.zip`.
3. Instalación básica:
   - Windows: ejecuta el `.exe` y sigue el asistente.
   - macOS: abre el `.dmg` y arrastra Protégé a Aplicaciones.
   - Linux: descomprime el `.zip` y ejecuta `run.sh` (posiblemente `chmod +x run.sh` antes).
4. Comprueba que se abre Protégé y que puedes crear una nueva ontología.

### 2.3 Instalación de Java (si vas a usar Jena/RDF4J)

1. Descarga un JDK desde:
   - <https://adoptium.net/> (Temurin) o cualquier distribución OpenJDK.
2. Instala siguiendo el asistente.
3. Comprueba en una terminal:

   ```bash
   java -version
   ```

   Debe mostrar la versión instalada (por ejemplo, `17.x`).

### 2.4 Apache Jena y Fuseki

1. Ve a: <https://jena.apache.org/download/index.cgi>
2. Descarga:
   - Apache Jena (si quieres programar) y
   - Apache Jena Fuseki (servidor SPARQL).
3. Fuseki:
   - Descomprime el archivo descargado.
   - En la carpeta:
     - Windows: ejecuta `fuseki-server.bat`.
     - macOS/Linux: ejecuta `./fuseki-server`.
   - Abre en el navegador: <http://localhost:3030>
   - Deberías ver la interfaz de administración de Fuseki.

### 2.5 GraphDB (alternativa amigable)

1. Ve a: <https://www.ontotext.com/products/graphdb/>
2. Descarga GraphDB Free.
3. Instálalo siguiendo el asistente (en Windows suele ser siguiente/siguiente).
4. Abre <http://localhost:7200> y crea un nuevo “repository”.

---

## 3. Creación de ontologías básicas

### 3.1 Elegir un dominio sencillo

Para aprender, es bueno un ejemplo simple. Por ejemplo:

- **Biblioteca de libros**: libros, autores y editoriales.

Clases (conceptos):

- `Libro`
- `Autor`
- `Editorial`

Propiedades de objeto (relaciones):

- `tieneAutor` (Libro → Autor)
- `publicadoPor` (Libro → Editorial)

Propiedades de datos (atributos):

- `titulo` (texto)
- `anioPublicacion` (año)
- `nombre` (para Autor y Editorial)

### 3.2 Crear la ontología en Protégé (paso a paso)

1. Abre Protégé.
2. Menú **File → New ontology**.
3. Define un IRI base (la “raíz” de tus URIs), por ejemplo:
   - `http://example.org/biblioteca#`
4. Crea las clases:
   - Ve a la pestaña **Classes**.
   - Haz clic en el botón **+**.
   - Añade `Libro`, `Autor`, `Editorial`.
5. Crea propiedades de objeto:
   - Ve a la pestaña **Object properties**.
   - Crea `tieneAutor`:
     - Domain: `Libro`.
     - Range: `Autor`.
   - Crea `publicadoPor`:
     - Domain: `Libro`.
     - Range: `Editorial`.
6. Crea propiedades de datos:
   - Ve a **Data properties**.
   - Crea `titulo`:
     - Domain: `Libro`.
     - Range: `xsd:string`.
   - Crea `anioPublicacion`:
     - Domain: `Libro`.
     - Range: `xsd:gYear`.
   - Crea `nombre`:
     - Domain: `Autor` y `Editorial`.
     - Range: `xsd:string`.

### 3.3 Uso de vocabularios existentes (FOAF, DC, SKOS)

- **FOAF**: describe personas y sus relaciones (`Person`, `name`, `mbox`, etc.).
- **DC / DCTerms** (Dublin Core): describe recursos como documentos y libros (`title`, `creator`, `date`).
- **SKOS**: ideal para listas de conceptos, tesauros, categorías, etc.

En Protégé:

1. Ve a la pestaña **Active Ontology**.
2. En el panel de ontologías importadas, haz clic en **Add**.
3. Elige “Import an ontology from the web” e introduce:
   - FOAF: `http://xmlns.com/foaf/0.1/`
   - Dublin Core Terms: `http://purl.org/dc/terms/`
4. Ahora podrás usar:
   - `foaf:Person`, `foaf:name`.
   - `dc:title`, `dc:creator`, etc.

Ejemplo de reutilización:

- En lugar de crear tu propia propiedad `titulo`, usa `dc:title`.
- En lugar de `nombre`, puedes usar `foaf:name` cuando se trate de personas.

---

## 4. Modelado de datos RDF

### 4.1 Tripletas RDF en la práctica

Ejemplo: Libro “El Hobbit” de Tolkien.

- Sujeto: `http://example.org/libros/1`
- Predicado: `dc:title`
- Objeto: `"El Hobbit"`

Otra tripleta:

- Sujeto: `http://example.org/libros/1`
- Predicado: `:tieneAutor`
- Objeto: `http://example.org/autores/1`

### 4.2 Formatos RDF (RDF/XML, Turtle, JSON-LD)

**Turtle (recomendado para aprender)**

```turtle
@prefix : <http://example.org/biblioteca#> .
@prefix dc: <http://purl.org/dc/terms/> .

<http://example.org/libros/1> a :Libro ;
    dc:title "El Hobbit" ;
    :tieneAutor <http://example.org/autores/1> .

<http://example.org/autores/1> a :Autor ;
    :nombre "J. R. R. Tolkien" .
```

**RDF/XML (más verboso)**

```xml
<rdf:RDF
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:dc="http://purl.org/dc/terms/"
  xmlns:bib="http://example.org/biblioteca#">

  <bib:Libro rdf:about="http://example.org/libros/1">
    <dc:title>El Hobbit</dc:title>
    <bib:tieneAutor rdf:resource="http://example.org/autores/1"/>
  </bib:Libro>

  <bib:Autor rdf:about="http://example.org/autores/1">
    <bib:nombre>J. R. R. Tolkien</bib:nombre>
  </bib:Autor>
</rdf:RDF>
```

**JSON-LD**

```json
{
  "@context": {
    "bib": "http://example.org/biblioteca#",
    "dc": "http://purl.org/dc/terms/",
    "titulo": "dc:title",
    "tieneAutor": { "@id": "bib:tieneAutor", "@type": "@id" },
    "nombre": "bib:nombre",
    "tipo": "@type",
    "id": "@id"
  },
  "@graph": [
    {
      "id": "http://example.org/libros/1",
      "tipo": "bib:Libro",
      "titulo": "El Hobbit",
      "tieneAutor": "http://example.org/autores/1"
    },
    {
      "id": "http://example.org/autores/1",
      "tipo": "bib:Autor",
      "nombre": "J. R. R. Tolkien"
    }
  ]
}
```

### 4.3 Ejercicio: convertir un CSV simple a RDF

Supón un archivo `libros.csv`:

```text
id;titulo;autor
1;El Hobbit;J. R. R. Tolkien
2;1984;George Orwell
```

Ejercicio manual (para entender el proceso):

1. Elige un IRI base: `http://example.org/`.
2. Para cada fila, transforma en Turtle:

```turtle
@prefix : <http://example.org/biblioteca#> .
@prefix dc: <http://purl.org/dc/terms/> .

<http://example.org/libros/1> a :Libro ;
  dc:title "El Hobbit" ;
  :autorNombre "J. R. R. Tolkien" .

<http://example.org/libros/2> a :Libro ;
  dc:title "1984" ;
  :autorNombre "George Orwell" .
```

Más adelante, podrás automatizar la conversión con scripts, pero este ejercicio te obliga a pensar en términos de tripletas.

---

## 5. Publicación de datos enlazados

### 5.1 URIs persistentes

- Elige una estructura para tus URIs:
  - Libros: `http://example.org/libros/{id}`
  - Autores: `http://example.org/autores/{id}`
- Intenta que no cambien con el tiempo (persistencia).

### 5.2 Crear dumps RDF

- Un “dump” es un archivo RDF con todos tus datos.
- Por ejemplo: `biblioteca.ttl` con todos tus libros y autores.
- Puedes mantener dumps separados (libros, autores, géneros).

### 5.3 Cargar datos en un triplestore (ejemplo con Fuseki)

1. Arranca Fuseki.
2. Ve a <http://localhost:3030>.
3. Crea un nuevo dataset (por ejemplo, `biblioteca`).
4. Usa la opción para **subir datos** y carga tu archivo `biblioteca.ttl`.
5. Si todo va bien, ya tienes tus datos RDF almacenados y listos para consultas SPARQL.

### 5.4 Configurar un endpoint SPARQL

- En Fuseki, tu dataset tendrá un endpoint SPARQL:
  - Algo como `http://localhost:3030/biblioteca/sparql`.
- Desde la propia interfaz de Fuseki puedes:
  - Escribir consultas SPARQL.
  - Ver resultados en tabla, CSV, JSON, etc.

### 5.5 Validar datos RDF

- Usa herramientas como:
  - JSON-LD Playground: <https://json-ld.org/playground/>
  - Validadores RDF (por ejemplo, herramientas de Jena como `riot`).
- Objetivo:
  - Comprobar que la sintaxis es correcta.
  - Ver que las URIs se resuelven correctamente y no hay errores obvios.

---

## 6. Consultas SPARQL básicas

### 6.1 Estructura general de una consulta SPARQL

```sparql
PREFIX bib: <http://example.org/biblioteca#>
PREFIX dc: <http://purl.org/dc/terms/>

SELECT ?libro ?titulo
WHERE {
  ?libro a bib:Libro ;
         dc:title ?titulo .
}
```

- `PREFIX`: define atajos para escribir IRIs.
- `SELECT`: indica qué variables quieres recuperar.
- `WHERE`: define patrones de tripletas que deben cumplirse.

### 6.2 Consultas simples

**Listar todos los libros y sus títulos**

```sparql
PREFIX bib: <http://example.org/biblioteca#>
PREFIX dc: <http://purl.org/dc/terms/>

SELECT ?libro ?titulo
WHERE {
  ?libro a bib:Libro ;
         dc:title ?titulo .
}
```

**Filtrar por palabra clave en el título**

```sparql
PREFIX bib: <http://example.org/biblioteca#>
PREFIX dc: <http://purl.org/dc/terms/>

SELECT ?libro ?titulo
WHERE {
  ?libro a bib:Libro ;
         dc:title ?titulo .
  FILTER (CONTAINS(LCASE(STR(?titulo)), "hobbit"))
}
```

### 6.3 Ejercicios con datasets públicos

Puedes practicar con datos reales:

- DBpedia SPARQL endpoint: <https://dbpedia.org/sparql>
- Wikidata Query Service: <https://query.wikidata.org/>

Ejemplo (DBpedia): buscar obras de Tolkien

```sparql
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX dbr: <http://dbpedia.org/resource/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

SELECT ?obra ?titulo
WHERE {
  ?obra dbo:author dbr:J._R._R._Tolkien ;
        rdfs:label ?titulo .
  FILTER (lang(?titulo) = "en")
}
LIMIT 20
```

---

## 7. Integración con HTML (RDFa y JSON-LD)

### 7.1 RDFa: Semántica dentro de HTML

Ejemplo: página con información de una persona.

```html
<div vocab="http://xmlns.com/foaf/0.1/" typeof="Person">
  <span property="name">Juan Pérez</span>,
  email: <a property="mbox" href="mailto:juan@example.com">juan@example.com</a>
</div>
```

- `vocab`: vocabulario de referencia (FOAF).
- `typeof="Person"`: el recurso descrito es una persona.
- `property="name"`: marca el nombre semánticamente.

### 7.2 JSON-LD para SEO y datos estructurados

Ejemplo: marcado de un libro para buscadores (schema.org):

```html
<script type="application/ld+json">
{
  "@context": "https://schema.org",
  "@type": "Book",
  "name": "El Hobbit",
  "author": {
    "@type": "Person",
    "name": "J. R. R. Tolkien"
  },
  "datePublished": "1937"
}
</script>
```

- Motores de búsqueda como Google pueden entender esta estructura.
- Permite mostrar resultados enriquecidos (estrellas, autor, fechas).

### 7.3 Otros tipos comunes en schema.org

- `Person`: personas.
- `Organization`: organizaciones.
- `Event`: eventos.
- `Product`: productos.
- `Movie` y `TVSeries`: películas y series.

Puedes consultar más tipos y propiedades en <https://schema.org>.

---

## 8. Validación y pruebas

### 8.1 Validar ontologías con razonadores (Protégé)

1. Abre tu ontología en Protégé.
2. Menú de **Reasoner** (Razonador).
3. Selecciona uno (por ejemplo, HermiT o Pellet).
4. Inícialo:
   - Detectará inconsistencias, por ejemplo:
     - Un individuo que pertenece a dos clases definidas como disjuntas.
   - Inferirá tipos adicionales según tus axiomas (subclases, restricciones, etc.).

### 8.2 Errores comunes en RDF para principiantes

- URIs con espacios o caracteres extraños.
- Prefijos mal definidos (por ejemplo, `dc:` que apunta a un IRI incorrecto).
- Tipos de datos incompatibles (pones una fecha pero no respetas el formato `YYYY-MM-DD`).
- JSON-LD sin `@context` o con contexto incorrecto.

### 8.3 Herramientas de validación online

- JSON-LD Playground: <https://json-ld.org/playground/>
  - Pega tu JSON-LD.
  - Verás la versión expandida/compacta y posibles errores.
- Validadores RDF (busca “RDF validator online”).
- SHACL o ShEx (más avanzado) para validar si tus datos siguen ciertas formas.

Interpretación de errores para principiantes:

- Si ves errores de sintaxis:
  - Revisa comillas, puntos, comas, prefijos.
- Si ves errores de razonamiento:
  - Revisa tus axiomas (clases disjuntas, dominios y rangos) y asegúrate de que las instancias tienen sentido.

---

## 9. Proyecto práctico final: Base de conocimiento de películas

### 9.1 Objetivo

Crear una pequeña base de conocimiento sobre películas, directores y actores que incluya:

- Ontología básica.
- Datos RDF cargados en un triplestore.
- Consultas SPARQL.
- Algo de marcado JSON-LD en HTML.

### 9.2 Paso 1: Diseño de la ontología

Clases:

- `Pelicula`
- `Director`
- `Actor`
- `Genero`

Propiedades de objeto:

- `tieneDirector` (Pelicula → Director)
- `tieneActor` (Pelicula → Actor)
- `tieneGenero` (Pelicula → Genero)

Propiedades de datos:

- `titulo` (texto).
- `anioEstreno` (año).
- `nombre` (para personas y género).

Puedes definir todo esto en Protégé, igual que en el ejemplo de libros.

### 9.3 Paso 2: Crear instancias

Ejemplo:

- Película: “Inception”
  - Director: “Christopher Nolan”
  - Actores: “Leonardo DiCaprio”, “Joseph Gordon-Levitt”.
  - Género: “Ciencia ficción”.

En Protégé:

1. Ve a la pestaña **Individuals** (individuos).
2. Crea un individuo `Pelicula_Inception` de tipo `Pelicula`.
3. Rellena sus propiedades (`titulo`, `anioEstreno`, etc.).
4. Crea individuos para `Director` y `Actor` y enlázalos con `tieneDirector` y `tieneActor`.

### 9.4 Paso 3: Exportar a RDF (Turtle)

1. En Protégé, exporta la ontología con individuos a Turtle (`.ttl`).
2. Guarda el archivo, por ejemplo, `peliculas.ttl`.

### 9.5 Paso 4: Cargar en un triplestore

1. Abre Fuseki o GraphDB.
2. Crea un repositorio/dataset nuevo (por ejemplo, `peliculas`).
3. Sube `peliculas.ttl`.

### 9.6 Paso 5: Consultas SPARQL de ejemplo

**Todas las películas y su año**

```sparql
PREFIX cine: <http://example.org/cine#>

SELECT ?pelicula ?titulo ?anio
WHERE {
  ?pelicula a cine:Pelicula ;
            cine:titulo ?titulo ;
            cine:anioEstreno ?anio .
}
```

**Películas de un director concreto**

```sparql
PREFIX cine: <http://example.org/cine#>

SELECT ?pelicula ?titulo
WHERE {
  ?pelicula a cine:Pelicula ;
            cine:titulo ?titulo ;
            cine:tieneDirector ?director .
  ?director cine:nombre "Christopher Nolan" .
}
```

### 9.7 Paso 6: HTML + JSON-LD

Ejemplo de página HTML para “Inception”:

```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Inception</title>
  <script type="application/ld+json">
  {
    "@context": "https://schema.org",
    "@type": "Movie",
    "name": "Inception",
    "director": {
      "@type": "Person",
      "name": "Christopher Nolan"
    },
    "actor": [
      { "@type": "Person", "name": "Leonardo DiCaprio" },
      { "@type": "Person", "name": "Joseph Gordon-Levitt" }
    ],
    "datePublished": "2010"
  }
  </script>
</head>
<body>
  <h1>Inception</h1>
  <p>Director: Christopher Nolan</p>
  <p>Año: 2010</p>
</body>
</html>
```

### 9.8 Checklist de verificación

- [ ] Tienes una ontología con clases y propiedades definidas (Pelicula, Director, Actor, Genero).
- [ ] Has creado al menos 5 películas, con sus directores, actores y géneros.
- [ ] Has exportado los datos a un archivo RDF (Turtle).
- [ ] Has cargado el archivo en un triplestore (Fuseki, GraphDB).
- [ ] Puedes ejecutar al menos 5 consultas SPARQL y entender los resultados.
- [ ] Tienes al menos una página HTML con JSON-LD que valida correctamente en la herramienta de datos estructurados de Google.

### 9.9 Criterios de éxito

- Puedes explicar con tus palabras:
  - Qué es RDF.
  - Qué es una ontología.
  - Qué es SPARQL.
- Puedes:
  - Crear tripletas simples.
  - Cargar datos en un triplestore.
  - Escribir varias consultas SPARQL básicas.
- Tienes un mini proyecto publicado (aunque sea en local) que integra:
  - Ontología.
  - Datos RDF.
  - Consultas SPARQL.
  - HTML con JSON-LD.


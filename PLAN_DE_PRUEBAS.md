# Plan de Pruebas - Sistema de Gestión de Inventario

## Información General
- **Proyecto:** Sistema de Gestión de Inventario
- **Versión:** 1.0.0
- **Fecha:** 2025-12-03
- **Responsable:** Equipo de Desarrollo

## Objetivo
Validar el correcto funcionamiento del sistema de gestión de inventario, incluyendo la API REST, la interfaz gráfica, la base de datos y la integración entre todos los componentes.

---

## Casos de Prueba

### 1. PRUEBAS UNITARIAS

#### PU-001: Crear Categoría (Servicio)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el servicio CategoryService crea correctamente una categoría
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Crear un CategoryRequestDTO con nombre "Electronics"
  2. Mockear el repositorio para simular que no existe una categoría con ese nombre
  3. Llamar al método createCategory del servicio
  4. Verificar que se llama al método save del repositorio
- **Resultado Esperado:** La categoría se crea exitosamente y se retorna un CategoryDTO
- **Resultado Obtenido:** ✅ PASS

#### PU-002: Prevenir Categorías Duplicadas (Servicio)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el servicio lanza excepción cuando se intenta crear una categoría con nombre duplicado
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el repositorio para simular que ya existe una categoría con el nombre
  2. Intentar crear una categoría con el mismo nombre
  3. Verificar que se lanza DuplicateResourceException
- **Resultado Esperado:** Se lanza DuplicateResourceException
- **Resultado Obtenido:** ✅ PASS

#### PU-003: Obtener Categoría por ID (Servicio)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el servicio obtiene correctamente una categoría por ID
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el repositorio para retornar una categoría
  2. Llamar al método getCategoryById
  3. Verificar que se retorna el DTO correcto
- **Resultado Esperado:** Se retorna la categoría correctamente
- **Resultado Obtenido:** ✅ PASS

#### PU-004: Categoría No Encontrada (Servicio)
- **Tipo:** Unitaria
- **Descripción:** Verificar que se lanza excepción cuando no se encuentra una categoría
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el repositorio para retornar Optional.empty()
  2. Llamar al método getCategoryById
  3. Verificar que se lanza ResourceNotFoundException
- **Resultado Esperado:** Se lanza ResourceNotFoundException
- **Resultado Obtenido:** ✅ PASS

#### PU-005: Crear Producto (Servicio)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el servicio ProductService crea correctamente un producto
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear CategoryService para retornar una categoría válida
  2. Crear un ProductRequestDTO con datos válidos
  3. Llamar al método createProduct
  4. Verificar que se llama al método save del repositorio
- **Resultado Esperado:** El producto se crea exitosamente
- **Resultado Obtenido:** ✅ PASS

#### PU-006: Buscar Productos por Nombre (Servicio)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el servicio busca productos por nombre correctamente
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el repositorio para retornar productos que coincidan
  2. Llamar al método searchProductsByName
  3. Verificar que se retornan los productos correctos
- **Resultado Esperado:** Se retornan los productos que coinciden con la búsqueda
- **Resultado Obtenido:** ✅ PASS

#### PU-007: Endpoint GET /api/categories (Controlador)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el endpoint retorna todas las categorías
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el servicio para retornar lista de categorías
  2. Realizar petición GET a /api/categories
  3. Verificar status 200 y que retorna JSON con las categorías
- **Resultado Esperado:** Status 200 con lista de categorías en JSON
- **Resultado Obtenido:** ✅ PASS

#### PU-008: Endpoint POST /api/categories con Validación (Controlador)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el endpoint valida los datos de entrada
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Enviar petición POST con nombre vacío
  2. Verificar que retorna status 400
  3. Verificar que no se llama al servicio
- **Resultado Esperado:** Status 400 Bad Request
- **Resultado Obtenido:** ✅ PASS

#### PU-009: Endpoint GET /api/products (Controlador)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el endpoint retorna todos los productos
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el servicio para retornar lista de productos
  2. Realizar petición GET a /api/products
  3. Verificar status 200 y JSON correcto
- **Resultado Esperado:** Status 200 con lista de productos
- **Resultado Obtenido:** ✅ PASS

#### PU-010: Endpoint DELETE /api/products/{id} (Controlador)
- **Tipo:** Unitaria
- **Descripción:** Verificar que el endpoint elimina un producto correctamente
- **Prerrequisitos:** Ninguno
- **Pasos:**
  1. Mockear el servicio deleteProduct
  2. Realizar petición DELETE a /api/products/1
  3. Verificar status 204 No Content
- **Resultado Esperado:** Status 204 No Content
- **Resultado Obtenido:** ✅ PASS

---

### 2. PRUEBAS DE INTEGRACIÓN

#### PI-001: CRUD Completo de Categorías
- **Tipo:** Integración
- **Descripción:** Verificar el flujo completo de creación, lectura, actualización y eliminación de categorías con base de datos
- **Prerrequisitos:** Base de datos H2 en memoria configurada
- **Pasos:**
  1. Crear una categoría vía POST /api/categories
  2. Leer la categoría vía GET /api/categories/{id}
  3. Actualizar la categoría vía PUT /api/categories/{id}
  4. Eliminar la categoría vía DELETE /api/categories/{id}
  5. Verificar que ya no existe vía GET /api/categories/{id}
- **Resultado Esperado:** Todas las operaciones se ejecutan correctamente con la base de datos
- **Resultado Obtenido:** ✅ PASS

#### PI-002: Prevenir Nombres Duplicados en Categorías
- **Tipo:** Integración
- **Descripción:** Verificar que la base de datos y API previenen categorías con nombres duplicados
- **Prerrequisitos:** Base de datos H2 en memoria
- **Pasos:**
  1. Crear categoría "Duplicate Test"
  2. Intentar crear otra categoría con el mismo nombre
  3. Verificar que retorna status 409 Conflict
- **Resultado Esperado:** Status 409 Conflict en segundo intento
- **Resultado Obtenido:** ✅ PASS

#### PI-003: Validación de Datos de Categoría
- **Tipo:** Integración
- **Descripción:** Verificar que las validaciones se aplican correctamente en toda la capa
- **Prerrequisitos:** Base de datos H2 en memoria
- **Pasos:**
  1. Intentar crear categoría con nombre vacío
  2. Intentar crear categoría con nombre de 1 carácter
  3. Verificar que ambos casos retornan 400 Bad Request
- **Resultado Esperado:** Status 400 Bad Request en ambos casos
- **Resultado Obtenido:** ✅ PASS

#### PI-004: CRUD Completo de Productos
- **Tipo:** Integración
- **Descripción:** Verificar el flujo completo de operaciones con productos y base de datos
- **Prerrequisitos:** Base de datos H2 en memoria, categoría creada
- **Pasos:**
  1. Crear un producto vía POST /api/products
  2. Leer el producto vía GET /api/products/{id}
  3. Actualizar el producto vía PUT /api/products/{id}
  4. Eliminar el producto vía DELETE /api/products/{id}
  5. Verificar que ya no existe
- **Resultado Esperado:** Todas las operaciones se ejecutan correctamente
- **Resultado Obtenido:** ✅ PASS

#### PI-005: Validación de Precio y Stock
- **Tipo:** Integración
- **Descripción:** Verificar que se validan correctamente precio y stock negativos
- **Prerrequisitos:** Base de datos H2 en memoria, categoría creada
- **Pasos:**
  1. Intentar crear producto con precio negativo
  2. Intentar crear producto con stock negativo
  3. Intentar crear producto sin datos requeridos
  4. Verificar que todos retornan 400 Bad Request
- **Resultado Esperado:** Status 400 en todos los casos
- **Resultado Obtenido:** ✅ PASS

#### PI-006: Filtrar Productos por Categoría
- **Tipo:** Integración
- **Descripción:** Verificar que el filtrado por categoría funciona correctamente
- **Prerrequisitos:** Base de datos H2 en memoria, categoría y producto creados
- **Pasos:**
  1. Crear categoría y producto asociado
  2. Realizar GET /api/products?categoryId={id}
  3. Verificar que solo retorna productos de esa categoría
- **Resultado Esperado:** Solo productos de la categoría especificada
- **Resultado Obtenido:** ✅ PASS

#### PI-007: Buscar Productos por Nombre
- **Tipo:** Integración
- **Descripción:** Verificar la funcionalidad de búsqueda de productos
- **Prerrequisitos:** Base de datos H2 en memoria, productos creados
- **Pasos:**
  1. Crear producto con nombre "Searchable Laptop"
  2. Realizar GET /api/products?search=Searchable
  3. Verificar que retorna el producto
- **Resultado Esperado:** Se retorna el producto que coincide con la búsqueda
- **Resultado Obtenido:** ✅ PASS

#### PI-008: Producto con Categoría Inexistente
- **Tipo:** Integración
- **Descripción:** Verificar que no se puede crear producto con categoría que no existe
- **Prerrequisitos:** Base de datos H2 en memoria
- **Pasos:**
  1. Intentar crear producto con categoryId=9999 (no existe)
  2. Verificar que retorna 404 Not Found
- **Resultado Esperado:** Status 404 Not Found
- **Resultado Obtenido:** ✅ PASS

---

### 3. PRUEBAS E2E (End-to-End)

#### PE-001: Cargar Página Principal
- **Tipo:** E2E
- **Descripción:** Verificar que la aplicación carga correctamente en el navegador
- **Prerrequisitos:** Aplicación ejecutándose en puerto configurado
- **Pasos:**
  1. Abrir navegador (Chrome headless)
  2. Navegar a http://localhost:{port}
  3. Verificar que se muestra el título "Sistema de Gestión de Inventario"
- **Resultado Esperado:** Página carga correctamente con título visible
- **Resultado Obtenido:** ✅ PASS

#### PE-002: Crear Categoría desde UI
- **Tipo:** E2E
- **Descripción:** Verificar que se puede crear una categoría desde la interfaz web
- **Prerrequisitos:** Aplicación ejecutándose
- **Pasos:**
  1. Abrir la aplicación en el navegador
  2. Localizar campo de nombre de categoría
  3. Ingresar "E2E Test Category"
  4. Click en botón "Crear Categoría"
  5. Verificar notificación de éxito
  6. Verificar que la categoría aparece en la lista
- **Resultado Esperado:** Categoría creada y visible en la lista
- **Resultado Obtenido:** ✅ PASS

#### PE-003: Flujo Completo - Crear Categoría, Crear Producto, Visualizar Producto
- **Tipo:** E2E
- **Descripción:** Automatización del flujo completo requerido en el proyecto
- **Prerrequisitos:** Aplicación ejecutándose
- **Pasos:**
  1. Crear categoría "Complete Flow Category"
  2. Cambiar a pestaña de productos
  3. Crear producto "Complete Flow Product" con precio $299.99, stock 25
  4. Seleccionar la categoría creada
  5. Verificar que el producto aparece en el listado
  6. Verificar que se muestran todos los detalles (nombre, precio, categoría, stock)
- **Resultado Esperado:** Flujo completo se ejecuta sin errores, producto visible con todos sus datos
- **Resultado Obtenido:** ✅ PASS

#### PE-004: Crear Producto y Verificar en Lista
- **Tipo:** E2E
- **Descripción:** Verificar que un producto creado se visualiza correctamente
- **Prerrequisitos:** Aplicación ejecutándose
- **Pasos:**
  1. Crear categoría "Electronics E2E"
  2. Cambiar a pestaña productos
  3. Crear producto "E2E Test Laptop" con precio $1499.99, stock 15
  4. Verificar que aparece en la lista de productos
  5. Verificar que muestra nombre y precio correctos
- **Resultado Esperado:** Producto visible con información correcta
- **Resultado Obtenido:** ✅ PASS

#### PE-005: Navegación entre Pestañas
- **Tipo:** E2E
- **Descripción:** Verificar que la navegación entre categorías y productos funciona
- **Prerrequisitos:** Aplicación ejecutándose
- **Pasos:**
  1. Abrir aplicación
  2. Click en pestaña "Productos"
  3. Verificar que se activa correctamente
  4. Click en pestaña "Categorías"
  5. Verificar que se activa correctamente
- **Resultado Esperado:** Navegación fluida entre pestañas
- **Resultado Obtenido:** ✅ PASS

---

### 4. PRUEBAS DE ANÁLISIS ESTÁTICO

#### PA-001: Checkstyle - Verificar Estándares de Código
- **Tipo:** Análisis Estático
- **Descripción:** Verificar que el código cumple con los estándares de Checkstyle
- **Prerrequisitos:** Configuración de Checkstyle en build.gradle.kts
- **Pasos:**
  1. Ejecutar `./gradlew checkstyleMain`
  2. Verificar que no hay violaciones críticas
- **Resultado Esperado:** Sin violaciones que impidan el build
- **Resultado Obtenido:** ✅ PASS

#### PA-002: PMD - Detectar Problemas de Código
- **Tipo:** Análisis Estático
- **Descripción:** Verificar que PMD no encuentra problemas críticos
- **Prerrequisitos:** Configuración de PMD en build.gradle.kts
- **Pasos:**
  1. Ejecutar `./gradlew pmdMain`
  2. Revisar reporte de PMD
  3. Verificar que no hay violaciones críticas
- **Resultado Esperado:** Sin problemas críticos detectados
- **Resultado Obtenido:** ✅ PASS

#### PA-003: SpotBugs - Detectar Bugs Potenciales
- **Tipo:** Análisis Estático
- **Descripción:** Verificar que SpotBugs no encuentra bugs potenciales
- **Prerrequisitos:** Configuración de SpotBugs en build.gradle.kts
- **Pasos:**
  1. Ejecutar `./gradlew spotbugsMain`
  2. Revisar reporte HTML generado
  3. Verificar que no hay bugs críticos
- **Resultado Esperado:** Sin bugs críticos detectados
- **Resultado Obtenido:** ✅ PASS

---

## Resumen de Ejecución

| Tipo de Prueba | Total | Exitosas | Fallidas | Cobertura |
|----------------|-------|----------|----------|-----------|
| Unitarias | 10 | 10 | 0 | 100% |
| Integración | 8 | 8 | 0 | 100% |
| E2E | 5 | 5 | 0 | 100% |
| Análisis Estático | 3 | 3 | 0 | 100% |
| **TOTAL** | **26** | **26** | **0** | **100%** |

## Conclusiones

1. Todas las pruebas unitarias pasan exitosamente, validando la lógica de negocio de servicios y controladores.
2. Las pruebas de integración confirman que la API funciona correctamente con la base de datos.
3. Las pruebas E2E validan el flujo completo requerido: crear categoría → crear producto → visualizar producto.
4. El análisis estático confirma que el código cumple con estándares de calidad.
5. El sistema está listo para producción.

## Comandos para Ejecutar Pruebas

```bash
# Todas las pruebas
./gradlew test

# Solo pruebas unitarias
./gradlew test --tests "*Test"

# Solo pruebas de integración
./gradlew test --tests "*IntegrationTest"

# Solo pruebas E2E
./gradlew test --tests "*E2ETest"

# Análisis estático completo
./gradlew check

# Checkstyle
./gradlew checkstyleMain

# PMD
./gradlew pmdMain

# SpotBugs
./gradlew spotbugsMain
```

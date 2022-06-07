# userws
Rest api para creacion de usuarios y atenticacion con JWT -spring boot - swagger

#pruebas con swagger en Autentificación Auth Rest Controller

http://localhost:8080/swagger-ui/index.html

-/auth/signup
creacion de usuarios y otencion de token de acceso(El usuario se crea con el rol USER)

-/auth/signin
iniciar sesión

-Autorizacion
En la parte superior, sobre el botón "Authorize", de click y en el campo "Value" escriba la pabara "Bearer", + un espacio " " y pegue el token devuelto al crear usuario o al iniciar sesión, finalmente de click en "Authorize"

#Pruebas de roles en Prueba Test Rest Controller

-puede probar el acceso a las rutas
/test/all allAccess->sin autenticacion
/test/user userAccess ->usuario creado 
las demas rutas no pueden accederse debido a que no tienen el rol

#Base de datos H2 en memoria
http://localhost:8080/h2-ui

en el campo JDBC URL poner el valor jdbc:h2:mem:testdb
JDBC URL:	jdbc:h2:mem:testdb

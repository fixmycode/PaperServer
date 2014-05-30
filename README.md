PaperServer
===========

Servidor de mensajería sobre TCP (Tarea de Redes de Computadores, 1er Semestre, UTFSM)

### Protocolo

El protocolo PP es un diálogo de mensajería, permite emitir y recibir mensajes con el mismo protocolo. También se
encarga del envío de archivos a través del servidor. A diferencia de otros protocolos como XMPP, un servidor Paper solo
se comunica con clientes y no con otros servidores.

### Enviar y rescatar mensajes

Para rescatar los mensajes nuevos, el cliente envía una llamada PULL al servidor, que responde OK, la cantidad de
mensajes y una lista. Cada mensaje de la lista tiene un identificador, su largo en bytes, la fecha y el mensaje.

    C: PULL 200.1.123.3:6000
    S: OK 2 messages 0 files
    S: 1 45 2014-05-29T10:41:24Z Listo para aparecer en el diálogo de prueba?
    S: 2 33 2014-05-29T10:41:36Z Recuerda el formato de la fecha!!

Para enviar un mensaje, el cliente tiene que usar la llamada PUSH, indicar el destinatario, el largo en bytes del
mensaje, seguido del contenido del mensaje. El servidor responderá OK seguido por el identificador y la fecha en que se
le dio, indicando que el mensaje fue almacenado con éxito.

    C: PUSH 200.1.123.3:6000 50 Lo tendré en consideración, siempre se me olvida
    S: OK 3 2014-05-29T10:42:00Z

Los mensajes permanencen en el servidor durante la ejecución del programa o hasta que se hayan enviado correctamente,
hay que considerar de que el servidor no necesita saber a priori que un contacto existe o no, y responderá con los
mensajes a quien lo solicite de la forma correcta con una IP y puerto de origen que corresponda.

El servidor nunca desconoce la existencia de un contacto, así es que solicitar una lista de mensajes para un usuario que
no existe no desencadena un error, solamente indicará que no hay mensajes.

    C: PULL 201.4.200.1:5656
    S: OK 0 messages 0 files

### Enviar y recibir archivos

Cuando el cliente quiere mandar un archivo, lo hace enviado la llamada FILE, indicando el destinatario, un nombre para
el archivo, que no puede tener espacios, el largo en bytes y el contenido del archivo como una cadena de bytes. El
servidor responderá igual como si se enviara un mensaje.

    C: FILE 200.1.123.3:6000 esquema.jpg 451450 /* bytes */
    S: OK 4 2014-05-29T10:45:32Z

El destinatario será notificado del nuevo archivo cuando haga una llamada PULL por más mensajes de la siguiente forma:

    C: PULL 200.1.123.101:5050
    S: OK 1 messages 1 files
    S: 4 38 2014-05-29T10:44:34Z Te envío el esquema de almacenamiento
    S: 1 11 2014-05-29T10:45:32Z esquema.jpg

Notar que en este caso ocurren tres cosas:

1. A través de la llamada PULL solo se envía una notificación que contiene el nombre del archivo, estas notificaciones
aparecerán siempre **al final** de la lista de mensajes.
2. El identificador del archivo no es correlativo a los identificadores de los mensajes.
3. El largo en bytes corresponde al largo del nombre del archivo.

Cuando el destinatario quiera obtener el archivo, tiene que hacer una llamada GETF indicando solamente el identificador
del archivo. El servidor responderá OK, el largo del nombre del archivo, el nombre del archivo, el largo del archivo y
la cadena de bytes correspondiente.

    C: GETF 1
    S: OK 11 esquema.jpg 451450 /* bytes */

El archivo permanecerá en el servidor durante la ejecución del servicio o hasta que se haya registrado un envío exitoso
de la información.

### Errores

El servidor responderá con un mensaje de error en el caso de detectarse. Los errores imitan de alguna forma los códigos
HTTP, algunos ejemplos a continuación:

El archivo 3 no existe, el servidor devuelve archivo no encontrado.

    C: GETF 3
    S: ERRN 404

No se ha especificado el largo del contenido, el servidor responde que falta el largo.

    C: PUSH 200.1.123.3:6000 Hola
    S: ERRN 411

El archivo 1 existe pero no está asociado a la IP que lo solicita, el servidor responde que el cliente no tiene
autorización.

    C: GETF 1
    S: ERRN 403

Otros errores también son aplicables como el `400` en el caso de algún error en el mensaje del cliente, pero para
cualquier error no especificado el servidor responderá con el código `500`.

Una lista completa de la gramática de cada instrucción y los códigos de error será agregada al Wiki en el futuro.

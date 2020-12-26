/* Populate tables */
INSERT INTO usuarios (id, bloqueado, email, es_admin, nombre, password, fecha_nacimiento) VALUES('1', 0, 'ana.garcia@gmail.com', 0, 'Ana García', '12345678', '2001-02-10');
INSERT INTO usuarios (id, bloqueado, email, es_admin, nombre, password, fecha_nacimiento) VALUES('2', 1, 'victor@gmail.com', 0, 'victor', '12345678', '2001-02-10');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id) VALUES('1', 'Lavar coche','Hay que dejar bien lavado', 0, 0,'2020-12-03', '2020-12-05', '1');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id) VALUES('2', 'Renovar DNI','Pedir cita para el dni', 0, 0,'2020-12-03', '2020-12-05', '1');
INSERT INTO equipos (id, nombre, administrador_id) VALUES('1', 'Proyecto P1','1');
INSERT INTO equipos (id, nombre, administrador_id) VALUES('2', 'Proyecto P3', '1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '1');

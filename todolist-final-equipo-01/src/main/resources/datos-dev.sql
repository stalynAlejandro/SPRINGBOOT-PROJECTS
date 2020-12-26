/* Populate tables */
INSERT INTO usuarios (id, bloqueado, email, es_admin, nombre, password, fecha_nacimiento) VALUES('1', 0, 'domingo@ua', 0, 'Domingo Gallardo', '123', '2001-02-10');
INSERT INTO usuarios (id, bloqueado, email, es_admin, nombre, password, fecha_nacimiento) VALUES('2', 0, 'vnm12@alu.ua.es', 0, 'Victor Navarro', '123', '2001-02-10');
INSERT INTO usuarios (id, bloqueado, email, es_admin, nombre, password, fecha_nacimiento) VALUES('3', 0, 'mbmr4@alu.ua.es', 0, 'María Belen', '123', '2001-02-10');
INSERT INTO usuarios (id, bloqueado, email, es_admin, nombre, password, fecha_nacimiento) VALUES('4', 0, 'saav@g.com', 1, 'saav', '123', '2001-02-10');

INSERT INTO equipos (id, nombre, administrador_id) VALUES('1', 'Proyecto P1', '1');
INSERT INTO equipos (id, nombre, administrador_id) VALUES('2', 'Proyecto P3', '1');

INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('2', '1');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '2');
INSERT INTO equipo_usuario (fk_equipo, fk_usuario) VALUES('1', '4');

INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id) VALUES('1', 'Lavar coche','Hay que dejar bien lavado', 0, 0,'2020-12-03', '2020-12-05', '1');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id) VALUES('2', 'Renovar DNI','Pedir cita para el dni', 0, 0,'2020-12-03', '2020-12-05', '1');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id, asignado_id, equipo_id) VALUES('3', 'Ejercicio Tema 3', 'Hacer los ejercicios del tema 3 y estudiar', 0, 0,'2020-12-19', '2020-12-05', '1', '1', '1');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id, asignado_id, equipo_id) VALUES('4', 'Pedir Apuntes Fisica', 'Resumir apuntes y estudiar para el examen', 0, 0,  '2020-12-19', '2020-12-05', '1', '1', '1');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id, asignado_id, equipo_id) VALUES('5', 'Tarea ', 'Hacer los ejercicios del tema 3 y estudiar', 0, 0,'2020-12-20', '2020-12-05', '1', '1', '2');
INSERT INTO tareas (id, titulo, descripcion, finalizada, duracion, fecha_inicio, fecha_fin, usuario_id, asignado_id, equipo_id) VALUES('6', 'Mas Tareas', 'Resumir apuntes y estudiar para el examen', 0, 0, '2020-12-20', '2020-12-05', '1', '1', '2');

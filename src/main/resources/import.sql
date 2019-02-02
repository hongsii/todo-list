INSERT INTO task (id, created_date, modified_date, content, is_completed) VALUES(1, current_timestamp(), current_timestamp(), '집안일', false)
INSERT INTO task (id, created_date, modified_date, content, is_completed) VALUES(2, current_timestamp(), current_timestamp(), '빨래', false)
INSERT INTO task (id, created_date, modified_date, content, is_completed) VALUES(3, current_timestamp(), current_timestamp(), '청소', false)
INSERT INTO task (id, created_date, modified_date, content, is_completed) VALUES(4, current_timestamp(), current_timestamp(), '방청소', false)

INSERT INTO task_relation(super_task_id, sub_task_id) VALUES(1, 2);
INSERT INTO task_relation(super_task_id, sub_task_id) VALUES(1, 3);
INSERT INTO task_relation(super_task_id, sub_task_id) VALUES(1, 4);
INSERT INTO task_relation(super_task_id, sub_task_id) VALUES(3, 4);


INSERT INTO auth.authschema.p_auth
(is_deleted, created_at, created_by, deleted_at, deleted_by, updated_at, updated_by, id, password, role, user_uuid,
 username)
VALUES (false, now(), '00000000-0000-0000-0000-000000000001', null, null, now(), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001', '$2a$10$uz2FlOHNfUN1W2Ob1Z3TGuYuaNYSNLoXXv7cQM1r11UAUunjvWD3W',
        'MASTER', 'cf151637-803c-4066-b0f7-c100d552bd5e', 'TestManager');
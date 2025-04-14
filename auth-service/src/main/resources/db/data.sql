INSERT INTO auth.authschema.p_auth
(is_deleted, created_at, created_by, deleted_at, deleted_by, updated_at, updated_by, id, password, role, user_uuid,
 username)
VALUES (false, now(), '00000000-0000-0000-0000-000000000001', null, null, now(), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001', '$2a$10$XHDJUbAcNlhc7kDEQl9/vOViOQ0S/JGaWoUaB421EnIpQbRRmAh2C',
        'MASTER', 'cf151637-803c-4066-b0f7-c100d552bd5e', 'TestMaster'),
       (false, now(), '00000000-0000-0000-0000-000000000001', null, null, now(), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000002', '$2a$10$npp6UyRWD2CdPoNpDbwEveB.wGXutiJpnXqgOWHEu7aoShlMhXvhi',
        'COMPANY', 'bacb9e60-d163-4595-9b6c-fe61911eeba6', 'TestCompany'),
       (false, now(), '00000000-0000-0000-0000-000000000001', null, null, now(), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000003', '$2a$10$ESqe8/OWHWPEfYAuUKUfb.qQnkLOzArtwcsesoYXX.YO5MPOfnd4W',
        'NORMAL', '44e88a08-9728-4be8-93dd-2e3cd092b1ff', 'TestNormal');
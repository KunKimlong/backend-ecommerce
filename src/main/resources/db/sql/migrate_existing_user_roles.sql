-- ============================================================
-- v1.3.0 RBAC: Migrate existing users to user_roles table
-- Maps users.role (ADMIN/EMPLOYEE) to the new roles system
-- ============================================================

-- Migrate ADMIN users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.role = 'ADMIN'
  AND r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- Migrate EMPLOYEE users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.role = 'EMPLOYEE'
  AND r.name = 'EMPLOYEE'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

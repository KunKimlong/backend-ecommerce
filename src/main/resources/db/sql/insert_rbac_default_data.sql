-- ============================================================
-- v1.3.0 RBAC: Seed default roles, permissions, and mappings
-- ============================================================

-- Insert default roles
INSERT INTO roles (name, description, created_at)
VALUES
  ('SUPER_ADMIN', 'Full system access including role and permission management', NOW()),
  ('ADMIN',       'Full module CRUD access without role management',             NOW()),
  ('MANAGER',     'Can manage products, categories, banners, and options',       NOW()),
  ('EMPLOYEE',    'Read-only access to products, categories, banners, options',  NOW())
ON CONFLICT (name) DO NOTHING;

-- Insert all granular permissions
INSERT INTO permissions (name, description, module, created_at)
VALUES
  -- Product
  ('product:create', 'Create products',           'PRODUCT',      NOW()),
  ('product:read',   'View products',             'PRODUCT',      NOW()),
  ('product:update', 'Update products',           'PRODUCT',      NOW()),
  ('product:delete', 'Delete products',           'PRODUCT',      NOW()),
  -- Category
  ('category:create', 'Create categories',        'CATEGORY',     NOW()),
  ('category:read',   'View categories',          'CATEGORY',     NOW()),
  ('category:update', 'Update categories',        'CATEGORY',     NOW()),
  ('category:delete', 'Delete categories',        'CATEGORY',     NOW()),
  -- Employee
  ('employee:create', 'Create employees',         'EMPLOYEE',     NOW()),
  ('employee:read',   'View employees',           'EMPLOYEE',     NOW()),
  ('employee:update', 'Update employees',         'EMPLOYEE',     NOW()),
  ('employee:delete', 'Delete employees',         'EMPLOYEE',     NOW()),
  -- Banner
  ('banner:create', 'Create banners',             'BANNER',       NOW()),
  ('banner:read',   'View banners',               'BANNER',       NOW()),
  ('banner:update', 'Update banners',             'BANNER',       NOW()),
  ('banner:delete', 'Delete banners',             'BANNER',       NOW()),
  -- Banner Type
  ('banner_type:create', 'Create banner types',   'BANNER_TYPE',  NOW()),
  ('banner_type:read',   'View banner types',     'BANNER_TYPE',  NOW()),
  ('banner_type:update', 'Update banner types',   'BANNER_TYPE',  NOW()),
  ('banner_type:delete', 'Delete banner types',   'BANNER_TYPE',  NOW()),
  -- Option
  ('option:create', 'Create options',             'OPTION',       NOW()),
  ('option:read',   'View options',               'OPTION',       NOW()),
  ('option:update', 'Update options',             'OPTION',       NOW()),
  ('option:delete', 'Delete options',             'OPTION',       NOW()),
  -- Option Value
  ('option_value:create', 'Create option values', 'OPTION_VALUE', NOW()),
  ('option_value:read',   'View option values',   'OPTION_VALUE', NOW()),
  ('option_value:update', 'Update option values', 'OPTION_VALUE', NOW()),
  ('option_value:delete', 'Delete option values', 'OPTION_VALUE', NOW()),
  -- Asset
  ('asset:create', 'Upload assets',               'ASSET',        NOW()),
  ('asset:read',   'View assets',                 'ASSET',        NOW()),
  ('asset:update', 'Update assets',               'ASSET',        NOW()),
  ('asset:delete', 'Delete assets',               'ASSET',        NOW()),
  -- Role
  ('role:create', 'Create roles',                 'ROLE',         NOW()),
  ('role:read',   'View roles',                   'ROLE',         NOW()),
  ('role:update', 'Update roles',                 'ROLE',         NOW()),
  ('role:delete', 'Delete roles',                 'ROLE',         NOW()),
  -- User
  ('user:read',   'View users',                   'USER',         NOW()),
  -- Admin
  ('admin:access','Access admin panel',           'ADMIN',        NOW())
ON CONFLICT (name) DO NOTHING;

-- ============================================================
-- Role-Permission Mappings
-- ============================================================

-- SUPER_ADMIN -> ALL permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- ADMIN -> ALL permissions EXCEPT role management
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name NOT IN ('role:create', 'role:update', 'role:delete')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- MANAGER -> product, category, banner, banner_type, option, option_value, asset CRUD
--           + employee:read, user:read, admin:access
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'MANAGER'
  AND (
       p.name LIKE 'product:%'
    OR p.name LIKE 'category:%'
    OR p.name LIKE 'banner:%'
    OR p.name LIKE 'banner_type:%'
    OR p.name LIKE 'option:%'
    OR p.name LIKE 'option_value:%'
    OR p.name LIKE 'asset:%'
    OR p.name IN ('employee:read', 'user:read', 'admin:access')
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- EMPLOYEE -> read-only permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'EMPLOYEE'
  AND (
       p.name = 'product:read'
    OR p.name = 'category:read'
    OR p.name = 'banner:read'
    OR p.name = 'banner_type:read'
    OR p.name = 'option:read'
    OR p.name = 'option_value:read'
    OR p.name = 'asset:read'
    OR p.name = 'user:read'
  )
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

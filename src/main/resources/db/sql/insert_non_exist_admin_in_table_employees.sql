INSERT INTO employees (user_id)
SELECT u.id
FROM users u
WHERE u.role = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1
    FROM employees e
    WHERE e.user_id = u.id
);

UPDATE users SET gender = 'MALE' WHERE gender IS NULL;
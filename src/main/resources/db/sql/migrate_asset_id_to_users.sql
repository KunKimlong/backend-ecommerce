UPDATE users u
SET asset_id = e.asset_id
FROM employees e
WHERE u.id = e.user_id;

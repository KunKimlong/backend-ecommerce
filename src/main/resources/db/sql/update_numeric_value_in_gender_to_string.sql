UPDATE users SET gender = 'FEMALE' WHERE gender = '1';
UPDATE users SET gender = 'MALE' WHERE gender = '0' OR gender IS NULL;
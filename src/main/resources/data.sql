INSERT INTO jobs (
    id,
    type,
    status,
    payload,
    run_at,
    attempt_count,
    max_attempts,
    locked_until,
    last_error,
    priority,
    created_at,
    updated_at
) VALUES

-- 1️⃣ CREATED job, not yet due
(
    '11111111-1111-1111-1111-111111111111',
    'SEND_EMAIL',
    'CREATED',
    JSON_OBJECT('email','user1@example.com','template','WELCOME'),
    DATE_ADD(NOW(), INTERVAL 10 MINUTE),
    0,
    5,
    NULL,
    NULL,
    5,
    NOW(),
    NOW()
),

-- 2️⃣ CREATED job, already due (scheduler should pick)
(
    '22222222-2222-2222-2222-222222222222',
    'GENERATE_PDF',
    'CREATED',
    JSON_OBJECT('documentId',42),
    DATE_SUB(NOW(), INTERVAL 1 MINUTE),
    0,
    3,
    NULL,
    NULL,
    3,
    NOW(),
    NOW()
),

-- 3️⃣ FAILED job, eligible for retry
(
    '33333333-3333-3333-3333-333333333333',
    'SEND_EMAIL',
    'FAILED',
    JSON_OBJECT('email','user2@example.com','template','RESET_PASSWORD'),
    DATE_ADD(NOW(), INTERVAL 5 MINUTE),
    1,
    5,
    NULL,
    'SMTP timeout',
    4,
    DATE_SUB(NOW(), INTERVAL 10 MINUTE),
    NOW()
),

-- 4️⃣ RUNNING job, locked by worker
(
    '44444444-4444-4444-4444-444444444444',
    'UPLOAD_FILE',
    'RUNNING',
    JSON_OBJECT('fileId',99),
    DATE_SUB(NOW(), INTERVAL 2 MINUTE),
    2,
    5,
    DATE_ADD(NOW(), INTERVAL 2 MINUTE),
    NULL,
    2,
    DATE_SUB(NOW(), INTERVAL 30 MINUTE),
    NOW()
),

-- 5️⃣ DLQ job, retries exhausted
(
    '55555555-5555-5555-5555-555555555555',
    'SEND_EMAIL',
    'DLQ',
    JSON_OBJECT('email','broken@example.com','template','WELCOME'),
    NOW(),
    5,
    5,
    NULL,
    'Invalid email format',
    1,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    NOW()
);

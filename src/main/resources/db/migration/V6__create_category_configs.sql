CREATE TABLE IF NOT EXISTS category_configs (
    id UUID PRIMARY KEY,
    category VARCHAR(80) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    description TEXT,
    icon_name VARCHAR(80),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_category_configs_category_not_deleted
    ON category_configs (category)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_category_configs_category ON category_configs (category);
CREATE INDEX IF NOT EXISTS idx_category_configs_active ON category_configs (active);
CREATE INDEX IF NOT EXISTS idx_category_configs_deleted ON category_configs (deleted);
CREATE INDEX IF NOT EXISTS idx_category_configs_sort_order ON category_configs (sort_order);

INSERT INTO category_configs (
    id,
    category,
    display_name,
    description,
    icon_name,
    active,
    sort_order,
    created_at,
    updated_at,
    deleted
)
VALUES
    (
        gen_random_uuid(),
        'BAGS',
        'Bags',
        'Backpacks, handbags, luggage, and other bags.',
        'briefcase',
        TRUE,
        10,
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'ELECTRONICS',
        'Electronics',
        'Phones, laptops, earbuds, chargers, and other electronic items.',
        'smartphone',
        TRUE,
        20,
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'DOCUMENTS',
        'Documents',
        'ID cards, passports, certificates, licenses, and other documents.',
        'file-text',
        TRUE,
        30,
        NOW(),
        NOW(),
        FALSE
    )
ON CONFLICT DO NOTHING;
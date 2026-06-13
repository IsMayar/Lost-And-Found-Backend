CREATE TABLE IF NOT EXISTS report_images (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    url VARCHAR(500) NOT NULL,
    original_name VARCHAR(255),
    content_type VARCHAR(120),
    size_bytes BIGINT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    primary_image BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_report_images_report_id ON report_images (report_id);
CREATE INDEX IF NOT EXISTS idx_report_images_deleted ON report_images (deleted);
CREATE INDEX IF NOT EXISTS idx_report_images_sort_order ON report_images (sort_order);

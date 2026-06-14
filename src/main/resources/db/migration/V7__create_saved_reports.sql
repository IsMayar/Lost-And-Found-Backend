CREATE TABLE IF NOT EXISTS saved_reports (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_saved_reports_user_report_not_deleted
    ON saved_reports (user_id, report_id)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_saved_reports_user_id ON saved_reports (user_id);
CREATE INDEX IF NOT EXISTS idx_saved_reports_report_id ON saved_reports (report_id);
CREATE INDEX IF NOT EXISTS idx_saved_reports_deleted ON saved_reports (deleted);
CREATE INDEX IF NOT EXISTS idx_saved_reports_created_at ON saved_reports (created_at);
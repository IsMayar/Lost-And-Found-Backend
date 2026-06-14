CREATE TABLE IF NOT EXISTS report_contact_requests (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    requester_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(40) NOT NULL,
    message TEXT,
    response_message TEXT,
    responded_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_report_contact_requests_requester_report_not_deleted
    ON report_contact_requests (requester_id, report_id)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_report_contact_requests_report_id ON report_contact_requests (report_id);
CREATE INDEX IF NOT EXISTS idx_report_contact_requests_requester_id ON report_contact_requests (requester_id);
CREATE INDEX IF NOT EXISTS idx_report_contact_requests_owner_id ON report_contact_requests (owner_id);
CREATE INDEX IF NOT EXISTS idx_report_contact_requests_status ON report_contact_requests (status);
CREATE INDEX IF NOT EXISTS idx_report_contact_requests_deleted ON report_contact_requests (deleted);
CREATE INDEX IF NOT EXISTS idx_report_contact_requests_created_at ON report_contact_requests (created_at);
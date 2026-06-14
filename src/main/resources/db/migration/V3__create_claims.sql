CREATE TABLE IF NOT EXISTS claims (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL REFERENCES reports(id) ON DELETE CASCADE,
    claimant_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(40) NOT NULL,
    message TEXT NOT NULL,
    proof_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_claims_report_id ON claims (report_id);
CREATE INDEX IF NOT EXISTS idx_claims_claimant_id ON claims (claimant_id);
CREATE INDEX IF NOT EXISTS idx_claims_status ON claims (status);
CREATE INDEX IF NOT EXISTS idx_claims_deleted ON claims (deleted);
CREATE UNIQUE INDEX IF NOT EXISTS uq_claims_report_claimant_active
    ON claims (report_id, claimant_id)
    WHERE deleted = FALSE;

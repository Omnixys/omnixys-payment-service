-- Enum-Typ für Zahlungsstatus
CREATE TYPE IF NOT EXISTS PAYMENTSTATUS AS ENUM (
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED',
    'CANCELLED',
    'REFUNDED'
);

-- Enum-Typen für Währung und Zahlungsmethode
CREATE TYPE IF NOT EXISTS CURRENCYTYPE AS ENUM (
    'EUR',
    'USD',
    'GBP',
    'CHF',
    'JPY',
    'CNY',
    'GHS'
);

CREATE TYPE IF NOT EXISTS PAYMENTMETHOD AS ENUM (
    'CREDIT_CARD',
    'DEBIT_CARD',
    'PAYPAL',
    'APPLE_PAY',
    'GOOGLE_PAY',
    'BANK_TRANSFER',
    'BITCOIN'
);

-- Zahlungstabelle OHNE TABLESPACE (Flyway-safe)
CREATE TABLE IF NOT EXISTS payment (
                                       id UUID PRIMARY KEY,
                                       username TEXT NOT NULL,
                                       account_id UUID NOT NULL,
                                       amount DECIMAL(8,2) NOT NULL CHECK (amount > 0),
                                       currency TEXT NOT NULL,
                                       method TEXT NOT NULL,
                                       status TEXT NOT NULL,
                                       invoice_id UUID,
                                       created TIMESTAMP NOT NULL
);

-- Index auf invoice_id ohne Tablespace
CREATE INDEX IF NOT EXISTS payment_invoice_id_idx ON payment(invoice_id);

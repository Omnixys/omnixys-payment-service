-- Nur lokal ausführen!
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_tablespace WHERE spcname = 'paymentspace') THEN
        ALTER TABLE payment SET TABLESPACE paymentspace;
        ALTER INDEX payment_invoice_id_idx SET TABLESPACE paymentspace;
    END IF;
END;
$$;

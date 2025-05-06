-- psql --dbname=bitnami_keycloak --username=bn_keycloak --file=/sql/payment/create-db-payment.sql
-- psql --dbname=payment_db --username=payment_db_user --file=/sql/payment/create-schema-payment.sql

CREATE ROLE payment_db_user LOGIN PASSWORD 'GentleCorp13.03.2025';

CREATE DATABASE payment_db;

GRANT ALL ON DATABASE payment_db TO payment_db_user;

-- CREATE TABLESPACE paymentspace OWNER payment_db_user LOCATION '/Users/gentlebookpro/GentleStore/Tablespace/Payment';
CREATE TABLESPACE paymentspace OWNER payment_db_user LOCATION '/var/lib/postgresql/tablespace/payment';

DROP SCHEMA IF EXISTS customer CASCADE;

CREATE SCHEMA customer;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customer.customers(
   id uuid NOT NULL,
   username varchar(255) NOT NULL,
   first_name varchar(255) NOT NULL,
   last_name varchar(255) NOT NULL,
   CONSTRAINT customers_pkey PRIMARY KEY (id)
);

DROP MATERIALIZED VIEW IF EXISTS customer.order_customer_m_view;

CREATE MATERIALIZED VIEW customer.order_customer_m_view AS
SELECT id,
       username,
       first_name,
       last_name
FROM customer.customers
    WITH DATA;

refresh materialized view customer.order_customer_m_view;

CREATE OR REPLACE FUNCTION customer.refresh_order_customer_m_view()
RETURNS trigger
AS '
BEGIN
    REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
    RETURN NEW;
END;
'LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS refresh_order_customer_m_view ON customer.customers;

CREATE TRIGGER refresh_order_customer_m_view
AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON customer.customers
FOR EACH STATEMENT EXECUTE PROCEDURE customer.refresh_order_customer_m_view();
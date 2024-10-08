PGDMP  3    0                |            postgres    16.2    16.2                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    5    postgres    DATABASE     t   CREATE DATABASE postgres WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'ru_RU.UTF-8';
    DROP DATABASE postgres;
                postgres    false                       0    0    DATABASE postgres    COMMENT     N   COMMENT ON DATABASE postgres IS 'default administrative connection database';
                   postgres    false    3612                        2615    16398    currencyexchange    SCHEMA         CREATE SCHEMA currencyexchange;
    DROP SCHEMA currencyexchange;
                postgres    false                        3079    16384 	   adminpack 	   EXTENSION     A   CREATE EXTENSION IF NOT EXISTS adminpack WITH SCHEMA pg_catalog;
    DROP EXTENSION adminpack;
                   false                       0    0    EXTENSION adminpack    COMMENT     M   COMMENT ON EXTENSION adminpack IS 'administrative functions for PostgreSQL';
                        false    2            �            1259    16399 
   currencies    TABLE        CREATE TABLE currencyexchange.currencies (
    id bigint NOT NULL,
    code text NOT NULL,
    fullname text,
    sign text
);
 (   DROP TABLE currencyexchange.currencies;
       currencyexchange         heap    postgres    false    7            �            1259    16402    Currencies_ID_seq    SEQUENCE     �   ALTER TABLE currencyexchange.currencies ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME currencyexchange."Currencies_ID_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            currencyexchange          postgres    false    7    217            �            1259    16412    exchange_rates    TABLE     �   CREATE TABLE currencyexchange.exchange_rates (
    id bigint NOT NULL,
    base_currency_id bigint,
    target_currency_id bigint,
    rate numeric
);
 ,   DROP TABLE currencyexchange.exchange_rates;
       currencyexchange         heap    postgres    false    7            �            1259    16439    exchange_rates_id_seq    SEQUENCE     �   ALTER TABLE currencyexchange.exchange_rates ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME currencyexchange.exchange_rates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            currencyexchange          postgres    false    219    7                      0    16399 
   currencies 
   TABLE DATA           H   COPY currencyexchange.currencies (id, code, fullname, sign) FROM stdin;
    currencyexchange          postgres    false    217   �                 0    16412    exchange_rates 
   TABLE DATA           b   COPY currencyexchange.exchange_rates (id, base_currency_id, target_currency_id, rate) FROM stdin;
    currencyexchange          postgres    false    219   �                  0    0    Currencies_ID_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('currencyexchange."Currencies_ID_seq"', 18, true);
          currencyexchange          postgres    false    218                        0    0    exchange_rates_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('currencyexchange.exchange_rates_id_seq', 19, true);
          currencyexchange          postgres    false    220            {           2606    16411    currencies Currencies_Code_key 
   CONSTRAINT     e   ALTER TABLE ONLY currencyexchange.currencies
    ADD CONSTRAINT "Currencies_Code_key" UNIQUE (code);
 T   ALTER TABLE ONLY currencyexchange.currencies DROP CONSTRAINT "Currencies_Code_key";
       currencyexchange            postgres    false    217            }           2606    16407    currencies Currencies_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY currencyexchange.currencies
    ADD CONSTRAINT "Currencies_pkey" PRIMARY KEY (id);
 P   ALTER TABLE ONLY currencyexchange.currencies DROP CONSTRAINT "Currencies_pkey";
       currencyexchange            postgres    false    217                       2606    16428 @   exchange_rates ExchangeRates_BaseCurrencyId_TargetCurrencyId_key 
   CONSTRAINT     �   ALTER TABLE ONLY currencyexchange.exchange_rates
    ADD CONSTRAINT "ExchangeRates_BaseCurrencyId_TargetCurrencyId_key" UNIQUE (base_currency_id, target_currency_id);
 v   ALTER TABLE ONLY currencyexchange.exchange_rates DROP CONSTRAINT "ExchangeRates_BaseCurrencyId_TargetCurrencyId_key";
       currencyexchange            postgres    false    219    219            �           2606    16426 !   exchange_rates ExchangeRates_pkey 
   CONSTRAINT     k   ALTER TABLE ONLY currencyexchange.exchange_rates
    ADD CONSTRAINT "ExchangeRates_pkey" PRIMARY KEY (id);
 W   ALTER TABLE ONLY currencyexchange.exchange_rates DROP CONSTRAINT "ExchangeRates_pkey";
       currencyexchange            postgres    false    219            �           2606    16429    exchange_rates fk_BaseCurrency    FK CONSTRAINT     �   ALTER TABLE ONLY currencyexchange.exchange_rates
    ADD CONSTRAINT "fk_BaseCurrency" FOREIGN KEY (base_currency_id) REFERENCES currencyexchange.currencies(id) NOT VALID;
 T   ALTER TABLE ONLY currencyexchange.exchange_rates DROP CONSTRAINT "fk_BaseCurrency";
       currencyexchange          postgres    false    219    217    3453            �           2606    16434     exchange_rates fk_TargetCurrency    FK CONSTRAINT     �   ALTER TABLE ONLY currencyexchange.exchange_rates
    ADD CONSTRAINT "fk_TargetCurrency" FOREIGN KEY (target_currency_id) REFERENCES currencyexchange.currencies(id) NOT VALID;
 V   ALTER TABLE ONLY currencyexchange.exchange_rates DROP CONSTRAINT "fk_TargetCurrency";
       currencyexchange          postgres    false    219    217    3453               �   x�%��N�0��s��Cg����)I�*�*N$�Z�cK׵��*���Xx�Gw8�9��:U�epN3$Qt%����^���bA稺U�߷Z��VhR��Ӌ3����	m���A8�:�/�DQeUQ���_�HW�]����:v���9�5��-������[�`d����+l"k㬨�$�`F�㝤�C�fw�v���7wk���(֬����|BD�O�         B   x��� 1��]
B���_Ǒߎ%;���+cP�s���Y�fH�d?h.ڧ}S��u~F��h�     
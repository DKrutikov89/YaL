-- ============================================
-- Инициализация базы данных Yandex Lavka
-- ============================================

-- Устанавливаем часовой пояс
SET TIMEZONE = 'Europe/Moscow';

-- Создаем схему для нашего приложения
CREATE SCHEMA IF NOT EXISTS lavka_schema;

-- Устанавливаем схему по умолчанию
SET search_path TO lavka_schema;

-- Создаем расширение для UUID поддержки
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Создаем расширение для работы с JSONB
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Выводим информацию о создании
DO $$
BEGIN
    RAISE NOTICE '✅ База данных Yandex Lavka инициализирована';
    RAISE NOTICE '📁 Схема: lavka_schema';
    RAISE NOTICE '🕐 Часовой пояс: Europe/Moscow';
END $$;
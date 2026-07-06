package com.yandex.lavka.model.entity;

import com.yandex.lavka.model.enums.CourierType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Entity для таблицы couriers.
 * Представляет курьера в базе данных.
 */
@Entity                                    // ← Это таблица в БД
@Table(
        name = "couriers",                 // ← Имя таблицы
        schema = "lavka_schema"            // ← Схема в БД
)
@Data                                      // ← Lombok: геттеры, сеттеры, toString, equals, hashCode
@NoArgsConstructor                         // ← Lombok: конструктор без параметров
@AllArgsConstructor                        // ← Lombok: конструктор со всеми полями
public class Courier {

    // ============================================
    // Первичный ключ
    // ============================================
    @Id                                     // ← Это первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //          ↑ Автоинкремент (BIGSERIAL)
    private Long id;

    // ============================================
    // Тип курьера (FOOT, BIKE, AUTO)
    // ============================================
    @Column(
            name = "courier_type",          // ← Имя колонки в БД
            nullable = false,               // ← NOT NULL
            length = 50                     // ← VARCHAR(50)
    )
    @Enumerated(EnumType.STRING)            // ← Сохранять как строку: "FOOT", "BIKE", "AUTO"
    private CourierType courierType;

    // ============================================
    // Регионы (массив чисел)
    // ============================================
    @JdbcTypeCode(SqlTypes.ARRAY)           // ← Массив в PostgreSQL
    @Column(
            name = "regions",
            nullable = false,
            columnDefinition = "INTEGER[]"  // ← Тип в БД
    )
    private List<Integer> regions;

    // ============================================
    // Рабочие часы (массив строк)
    // ============================================
    @JdbcTypeCode(SqlTypes.ARRAY)           // ← Массив в PostgreSQL
    @Column(
            name = "working_hours",
            nullable = false,
            columnDefinition = "TEXT[]"     // ← Тип в БД
    )
    private List<String> workingHours;

    // ============================================
    // Время создания (автоматически)
    // ============================================
    @CreationTimestamp                       // ← Устанавливается при создании
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false               // ← Не обновлять
    )
    private LocalDateTime createdAt;

    // ============================================
    // Время обновления (автоматически)
    // ============================================
    @UpdateTimestamp                         // ← Устанавливается при обновлении
    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
}
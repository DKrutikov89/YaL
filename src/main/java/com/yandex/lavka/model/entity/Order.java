package com.yandex.lavka.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders", schema = "lavka_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "region", nullable = false)
    private Integer region;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "delivery_hours", nullable = false)
    private List<String> deliveryHours;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Courier courier;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

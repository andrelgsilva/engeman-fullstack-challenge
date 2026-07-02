package com.engeman.imoveis.entity;

import com.engeman.imoveis.enums.TipoImovel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "imoveis")
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoImovel tipo;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer quartos;

    @PositiveOrZero
    private Integer banheiros;

    @Column(name = "area_m2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @NotBlank
    @Column(nullable = false)
    private String endereco;

    @NotBlank
    @Column(nullable = false)
    private String cidade;

    @NotBlank
    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corretor_id", nullable = false)
    private User corretor;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
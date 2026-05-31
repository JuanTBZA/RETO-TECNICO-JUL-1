package com.juan.tirado.reto.repository;

import com.juan.tirado.reto.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar activo por ID
    Optional<Producto> findByIdProductoAndEstado(Long id, String estado);

    // Verificar código único (excluyendo un ID)
    boolean existsByCodigoAndEstadoAndIdProductoNot(String codigo, String estado, Long id);

    // Verificar código único (sin excluir)
    boolean existsByCodigoAndEstado(String codigo, String estado);

    // Listar activos con filtro opcional por marca y modelo.
    @Query(
            value = "SELECT * FROM PRODUCTO " +
                    "WHERE ESTADO = 'A' " +
                    "  AND (:marca  IS NULL OR UPPER(MARCA)  LIKE '%' || UPPER(:marca)  || '%') " +
                    "  AND (:modelo IS NULL OR UPPER(MODELO) LIKE '%' || UPPER(:modelo) || '%') " +
                    "ORDER BY ID_PRODUCTO DESC",
            countQuery =
                    "SELECT COUNT(*) FROM PRODUCTO " +
                            "WHERE ESTADO = 'A' " +
                            "  AND (:marca  IS NULL OR UPPER(MARCA)  LIKE '%' || UPPER(:marca)  || '%') " +
                            "  AND (:modelo IS NULL OR UPPER(MODELO) LIKE '%' || UPPER(:modelo) || '%')",
            nativeQuery = true
    )
    Page<Producto> listarConFiltro(
            @Param("marca")  String marca,
            @Param("modelo") String modelo,
            Pageable pageable
    );
}

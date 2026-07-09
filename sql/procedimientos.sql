-- Procedimientos almacenados para el panel administrativo
-- Ejecutar en la base de datos hostal_boutique

USE hostal_boutique;

DROP PROCEDURE IF EXISTS sp_resumen_dashboard;
DROP PROCEDURE IF EXISTS sp_estadisticas_mensuales;
DROP PROCEDURE IF EXISTS sp_habitaciones_por_tipo;

DELIMITER //

-- 1) Resumen de indicadores para dashboard y reportes
CREATE PROCEDURE sp_resumen_dashboard(IN p_anio INT)
BEGIN
    SELECT
        (SELECT COUNT(*) FROM habitacion) AS total_habitaciones,
        (SELECT COUNT(*) FROM habitacion WHERE estado = 'ocupada') AS habitaciones_ocupadas,
        (SELECT COUNT(*) FROM habitacion WHERE estado = 'disponible') AS habitaciones_disponibles,
        (SELECT COUNT(*) FROM reserva WHERE estado IN ('pendiente', 'confirmada')) AS reservas_activas,
        COALESCE((
            SELECT SUM(total)
            FROM reserva
            WHERE estado IN ('confirmada', 'completada')
              AND YEAR(fecha_checkin) = p_anio
        ), 0) AS ingresos_anio,
        (SELECT COUNT(*) FROM cliente WHERE estado = 'activo') AS total_clientes,
        (SELECT COUNT(*) FROM reserva WHERE YEAR(fecha_checkin) = p_anio) AS total_reservas_anio,
        (SELECT COUNT(*) FROM reserva WHERE estado = 'confirmada') AS reservas_confirmadas,
        CASE
            WHEN (SELECT COUNT(*) FROM habitacion) = 0 THEN 0
            ELSE ROUND(
                (SELECT COUNT(*) FROM habitacion WHERE estado = 'ocupada')
                / (SELECT COUNT(*) FROM habitacion) * 100
            )
        END AS tasa_ocupacion;
END //

-- 2) Estadísticas mensuales para gráficos (consumido vía @Procedure)
CREATE PROCEDURE sp_estadisticas_mensuales(IN p_anio INT)
BEGIN
    WITH RECURSIVE meses AS (
        SELECT 1 AS mes_num
        UNION ALL
        SELECT mes_num + 1 FROM meses WHERE mes_num < 12
    )
    SELECT
        m.mes_num,
        ELT(m.mes_num, 'Ene','Feb','Mar','Abr','May','Jun','Jul','Ago','Sep','Oct','Nov','Dic') AS mes_nombre,
        COALESCE(SUM(
            CASE WHEN r.estado IN ('confirmada', 'completada') THEN r.total ELSE 0 END
        ), 0) AS ingresos,
        COALESCE(COUNT(r.id_reserva), 0) AS num_reservas,
        LEAST(100, COALESCE(COUNT(r.id_reserva), 0) * 8) AS ocupacion_pct
    FROM meses m
    LEFT JOIN reserva r
        ON YEAR(r.fecha_checkin) = p_anio
       AND MONTH(r.fecha_checkin) = m.mes_num
       AND r.estado IN ('confirmada', 'completada', 'pendiente')
    GROUP BY m.mes_num
    ORDER BY m.mes_num;
END //

-- 3) Distribución de habitaciones por tipo (consumido vía JdbcTemplate)
CREATE PROCEDURE sp_habitaciones_por_tipo()
BEGIN
    SELECT
        t.nombre AS nombre_tipo,
        COUNT(h.id_habitacion) AS cantidad
    FROM tipo_habitacion t
    LEFT JOIN habitacion h ON h.id_tipo = t.id_tipo
    WHERE t.activo = TRUE
    GROUP BY t.id_tipo, t.nombre
    ORDER BY cantidad DESC, t.nombre ASC;
END //

DELIMITER ;

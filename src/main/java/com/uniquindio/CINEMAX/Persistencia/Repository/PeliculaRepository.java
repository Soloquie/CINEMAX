package com.uniquindio.CINEMAX.Persistencia.Repository;

import com.uniquindio.CINEMAX.Persistencia.Entity.PeliculaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PeliculaRepository extends JpaRepository<PeliculaEntity, Long> {
    List<PeliculaEntity> findByActivaTrueOrderByTituloAsc();

    @Query(
            value = """
        select distinct p
        from PeliculaEntity p
        left join p.generos g
        where (:soloActivas = false or p.activa = true)
          and (:q is null or lower(p.titulo) like lower(concat('%', :q, '%')))
          and (:generoId is null or g.id = :generoId)
          and (:desde is null or p.fechaEstreno >= :desde)
          and (:hasta is null or p.fechaEstreno <= :hasta)
        """,
            countQuery = """
        select count(distinct p.id)
        from PeliculaEntity p
        left join p.generos g
        where (:soloActivas = false or p.activa = true)
          and (:q is null or lower(p.titulo) like lower(concat('%', :q, '%')))
          and (:generoId is null or g.id = :generoId)
          and (:desde is null or p.fechaEstreno >= :desde)
          and (:hasta is null or p.fechaEstreno <= :hasta)
        """
    )
    Page<PeliculaEntity> search(
            @Param("q") String q,
            @Param("generoId") Long generoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("soloActivas") boolean soloActivas,
            Pageable pageable
    );

    @Query("""
        select p
        from PeliculaEntity p
        where p.activa = true
          and p.fechaEstreno is not null
          and p.fechaEstreno > :hoy
          and p.fechaEstreno <= :hasta
        order by p.fechaEstreno asc
    """)
    List<PeliculaEntity> findProximas(@Param("hoy") LocalDate hoy, @Param("hasta") LocalDate hasta);

}
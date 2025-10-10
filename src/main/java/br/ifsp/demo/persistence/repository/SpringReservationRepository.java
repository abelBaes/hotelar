package br.ifsp.demo.persistence.repository;

import br.ifsp.demo.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringReservationRepository extends JpaRepository<ReservationEntity, String> {
}

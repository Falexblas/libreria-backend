package com.libreria.repository;

import com.libreria.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, String> {
	List<Ticket> findByOrden_IdOrderByCreatedAtDesc(Long ordenId);
}

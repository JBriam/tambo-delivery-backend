package com.tambo.tambo_delivery_backend.repositories;

import com.tambo.tambo_delivery_backend.auth.entities.User;
import com.tambo.tambo_delivery_backend.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserOrderByUpdatedAtDesc(User user); // Más reciente primero

    List<Address> findByUserOrderByUpdatedAtAsc(User user); // Más antiguo primero

    List<Address> findByUserOrderByIsPrimaryDescUpdatedAtDesc(User user); // primero las direcciones primarias y luego
                                                                          // por fecha
}

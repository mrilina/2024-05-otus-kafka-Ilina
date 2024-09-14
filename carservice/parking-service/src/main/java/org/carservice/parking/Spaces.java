package org.carservice.parking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Spaces extends JpaRepository<Space, Space.SpaceIdentifier> {
}

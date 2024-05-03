package com.github.thiagomarqs.gerenciamentopessoas.domain.repository;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByPersonId(Long personId);

    List<Address> findAllByPersonIdAndActive(Long personId, boolean active);

    List<Address> findAllByPersonIdAndAddressContainingIgnoreCase(Long personId, String address);

}

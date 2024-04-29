package com.github.thiagomarqs.gerenciamentopessoas.domain.repository;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {

    Iterable<Address> findAllByPersonId(Long personId);

    Iterable<Address> findAllActiveByPersonId(Long personId);

    Iterable<Address> findAllInactiveByPersonId(Long personId);

    Iterable<Address> findAllByPersonIdAndAddressContaining(Long personId, String address);

}

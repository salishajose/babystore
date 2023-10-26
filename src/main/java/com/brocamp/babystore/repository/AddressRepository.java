package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    @Query("from Address  a where a.users.id=?1 and a.deleted=false")
    Optional<List<Address>> findAllByUsersId(long usersId);
}

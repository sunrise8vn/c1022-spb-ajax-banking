package com.cg.repository;

import com.cg.model.Customer;
import com.cg.model.dto.CustomerDTO;
import com.cg.model.dto.CustomerResDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT NEW com.cg.model.dto.CustomerResDTO (" +
                "cus.id, " +
                "cus.fullName, " +
                "cus.email, " +
                "cus.phone, " +
                "cus.balance, " +
                "ca.id," +
                "ca.fileFolder, " +
                "ca.fileName, " +
                "ca.fileUrl, " +
                "cus.locationRegion" +
            ") " +
            "FROM Customer AS cus " +
            "LEFT JOIN CustomerAvatar AS ca " +
            "ON ca.customer = cus " +
            "WHERE cus.deleted = false "
    )
    List<CustomerResDTO> findAllByDeletedIsFalse();

    List<Customer> findAllByIdNot(Long id);


    @Query("SELECT NEW com.cg.model.dto.CustomerResDTO (" +
                "cus.id, " +
                "cus.fullName, " +
                "cus.email, " +
                "cus.phone, " +
                "cus.balance, " +
                "ca.id," +
                "ca.fileFolder, " +
                "ca.fileName, " +
                "ca.fileUrl, " +
                "cus.locationRegion" +
            ") " +
            "FROM Customer AS cus " +
            "LEFT JOIN CustomerAvatar AS ca " +
            "ON ca.customer = cus " +
            "WHERE cus.deleted = false " +
            "AND cus.id = :id "
    )
    Optional<CustomerResDTO> findCustomerResDTOById(@Param("id") Long id);

    Boolean existsByEmailEquals(String email);


    @Query("SELECT NEW com.cg.model.dto.CustomerDTO (" +
                "cus.id, " +
                "cus.fullName, " +
                "cus.email, " +
                "cus.phone, " +
                "cus.balance, " +
                "cus.locationRegion" +
            ") " +
            "FROM Customer AS cus"
    )
    List<CustomerDTO> findAllCustomerDTO();


    @Modifying
    @Query("UPDATE Customer AS cus " +
            "SET cus.balance = cus.balance + :transactionAmount " +
            "WHERE cus = :customer"
    )
    void incrementBalance(@Param("transactionAmount") BigDecimal transactionAmount, @Param("customer") Customer customer);
}

package com.thinkitve.aidemo.repository;

import com.thinkitve.aidemo.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT p FROM Patient p WHERE " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:city IS NULL OR p.address.city = :city) AND " +
           "(:state IS NULL OR p.address.state = :state) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive) AND " +
           "p.isActive = true " +
           "ORDER BY p.firstName, p.lastName")
    Page<Patient> findPatientsWithFilters(
            @Param("gender") String gender,
            @Param("city") String city,
            @Param("state") String state,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.id = :id AND p.isActive = true")
    Optional<Patient> findActiveById(@Param("id") UUID id);

    @Query("SELECT p FROM Patient p WHERE p.email = :email AND p.id != :excludeId")
    Optional<Patient> findByEmailExcludingId(@Param("email") String email, @Param("excludeId") UUID excludeId);

    @Query("SELECT p FROM Patient p WHERE p.phoneNumber = :phoneNumber AND p.id != :excludeId")
    Optional<Patient> findByPhoneNumberExcludingId(@Param("phoneNumber") String phoneNumber, @Param("excludeId") UUID excludeId);
} 
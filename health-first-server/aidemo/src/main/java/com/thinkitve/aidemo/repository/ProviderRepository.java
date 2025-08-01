package com.thinkitve.aidemo.repository;

import com.thinkitve.aidemo.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    Optional<Provider> findByEmail(String email);
    Optional<Provider> findByPhoneNumber(String phoneNumber);
    Optional<Provider> findByLicenseNumber(String licenseNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByLicenseNumber(String licenseNumber);

    @Query("SELECT p FROM Provider p WHERE " +
           "(:specialization IS NULL OR p.specialization = :specialization) AND " +
           "(:city IS NULL OR p.clinicAddress.city = :city) AND " +
           "(:state IS NULL OR p.clinicAddress.state = :state) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive) AND " +
           "p.isActive = true " +
           "ORDER BY p.firstName, p.lastName")
    Page<Provider> findProvidersWithFilters(
            @Param("specialization") String specialization,
            @Param("city") String city,
            @Param("state") String state,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    @Query("SELECT p FROM Provider p WHERE p.id = :id AND p.isActive = true")
    Optional<Provider> findActiveById(@Param("id") UUID id);

    @Query("SELECT p FROM Provider p WHERE p.email = :email AND p.id != :excludeId")
    Optional<Provider> findByEmailExcludingId(@Param("email") String email, @Param("excludeId") UUID excludeId);

    @Query("SELECT p FROM Provider p WHERE p.phoneNumber = :phoneNumber AND p.id != :excludeId")
    Optional<Provider> findByPhoneNumberExcludingId(@Param("phoneNumber") String phoneNumber, @Param("excludeId") UUID excludeId);
} 
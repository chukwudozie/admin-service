package com.stitch.admin.repository;

import com.stitch.admin.model.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>, RevisionRepository<UserEntity,Long,Long>, JpaSpecificationExecutor<UserEntity> {

    long countUserEntitiesByEnabledTrue();
    boolean existsByUsernameAndRole_Name(String username,String roleName);

    long countUserEntitiesByRole_NameIgnoreCase(String roleName);
    long countUserEntitiesByEnabledTrueAndRole_NameIgnoreCase(String roleName);

    Optional<UserEntity> findByEmailAddressIgnoreCase(String email);

    List<UserEntity> findAllByEmailAddressIgnoreCase(String email, Pageable pageable);
    List<UserEntity> findAllByCountryIgnoreCase(String country, Pageable pageable);
    List<UserEntity> findAllByEnabledIs(boolean enabled, Pageable pageable);
    List<UserEntity> findAllByUsernameContains(String username, Pageable pageable);
    List<UserEntity> findAllByRole_NameIgnoreCase(String roleName, Pageable pageable);
    List<UserEntity> findAllByRole_NameIgnoreCaseAndEnabledIs(String roleName,boolean enabled, Pageable pageable);

}

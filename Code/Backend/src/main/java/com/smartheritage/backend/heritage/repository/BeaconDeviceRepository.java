package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.BeaconDevice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeaconDeviceRepository extends JpaRepository<BeaconDevice, Long> {

    Optional<BeaconDevice> findByBeaconUuidIgnoreCaseAndMajorAndMinorAndActiveTrue(
            String beaconUuid, int major, int minor);

    List<BeaconDevice> findAllByOrderByIdAsc();

    List<BeaconDevice> findByArtifact_Id(Long artifactId);
}

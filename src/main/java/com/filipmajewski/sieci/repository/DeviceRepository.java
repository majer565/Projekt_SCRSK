package com.filipmajewski.sieci.repository;

import com.filipmajewski.sieci.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    List<Device> findAllByUserID(int userID);

}

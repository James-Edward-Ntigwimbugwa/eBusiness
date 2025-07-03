package tz.business.eCard.services;

import tz.business.eCard.dtos.NearbyCardInfo;
import tz.business.eCard.models.DeviceProximity;
import tz.business.eCard.utils.Response;

import java.util.List;
import java.util.UUID;

public interface DeviceProximityService {
    List<DeviceProximity> findNearbyDevices(UUID userId, double latitude, double longitude);
    Response<DeviceProximity> saveDeviceProximity(DeviceProximity deviceProximity);
    List<NearbyCardInfo> findNearbyCardsWithDetails(UUID userId, double latitude, double longitude);
}

package tz.business.eCard.services;

import tz.business.eCard.dtos.DashboardDto;
import tz.business.eCard.utils.Response;

public interface DashboardService {
    Response<DashboardDto> getDashboardData();
}

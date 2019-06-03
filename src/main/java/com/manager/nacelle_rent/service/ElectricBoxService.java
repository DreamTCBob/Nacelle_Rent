package com.manager.nacelle_rent.service;

import com.manager.nacelle_rent.entity.ElectricRes;

import java.util.List;

public interface ElectricBoxService {
    List<ElectricRes> serializeElectricRes(List<ElectricRes> list);
}

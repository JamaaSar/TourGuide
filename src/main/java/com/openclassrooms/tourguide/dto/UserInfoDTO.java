package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfoDTO {

    private Location touristLocation;
    private List<AttractionDTO> nearestAttractionsList;


}

package com.openclassrooms.tourguide.dto;

import com.openclassrooms.tourguide.model.UserReward;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInfoDTO {

    private String touristName;
    private List<AttractionDTO> attractionDTOList;
    private VisitedLocation touristLocation;
    private List<AttractionAndDistanceDTO> attractionsAndDistanceDTO;
    private List<UserReward> userReward;

    //TODO
    //junit !!!


}

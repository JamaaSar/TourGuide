package com.openclassrooms.tourguide.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttractionDTO {
    private String attractionName;
    private double lattitude;
    private double longitude;
    private double distanceFromLocation;
    private double rewardForVisiting;

}

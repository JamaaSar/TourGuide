package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.openclassrooms.tourguide.dto.UserInfoDTO;
import gpsUtil.location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.model.User;
import tripPricer.Provider;
@ExtendWith(MockitoExtension.class)
public class TestTourGuideService {

	@InjectMocks
	TourGuideService tourGuideService;
	@Mock
	RewardsService rewardsService;
	GpsUtil gpsUtil;
	User user;
	User user2;

	@BeforeEach
	public void setUp() {
		gpsUtil = new GpsUtil();
		rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

	}

	@Test
	public void getUserLocation() {

		VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void trackUserLocation() {

		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void addUser() {

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() {

		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {

		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}
	@Test
	public void getTripDeals() {

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}
	@Test
	public void getUserInfo() {
		InternalTestHelper.setInternalUserNumber(0);

		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(),
				new Location(111.26395945676398, -31.300209043463227), new Date());
		List<VisitedLocation> visitedLocationList = new ArrayList<>();
		visitedLocationList.add(visitedLocation);
		user.setVisitedLocations(visitedLocationList);


		UserInfoDTO userInfoDTO = tourGuideService.getUserInfo(visitedLocation,
				user.getUserId());

		tourGuideService.tracker.stopTracking();

		assertEquals(5, userInfoDTO.getNearestAttractionsList().size());
	}

}

package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;
	private static final String tripPricerApiKey = "test-server-api-key";
	private Map<String, User> internalUserMap = new HashMap<>();
	private final TourGuideTestModeService tourGuideTestModeService = new TourGuideTestModeService();
	ExecutorService executorService = Executors.newFixedThreadPool(100);

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService){
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;

		Locale.setDefault(Locale.US);

		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalUserMap = tourGuideTestModeService.initializeInternalUsers();
			System.out.println(internalUserMap);
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation()
				: trackUserLocation(user);
		return visitedLocation;
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation1(User user) {

		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}
	public VisitedLocation trackUserLocation(User user) {


        CompletableFuture<VisitedLocation> visitedLocationsFuture =
				CompletableFuture.supplyAsync(() -> gpsUtil.getUserLocation(user.getUserId()));
		visitedLocationsFuture.thenAccept(user::addToVisitedLocations).join();


		CompletableFuture<Void> calculateRewardsFuture =
				visitedLocationsFuture.thenAcceptAsync(visitedLocation -> {
					rewardsService.calculateRewards(user);
				});

		CompletableFuture.allOf(visitedLocationsFuture, calculateRewardsFuture).join();

		return visitedLocationsFuture.join();
	}
	public void trackAllUsersLocations(List<User> users) {
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		users.forEach(user -> futures.add(
				CompletableFuture.runAsync(() -> trackUserLocation(user), executorService)));

		futures.forEach(CompletableFuture::join);

		executorService.shutdown();

	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		return gpsUtil.getAttractions()
				.stream().sorted(
						Comparator.comparingDouble(
								attraction->
										rewardsService.getDistance(visitedLocation.location, attraction)))
				.limit(5)
				.collect(Collectors.toList());
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}



}

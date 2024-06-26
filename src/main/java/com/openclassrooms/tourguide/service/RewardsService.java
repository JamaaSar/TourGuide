package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	ExecutorService executorService = Executors.newFixedThreadPool(100);

	private List<Attraction> gpsList = new ArrayList<>();

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
		this.gpsList = gpsUtil.getAttractions();
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	

	public void calculateRewards(User user) {

		List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());

		Iterator<Attraction> attractionIterator = gpsList.iterator();
		CopyOnWriteArrayList<UserReward> userRewards = new CopyOnWriteArrayList<>(user.getUserRewards());


		while(attractionIterator.hasNext()){
			Attraction attraction = attractionIterator.next();
			boolean match = !userRewards.isEmpty() && userRewards.stream()
					.anyMatch(r -> r.attraction.attractionName.equals(attraction.attractionName));

			if(!match)
				for(VisitedLocation visitedLocation: userLocations){
					if(nearAttraction(visitedLocation, attraction)){
						userRewards.add(new UserReward(visitedLocation, attraction,
								getRewardPoints(attraction, user.getUserId())));
						break;
					}
				}
		}
		user.setUserRewards(userRewards);
	}

	public void calculateAllUsersRewards(List<User> users) {
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		users.forEach(user -> futures.add(
				CompletableFuture.runAsync(() -> calculateRewards(user), executorService)));

		futures.forEach(CompletableFuture::join);

		executorService.shutdown();

	}

	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	protected int getRewardPoints(Attraction attraction, UUID userId) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userId);
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}

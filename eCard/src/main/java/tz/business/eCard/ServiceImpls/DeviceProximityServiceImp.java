package tz.business.eCard.ServiceImpls;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import tz.business.eCard.dtos.NearbyCardInfo;
import tz.business.eCard.models.Card;
import tz.business.eCard.models.DeviceProximity;
import tz.business.eCard.models.Account;
import tz.business.eCard.repositories.CardRepository;
import tz.business.eCard.repositories.DeviceProximityRepository;
import tz.business.eCard.services.DeviceProximityService;
import tz.business.eCard.utils.Response;
import tz.business.eCard.utils.ResponseCode;
import tz.business.eCard.utils.userExtractor.LoggedUser;

import java.time.LocalDateTime;
import java.util.*;


/**
 * This class calculates the estimated walk distance , drive distance , walking time , shortest path and bearings using
 * Djikstras and A* Algorithms
 */

@Slf4j
@Service
public class DeviceProximityServiceImp implements DeviceProximityService {
    private static final double MAX_DISTANCE = 5000.00; // in meters
    private static final double EARTH_RADIUS = 6371000; // Earth's radius in meters

    @Autowired
    private DeviceProximityRepository deviceProximityRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private LoggedUser loggedUser;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Inner classes for pathfinding
     */
    public static class Node {
        public double lat, lng;
        public double gCost, hCost, fCost;
        public Node parent;
        public String id;

        public Node(double lat, double lng, String id) {
            this.lat = lat;
            this.lng = lng;
            this.id = id;
        }

        public Node(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
            this.id = UUID.randomUUID().toString();
        }

        public double getFCost() {
            return gCost + hCost;
        }
    }

    public static class Edge {
        public Node from, to;
        public double weight;
        public String roadType; // "walking", "driving", "highway", etc.

        public Edge(Node from, Node to, double weight, String roadType) {
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.roadType = roadType;
        }
    }

    public static class PathResult {
        public double distance;
        public double estimatedTime; // in minutes
        public List<Node> path;
        public String transportMode;

        public PathResult(double distance, double estimatedTime, List<Node> path, String transportMode) {
            this.distance = distance;
            this.estimatedTime = estimatedTime;
            this.path = path;
            this.transportMode = transportMode;
        }
    }

    @Override
    public List<DeviceProximity> findNearbyDevices(UUID userId, double latitude, double longitude) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                log.error("Unauthorized access attempt");
                return new ArrayList<>();
            }

            List<Card> nearbyCards = getNearbyCards(latitude, longitude, MAX_DISTANCE);
            List<DeviceProximity> nearbyDevices = new ArrayList<>();

            for (Card card : nearbyCards) {
                try {
                    double cardLat = Double.parseDouble(card.getLatitude());
                    double cardLng = Double.parseDouble(card.getLongitude());
                    double distance = calculateDistance(latitude, longitude, cardLat, cardLng);

                    if (distance <= MAX_DISTANCE) {
                        nearbyDevices.add(new DeviceProximity(
                                UUID.randomUUID(),
                                userId,
                                card.getUuid(),
                                distance,
                                LocalDateTime.now()
                        ));
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid coordinates for card {}: lat={}, lng={}",
                            card.getUuid(), card.getLatitude(), card.getLongitude());
                }
            }

            log.info("Found {} nearby devices for user {} at coordinates ({}, {})",
                    nearbyDevices.size(), userId, latitude, longitude);

            return nearbyDevices;
        } catch (Exception e) {
            log.error("An exception occurred while finding nearby devices: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Enhanced method to find nearby cards with pathfinding and directional information
     */
    public List<NearbyCardInfo> findNearbyCardsWithPathfinding(UUID userId, double latitude, double longitude) {
        try {
            Account user = loggedUser.getUserAccount();
            if (user == null) {
                log.error("Unauthorized access attempt");
                return new ArrayList<>();
            }

            List<Card> nearbyCards = getNearbyCards(latitude, longitude, MAX_DISTANCE);
            List<NearbyCardInfo> result = new ArrayList<>();

            for (Card card : nearbyCards) {
                try {
                    double cardLat = Double.parseDouble(card.getLatitude());
                    double cardLng = Double.parseDouble(card.getLongitude());

                    NearbyCardInfo cardInfo = createEnhancedNearbyCardInfo(
                            card, latitude, longitude, cardLat, cardLng
                    );

                    result.add(cardInfo);
                } catch (NumberFormatException e) {
                    log.warn("Invalid coordinates for card {}", card.getUuid());
                }
            }

            return result;
        } catch (Exception e) {
            log.error("Error finding nearby cards with pathfinding: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Create enhanced NearbyCardInfo with pathfinding calculations
     */
    private NearbyCardInfo createEnhancedNearbyCardInfo(Card card, double userLat, double userLng,
                                                        double cardLat, double cardLng) {

        // Calculate straight-line distance
        double straightDistance = calculateDistance(userLat, userLng, cardLat, cardLng);

        // Calculate direction
        String direction = calculateDirection(userLat, userLng, cardLat, cardLng);

        // Generate synthetic road network for demonstration
        List<Node> roadNetwork = generateSyntheticRoadNetwork(userLat, userLng, cardLat, cardLng);

        // Calculate walking distance using Dijkstra's algorithm
        PathResult walkingPath = dijkstraPathfinding(roadNetwork, userLat, userLng, cardLat, cardLng, "walking");

        // Calculate driving distance using A* algorithm
        PathResult drivingPath = aStarPathfinding(roadNetwork, userLat, userLng, cardLat, cardLng, "driving");

        // Create enhanced NearbyCardInfo
        NearbyCardInfo cardInfo = new NearbyCardInfo();
        cardInfo.setCardId(card.getUuid());
        cardInfo.setTitle(card.getTitle());
        cardInfo.setOrganization(card.getOrganization());
        cardInfo.setEmail(card.getEmail());
        cardInfo.setPhoneNumber(card.getPhoneNumber());
        cardInfo.setCardLogo(card.getCardLogo());
        cardInfo.setProfilePhoto(card.getProfilePhoto());
        cardInfo.setDistance(straightDistance);
        cardInfo.setLatitude(cardLat);
        cardInfo.setLongitude(cardLng);

        // Add new pathfinding information
        cardInfo.setDirection(direction);
        cardInfo.setWalkingDistance(walkingPath.distance);
        cardInfo.setWalkingTime(walkingPath.estimatedTime);
        cardInfo.setDrivingDistance(drivingPath.distance);
//        cardInfo.setPath(drivingPath.path);
        cardInfo.setDrivingTime(drivingPath.estimatedTime);

        return cardInfo;
    }

    /**
     * Calculate direction from user to card (N, NE, E, SE, S, SW, W, NW)
     */
    private String calculateDirection(double lat1, double lng1, double lat2, double lng2) {
        double deltaLng = lng2 - lng1;
        double deltaLat = lat2 - lat1;

        // Calculate bearing in radians
        double bearing = Math.atan2(deltaLng, deltaLat);

        // Convert to degrees and normalize to 0-360
        double bearingDegrees = Math.toDegrees(bearing);
        if (bearingDegrees < 0) {
            bearingDegrees += 360;
        }

        // Convert to compass direction
        if (bearingDegrees >= 337.5 || bearingDegrees < 22.5) {
            return "N";
        } else if (bearingDegrees >= 22.5 && bearingDegrees < 67.5) {
            return "NE";
        } else if (bearingDegrees >= 67.5 && bearingDegrees < 112.5) {
            return "E";
        } else if (bearingDegrees >= 112.5 && bearingDegrees < 157.5) {
            return "SE";
        } else if (bearingDegrees >= 157.5 && bearingDegrees < 202.5) {
            return "S";
        } else if (bearingDegrees >= 202.5 && bearingDegrees < 247.5) {
            return "SW";
        } else if (bearingDegrees >= 247.5 && bearingDegrees < 292.5) {
            return "W";
        } else {
            return "NW";
        }
    }

    /**
     * Generate a synthetic road network for pathfinding demonstration
     * In a real implementation, this would come from a mapping service or database
     */
    private List<Node> generateSyntheticRoadNetwork(double startLat, double startLng,
                                                    double endLat, double endLng) {
        List<Node> network = new ArrayList<>();

        // Create a grid of nodes between start and end points
        int gridSize = 10;
        double latStep = (endLat - startLat) / gridSize;
        double lngStep = (endLng - startLng) / gridSize;

        for (int i = 0; i <= gridSize; i++) {
            for (int j = 0; j <= gridSize; j++) {
                double lat = startLat + (i * latStep);
                double lng = startLng + (j * lngStep);
                network.add(new Node(lat, lng, "node_" + i + "_" + j));
            }
        }

        return network;
    }

    /**
     * Dijkstra's algorithm for shortest path calculation
     */
    private PathResult dijkstraPathfinding(List<Node> network, double startLat, double startLng,
                                           double endLat, double endLng, String mode) {

        Node start = new Node(startLat, startLng, "start");
        Node end = new Node(endLat, endLng, "end");

        Map<String, Double> distances = new HashMap<>();
        Map<String, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> n.gCost));

        // Initialize distances
        for (Node node : network) {
            distances.put(node.id, Double.MAX_VALUE);
        }
        distances.put(start.id, 0.0);
        start.gCost = 0.0;

        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (calculateDistance(current.lat, current.lng, endLat, endLng) < 100) {
                // Found path to destination
                List<Node> path = reconstructPath(previous, current, start);
                double totalDistance = calculatePathDistance(path);
                double estimatedTime = estimateTime(totalDistance, mode);

                return new PathResult(totalDistance, estimatedTime, path, mode);
            }

            // Check neighbors
            for (Node neighbor : getNeighbors(current, network)) {
                double edgeWeight = calculateDistance(current.lat, current.lng, neighbor.lat, neighbor.lng);

                // Apply mode-specific weight adjustments
                if (mode.equals("walking")) {
                    edgeWeight *= 1.2; // Walking paths might be longer
                } else if (mode.equals("driving")) {
                    edgeWeight *= 0.8; // Driving routes might be more direct
                }

                double newDistance = distances.get(current.id) + edgeWeight;

                if (newDistance < distances.get(neighbor.id)) {
                    distances.put(neighbor.id, newDistance);
                    previous.put(neighbor.id, current);
                    neighbor.gCost = newDistance;
                    queue.add(neighbor);
                }
            }
        }

        // If no path found, return straight-line distance
        double straightDistance = calculateDistance(startLat, startLng, endLat, endLng);
        return new PathResult(straightDistance * 1.3, estimateTime(straightDistance * 1.3, mode),
                Arrays.asList(start, end), mode);
    }

    /**
     * A* algorithm for pathfinding
     */
    private PathResult aStarPathfinding(List<Node> network, double startLat, double startLng,
                                        double endLat, double endLng, String mode) {

        Node start = new Node(startLat, startLng, "start");
        Node end = new Node(endLat, endLng, "end");

        List<Node> openSet = new ArrayList<>();
        Set<String> closedSet = new HashSet<>();
        Map<String, Node> allNodes = new HashMap<>();

        start.gCost = 0;
        start.hCost = calculateDistance(startLat, startLng, endLat, endLng);
        start.fCost = start.getFCost();

        openSet.add(start);
        allNodes.put(start.id, start);

        while (!openSet.isEmpty()) {
            // Find node with lowest fCost
            Node current = openSet.stream()
                    .min(Comparator.comparingDouble(Node::getFCost))
                    .orElse(null);

            if (current == null) break;

            openSet.remove(current);
            closedSet.add(current.id);

            // Check if we reached the destination
            if (calculateDistance(current.lat, current.lng, endLat, endLng) < 100) {
                List<Node> path = reconstructPath(current);
                double totalDistance = calculatePathDistance(path);
                double estimatedTime = estimateTime(totalDistance, mode);

                return new PathResult(totalDistance, estimatedTime, path, mode);
            }

            // Check neighbors
            for (Node neighbor : getNeighbors(current, network)) {
                if (closedSet.contains(neighbor.id)) continue;

                double tentativeGCost = current.gCost +
                        calculateDistance(current.lat, current.lng, neighbor.lat, neighbor.lng);

                // Apply mode-specific adjustments
                if (mode.equals("driving")) {
                    tentativeGCost *= 0.8; // Driving routes are typically faster
                }

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (tentativeGCost >= neighbor.gCost) {
                    continue;
                }

                neighbor.parent = current;
                neighbor.gCost = tentativeGCost;
                neighbor.hCost = calculateDistance(neighbor.lat, neighbor.lng, endLat, endLng);
                neighbor.fCost = neighbor.getFCost();

                allNodes.put(neighbor.id, neighbor);
            }
        }

        // If no path found, return straight-line distance
        double straightDistance = calculateDistance(startLat, startLng, endLat, endLng);
        return new PathResult(straightDistance * 1.3, estimateTime(straightDistance * 1.3, mode),
                Arrays.asList(start, end), mode);
    }

    /**
     * Get neighboring nodes within a reasonable distance
     */
    private List<Node> getNeighbors(Node current, List<Node> network) {
        List<Node> neighbors = new ArrayList<>();
        double maxNeighborDistance = 500; // 500 meters

        for (Node node : network) {
            double distance = calculateDistance(current.lat, current.lng, node.lat, node.lng);
            if (distance <= maxNeighborDistance && !node.id.equals(current.id)) {
                neighbors.add(node);
            }
        }

        return neighbors;
    }

    /**
     * Reconstruct path from A* algorithm
     */
    private List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path;
    }

    /**
     * Reconstruct path from Dijkstra's algorithm
     */
    private List<Node> reconstructPath(Map<String, Node> previous, Node end, Node start) {
        List<Node> path = new ArrayList<>();
        Node current = end;

        while (current != null) {
            path.add(0, current);
            current = previous.get(current.id);
            if (current != null && current.id.equals(start.id)) {
                path.add(0, start);
                break;
            }
        }

        return path;
    }

    /**
     * Calculate total distance of a path
     */
    private double calculatePathDistance(List<Node> path) {
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += calculateDistance(
                    path.get(i).lat, path.get(i).lng,
                    path.get(i + 1).lat, path.get(i + 1).lng
            );
        }
        return totalDistance;
    }

    /**
     * Estimate time based on distance and mode of transport
     */
    private double estimateTime(double distance, String mode) {
        switch (mode.toLowerCase()) {
            case "walking":
                return distance / 1.4 / 60; // 1.4 m/s walking speed, convert to minutes
            case "driving":
                return distance / 13.89 / 60; // 50 km/h average speed, convert to minutes
            default:
                return distance / 1.4 / 60;
        }
    }

    /**
     * Calculate the precise distance between two coordinates using the haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c; // Distance in meters
    }

    /**
     * Get nearby cards that are published and have valid coordinates
     */
    private List<Card> getNearbyCards(double userLat, double userLng, double maxDistance) {
        try {
            List<Card> publishedCards = cardRepository.findByPublishCardTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndDeletedFalse();
            List<Card> nearbyCards = new ArrayList<>();

            for (Card card : publishedCards) {
                try {
                    double cardLat = Double.parseDouble(card.getLatitude());
                    double cardLng = Double.parseDouble(card.getLongitude());
                    double distance = calculateDistance(userLat, userLng, cardLat, cardLng);

                    if (distance <= maxDistance) {
                        nearbyCards.add(card);
                    }
                } catch (NumberFormatException e) {
                    log.debug("Skipping card {} due to invalid coordinates", card.getUuid());
                }
            }

            return nearbyCards;
        } catch (Exception e) {
            log.error("Error finding nearby cards: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Original method for backward compatibility
     */
    public List<NearbyCardInfo> findNearbyCardsWithDetails(UUID userId, double latitude, double longitude) {
        return findNearbyCardsWithPathfinding(userId, latitude, longitude);
    }

    @Override
    public Response<DeviceProximity> saveDeviceProximity(DeviceProximity deviceProximity) {
        try {
            DeviceProximity saved = deviceProximityRepository.save(deviceProximity);
            return new Response<>(false, ResponseCode.CREATED, "Proximity Saved", saved);
        } catch (Exception e) {
            log.error("An error occurred while saving device proximity: {}", e.getMessage());
            return new Response<>(true, ResponseCode.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }
}
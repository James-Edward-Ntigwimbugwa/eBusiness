package tz.business.eCard.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NearbyCardInfo {
    // Original fields
    private String cardId;
    private String title;
    private String organization;
    private String email;
    private String phoneNumber;
    private String cardLogo;
    private String profilePhoto;
    private double distance; // straight-line distance in meters
    private double latitude;
    private double longitude;

    // New pathfinding and directional fields
    private String direction; // N, NE, E, SE, S, SW, W, NW
    private double walkingDistance; // estimated walking distance in meters
    private double walkingTime; // estimated walking time in minutes
    private double drivingDistance; // estimated driving distance in meters
    private double drivingTime; // estimated driving time in minutes

    // Constructor for backward compatibility
    public NearbyCardInfo(String cardId, String title, String organization, String email,
                          String phoneNumber, String cardLogo, String profilePhoto,
                          double distance, double latitude, double longitude) {
        this.cardId = cardId;
        this.title = title;
        this.organization = organization;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.cardLogo = cardLogo;
        this.profilePhoto = profilePhoto;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Get formatted walking time as a readable string
     */
    public String getFormattedWalkingTime() {
        if (walkingTime < 1) {
            return "< 1 min";
        } else if (walkingTime < 60) {
            return String.format("%.0f min", walkingTime);
        } else {
            int hours = (int) (walkingTime / 60);
            int minutes = (int) (walkingTime % 60);
            return String.format("%d h %d min", hours, minutes);
        }
    }

    /**
     * Get formatted driving time as a readable string
     */
    public String getFormattedDrivingTime() {
        if (drivingTime < 1) {
            return "< 1 min";
        } else if (drivingTime < 60) {
            return String.format("%.0f min", drivingTime);
        } else {
            int hours = (int) (drivingTime / 60);
            int minutes = (int) (drivingTime % 60);
            return String.format("%d h %d min", hours, minutes);
        }
    }

    /**
     * Get formatted walking distance as a readable string
     */
    public String getFormattedWalkingDistance() {
        if (walkingDistance < 1000) {
            return String.format("%.0f m", walkingDistance);
        } else {
            return String.format("%.1f km", walkingDistance / 1000);
        }
    }

    /**
     * Get formatted driving distance as a readable string
     */
    public String getFormattedDrivingDistance() {
        if (drivingDistance < 1000) {
            return String.format("%.0f m", drivingDistance);
        } else {
            return String.format("%.1f km", drivingDistance / 1000);
        }
    }

    /**
     * Get full directional description
     */
    public String getDirectionDescription() {
        switch (direction) {
            case "N": return "North";
            case "NE": return "Northeast";
            case "E": return "East";
            case "SE": return "Southeast";
            case "S": return "South";
            case "SW": return "Southwest";
            case "W": return "West";
            case "NW": return "Northwest";
            default: return "Unknown";
        }
    }
}
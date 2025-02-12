package com.example.madproject.helper.API.OnemapRoute;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OnemapRouteResponse {
    @SerializedName("plan")
    private Plan plan;

    public Plan getPlan() {
        return plan;
    }

    public static class Plan {
        @SerializedName("itineraries")
        private List<Itinerary> itineraries;

        public List<Itinerary> getItineraries() {
            return itineraries;
        }
    }

    public static class Itinerary {
        @SerializedName("duration")
        private int duration;
        @SerializedName("legs")
        private List<Leg> legs;

        public int getDuration() {
            return duration;
        }

        public List<Leg> getLegs() {
            return legs;
        }
    }

    public static class Leg {
        @SerializedName("mode")
        private String mode;
        @SerializedName("route")
        private String route;
        @SerializedName("distance")
        private double distance;

        public String getMode() {
            return mode;
        }

        public String getRoute() {
            return route;
        }

        public double getDistance() {
            return distance;
        }
    }
}
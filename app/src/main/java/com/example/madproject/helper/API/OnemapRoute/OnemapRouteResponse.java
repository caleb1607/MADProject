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
        @SerializedName("endTime")
        private int endTime;
        @SerializedName("startTime")
        private int startTime;
        @SerializedName("fare")
        private String fare;

        public int getDuration() {
            return duration;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public int getEndTime() {
            return endTime;
        }

        public int getStartTime() {
            return startTime;
        }

        public String getFare() {
            return fare;
        }

    }

    public static class Leg {
        @SerializedName("mode")
        private String mode;
        @SerializedName("route")
        private String route;
        @SerializedName("distance")
        private double distance;
        @SerializedName("from")
        private Object from;
        @SerializedName("to")
        private Object to;

        public String getMode() {
            return mode;
        }

        public String getRoute() {
            return route;
        }

        public double getDistance() {
            return distance;
        }

        public Object getFrom() { return from;}
        public Object getTo() { return to;}
    }

    public static class from {
        @SerializedName("name")
        private String name;
        @SerializedName("stopIndex")
        private String stopIndex;
    }

    public static class to {
        @SerializedName("name")
        private String name;
        @SerializedName("stopIndex")
        private String stopIndex;
    }
}
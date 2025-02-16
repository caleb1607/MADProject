package com.example.madproject.helper.API.OnemapRoute;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import android.util.Log;


public class OnemapRouteResponse {
    @SerializedName("plan")
    private Plan plan;

    public Plan getPlan() {
        return plan;
    }

    public class Plan {
        @SerializedName("itineraries")
        private List<Itinerary> itineraries;

        public List<Itinerary> getItineraries() {
            return itineraries;
        }
    }

    public class Itinerary {
        @SerializedName("duration")
        private int duration;
        @SerializedName("legs")
        private List<Leg> legs;
        @SerializedName("endTime")
        private long endTime;
        @SerializedName("startTime")
        private long startTime;
        @SerializedName("fare")
        private String fare;

        public int getDuration() {
            return duration;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public long getEndTime() {
            return endTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public String getFare() {
            return fare;
        }

    }

    public class Leg {
        @SerializedName("mode")
        private String mode;
        @SerializedName("route")
        private String route;
        @SerializedName("distance")
        private double distance;
        @SerializedName("from")
        private from from;
        @SerializedName("to")
        private to to;
        @SerializedName("duration")
        private int Duration;

        public String getMode() {
            return mode;
        }

        public String getRoute() {
            return route;
        }

        public double getDistance() {
            return distance;
        }

        public from getFrom() { return from;}
        public to getTo() { return to;}
        public int getDuration() {return Duration;}
    }

    public class from {
        @SerializedName("name")
        private String name;
        @SerializedName("stopIndex")
        private int stopIndex;
        @SerializedName("lat")
        private double lat;
        @SerializedName("lon")
        private double lon;

        public String getName() {return name;}

        public int getStopIndex() {return stopIndex;}

        public double getLat() {return lat;}

        public double getLon() {return lon;}
    }

    public class to {
        @SerializedName("name")
        private String name;
        @SerializedName("stopIndex")
        private int stopIndex;
        @SerializedName("lat")
        private double lat;
        @SerializedName("lon")
        private double lon;

        public String getName() {return name;}

        public int getStopIndex() {return stopIndex;}

        public double getLat() {return lat;}

        public double getLon() {return lon;}
    }
}
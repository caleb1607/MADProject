package com.example.madproject.helper;

import java.util.List;

public class BusArrivalResponse {
    public List<Service> Services;

    public static class Service {
        public String ServiceNo;
        public NextBus NextBus;
        public NextBus NextBus2;
        public NextBus NextBus3;
    }

    public static class NextBus {
        public String EstimatedArrival;
    }
    public static class NextBus2 {
        public String EstimatedArrival;
    }
    public static class NextBus3 {
        public String EstimatedArrival;
    }
}

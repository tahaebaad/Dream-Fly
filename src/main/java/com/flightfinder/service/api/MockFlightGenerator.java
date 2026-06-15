package com.flightfinder.service.api;

import com.flightfinder.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A concrete implementation of IFlightDataProvider that generates mock flights.
 * Demonstrates polymorphism when swapped with AmadeusAPIClient.
 */
public class MockFlightGenerator implements IFlightDataProvider {

    @Override
    public List<FlightItinerary> searchFlights(SearchCriteria criteria) throws APIException {
        System.out.println("MockFlightGenerator: Generating mock data...");
        List<FlightItinerary> mocks = new ArrayList<>();
        Random rand = new Random();
        int numResults = 20 + rand.nextInt(30);
        
        String[] mockAirlines = { "EK", "PK", "QR", "BA", "TK", "DL", "AA", "UA", "LH", "AF" };
        String[] mockAirlineNames = { "Emirates", "PIA", "Qatar Airways", "British Airways", "Turkish Airlines", "Delta Air Lines", "American Airlines", "United Airlines", "Lufthansa", "Air France" };
        
        for (int i = 0; i < numResults; i++) {
            FlightItinerary itin = new FlightItinerary();
            itin.setItineraryId("MOCK-" + i);
            itin.setCurrency("USD");
            
            int airlineIdx = rand.nextInt(mockAirlines.length);
            Airline airline = new Airline(mockAirlines[airlineIdx], mockAirlineNames[airlineIdx], 
                "https://pics.avs.io/200/200/" + mockAirlines[airlineIdx] + ".png", 4.5);
            
            List<FlightSegment> segments = new ArrayList<>();
            LocalDateTime depTime = criteria.getDepartureDate().atTime(8 + rand.nextInt(12), rand.nextInt(60));
            boolean isDirect = rand.nextBoolean() || criteria.isDirectFlightsOnly();
            
            if (isDirect) {
                FlightSegment seg = createMockSegment(criteria.getOriginAirportCode(), criteria.getDestinationAirportCode(), depTime, airline, 8 + rand.nextInt(8));
                segments.add(seg);
            } else {
                String hub = airline.getAirlineCode().equals("TK") ? "IST" : "DXB";
                FlightSegment seg1 = createMockSegment(criteria.getOriginAirportCode(), hub, depTime, airline, 3 + rand.nextInt(3));
                LocalDateTime layoverEnd = seg1.getArrivalTime().plusHours(2 + rand.nextInt(6));
                FlightSegment seg2 = createMockSegment(hub, criteria.getDestinationAirportCode(), layoverEnd, airline, 7 + rand.nextInt(5));
                    
                segments.add(seg1);
                segments.add(seg2);
                itin.getLayovers().add(new Layover(seg1.getArrivalAirport(), seg1.getArrivalTime(), layoverEnd));
            }
            
            itin.setSegments(segments);
            itin.calculateTotalDuration();
            itin.setNumberOfStops(segments.size() - 1);
            itin.setDirectFlight(isDirect);
            itin.setDepartureTime(segments.get(0).getDepartureTime());
            itin.setArrivalTime(segments.get(segments.size() - 1).getArrivalTime());
            
            double basePrice = isDirect ? 1200 : 900;
            itin.setTotalPrice(basePrice + rand.nextInt(500));
            
            mocks.add(itin);
        }
        
        return mocks;
    }
    
    private FlightSegment createMockSegment(String origin, String dest, LocalDateTime dep, Airline airline, int hours) {
        FlightSegment seg = new FlightSegment();
        seg.setFlightNumber(airline.getAirlineCode() + "-" + (100 + new Random().nextInt(900)));
        seg.setAirline(airline);
        Airport depAirport = new Airport(); depAirport.setAirportCode(origin); depAirport.setCity(origin);
        Airport arrAirport = new Airport(); arrAirport.setAirportCode(dest); arrAirport.setCity(dest);
        seg.setDepartureAirport(depAirport);
        seg.setArrivalAirport(arrAirport);
        seg.setDepartureTime(dep);
        seg.setArrivalTime(dep.plusHours(hours));
        seg.setAvailableSeats(50);
        seg.setAircraft("Boeing 777");
        seg.calculateDuration();
        return seg;
    }
}

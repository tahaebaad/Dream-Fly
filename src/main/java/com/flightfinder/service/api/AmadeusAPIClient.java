package com.flightfinder.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightfinder.model.*;
import com.flightfinder.util.ConfigManager;
import com.flightfinder.util.Logger;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AmadeusAPIClient implements IFlightDataProvider {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private String accessToken;
    private LocalDateTime tokenExpiry;
    
    private final String apiKey;
    private final String apiSecret;
    
    private final FlightDataCache cache;
    private boolean useMockMode = false;
    private final MockFlightGenerator mockProvider = new MockFlightGenerator();

    public AmadeusAPIClient(FlightDataCache cache) {
        this.cache = cache;
        this.client = new OkHttpClient.Builder()
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        ConfigManager config = ConfigManager.getInstance();
        this.apiKey = config.getProperty("amadeus.api.key", "");
        this.apiSecret = config.getProperty("amadeus.api.secret", "");
        
        boolean isTestEnv = Boolean.parseBoolean(config.getProperty("amadeus.env.test", "true"));
        this.baseUrl = isTestEnv ? "https://test.api.amadeus.com" : "https://api.amadeus.com";
        
        // Auto-enable mock mode if keys are missing
        if (apiKey.isEmpty() || apiSecret.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            this.useMockMode = true;
            Logger.logInfo("API Keys missing. Switched to MOCK MODE.");
        }
    }

    public void authenticate() throws APIException {
        if (useMockMode) return;
        
        RequestBody formBody = new FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("client_id", apiKey)
            .add("client_secret", apiSecret)
            .build();
        
        Request request = new Request.Builder()
            .url(baseUrl + "/v1/security/oauth2/token")
            .post(formBody)
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Logger.logError("Auth failed, switching to Mock Mode", null);
                useMockMode = true; // Fallback
                return;
            }
            
            String jsonResponse = response.body().string();
            JsonNode root = objectMapper.readTree(jsonResponse);
            this.accessToken = root.get("access_token").asText();
            int expiresIn = root.get("expires_in").asInt();
            this.tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn);
            
            Logger.logInfo("Amadeus API Authenticated. Token expires in " + expiresIn + " seconds.");
            
        } catch (IOException e) {
            Logger.logError("Network error, switching to Mock Mode", e);
            useMockMode = true; // Fallback
        }
    }

    private void refreshTokenIfNeeded() throws APIException {
        if (useMockMode) return;
        if (accessToken == null || tokenExpiry == null || LocalDateTime.now().plusMinutes(5).isAfter(tokenExpiry)) {
            authenticate();
        }
    }

    public List<FlightItinerary> searchFlights(SearchCriteria criteria) throws APIException {
        System.out.println("AmadeusAPIClient: Search requested...");
        try {
            refreshTokenIfNeeded();
        } catch (Exception e) {
             Logger.logError("Token refresh failed, using mock", e);
             System.out.println("Token refresh failed. Switching to mock.");
             useMockMode = true;
        }

        if (useMockMode) {
            System.out.println("AmadeusAPIClient: Using Mock Data Fallback");
            return mockProvider.searchFlights(criteria);
        }

        // Construct URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/v2/shopping/flight-offers").newBuilder();
        urlBuilder.addQueryParameter("originLocationCode", criteria.getOriginAirportCode());
        urlBuilder.addQueryParameter("destinationLocationCode", criteria.getDestinationAirportCode());
        urlBuilder.addQueryParameter("departureDate", criteria.getDepartureDate().toString());
        if (criteria.getReturnDate() != null) {
             urlBuilder.addQueryParameter("returnDate", criteria.getReturnDate().toString());
        }
        urlBuilder.addQueryParameter("adults", String.valueOf(criteria.getNumberOfPassengers()));
        urlBuilder.addQueryParameter("currencyCode", "USD");
        urlBuilder.addQueryParameter("max", "50");
        
        if (criteria.isDirectFlightsOnly()) {
            urlBuilder.addQueryParameter("nonStop", "true");
        }

        String url = urlBuilder.build().toString();
        System.out.println("AmadeusAPIClient: Calling URL: " + url);

        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bearer " + accessToken)
            .get()
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            System.out.println("AmadeusAPIClient: Response received with code " + response.code());
            if (!response.isSuccessful()) {
                 Logger.logError("API request failed: " + response.code(), null);
                 return mockProvider.searchFlights(criteria);
            }
            
            String jsonResponse = response.body().string();
            return parseFlightOffersResponse(jsonResponse);
            
        } catch (IOException e) {
             Logger.logError("Network error during search", e);
             return mockProvider.searchFlights(criteria);
        }
    }

    // --- Mock Data Generation removed. Using MockFlightGenerator strategy instead ---

    private List<FlightItinerary> parseFlightOffersResponse(String jsonResponse) throws IOException {
        JsonNode root = objectMapper.readTree(jsonResponse);
        List<FlightItinerary> itineraries = new ArrayList<>();
        JsonNode data = root.get("data");
        JsonNode dictionaries = root.get("dictionaries"); 
        
        if (data != null && data.isArray()) {
            for (JsonNode offer : data) {
                itineraries.addAll(parseOffer(offer, dictionaries));
            }
        }
        return itineraries;
    }

    private List<FlightItinerary> parseOffer(JsonNode offer, JsonNode dictionaries) {
        List<FlightItinerary> offerItineraries = new ArrayList<>();
        String currency = offer.get("price").get("currency").asText();
        double total = offer.get("price").get("total").asDouble();
        
        JsonNode itinerarySegments = offer.get("itineraries");
        if (itinerarySegments != null) {
            FlightItinerary itinerary = new FlightItinerary();
            itinerary.setItineraryId(offer.get("id").asText());
            itinerary.setTotalPrice(total);
            itinerary.setCurrency(currency);
            // itinerary.setAvailableSeats(...) // Property removed from model
            
            List<FlightSegment> segments = new ArrayList<>();
            List<Layover> layovers = new ArrayList<>();
            
            for (JsonNode itin : itinerarySegments) {
                JsonNode segmentsNode = itin.get("segments");
                for (JsonNode seg : segmentsNode) {
                    FlightSegment flightSegment = parseSegment(seg, dictionaries);
                    segments.add(flightSegment);
                }
            }
            
            itinerary.setSegments(segments);
            
            for (int i = 0; i < segments.size() - 1; i++) {
                FlightSegment current = segments.get(i);
                FlightSegment next = segments.get(i+1);
                 Layover layover = new Layover(current.getArrivalAirport(), current.getArrivalTime(), next.getDepartureTime());
                 layovers.add(layover);
            }
            itinerary.setLayovers(layovers);
            itinerary.calculateTotalDuration();
            itinerary.setNumberOfStops(layovers.size());
            itinerary.setDirectFlight(layovers.isEmpty());
            
            if (!segments.isEmpty()) {
                itinerary.setDepartureTime(segments.get(0).getDepartureTime());
                itinerary.setArrivalTime(segments.get(segments.size() - 1).getArrivalTime());
            }
            
            offerItineraries.add(itinerary);
        }
        
        return offerItineraries;
    }

    private FlightSegment parseSegment(JsonNode seg, JsonNode dictionaries) {
        FlightSegment fs = new FlightSegment();
        
        String depCode = seg.get("departure").get("iataCode").asText();
        String arrCode = seg.get("arrival").get("iataCode").asText();
        String carrierCode = seg.get("carrierCode").asText();
        String number = seg.get("number").asText();
        
        fs.setDepartureAirport(getAirport(depCode, dictionaries));
        fs.setArrivalAirport(getAirport(arrCode, dictionaries));
        
        fs.setDepartureTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(seg.get("departure").get("at").asText(), LocalDateTime::from));
        fs.setArrivalTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(seg.get("arrival").get("at").asText(), LocalDateTime::from));
        
        fs.setFlightNumber(carrierCode + "-" + number);
        
        Airline airline = new Airline();
        airline.setAirlineCode(carrierCode);
        if (dictionaries != null && dictionaries.has("carriers")) {
            JsonNode carrierName = dictionaries.get("carriers").get(carrierCode);
            if (carrierName != null) airline.setAirlineName(carrierName.asText());
            else airline.setAirlineName(carrierCode);
        } else {
             airline.setAirlineName(carrierCode);
        }
        airline.setLogo("https://pics.avs.io/200/200/" + carrierCode + ".png"); 
        fs.setAirline(airline);

        fs.setAircraft(seg.has("aircraft") ? seg.get("aircraft").get("code").asText() : "Unknown");
        fs.calculateDuration();
        
        return fs;
    }

    private Airport getAirport(String code, JsonNode dictionaries) {
        Airport cached = cache.getCachedAirport(code);
        if (cached != null) return cached;
        
        Airport airport = new Airport();
        airport.setAirportCode(code);
        if (dictionaries != null && dictionaries.has("locations") && dictionaries.get("locations").has(code)) {
             airport.setAirportName(code); 
        } else {
            airport.setAirportName(code); 
        }
        
        airport.setCity(code);
        cache.cacheAirport(airport);
        return airport;
    }
}

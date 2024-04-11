package greencity.service;

import com.google.maps.model.GeocodingResult;

public interface GeocodingService {
    GeocodingResult[] getAddress(double latitude, double longitude, String lang);
}
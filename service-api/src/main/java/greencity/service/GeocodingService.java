package greencity.service;

import com.google.maps.model.GeocodingResult;

public interface GeocodingService {
    GeocodingResult[] getGeocodingResultsFromAddress(String address, String lang);
}

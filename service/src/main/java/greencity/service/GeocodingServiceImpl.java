package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {
    private final GeoApiContext context;

    public GeocodingResult[] getAddress(double latitude, double longitude, String lang) {
        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new LatLng(latitude, longitude))
                    .language(lang)
                    .await();
            if (results.length > 0) {
                return results;
            } else {
                throw new Exception("No address found for the given coordinates");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
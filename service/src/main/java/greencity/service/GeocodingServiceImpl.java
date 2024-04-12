package greencity.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import greencity.exception.exceptions.GeocodeFailedException;
import greencity.exception.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static greencity.constant.ErrorMessage.*;
import static java.util.Objects.*;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {
    private final GeoApiContext context;

    public GeocodingResult[] getGeocodingResultsFromAddress(String address, String lang) {
        var geocodingResults = tryGetGeocodingResults(address, lang);
        checkAreGeocodingPresent(geocodingResults,address);

        return geocodingResults;
    }

    private void checkAreGeocodingPresent(GeocodingResult[] geocodingResults, String address) {
        if (isNull(geocodingResults) || geocodingResults.length == 0){
            throw new NotFoundException(GEOCODE_NOT_FOUND_BY_ADDRESS + address);
        }
    }

    private GeocodingResult[] tryGetGeocodingResults(String address, String lang) {
        try {
            return getGeocodingResults(address, lang);
        } catch (IOException | InterruptedException | ApiException e) {
            throw new GeocodeFailedException(GEOCODE_LOADING_FAILED + address);
        }
    }

    private GeocodingResult[] getGeocodingResults(String address, String lang) throws IOException, InterruptedException, ApiException {
        return GeocodingApi.geocode(context, address)
                .language(lang)
                .await();
    }
}

package greencity.service;

import greencity.dto.place.PlaceInfoDto;
import greencity.dto.place.PlaceUpdateDto;
import greencity.repository.PlaceRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepo placeRepo;
    private final ModelMapper modelMapper;

    @Override
    public PlaceInfoDto getInfo(Long id){
        return modelMapper.map(placeRepo.findById(id), PlaceInfoDto.class);
    }

    @Override
    public PlaceUpdateDto getPlaceById(Long id){
        return modelMapper.map(placeRepo.findById(id), PlaceUpdateDto.class);
    }
}

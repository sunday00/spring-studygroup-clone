package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Location;
import lec.spring.studygroupclone.Models.Tag;
import lec.spring.studygroupclone.Repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    @PostConstruct
    public void initLocationService() throws IOException {
        if ( locationRepository.count() == 0 ){
            Resource resource = new ClassPathResource("city_list_korea.csv");
            List<Location> list = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(line -> {
                        String[] columns = line.split(",");
                        return Location.builder().city(columns[0]).localName(columns[1]).province(columns[2]).build();
                    }).collect(Collectors.toList());
            locationRepository.saveAll(list);
        }
    }

    public List<String> getAllLocations() {
        List<Location> list = locationRepository.findAll();
        if( list.size() == 0 ) return new ArrayList<String>(Collections.singletonList(""));
        return list.stream().map(Location::toString).collect(Collectors.toList());
    }

    public Location findByCity(Location location) {
        return locationRepository.findByCity(location.getCity());
    }

    public Location findByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public Location findById(int id) {
        return locationRepository.getOne(Long.parseLong(String.valueOf(id)));
    }
}

package lec.spring.studygroupclone.Services;

import lec.spring.studygroupclone.Models.Location;
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
}

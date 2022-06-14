package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    private static final int MIN_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        validateAddSection(section);
        if (getStations().isEmpty()) {
            sections.add(section);
            return;
        }

        if (hasStation(section.getUpStation())) {
            addFromUpStation(section);
            return;
        }

        if (hasStation(section.getDownStation())) {
            addFromDownStation(section);
            return;
        }
        throw new RuntimeException();
    }

    public void removeStation(Station station) {
        validateRemoveStation();

        Optional<Section> upLineStation = getUpLineStation(station);
        Optional<Section> downLineStation = getDownLineStation(station);

        if (upLineStation.isPresent() && downLineStation.isPresent()) {
            changeLineUpStation(upLineStation.get(), downLineStation.get());
        }

        upLineStation.ifPresent(it -> sections.remove(it));
        downLineStation.ifPresent(it -> sections.remove(it));
    }

    private void changeLineUpStation(Section upLineStation, Section downLineStation) {
        sections.add(
                new Section.Builder()
                        .line(upLineStation.getLine())
                        .upStation(downLineStation.getUpStation())
                        .downStation(upLineStation.getDownStation())
                        .distance(upLineStation.getDistance().sum(downLineStation.getDistance()))
                        .build()
        );
    }

    private Optional<Section> getUpLineStation(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();
    }

    private Optional<Section> getDownLineStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst();
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        Optional<Section> nextLineStation = findNextStationFromUpStation(downStation);

        while (nextLineStation.isPresent()) {
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);

            nextLineStation = findNextStationFromUpStation(downStation);
        }

        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        Optional<Section> nextLineStation = findNextStationFromDownStation(downStation);
        while (nextLineStation.isPresent()) {
            downStation = nextLineStation.get().getUpStation();
            nextLineStation = findNextStationFromDownStation(downStation);
        }

        return downStation;
    }

    private Optional<Section> findNextStationFromUpStation(Station station) {
        return sections.stream()
                .filter(it -> it.isSameUpStation(station))
                .findFirst();
    }

    private Optional<Section> findNextStationFromDownStation(Station station) {
        return sections.stream()
                .filter(it -> it.isSameDownStation(station))
                .findFirst();
    }

    private boolean hasStation(Station station) {
        return getStations().stream().anyMatch(it -> it == station);
    }


    private void addFromDownStation(Section section) {
        sections.stream()
                .filter(it -> it.isSameDownStation(section))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));

        sections.add(section);
    }

    private void addFromUpStation(Section section) {
        sections.stream()
                .filter(it -> it.isSameUpStation(section))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));

        sections.add(section);
    }

    private void validateAddSection(Section section) {
        if (hasStation(section.getUpStation()) && hasStation(section.getDownStation())) {
            throw new RuntimeException("이미 등록된 구간 입니다.");
        }

        List<Station> stations = getStations();
        if (!stations.isEmpty() && stations.stream().noneMatch(it -> it == section.getUpStation()) &&
                stations.stream().noneMatch(it -> it == section.getDownStation())) {
            throw new RuntimeException("등록할 수 없는 구간 입니다.");
        }
    }

    private void validateRemoveStation() {
        if (sections.size() <= MIN_SIZE) {
            throw new RuntimeException("구간이 하나 밖에 존재하지 않으면, 역을 삭제 할 수 없습니다.");
        }
    }

}

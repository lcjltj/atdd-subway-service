package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    private final WeightedMultigraph<Station, SectionEdge> path;
    private ShortestPathStrategy strategy;

    public PathFinder() {
        path = new WeightedMultigraph<>(SectionEdge.class);
    }

    public void decideShortestPathStrategy(ShortestPathStrategy strategy) {
        this.strategy = strategy;
    }

    public Path findShortestPath(List<Sections> allSections, Station source, Station target) {
        drawPath(allSections);
        validateStation(source, target);
        return strategy.findShortestPath(path, source, target);
    }

    private void validateStation(Station source, Station target) {
        if (isEquals(source, target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
        if (hasNot(source, target)) {
            throw new IllegalArgumentException("출발역 혹은 도착역이 존재하지 않습니다.");
        }
    }

    private boolean hasNot(Station source, Station target) {
        return !path.containsVertex(source) || !path.containsVertex(target);
    }

    private boolean isEquals(Station source, Station target) {
        return source.equals(target);
    }

    private void drawPath(List<Sections> allSections) {
        allSections.forEach(sections -> {
            addStations(sections.findOrderedAllStations());
            connectSections(sections.getSections());
        });
    }

    private void connectSections(List<Section> sections) {
        sections.forEach(this::addSectionToEdge);
    }

    private void addSectionToEdge(Section section) {
        SectionEdge sectionEdge = new SectionEdge(section);
        path.addEdge(section.getUpStation(), section.getDownStation(), sectionEdge);
        path.setEdgeWeight(sectionEdge, section.getDistance());
    }

    private void addStations(List<Station> allStations) {
        allStations.forEach(path::addVertex);
    }
}

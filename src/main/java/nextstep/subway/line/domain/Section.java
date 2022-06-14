package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @Embedded
    private Distance distance;

    public static class Builder {
        private Line line;
        private Station upStation;
        private Station downStation;
        private Distance distance;

        public Builder() {
        }

        public Builder(Line line, Station upStation, Station downStation, Distance distance) {
            this.line = line;
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        public Builder line(Line line) {
            this.line = line;
            return this;
        }

        public Builder upStation(Station station) {
            this.upStation = station;
            return this;
        }

        public Builder downStation(Station station) {
            this.downStation = station;
            return this;
        }

        public Builder distance(Distance distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(line, upStation, downStation, distance);
        }
    }

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public void updateUpStation(Station station, Distance newDistance) {
        if (this.distance.getValue() <= newDistance.getValue()) {
            throw new RuntimeException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        this.upStation = station;
        this.distance = this.distance.minus(newDistance);
    }

    public void updateDownStation(Station station, Distance newDistance) {
        if (this.distance.getValue() <= newDistance.getValue()) {
            throw new RuntimeException("역과 역 사이의 거리보다 좁은 거리를 입력해주세요");
        }
        this.downStation = station;
        this.distance = this.distance.minus(newDistance);
    }

    public boolean isSameUpStation(Section target) {
        return upStation.equals(target.upStation);
    }

    public boolean isSameDownStation(Section target) {
        return downStation.equals(target.downStation);
    }

    public boolean isSameUpStation(Station targetStation) {
        return upStation.equals(targetStation);
    }

    public boolean isSameDownStation(Station targetStation) {
        return downStation.equals(targetStation);
    }
}

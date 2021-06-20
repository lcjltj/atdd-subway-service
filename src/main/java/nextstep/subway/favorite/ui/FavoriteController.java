package nextstep.subway.favorite.ui;

import java.net.URI;
import java.util.List;
import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.favorite.application.FavoriteService;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.favorite.dto.FavoriteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping(value = "/favorites")
    public ResponseEntity<URI> createFavorite(@AuthenticationPrincipal LoginMember loginMember,
                                              @RequestBody FavoriteRequest favoriteRequest) {

        Long id = favoriteService.save(loginMember.getId(),
                                       favoriteRequest.getSource(),
                                       favoriteRequest.getTarget());

        return ResponseEntity.created(URI.create("/favorites" + id)).build();
    }

    @GetMapping(value = "/favorites")
    public ResponseEntity<List<FavoriteResponse>> findFavorite(@AuthenticationPrincipal LoginMember loginMember) {

        List<FavoriteResponse> responses = favoriteService.findAllByMember(loginMember.getId())
                                                          .stream()
                                                          .map(FavoriteResponse::of)
                                                          .collect(toList());

        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal LoginMember loginMember,
                                               FavoriteRequest favoriteRequest) {
        return null;
    }
}

package hcmute.puzzle.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchBetween {
    String fieldSearch;
    ModelQuery min;
    ModelQuery max;
}

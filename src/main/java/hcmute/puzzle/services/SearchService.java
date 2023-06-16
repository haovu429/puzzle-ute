package hcmute.puzzle.services;

import hcmute.puzzle.infrastructure.models.ModelQuery;
import hcmute.puzzle.infrastructure.models.SearchBetween;

import java.util.List;
import java.util.Map;

public interface SearchService{

    List filterObject( String objectName,
                       // List<Long> manufacturer,
                       List<SearchBetween> searchBetweens,
                       Map<String, List<ModelQuery>> fieldSearchValue,
                       Map<String, List<ModelQuery>> valueSpecialAttributes,
                       List<String> commonFieldSearch,
                       List<ModelQuery> valueCommonFieldSearch,
                       int noOfRecords,
                       int pageIndex,
                       boolean sortById);
}

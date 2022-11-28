package hcmute.puzzle.services;

import hcmute.puzzle.model.SearchBetween;

import java.util.List;
import java.util.Map;

public interface SearchService{

    List filterObject(String objectName,
                      //List<Long> manufacturer,
                      List<SearchBetween> searchBetween,
                      Map<String,List<String>> fieldSearchText,
                      Map<String, List<Long>> fieldSearchNumber,
                      List<String> commonFieldSearch,
                      List<String> valueCommonFieldSearch,
                      int noOfRecords,
                      int pageIndex,
                      boolean sortById);
}

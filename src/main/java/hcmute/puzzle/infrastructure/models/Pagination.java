package hcmute.puzzle.infrastructure.models;

import lombok.Data;

@Data
public class Pagination {
	private int page = 0;
	private int size = 6;
}

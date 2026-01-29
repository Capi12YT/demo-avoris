package demo.avoris.application.port.out;

import demo.avoris.domain.model.Search;

public interface SearchRepositoryPort {

    Search save(Search search);

    Search findBySearchId(String searchId);
}

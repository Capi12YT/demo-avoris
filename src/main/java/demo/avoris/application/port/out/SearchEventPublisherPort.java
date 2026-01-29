package demo.avoris.application.port.out;

import demo.avoris.domain.model.Search;

public interface SearchEventPublisherPort {
    void publishSearch(Search search);
}

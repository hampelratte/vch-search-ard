package de.berlios.vch.search.ard;

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.framework.ServiceException;
import org.osgi.service.log.LogService;

import de.berlios.vch.parser.IOverviewPage;
import de.berlios.vch.parser.IVideoPage;
import de.berlios.vch.parser.IWebPage;
import de.berlios.vch.parser.IWebParser;
import de.berlios.vch.parser.OverviewPage;
import de.berlios.vch.search.ISearchProvider;

@Component
@Provides
public class ArdSearchProvider implements ISearchProvider {

    public static final String BASE_URL = "http://www.ardmediathek.de";

    private static final String SEARCH_PAGE = BASE_URL + "/tv/suche?source=tv&searchText=";

    public static final String CHARSET = "UTF-8";

    public static Map<String, String> HTTP_HEADERS = new HashMap<String, String>();

    static {
        HTTP_HEADERS.put("User-Agent", "Mozilla/5.0 (X11; Linux i686; rv:35.0) Gecko/20100101 Firefox/35.0");
        HTTP_HEADERS.put("Accept-Language", "de-de,de;q=0.8,en-us;q=0.5,en;q=0.3");
    }

    @Requires(filter = "(instance.name=vch.parser.ard)")
    private IWebParser parser;

    @Requires
    private LogService logger;

    @Override
    public String getName() {
        return parser.getTitle();
    }

    @Override
    public IOverviewPage search(String query) throws Exception {
        if (parser == null) {
            throw new ServiceException("ARD Mediathek Parser is not available");
        }

        // execute the search (parsing is completely done by the parser module)
        String uri = SEARCH_PAGE + URLEncoder.encode(query, "UTF-8");
        IOverviewPage opage = new OverviewPage();
        opage.setParser(getId());
        opage.setUri(new URI(uri));
        parser.parse(opage);
        return opage;
    }

    @Override
    public String getId() {
        return parser.getId();
    }

    @Override
    public IWebPage parse(IWebPage page) throws Exception {
        if (parser == null) {
            throw new ServiceException("ARD Mediathek Parser is not available");
        }

        if (page instanceof IVideoPage) {
            return parser.parse(page);
        }

        return page;
    }
}

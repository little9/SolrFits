package org.solrfits;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Path;

/**
 * Created by jlittle on 7/6/16.
 */
public class FitsRunner {

    private SolrClient sc;
    private File file;
    private Fits fits;
    private FitsExaminer fitsExaminer;

    FitsRunner(SolrClient solrClient, File file, Fits fits) {
        this.fits = fits;
        this.sc = solrClient;
        this.file = file;
    }

    void run() {
        try {
            fitsExaminer = new FitsExaminer(fits, sc);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            fitsExaminer.examineFile(file);
        } catch (FitsException | IOException | SolrServerException | XMLStreamException e) {
            e.printStackTrace();
        }

    }
}

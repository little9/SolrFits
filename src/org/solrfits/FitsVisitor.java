package org.solrfits;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.*;

/**
 * Created by jamie on 7/3/16.
 */
class FitsVisitor extends SimpleFileVisitor<Path> {
    private final Fits fits;
    private final SolrClient sc;

    FitsVisitor(Fits myFits, SolrClient mySc) {
        fits = myFits;
        sc = mySc;
    }

    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attr) throws UnknownHostException {

        if (attr.isRegularFile() && !file.getFileName().toString().contains(".mtf") && !file.getFileName().toString().contains("checksum_manifest")) {


            FitsExaminer fj = new FitsExaminer(fits,sc);

            try {
                try {
                    fj.examineFile(file.toFile());
                } catch (SolrServerException | XMLStreamException e) {
                    e.printStackTrace();
                }
            } catch (FitsException | IOException e) {
                e.printStackTrace();
            }
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
}

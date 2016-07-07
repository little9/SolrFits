package org.solrfits;

import edu.harvard.hul.ois.fits.Fits;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by jamie on 7/3/16.
 */
class FitsVisitor extends SimpleFileVisitor<Path> {
    private final Fits fits;
    private String fp;
    private final SolrClient sc;

    FitsVisitor(Fits myFits, SolrClient mySc) throws NoSuchAlgorithmException {
        fits = myFits;
        sc = mySc;
    }

    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attr) throws IOException {

        if (attr.isRegularFile() && !file.getFileName().toString().contains(".mtf") && !file.getFileName().toString().contains("checksum_manifest")) {
            DupeChecker dupeChecker = new DupeChecker(file, sc);
            try {
                if (!dupeChecker.inSolr()) {

                    new FitsRunner(sc,file.toFile(),fits).run();

                } else {
                    System.out.println("Skipping " + file.getFileName());
                }
            } catch (SolrServerException e) {
                e.printStackTrace();
            }
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
        System.err.println(exc);
        System.err.println(exc);
        return CONTINUE;
    }
}

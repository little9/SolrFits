package org.solrfits;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static java.nio.file.Files.walkFileTree;

public class Main {

    public static void main(String[] args) throws IOException, FitsConfigurationException, NoSuchAlgorithmException {

        if (args.length < 2) {
            System.out.println("You need to add the path to the FITS directory, the URI of your Solr server, and the path to the directory to index.");
        } else {
            Fits fits = new Fits(args[0]);
            String urlString = args[1];
            SolrClient solr = new HttpSolrClient.Builder(urlString).build();
            DupeChecker dupeChecker = new DupeChecker(solr);
            FitsVisitor fv = new FitsVisitor(fits,solr,dupeChecker);

            Path startingDir = Paths.get(args[2]);
            walkFileTree(startingDir, fv);
        }

    }
}

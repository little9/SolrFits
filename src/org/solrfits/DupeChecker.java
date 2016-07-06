package org.solrfits;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jlittle on 7/6/16.
 */
class DupeChecker {

    private Path file;
    private SolrClient solrClient;
    private boolean status;
    private MessageDigest messageDigest;

    DupeChecker(Path file, SolrClient solrClient)  {
        this.file = file;
        this.solrClient = solrClient;

        try {
            this.messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    boolean inSolr() throws IOException, SolrServerException {
        // Check Solr to see if the file has already been index by getting the md5 checksum
        // of the file and doing a Solr query using that checksum. Don't run FITS if the
        // file already exists.
        try (InputStream inputStream = Files.newInputStream(this.file)) {
            byte[] buffer = new byte[8192];
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);

            try {
                while (digestInputStream.read(buffer) != -1) ;
            } finally {
                digestInputStream.close();
            }

            SolrQuery md5Query = new SolrQuery();
            md5Query.setQuery("md5checksum:" + DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase());
            QueryResponse queryResponse = solrClient.query(md5Query);

            if (queryResponse.getResults().getNumFound() == 0) {
             this.status = false;
            } else{
                this.status = true;
            }
        }
        return this.status;
    }
}

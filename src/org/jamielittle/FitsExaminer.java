package org.jamielittle;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Created by jamie on 7/3/16.
 */
public class FitsExaminer {

    public void setFits(Fits fits) {
        this.fits = fits;
    }

    private Fits fits;
    private SolrClient solr;

    public void setSolr(SolrClient solr) {
        this.solr = solr;
    }

    public FitsExaminer(Fits myFits, SolrClient sc) throws UnknownHostException {
        setFits(myFits);
        setSolr(sc);
    }


    public void indexFitsXml(Document fitsXml, SolrInputDocument solrDoc) {
        /* Add the full FITS XML content to the index */
        solrDoc.addField("fits_xml", new XMLOutputter().outputString(fitsXml));
    }

    public void indexFitsFileInfo(FitsOutput fitsOutput, SolrInputDocument solrDoc) {
        for (FitsMetadataElement metadataElement : fitsOutput.getFileInfoElements()) {
            System.out.println(metadataElement.getName() + " : " + metadataElement.getValue());
            solrDoc.addField(metadataElement.getName(), metadataElement.getValue());
        }
    }

    public void indexFitsIdentities(FitsOutput fitsOutput, SolrInputDocument solrDoc) {
        for (FitsIdentity fitsIdentity : fitsOutput.getIdentities()) {
            //  solrDoc.addField("mime_type", fitsIdentity.getMimetype());
            solrDoc.addField("format", fitsIdentity.getFormat());
        }
    }

    public void indexTechMetadata(FitsOutput fitsOutput, SolrInputDocument solrDoc) {
        try {
            for (FitsMetadataElement el : fitsOutput.getTechMetadataElements()) {
                solrDoc.addField(el.getName(), el.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String examineFile(File file) throws FitsException, IOException, SolrServerException, XMLStreamException {

        FitsOutput fitsOutput = fits.examine(file);


        fitsOutput.addStandardCombinedFormat();

        SolrInputDocument solrDoc = new SolrInputDocument();

        solrDoc.addField("id", UUID.randomUUID().toString());

        this.indexFitsFileInfo(fitsOutput, solrDoc);
        this.indexFitsIdentities(fitsOutput, solrDoc);
        this.indexTechMetadata(fitsOutput, solrDoc);

        Document fitsXml = fitsOutput.getFitsXml();
        this.indexFitsXml(fitsXml, solrDoc);

        try {
            UpdateResponse response = solr.add(solrDoc);
            solr.commit();

        } catch (HttpSolrClient.RemoteSolrException e) {
            e.printStackTrace();
        }

        return "";
    }
}

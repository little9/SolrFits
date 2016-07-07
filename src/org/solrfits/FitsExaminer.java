package org.solrfits;

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
import org.apache.solr.common.SolrInputField;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import java.net.UnknownHostException;
import java.util.*;


/**
 * Created by jamie on 7/3/16.
 */
class FitsExaminer {
    private void setFits(Fits fits) {
        this.fits = fits;
    }

    private Fits fits;
    private SolrClient solr;

    private void setSolr(SolrClient solr) {
        this.solr = solr;
    }

    FitsExaminer(Fits myFits, SolrClient sc) throws UnknownHostException {
        setFits(myFits);
        setSolr(sc);
    }


    private void indexFitsXml(Document fitsXml, SolrInputDocument solrDoc) {
        solrDoc.addField("fits_xml", new XMLOutputter().outputString(fitsXml));
    }

    private void indexFitsFileInfo(FitsOutput fitsOutput, SolrInputDocument solrDoc) {
        for (FitsMetadataElement metadataElement : fitsOutput.getFileInfoElements()) {
            solrDoc.addField(metadataElement.getName(), metadataElement.getValue());
        }
    }

    private void indexFitsIdentities(FitsOutput fitsOutput, SolrInputDocument solrDoc) {
        for (FitsIdentity fitsIdentity : fitsOutput.getIdentities()) {
            solrDoc.addField("format", fitsIdentity.getFormat());
        }
    }

    private void indexTechMetadata(FitsOutput fitsOutput, SolrInputDocument solrDoc) {
        try {
            for (FitsMetadataElement el : fitsOutput.getTechMetadataElements()) {
                solrDoc.addField(el.getName(), el.getValue());
            }
        } catch (Exception ignored) {
        }
    }

    void examineFile(File file) throws FitsException, IOException, SolrServerException, XMLStreamException {
        System.out.println("Adding " + file.getName());
        FitsOutput fitsOutput = fits.examine(file);
        fitsOutput.addStandardCombinedFormat();

        SolrInputDocument solrDoc = new SolrInputDocument();

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

    }
}

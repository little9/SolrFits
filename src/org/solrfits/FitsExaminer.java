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
    private Set<String> elementSet;


    private void setSolr(SolrClient solr) {
        this.solr = solr;
    }

    FitsExaminer(Fits myFits, SolrClient sc) throws UnknownHostException {
        setFits(myFits);
        setSolr(sc);
        this.elementSet = new HashSet<String>();
    }


    private void indexFitsXml(Document fitsXml, SolrInputDocument solrDoc) {
        /* Add the full FITS XML content to the index */
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
                System.out.println(el.getName() + ":" + el.getValue());

                //this.elementSet.add("<field name=\"" + el.getName() + "\" type=\"string\" indexed=\"true\" stored=\"true\" />");
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

            /*
            if (this.elementSet != null) {
                for (String s : this.elementSet) {
                    System.out.println(s);
                }
            }
            */

            e.printStackTrace();
        }

    }
}

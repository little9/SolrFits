# SolrFits

This is a Java application that uses FITS to generate metadata which is then indexed in Solr – it 
is very much a work in progress. 

# Running SolrFits

* Download the repository and create a new Solr core
* Replace the managed_schema and solr_config.xml files in the core's conf folder with the files located in the /solr folder of this repository
* Start Solr
* Run the jar with the path to fits, the URI of your Solr server, and a path to some files:

```bash
java -jar SolrFits.jar "/path/to/fits-1.0.0" "http://localhost:8983/solr/fits" "/path/to/be/analyzed"
```





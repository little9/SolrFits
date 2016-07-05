# SolrFits

This is a Java application that uses FITS to generate metadata which is then indexed in Solr – it 
is very much a work in progress. 

Running the app:

```bash
java -jar SolrFits.jar "/path/to/fits-1.0.0" "http://localhost:8983/solr/fits" "/path/to/be/analyzed"
```

You'll need a Solr core with this <a href="https://gist.github.com/little9/aaa4d0984afd362691ed3967544c980c">managed schema</a>.

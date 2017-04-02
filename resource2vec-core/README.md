# Resource2Vec
Resource2Vec is a knowledge graph embedding model for RDF graphs based on Latent Semantic Analysis. Textual information (e.g., labels, abstracts) are extracted from data and used to build a tf-idf (term frequency-inverse document frequency) matrix for each instance in the graph. The dimensions of the feature vectors are then reduced using SVD (singular value decomposition).

## Examples

### Command-line

```bash
$ java -Xmx4g -jar resource2vec-core.jar \
    --dataset src/test/resources/person11-lite.rdf \
    --classUri null \
    --ns http://www.okkam.org/oaie/person1- \
    --ttype ujmp-svd \
    --dim 3
```

### Programmatically

```java
R2VModel model = R2VManager.train("src/test/resources/person11-lite.rdf", new TfidfFEXStrategy());
model.reduce("ujmp-svd", "http://www.okkam.org/oaie/person1-", "3");
Iterator<R2VInstance> it = model.getInstances().values().iterator();
while(it.hasNext()) {
  R2VInstance inst = it.next();
  logger.info(inst.getUri() + "\t" + inst.getReducedV());
}
```

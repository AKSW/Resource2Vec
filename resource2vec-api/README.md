# Resource2Vec API
Resource2Vec provides knowledge graph embeddings of RDF graphs.

### Online service
Read the [API documentation](https://w3id.org/resource2vec/api) to query our service.

### Deploy your own instance
Just run the following:

```bash
mkdir -p /tmp/resource2vec/datasets/
mvn spring-boot:run -Dserver.port=8484
```

Your service should be up at [http://localhost:8484/](http://localhost:8484/).

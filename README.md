# InChI Key Recoding 

This project demonstrates how to decode an InChIKey into a binary representation
and pack it more optimally for database storage/retrieval.

# Quickstart

```shell
# Build
$ mvn install
# compress
$ java -jar target/ikeyzip.jar input.inchikeys output.packed
# decompress
$ java -jar target/ikeyzip.jar -d input.packed output.inchikeys 
```
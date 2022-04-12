# SPID/CIE OIDC Federation Starter Kit

Both Snapshots and Released artifacts are available on [GitHub Packages](https://github.com/orgs/italia/packages?repo_name=spid-cie-oidc-java):

* if you use Maven

```xml
<dependency>
  <groupId>it.spid.cie.oidc</groupId>
  <artifactId>it.spid.cie.oidc.starter.kit</artifactId>
  <version><!--replace with the wanted version --></version>
</dependency>
```

* if you use Gradle

```
implementation group: 'it.spid.cie.oidc', name: 'it.spid.cie.oidc.starter.kit', version: 'xxx'
```

Unfortunately, as stated in the [documentation](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages), to use GitHub packages you have define GitHub repository in your `~/.m2/settings.xml` together with your credentials.


The "starter-kit" is a _backend_ library with few dependencies:
* [`org.json:json`](https://github.com/stleary/JSON-java), a simple and light-weigth to parse and create JSON documents
* [`com.nimbusds:nimbus-jose-jwt`](https://connect2id.com/products/nimbus-jose-jwt), the most popular java Library to manage JSON Web Token (JWT, JWE, JWS)
* [`com.github.stephenc.jcip:jcip-annotations:1.0-1`](https://mvnrepository.com/artifact/com.github.stephenc.jcip/jcip-annotations/1.0-1), a clean room implementation of the JCIP Annotations
* [`org.slf4j:slf4j-api`](https://mvnrepository.com/artifact/org.slf4j/slf4j-api)


## PersistenceAdapter

The starter-kit doesn't interact directly with the database: it delegates to the implementer this job.

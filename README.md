# `gitar`

[![Travis CI Build Status](https://travis-ci.org/gitenter/gitar.svg?branch=master)](https://travis-ci.org/gitenter/gitar)
[![CircleCI](https://circleci.com/gh/gitenter/gitar.svg?style=svg)](https://circleci.com/gh/gitenter/gitar)

`gitar` is an object-orient API wrapper on top of JGit.

## Usage

```
<dependency>
  <groupId>com.gitenter</groupId>
  <artifactId>gitar</artifactId>
  <version>0.0.2-prototype</version>
</dependency>
```

## Compile and Install

This repository should be compiled in Java 10/11 (notice that there's [no openjdk-10 but only has openjdk-11 on Ubuntu 18.04](https://askubuntu.com/questions/1037646/why-is-openjdk-10-packaged-as-openjdk-11)). The produced jar is compatible with Java 8+, which makes it Spring compatible (Spring only supports Java 8 yet).

```
mvn clean install
```

## Deployment

```
mvn clean deploy
```

Then the result files will be in `/deploy` folder. (TODO: It seems I cannot specify repository-id which deployment location I want?)

### Maven Central Repository

#### Useful links

- https://maven.apache.org/repository/guide-central-repository-upload.html
- https://central.sonatype.org/pages/requirements.html

#### GPG

```
brew install gnupg
```

Follow [this link](https://central.sonatype.org/pages/working-with-pgp-signatures.html). If the gpg server is down, one can list all the available ones using `gpg-connect-agent --dirmngr 'keyserver --hosttable'`, and upload to another one.

```
$ gpg --list-keys
gpg: checking the trustdb
gpg: marginals needed: 3  completes needed: 1  trust model: pgp
gpg: depth: 0  valid:   1  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 1u
gpg: next trustdb check due at 2021-03-07
/Users/cynthia/.gnupg/pubring.kbx
---------------------------------
pub   rsa2048 2019-03-08 [SC] [expires: 2021-03-07]
      1F42253DD11F1E7718F35DACC6D9969E8A7371F8
uid           [ultimate] Cong-Xin Qiu <ozooxo@gmail.com>
sub   rsa2048 2019-03-08 [E] [expires: 2021-03-07]

$ gpg-connect-agent --dirmngr 'keyserver --hosttable'
$ gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 1F42253DD11F1E7718F35DACC6D9969E8A7371F8
$ gpg --keyserver hkp://keyserver.timlukas.de --send-keys 1F42253DD11F1E7718F35DACC6D9969E8A7371F8
$ gpg --keyserver hkp://keyserver.timlukas.de --recv-keys 1F42253DD11F1E7718F35DACC6D9969E8A7371F8
$ gpg --keyserver keyserver-prod-fsn1-01.2ndquadrant.it --send-keys 1F42253DD11F1E7718F35DACC6D9969E8A7371F8
$ gpg --keyserver keyserver-prod-fsn1-01.2ndquadrant.it --recv-keys 1F42253DD11F1E7718F35DACC6D9969E8A7371F8
```

#### Hosting in/Deploy to OSSRH

Open Source Software Repository Hosting (OSSRH): https://central.sonatype.org/pages/ossrh-guide.html

Generating `.asc` files is basically

```
$ cd deploy/com/gitenter/gitar/0.0.2-prototype/
$ gpg -ab gitar-0.0.2-prototype.jar
$ gpg -ab gitar-0.0.2-prototype.pom
$ gpg -ab gitar-0.0.2-prototype-sources.jar
$ gpg -ab gitar-0.0.2-prototype-javadoc.jar
```

but it can be automatic using `nexus-staging-maven-plugin` with `maven-gpg-plugin`.

Deployment

```
mvn clean deploy
```

If got `No public key: Key with id: (c6d9969e8a7371f8) was not able to be located on <a href=http://pool.sks-keyservers.net:11371/&gt;http://pool.sks-keyservers.net:11371/&lt;/a&gt;. Upload your public key and try the operation again.`, that means the public key has not been propagate. Wait several hours and try again.

If got `gpg: signing failed: Inappropriate ioctl for device` error, not sure the exact reason but follow [this link](https://d.sb/2016/11/gpg-inappropriate-ioctl-for-device-errors) works.

Add to `~/.gnupg/gpg.conf`:

```
use-agent
pinentry-mode loopback
```

Add to `~/.gnupg/gpg-agent.conf`

```
allow-loopback-pinentry
```

Then

```
...
[INFO] --- maven-gpg-plugin:1.5:sign (sign-artifacts) @ gitar ---
[INFO]
[INFO] --- maven-install-plugin:3.0.0-M1:install (default-install) @ gitar ---
[INFO] Installing ~/Workspace/gitar/target/gitar-0.0.2-prototype.jar to ~/.m2/repository/com/gitenter/gitar/0.0.2-prototype/gitar-0.0.2-prototype.jar
...
[INFO]
[INFO] --- nexus-staging-maven-plugin:1.6.7:deploy (injected-nexus-deploy) @ gitar ---
[INFO] Performing local staging (local stagingDirectory="/Users/cynthia/Workspace/gitar/target/nexus-staging/staging")...
[INFO]  + Using server credentials "ossrh" from Maven settings.
[INFO]  * Connected to Nexus at https://oss.sonatype.org:443/, is version 2.14.11-01 and edition "Professional"
[INFO]  * Using staging profile ID "a0db7f06860d88" (matched by Nexus).
[INFO] Installing /Users/cynthia/Workspace/gitar/target/gitar-0.0.2-prototype.jar to /Users/cynthia/Workspace/gitar/target/nexus-staging/staging/a0db7f06860d88/com/gitenter/gitar/0.0.2-prototype/gitar-0.0.2-prototype.jar
...
[INFO] Installing /Users/cynthia/Workspace/gitar/target/gitar-0.0.2-prototype-sources.jar.asc to /Users/cynthia/Workspace/gitar/target/nexus-staging/staging/a0db7f06860d88/com/gitenter/gitar/0.0.2-prototype/gitar-0.0.2-prototype-sources.jar.asc
[INFO] Performing remote staging...
[INFO]
[INFO]  * Remote staging into staging profile ID "a0db7f06860d88"
[INFO]  * Created staging repository with ID "comgitenter-1003".
[INFO]  * Staging repository at https://oss.sonatype.org:443/service/local/staging/deployByRepositoryId/comgitenter-1003
[INFO]  * Uploading locally staged artifacts to profile com.gitenter
Uploading to ossrh: https://oss.sonatype.org:443/service/local/staging/deployByRepositoryId/comgitenter-1003/com/gitenter/gitar/0.0.2-prototype/gitar-0.0.2-prototype-sources.jar
Uploaded to ossrh: https://oss.sonatype.org:443/service/local/staging/deployByRepositoryId/comgitenter-1003/com/gitenter/gitar/0.0.2-prototype/gitar-0.0.2-prototype-sources.jar (37 kB at 5.0 kB/s)
...
[INFO]  * Upload of locally staged artifacts finished.
[INFO]  * Closing staging repository with ID "comgitenter-1003".

Waiting for operation to complete...
.......

[INFO] Remote staged 1 repositories, finished with success.
[INFO] Remote staging repositories are being released...

Waiting for operation to complete...
...........

[INFO] Remote staging repositories released.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 02:25 min
[INFO] Finished at: 2019-03-11T06:57:58-04:00
[INFO] ------------------------------------------------------------------------
```

It seems after that no need to do anything special through [this link](https://central.sonatype.org/pages/releasing-the-deployment.html). The repository is already in https://oss.sonatype.org/ and properly released.

Repository URL for release download access: https://repo1.maven.org/maven2/com/gitenter/gitar/0.0.2-prototype/

Repository group that contains snapshots and releases: https://repo1.maven.org/maven2/com/gitenter/gitar/0.0.2-prototype/

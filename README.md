# `gitar`

[![Travis CI Build Status](https://travis-ci.org/gitenter/gitar.svg?branch=master)](https://travis-ci.org/gitenter/gitar)
[![CircleCI](https://circleci.com/gh/gitenter/gitar.svg?style=svg)](https://circleci.com/gh/gitenter/gitar)

`gitar` is an object-orient API wrapper on top of JGit.

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

#### Generate `.asc` files

```
brew install gnupg
```

Then follow [this link](https://central.sonatype.org/pages/working-with-pgp-signatures.html).

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
$ gpg --keyserver hkp://keyserver.timlukas.de --send-keys 1F42253DD11F1E7718F35DACC6D9969E8A7371F8

$ cd deploy/com/gitenter/gitar/0.0.2-prototype/
$ gpg -ab gitar-0.0.2-prototype.jar
$ gpg -ab gitar-0.0.2-prototype.pom
$ gpg -ab gitar-0.0.2-prototype-sources.jar
$ gpg -ab gitar-0.0.2-prototype-javadoc.jar
```

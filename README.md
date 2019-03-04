# `gitar`

[![Travis CI Build Status](https://travis-ci.org/gitenter/gitar.svg?branch=master)](https://travis-ci.org/gitenter/gitar)
[![CircleCI](https://circleci.com/gh/gitenter/gitar.svg?style=svg)](https://circleci.com/gh/gitenter/gitar)

`gitar` is an object-orient API wrapper on top of JGit.

## Compile and Deployment

This repository should be compiled in Java 10/11 (notice that there's [no openjdk-10 but only has openjdk-11 on Ubuntu 18.04](https://askubuntu.com/questions/1037646/why-is-openjdk-10-packaged-as-openjdk-11)). The produced jar is compatible with Java 8+, which makes it Spring compatible (Spring only supports Java 8 yet).

```
mvn clean install
```

So then the local `.m2` directory will be updated.

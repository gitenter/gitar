# `gitar`

[![CircleCI](https://circleci.com/gh/gitenter/gitar.svg?style=svg)](https://circleci.com/gh/gitenter/gitar)

`gitar` is an object-orient API wrapper on top of JGit.

## Compile

Gitar can be compile on Java 8 and 10. Although `mvn clean install` can only work on Java 8, due to the reason that `maven-jar-plugin` is not supporting Java 10 yet.

Mac:

```
$ export JAVA_HOME=/Library/Java/Home
$ mvn clean install
```

## Deployment

To make other sub-projects (`capsid` and various hooks) compilable, `mvn install` of this package to the `.m2` directory is needed.

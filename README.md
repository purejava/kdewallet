# kdewallet
[![Build Status](https://secure.travis-ci.org/purejava/kdewallet.png)](http://travis-ci.org/purejava/kdewallet)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/da634cf61b71475293312f9bfadafde7)](https://www.codacy.com/manual/purejava/kdewallet?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=purejava/kdewallet&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://img.shields.io/maven-central/v/org.purejava/kdewallet.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.purejava%22%20AND%20a:%22kdewallet%22)
[![License](https://img.shields.io/github/license/purejava/kdewallet.svg)](https://github.com/purejava/kdewallet/blob/master/LICENSE)

A Java library for storing secrets on linux in a KDE wallet over D-Bus.

# Usage
The library provides a simplified high-level API, which sends transport encrypted secrets over D-Bus and has D-Bus signaling enabled.

# Dependency
Add `kdewallet` as a dependency to your project.
```maven
<dependency>
    <groupId>org.purejava</groupId>
    <artifactId>kdewallet</artifactId>
    <version>1.0.0-RC.1</version>
</dependency>
```

# Copyright
Copyright (C) 2020 Ralph Plawetzki
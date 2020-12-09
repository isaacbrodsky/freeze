package com.isaacbrodsky.zztsearch.web;

import io.dropwizard.Configuration;
import lombok.Getter;

import java.io.File;

public class ZZTSearchConfiguration extends Configuration {
    @Getter
    public File worldDirectory;
    @Getter
    public File indexDirectory;

    @Getter
    public String museumUrlBase;
    @Getter
    public File museumFile;
}

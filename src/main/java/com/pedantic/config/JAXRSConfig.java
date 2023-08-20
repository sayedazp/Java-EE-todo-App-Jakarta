package com.pedantic.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("api/v1") //https://foo.bax/api/v1/hello/{name}
public class JAXRSConfig extends Application {


}
    
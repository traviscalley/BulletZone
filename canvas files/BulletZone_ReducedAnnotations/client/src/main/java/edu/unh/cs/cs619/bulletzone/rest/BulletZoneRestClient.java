package edu.unh.cs.cs619.bulletzone.rest;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

/** "http://stman1.cs.unh.edu:6191/games"
 * "http://10.0.0.145:6191/games"
 * http://10.0.2.2:8080/
 * 10.20.245.90
 * Created by simon on 10/1/14.
 */

@Rest(rootUrl = "http://stman1.cs.unh.edu:6191/games",
        converters = {StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class}
        // TODO: disable intercepting and logging
        // , interceptors = { HttpLoggerInterceptor.class }
)
public interface BulletZoneRestClient extends RestClientErrorHandling {
    void setRootUrl(String rootUrl);

    @Post("")
    LongWrapper join() throws RestClientException;

    @Get("")
    GridWrapper grid();

    @Put("/{tankId}/move/{direction}")
    BooleanWrapper move(long tankId, byte direction);

    @Put("/{tankId}/turn/{direction}")
    BooleanWrapper turn(long tankId, byte direction);

    @Put("/{tankId}/fire/{bulletType}")
    BooleanWrapper fire(long tankId, int bulletType);

    @Delete("/{tankId}/leave")
    BooleanWrapper leave(long tankId);
}

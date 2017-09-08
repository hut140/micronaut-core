/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.http.server.netty

import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import org.particleframework.core.convert.DefaultConversionService
import org.particleframework.http.HttpHeaders
import org.particleframework.http.HttpResponse
import org.particleframework.http.HttpStatus
import org.particleframework.http.MutableHttpResponse
import org.particleframework.http.cookie.Cookie
import spock.lang.Specification

import java.time.Duration
import java.time.Period

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class NettyHttpResponseSpec extends Specification {

    void "test add headers"() {
        given:
        DefaultFullHttpResponse nettyResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        HttpResponse response = new NettyHttpResponse(nettyResponse, new DefaultConversionService())

        response.status(HttpStatus."$status")
        response.headers.add(header, value)

        expect:
        response.status == HttpStatus."$status"
        response.headers.get(header) == value

        where:
        status        | header                   | value
        HttpStatus.OK | HttpHeaders.CONTENT_TYPE | "application/json"
    }


    void "test add simple cookie"() {
        given:
        DefaultFullHttpResponse nettyResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        MutableHttpResponse response = new NettyHttpResponse(nettyResponse, new DefaultConversionService())

        response.status(HttpStatus."$status")
        response.cookie(Cookie.of("foo", "bar"))

        expect:
        response.status == HttpStatus."$status"
        response.headers.get(header) == value

        where:
        status        | header                 | value
        HttpStatus.OK | HttpHeaders.SET_COOKIE | "foo=bar"
    }

    void "test add cookie with max age"() {
        given:
        DefaultFullHttpResponse nettyResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        MutableHttpResponse response = new NettyHttpResponse(nettyResponse, new DefaultConversionService())

        response.status(HttpStatus."$status")
        response.cookie(Cookie.of("foo", "bar").setMaxAge(Duration.ofHours(2)))

        expect:
        response.status == HttpStatus."$status"
        response.headers.get(header).contains('foo=bar;')
        response.headers.get(header).contains('Max-Age=')

        where:
        status        | header
        HttpStatus.OK | HttpHeaders.SET_COOKIE
    }
}

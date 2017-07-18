/* 
 * Copyright 2017 Peter Bencze.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.peterbencze.serritor.internal;

import com.github.peterbencze.serritor.api.CrawlRequest;
import java.net.URL;
import java.util.Optional;

/**
 * The base class from which all callback parameters inherit from.
 *
 * @author Peter Bencze
 */
public abstract class CallbackParameter {

    private final int crawlDepth;
    private final URL refererUrl;
    private final CrawlRequest crawlRequest;

    protected CallbackParameter(final CallbackParameterBuilder builder) {
        crawlDepth = builder.crawlDepth;
        refererUrl = builder.refererUrl;
        crawlRequest = builder.crawlRequest;
    }

    /**
     * Returns the referer URL.
     *
     * @return The referer URL
     */
    public final Optional<URL> getRefererUrl() {
        return Optional.ofNullable(refererUrl);
    }

    /**
     * Returns the current crawl depth.
     *
     * @return The current crawl depth
     */
    public final int getCrawlDepth() {
        return crawlDepth;
    }

    /**
     * Returns the crawl request that was processed by the crawler.
     *
     * @return The processed crawl request
     */
    public final CrawlRequest getCrawlRequest() {
        return crawlRequest;
    }

    /**
     * Returns the request's URL.
     *
     * @return The request's URL
     *
     * @deprecated As of release 1.2, replaced by {@link #getCrawlRequest()}
     */
    @Deprecated
    public final URL getCurrentUrl() {
        return crawlRequest.getRequestUrl();
    }

    public static abstract class CallbackParameterBuilder<T extends CallbackParameterBuilder<T>> {

        private final URL refererUrl;
        private final int crawlDepth;
        private final CrawlRequest crawlRequest;

        public CallbackParameterBuilder(final URL refererUrl, final int crawlDepth, final CrawlRequest crawlRequest) {
            this.refererUrl = refererUrl;
            this.crawlDepth = crawlDepth;
            this.crawlRequest = crawlRequest;
        }

        public abstract CallbackParameter build();
    }
}
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
import com.github.peterbencze.serritor.api.CrawlRequest.CrawlRequestBuilder;
import com.github.peterbencze.serritor.api.CrawlingStrategy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for CrawlFrontier.
 *
 * @author Krisztian Mozsi
 * @author Peter Bencze
 */
public final class CrawlFrontierTest {

    // Root URLs
    private static final URL ROOT_URL_0;
    private static final URL ROOT_URL_1;

    // Root URL crawl depth
    private static final int ROOT_URL_CRAWL_DEPTH = 0;

    // Root URL priorities
    private static final int ROOT_URL_0_PRIORITY = 0;
    private static final int ROOT_URL_1_PRIORITY = 1;

    // Root URL crawl requests
    private static final CrawlRequest ROOT_URL_0_CRAWL_REQUEST;
    private static final CrawlRequest ROOT_URL_1_CRAWL_REQUEST;

    // Child URLs
    private static final URL CHILD_URL_0;
    private static final URL CHILD_URL_1;
    private static final URL CHILD_URL_2;

    // Child URL crawl depth
    private static final int CHILD_URL_CRAWL_DEPTH = 1;

    // Child URL priorities
    private static final int CHILD_URL_0_PRIORITY = 0;
    private static final int CHILD_URL_1_PRIORITY = CHILD_URL_0_PRIORITY;
    private static final int CHILD_URL_2_PRIORITY = 1;

    // Child URL crawl requests  
    private static final CrawlRequest CHILD_URL_0_CRAWL_REQUEST;
    private static final CrawlRequest CHILD_URL_1_CRAWL_REQUEST;
    private static final CrawlRequest CHILD_URL_2_CRAWL_REQUEST;
    
    // Child URL path
    private static final String CHILD_URL_PATH = "/child";

    // Offsite URL
    private static final URL OFFSITE_URL;

    // Offsite URL priority
    private static final int OFFSITE_URL_PRIORITY = 0;

    // Offsite URL crawl request
    private static final CrawlRequest OFFSITE_URL_CRAWL_REQUEST;
    
    // Max crawl depth
    private static final int MAX_CRAWL_DEPTH = 1;

    static {
        try {
            // Initialization of root URLs
            ROOT_URL_0 = new URL("http://root_url_0.com");
            ROOT_URL_1 = new URL("http://root_url_1.com");

            // Initialization of child URLs
            CHILD_URL_0 = new URL(String.format("http://root_url_0.com%s_0.html", CHILD_URL_PATH));
            CHILD_URL_1 = new URL(String.format("http://root_url_0.com%s_1.html", CHILD_URL_PATH));

            CHILD_URL_2 = new URL(String.format("http://root_url_1.com%s_0.html", CHILD_URL_PATH));

            // Initialization of the offsite URL
            OFFSITE_URL = new URL("http://offsite_url.com");
        } catch (MalformedURLException ex) {
            throw new Error(ex);
        }

        // Initialize crawl requests
        ROOT_URL_0_CRAWL_REQUEST = new CrawlRequestBuilder(ROOT_URL_0)
                .setPriority(ROOT_URL_0_PRIORITY)
                .build();

        ROOT_URL_1_CRAWL_REQUEST = new CrawlRequestBuilder(ROOT_URL_1)
                .setPriority(ROOT_URL_1_PRIORITY)
                .build();

        CHILD_URL_0_CRAWL_REQUEST = new CrawlRequestBuilder(CHILD_URL_0)
                .setPriority(CHILD_URL_0_PRIORITY)
                .build();

        CHILD_URL_1_CRAWL_REQUEST = new CrawlRequestBuilder(CHILD_URL_1)
                .setPriority(CHILD_URL_1_PRIORITY)
                .build();

        CHILD_URL_2_CRAWL_REQUEST = new CrawlRequestBuilder(CHILD_URL_2)
                .setPriority(CHILD_URL_2_PRIORITY)
                .build();

        OFFSITE_URL_CRAWL_REQUEST = new CrawlRequestBuilder(OFFSITE_URL)
                .setPriority(OFFSITE_URL_PRIORITY)
                .build();
    }

    private CrawlerConfiguration config;
    private CrawlFrontier frontier;

    @Before
    public void initialize() {
        // Create configuration
        config = new CrawlerConfiguration();
        config.setOffsiteRequestFiltering(true);
        config.addCrawlSeeds(Arrays.asList(ROOT_URL_0_CRAWL_REQUEST, ROOT_URL_1_CRAWL_REQUEST));

        // Create frontier
        frontier = new CrawlFrontier(config);
    }

    @Test
    public void hasNextRequestTest() {
        // At this point, there are 2 candidates in the queue

        // Check if there are any candidates in the queue, the method should return true
        assertTrue(frontier.hasNextCandidate());

        // Get the next candidate from the queue
        frontier.getNextCandidate();

        // Check if there are any candidates in the queue, the method should return true again
        assertTrue(frontier.hasNextCandidate());

        // Get the next candidate from the queue
        frontier.getNextCandidate();

        // Check if there are any candidates in the queue, the method should return false at this point
        assertFalse(frontier.hasNextCandidate());

        // Feed 2 crawl requests
        frontier.feedRequest(CHILD_URL_0_CRAWL_REQUEST, false);
        frontier.feedRequest(CHILD_URL_1_CRAWL_REQUEST, false);

        // Check if there are any candidates in the queue, the method should return true
        assertTrue(frontier.hasNextCandidate());

        // Get the next candidate from the queue
        frontier.getNextCandidate();

        // Check if there are any candidates in the queue, the method should return true once again
        assertTrue(frontier.hasNextCandidate());

        // Get the next candidate from the queue
        frontier.getNextCandidate();

        // Finally, check if there are any candidates in the queue, the method should return false at this point
        assertFalse(frontier.hasNextCandidate());
    }

    @Test
    public void hasNextRequestEmptyQueueTest() {
        // Create frontier without any seeds
        frontier = new CrawlFrontier(new CrawlerConfiguration());

        // Check if there are any candidates in the queue, the method should return false
        assertFalse(frontier.hasNextCandidate());
    }

    @Test
    public void getNextRequestWithDuplicateRequestFilterTest() {
        // Clear the crawl candidate queue of the frontier
        clearCrawlCandidateQueue();

        // Feed a duplicate crawl request (root URL 0 is a seed)
        frontier.feedRequest(ROOT_URL_0_CRAWL_REQUEST, true);

        // Check if the candidate was added to the queue, the method should return false
        assertFalse(frontier.hasNextCandidate());
    }

    @Test
    public void getNextRequestWithOffsiteRequestFilterTest() {
        // Clear the crawl candidate queue of the frontier
        clearCrawlCandidateQueue();

        // Feed an offsite request
        frontier.feedRequest(OFFSITE_URL_CRAWL_REQUEST, false);

        // Check if the candidate was added to the queue, the method should return false
        assertFalse(frontier.hasNextCandidate());
    }

    @Test
    public void getNextRequestWithoutDuplicateRequestFilterTest() {
        // Turn off duplicate request filtering
        config.setDuplicateRequestFiltering(false);

        // Clear the crawl candidate queue of the frontier
        clearCrawlCandidateQueue();

        // Feed a duplicate crawl request
        frontier.feedRequest(ROOT_URL_0_CRAWL_REQUEST, true);

        // Check if the candidates was added to the queue, the method should return true
        assertTrue(frontier.hasNextCandidate());

        // Check if the URLs match
        assertEquals(ROOT_URL_0.toString(), frontier.getNextCandidate().getCandidateUrl().toString());
    }

    @Test
    public void getNextRequestWithoutOffsiteRequestFilterTest() {
        // Turn off offsite request filtering
        config.setOffsiteRequestFiltering(false);

        // Clear the crawl candidate queue of the frontier
        clearCrawlCandidateQueue();

        // Feed an offsite request
        frontier.feedRequest(OFFSITE_URL_CRAWL_REQUEST, false);

        // Check if the candidates was added to the queue, the method should return true
        assertTrue(frontier.hasNextCandidate());

        // Check if the URLs match
        assertEquals(OFFSITE_URL.toString(), frontier.getNextCandidate().getCandidateUrl().toString());
    }

    @Test
    public void getNextRequestBreadthFirstTest() {
        // Get the crawl candidate of root URL 1.
        CrawlCandidate nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be root URL 1.
        assertEquals(ROOT_URL_1.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 0 because it is a root URL.
        assertEquals(ROOT_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 1.
        assertEquals(ROOT_URL_1_PRIORITY, nextCandidate.getPriority());

        // Feed a child request that come from root URL 1.
        frontier.feedRequest(CHILD_URL_2_CRAWL_REQUEST, false);

        // Get the crawl candidate of root URL 0.
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be root URL 0.
        assertEquals(ROOT_URL_0.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 0 again because it is also a root URL.
        assertEquals(ROOT_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 0.
        assertEquals(ROOT_URL_0_PRIORITY, nextCandidate.getPriority());

        // Feed 2 child requests that come from root URL 0.
        frontier.feedRequest(CHILD_URL_0_CRAWL_REQUEST, false);
        frontier.feedRequest(CHILD_URL_1_CRAWL_REQUEST, false);

        // Get the crawl candidate of child URL 2.
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be child URL 2.
        assertEquals(CHILD_URL_2.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 1 because it is a child URL that comes from root URL 1.
        assertEquals(CHILD_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 1.
        assertEquals(CHILD_URL_2_PRIORITY, nextCandidate.getPriority());

        // Get the crawl candidate of a child URL.
        // Note: a priority queue does not ensure FIFO order when elements have the same depth and priority
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this request, it should be a child URL.
        assertTrue(nextCandidate.getCandidateUrl().toString().contains(CHILD_URL_PATH));

        // Check the crawl depth of this candidate, it should be 1 again because it is a child URL that comes from root URL 0.
        assertEquals(CHILD_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Get the priority of this candidate
        int previousChildCandidatePriority = nextCandidate.getPriority();

        // Get the crawl candidate of the next child URL.
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be a child URL.
        assertTrue(nextCandidate.getCandidateUrl().toString().contains(CHILD_URL_PATH));

        // Check the crawl depth of this candidate, it should be 1 again becaise it is another child URL that also comes from root URL 0.
        assertEquals(CHILD_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Compare the priority of this candidate to the previous candidate's priority.
        assertEquals(previousChildCandidatePriority, nextCandidate.getPriority());

        // There should be no more candidates left at this point.
        assertFalse(frontier.hasNextCandidate());
    }

    @Test
    public void getNextRequestDepthFirstTest() {
        // Set the crawling strategy to depth-first
        config.setCrawlingStrategy(CrawlingStrategy.DEPTH_FIRST);
        frontier = new CrawlFrontier(config);

        // Get the crawl candidate of root URL 1
        CrawlCandidate nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be root URL 1
        assertEquals(ROOT_URL_1.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 0 because it is a root URL
        assertEquals(ROOT_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 1
        assertEquals(ROOT_URL_1_PRIORITY, nextCandidate.getPriority());

        // Feed a child request that comes from root URL 1
        frontier.feedRequest(CHILD_URL_2_CRAWL_REQUEST, false);

        // Get the crawl candidate of a child URL
        // Note: a priority queue does not ensure FIFO order when elements have the same depth and priority
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be a child URL
        assertTrue(nextCandidate.getCandidateUrl().toString().contains(CHILD_URL_PATH));

        // Check the crawl depth of this candidate, it should be 1 because it is a child URL that comes from root URL 1
        assertEquals(CHILD_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 1
        assertEquals(CHILD_URL_2_PRIORITY, nextCandidate.getPriority());

        // Get the crawl candidate of root URL 0.
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be root URL 0
        assertEquals(ROOT_URL_0.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 0 again because it is also a root URL
        assertEquals(ROOT_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 0
        assertEquals(ROOT_URL_0_PRIORITY, nextCandidate.getPriority());

        // Feed 2 child requests that come from root URL 0
        frontier.feedRequest(CHILD_URL_0_CRAWL_REQUEST, false);
        frontier.feedRequest(CHILD_URL_1_CRAWL_REQUEST, false);

        // Get the crawl candidate of child URL 0
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be child URL 0
        assertEquals(CHILD_URL_0.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 1 again because it is a child URL that comes from root URL 0
        assertEquals(CHILD_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 0
        assertEquals(CHILD_URL_0_PRIORITY, nextCandidate.getPriority());

        // Get the crawl candidate of child URL 1
        nextCandidate = frontier.getNextCandidate();

        // Check the URL of this candidate, it should be child URL 1
        assertEquals(CHILD_URL_1.toString(), nextCandidate.getCandidateUrl().toString());

        // Check the crawl depth of this candidate, it should be 1 again becaise it is a child URL that also comes from root URL 0
        assertEquals(CHILD_URL_CRAWL_DEPTH, nextCandidate.getCrawlDepth());

        // Check the priority of this candidate, it should be 0
        assertEquals(CHILD_URL_1_PRIORITY, nextCandidate.getPriority());

        // There should be no more candidates left at this point
        assertFalse(frontier.hasNextCandidate());
    }
    
    @Test
    public void maxCrawlDepthTest() {
        // Set max crawl depth
        config.setMaxCrawlDepth(MAX_CRAWL_DEPTH);
        
        // Clear the crawl candidate queue of the frontier
        clearCrawlCandidateQueue();
        
        // Feed a child request, its crawl depth will be 1
        frontier.feedRequest(CHILD_URL_0_CRAWL_REQUEST, false);
        
        // Get the crawl candidate of the previously added child URL
        CrawlCandidate nextCandidate = frontier.getNextCandidate();
        
        // Check its crawl depth, it should be less than or equal to the limit
        assertTrue(nextCandidate.getCrawlDepth() <= MAX_CRAWL_DEPTH);
        
        // Feed another child request, its crawl depth will be 2 which is above the limit
        frontier.feedRequest(CHILD_URL_1_CRAWL_REQUEST, false);
        
        // There should be no more candidates at this point
        assertFalse(frontier.hasNextCandidate());
    }

    private void clearCrawlCandidateQueue() {
        // Loop until there are no remaining candidates in the queue
        while (frontier.hasNextCandidate()) {
            frontier.getNextCandidate();
        }
    }
}
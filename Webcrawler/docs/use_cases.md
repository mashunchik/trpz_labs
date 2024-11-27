# Web Crawler Use Cases

## Use Case 1: Start Web Crawling

**Name**: Start Web Crawling
**Actor**: User
**Description**: User initiates the web crawling process for a specific website

**Preconditions**:
1. The system is properly configured
2. Database is initialized
3. Target URL is accessible

**Main Flow**:
1. User provides the starting URL
2. User sets crawling parameters (depth, max pages, threads)
3. System validates the URL
4. System initializes the crawler components
5. System starts crawling the website
6. System processes each page through the chain of processors:
   - Extracts title
   - Cleans and extracts content
   - Identifies and validates links
   - Collects metadata
7. System stores processed pages in the database
8. System continues until reaching limits or completing the crawl
9. System shuts down gracefully

**Alternative Flows**:
- A1: Invalid URL provided
  1. System displays error message
  2. Returns to step 1
- A2: Network error during crawling
  1. System logs the error
  2. Continues with next URL in queue
- A3: Database error
  1. System logs the error
  2. Attempts to save state
  3. Notifies user of the error

**Postconditions**:
1. Crawled pages are stored in database
2. Crawling statistics are updated
3. Resources are properly released

## Use Case 2: Save and Restore Crawler State

**Name**: Save and Restore Crawler State
**Actor**: User
**Description**: User saves the current state of the crawler and restores it later

**Preconditions**:
1. Crawler is initialized
2. Database is accessible

**Main Flow**:
1. User requests to save current crawler state
2. System captures current state:
   - Set of visited URLs
   - Queue of pending URLs
   - Timestamp
3. System serializes the state
4. System stores the state
5. User can later request to restore the state
6. System loads the saved state
7. System reinitializes the crawler with saved state
8. Crawling continues from the restored point

**Alternative Flows**:
- A1: State save fails
  1. System logs the error
  2. Notifies user
  3. Continues crawling
- A2: State restore fails
  1. System logs the error
  2. Offers to start fresh crawl

**Postconditions**:
1. Crawler state is saved/restored
2. Crawling can continue from saved point

## Use Case 3: Process and Clean Web Content

**Name**: Process and Clean Web Content
**Actor**: System (Chain of Processors)
**Description**: System processes a web page through the chain of responsibility

**Preconditions**:
1. Raw HTML content is fetched
2. Processing chain is initialized

**Main Flow**:
1. System creates processing context for the page
2. Title Processor:
   - Extracts page title
   - Passes to next processor
3. Content Processor:
   - Removes non-semantic elements (ads, scripts)
   - Extracts main content
   - Passes to next processor
4. Link Processor:
   - Identifies all links
   - Validates links
   - Filters to same domain
   - Passes to next processor
5. Metadata Processor:
   - Extracts meta tags
   - Collects OpenGraph data
6. System creates final Page object
7. System saves processed page to database

**Alternative Flows**:
- A1: Processing error in any processor
  1. Logs the error
  2. Skips to next processor
  3. Marks page as partially processed
- A2: Invalid content
  1. Marks page as invalid
  2. Logs the issue
  3. Skips further processing

**Postconditions**:
1. Page is processed and cleaned
2. Structured data is ready for storage
3. Links are extracted for further crawling

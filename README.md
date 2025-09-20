# web-crawler

**Crawler** - Orchestrator. Submits tasks to the pool.

**Worker** - A unit of work (Runnable/Callable) that represents “crawl this one URL.”

**Fetcher** - Responsible for making the HTTP request and getting the raw HTML (or error).

**Parser** - Consumes the HTML from Fetcher, extracts links + metadata, returns CrawlResult.

**Storage** - Saves metadata/content.

**Frontier** - Collects new URLs to be crawled.
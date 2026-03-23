This service provides automatic spelling correction for text using the Yandex Speller API. It processes correction tasks asynchronously, handles long texts by splitting them into chunks, and stores the results in a database.

## Key Features
- **Text Correction**: Automatically corrects spelling errors in text using Yandex Speller API.
- **Long Text Support**: Splits text into chunks respecting API limits (10,000 characters) while preserving sentence boundaries.
- **Smart Options Detection**: Automatically enables API options for digits (IGNORE_DIGITS) and URLs (IGNORE_URLS) when present in the text.
- **Async Task Processing**: Processes correction tasks asynchronously using Java 21 virtual threads.
- **Task Status Management**: Maintains task statuses (NEW, IN_PROGRESS, COMPLETED, ERROR) throughout the correction lifecycle.

## Technologies Used
- Java 21
- Spring Boot 3
- Gradle
- PostgreSQL
- Docker / Docker Compose

## API Endpoints
- `POST /tasks`: Creates a new text correction task. Accepts `text` (min 3 characters) and `language` (RU or EN). Returns task ID.
- `GET /tasks/{id}`: Retrieves correction result. Returns status, corrected text (if completed), or error message (if failed).
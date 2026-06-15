# DreamFly: Flight Search & Comparison Engine

DreamFly is a JavaFX-based desktop application that allows users to search, filter, and compare global flight itineraries. The system integrates with the Amadeus REST API to fetch live flight data and demonstrates a strict 3-tier layered architecture, object-oriented principles, and GoF design patterns. 

This project was built to showcase clean software architecture, where the UI, business logic, and persistence layers are entirely decoupled.

## ✈️ Features

* **Real-Time API Integration:** Fetches live flight data using the Amadeus Self-Service API via OAuth2.
* **Dynamic Filtering & Sorting:** Users can filter by price and stops, and sort results by travel time, price, or duration using a custom UI.
* **Segment-by-Segment Breakdown:** Detailed modal views showing layover durations, flight numbers, and exact departure/arrival times.
* **In-Memory Caching:** A thread-safe `ConcurrentHashMap` caches identical search queries with a 15-minute TTL to reduce redundant network calls.
* **Graceful Degradation (Mock Mode):** If the API is unreachable or keys are missing, the system automatically falls back to an internal mock-data generator to ensure the app never crashes during a demo.
* **Multi-Currency Support:** Converts and displays flight prices across 6 different currencies.

## 🛠️ Technical Implementation

This project was developed focusing on heavily structured OOP and Design Patterns:

* **Layered Architecture:** Strict separation of UI (JavaFX/FXML), Service Layer, and Data Layer.
* **Strategy Pattern:** Utilized interchangeable `Comparator` and `Predicate` strategies to handle dynamic sorting and filtering without modifying the core search logic.
* **Facade Pattern:** The `FlightSearchService` hides the complexity of cache checking, OAuth token management, and JSON parsing from the UI controllers.
* **Singleton Pattern:** Manages application-wide configurations (like API keys loaded from `config.properties`).
* **Factory & Template Methods:** Custom FXML controller factories for dependency injection and custom JavaFX `ListCell` rendering.

## 🚀 How to Run

1. **Clone the repository:**
   `git clone https://github.com/tahaebaad/DreamFly.git`
2. **Dependencies:** 
   Ensure you have Java 17+ and Maven installed. Run `mvn clean install` to pull required dependencies (JavaFX, OkHttp, Jackson).
3. **API Configuration:** 
   On the first run, the app will prompt you for your Amadeus API Key and Secret. These are saved locally to `config.properties`. 
   *(Note: You can skip this and just run the app to see it operate via the Mock Mode fallback).*
4. **Run:** 
   Execute the `FlightFinderApp` main class.

## 👥 Authors

* **Taha Ebaad**
* **Shavaiz Tariq**
* **Ahmed Cheema**

* 🔗 Read the detailed technical breakdown and discussion of this project on my [Linkdin](https://www.linkedin.com/posts/taha-ebaad-819b8328b_built-and-launched-%F0%9D%90%83%F0%9D%90%AB%F0%9D%90%9E%F0%9D%90%9A%F0%9D%90%A6%F0%9D%90%85%F0%9D%90%A5%F0%9D%90%B2-along-activity-7472383198560452608-iscO?utm_source=share&utm_medium=member_desktop&rcm=ACoAAEZ5lNUBO_-anndHWlDvE3UmJfwqGNORPsk).

Built for the Software Design & Analysis course, Spring 2026.

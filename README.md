# Richfield Campus Event Management System

A Java console application for managing campus event. The system supports two user roles — **Staff** and **Student** — each with their own menu and set of operations.

## Table of Contents

- [Richfield Campus Event Management System](#richfield-campus-event-management-system)
  - [Table of Contents](#table-of-contents)
  - [Project Overview](#project-overview)
  - [Features](#features)
    - [Staff](#staff)
    - [Student](#student)
  - [Project Structure](#project-structure)
  - [Class Descriptions](#class-descriptions)
  - [Data Structures Used](#data-structures-used)
  - [OOP Concepts Demonstrated](#oop-concepts-demonstrated)
  - [How to Compile and Run](#how-to-compile-and-run)
    - [Prerequisites](#prerequisites)
    - [Steps](#steps)
  - [Usage Guide](#usage-guide)
    - [On Launch](#on-launch)
    - [Staff Workflow Example](#staff-workflow-example)
    - [Student Workflow Example](#student-workflow-example)
  - [Data Persistence](#data-persistence)
  - [Input Validation](#input-validation)
  - [Threading](#threading)

## Project Overview

This system allows campus staff to create, update, cancel, sort, and search events, while students can browse available events, register for them, cancel registrations, and check their registration status. When an event reaches its maximum capacity, additional students are placed on a waitlist. If a registered student cancels, the first person on the waitlist is automatically promoted.

## Features

### Staff

- Create new events (with unique ID, name, date, time, location, max participants)
- Update existing event details (partial updates supported — skip fields by pressing Enter)
- Cancel/delete events
- View all events (with optional sorting)
- View registered participants and waitlist for any event
- Sort events by name or date
- Search events by name (keyword) or exact date

### Student

- View all available events
- Register for an event (auto-waitlisted if full)
- Cancel registration (triggers automatic waitlist promotion)
- View personal registration status across all events
- Search events by name or date

## Project Structure

```
CampusEventSystem/
├── src/
│   ├── Main.java             # Entry point — handles menus and user interaction
│   ├── User.java             # Abstract base class for Staff and Student
│   ├── Staff.java            # Staff subclass with staff-specific menu
│   ├── Student.java          # Student subclass with student-specific menu
│   ├── Event.java            # Event model — stores event data, registrations, waitlist
│   ├── EventManager.java     # Business logic — CRUD, search, sort, registration
│   └── DataPersistence.java  # File I/O — saves/loads data to/from text files
├── events.txt                # Persisted event data (auto-generated)
├── registrations.txt         # Persisted registration/waitlist data (auto-generated)
└── README.md
```

## Class Descriptions

| Class             | Role           | Key Responsibility                                                                                    |
| ----------------- | -------------- | ----------------------------------------------------------------------------------------------------- |
| `Main`            | Controller     | Runs the program, displays menus, handles user input and validation                                   |
| `User`            | Abstract Model | Base class with common fields (`userId`, `name`, `role`) and abstract `displayMenu()`                 |
| `Staff`           | Model          | Extends `User`, implements the staff menu                                                             |
| `Student`         | Model          | Extends `User`, implements the student menu                                                           |
| `Event`           | Model          | Stores event details, manages registered students list and waitlist queue                             |
| `EventManager`    | Service        | Contains all business logic — create/update/cancel events, register/unregister students, search, sort |
| `DataPersistence` | Utility        | Reads from and writes to `events.txt` and `registrations.txt` using file I/O                          |

## Data Structures Used

| Data Structure                    | Where Used                                     | Why                                                                              |
| --------------------------------- | ---------------------------------------------- | -------------------------------------------------------------------------------- |
| `LinkedHashMap<Integer, Event>`   | `EventManager` — stores all events keyed by ID | O(1) lookup by event ID while preserving insertion order                         |
| `ArrayList<String>`               | `Event` — registered students list             | Dynamic array for fast indexed access and iteration                              |
| `LinkedList` (as `Queue<String>`) | `Event` — waitlist                             | FIFO queue so the first student to join the waitlist is the first to be promoted |
| `List<Event>`                     | Search and sort results                        | Returned as sorted/filtered copies without modifying the original map            |
| `Comparator`                      | `EventManager.sortByName()` / `sortByDate()`   | Custom comparison logic for sorting events                                       |

## OOP Concepts Demonstrated

| Concept           | Where                                                                                                                                                  |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Abstraction**   | `User` is an abstract class with an abstract `displayMenu()` method                                                                                    |
| **Inheritance**   | `Staff` and `Student` extend `User`                                                                                                                    |
| **Polymorphism**  | `displayMenu()` is overridden in both `Staff` and `Student` to show different menus; `selectRole()` returns a `User` reference that can be either type |
| **Encapsulation** | All fields are `private` with getters/setters; internal logic is hidden behind method calls                                                            |

## How to Compile and Run

### Prerequisites

- Java JDK 8 or higher installed
- A terminal or command prompt

### Steps

```bash
# 1. Navigate to the src directory
cd CampusEventSystem/src

# 2. Compile all Java files
javac *.java

# 3. Run the program
java Main
```

> **Note:** The `events.txt` and `registrations.txt` files are created automatically in the working directory when you first save data.

## Usage Guide

### On Launch

1. The system asks you to select your role: **Staff** (1) or **Student** (2).
2. Enter your User ID and Name.
3. You are presented with the corresponding menu.

### Staff Workflow Example

```
Select role → 1 (Staff) → Enter ID & Name
→ 1. Create Event → fill in ID, name, date (dd/MM/yyyy), time (HH:mm), location, max participants
→ 4. View All Events → choose sort option
→ 5. View Participants → enter event ID to see registered list and waitlist
→ 8. Exit
```

### Student Workflow Example

```
Select role → 2 (Student) → Enter ID & Name
→ 1. View Available Events
→ 2. Register for Event → enter event ID (auto-waitlisted if full)
→ 4. View My Registration Status
→ 6. Exit
```

## Data Persistence

Data is saved to two plain-text files using pipe (`|`) delimiters:

**events.txt** — one event per line:

```
1|Orientation Day|15/01/2025|09:00|Main Hall|200
2|Career Fair|20/03/2025|10:00|Auditorium|150
```

**registrations.txt** — one registration per line:

```
1|STU001|REGISTERED
1|STU002|WAITLISTED
```

Data is loaded on startup and saved immediately after every create, update, cancel, or registration operation, so nothing is lost if the program closes unexpectedly.

## Input Validation

All user inputs are validated in a loop — the program keeps asking until valid input is provided:

- **Event ID**: must be a valid integer and unique when creating
- **Event Name / Location**: cannot be empty
- **Date**: must match `dd/MM/yyyy` format (strict parsing — e.g., `32/01/2025` is rejected)
- **Time**: must match `HH:mm` format (strict parsing)
- **Max Participants**: must be a positive integer
- **Menu choices**: must be valid integers within the menu range

## Threading

A `Thread` is used in `EventManager.cancelStudentRegistration()` to print a notification when a waitlisted student is automatically promoted to registered status after another student cancels. The main thread waits for this notification thread to complete using `Thread.join()`.

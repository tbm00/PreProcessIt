# PreProcessIt
A robust Java application for parsing and standardizing raw input data. 
 
Designed with flexibility and extensibility in mind, PreProcessIt transforms unstructured input into well-formatted, structured output. PreProcessIt leverages a configurable processing pipeline to handle complex tokenization and data transformation requirements, making it an effective tool for a range of data processing tasks.

Created by tbm00.

## Overview
PreProcessIt follows the Model-View-Controller (MVC) design pattern, ensuring clean separation of concerns and maintainability. The program processes input data line-by-line and token-by-token, dynamically matching tokens against a set of attributes defined in customizable component configurations. Depending on whether tokens satisfy specific conditions, the system applies a series of configurable actions to standardize the data.

## Key Features
- **Flexible Parsing Engine:** Define custom algorithms for different types of input data (components) and attributes with detailed qualifiers for high-precision data parsing.
- **Advanced Token Handling:** Process tokens using configurable rules that consider neighboring tokens, enabling complex pattern matching and data transformation.
- **Highly Configurable:** Use a YAML configuration file to control the behavior of the parser, ensuring it meets the needs of varied input data formats.
- **Robust Error Handling and Logging:** Detailed logging and error management provide transparency during data processing and facilitate troubleshooting and config creation.

## Dependencies
- **Java 8+**: REQUIRED

## How It Works
PreProcessIt reads input data one line at a time and splits each line into tokens. For every attribute in your selected component's configuration, the program checks if the current token applies-to/matches the current attribute using the attributes's qualifiers. If there is a match detected, the qualified actions will run. Else, the unqualified actions will run. 

Correctly implemented qualifiers contain:
- **Word Source:** Which token value to evaluate; e.g., the current working token, a copy of the initial token, or tokens from neighboring positions
- **Condition:** The criteria a token must meet; e.g., containing a specific string, matching a specific data type, etc.
- **Value:** The expected literal or range for validation
- **Actions:** Procedures to execute when a token is deemed qualified or unqualified. These actions may modify the token, skip iterations, try neigbhoring tokens, and "ship" the token to the final output.

Once a token has been "shipped" for a particular attribute, the program will search the line's remaining tokens for the next attribute, until all attributes have been processed. Any tokens that remain unprocessed are appended as leftovers at the end of the output line.

## Configuration

### Available Words
 - `INITIAL_TOKEN_COPY` Initial copy of the working token (therefore, unmodified by prior qualifiers)
 - `WORKING_TOKEN` Current working token that may be modified by prior qualifiers
 - `LEFT_NEIGHBOR` The token preceeding the current token in the input line
 - `RIGHT_NEIGHBOR` The token following the current token in the input line

### Available Conditions
 - `GREATER_THAN` Numerical values only
 - `GREATER_THAN_EQUAL_TO` Numerical values only
 - `LESS_THAN` Numerical values only
 - `LESS_THAN_EQUAL_TO` Numerical values only
 - `IN_BETWEEN_INCLUSIVE` Numerical values only
 - `IN_BETWEEN_EXCLUSIVE` Numerical values only
 - `EQUALS_VALUE` Numerical values only
 - `EQUALS_STRING` Case insensitive
 - `CONTAINS` Case insensitive
 - `START_CONTAINS` First character(s) match, Case insensitive
 - `END_CONTAINS` Last character(s) match, Case insensitive
 - `IS_TYPE` INTEGER, DOUBLE, NUMBER, or STRING
 - `IS_EMPTY`
 - `NOT_IN_BETWEEN_INCLUSIVE`
 - `NOT_IN_BETWEEN_EXCLUSIVE`
 - `NOT_EQUALS_VALUE`
 - `NOT_EQUALS_STRING`
 - `NOT_CONTAINS`
 - `NOT_START_CONTAINS`
 - `NOT_END_CONTAINS`
 - `NOT_IS_TYPE`
 - `NOT_IS_EMPTY`

### Available Actions
 - `SHIP` Ship current token and go to next attribute iteration
 - `TRY_NEIGHBORS(max_characters)` Try appending neighbors' characters to see if it might qualify (with the same matcher)
 - `EXIT_TO_NEXT_ATTRIBUTE_ITERATION` Go to next attribute iteration
 - `EXIT_TO_NEXT_TOKEN_ITERATION` Go to next token iteration on current attribute iteration
 - `CONTINUE_AND_SKIP_NEXT_QUALIFIER(count)` Skip next qualifer(s) on current attribute iteration
 - `CONTINUE` Continue to the next, immediate qualifier on current attribute iteration
 - `REPLACE_ALL(fromString,toString)`Replace all occurences of the fromString in the working token with toString, Case sensitive
 - `REPLACE_FIRST(fromString,toString)` Replace first occurence of the fromString in the working token with toString, Case sensitive
 - `INSERT_AT(index,String)` Insert a String at specifc index in working token
 - `APPEND(String)` Attach a String to end of working token
 - `PREPEND(String)` Attach a String to start of working token
 - `KEEP_MATCH` Set current working token to equal only the matched portion
 - `REPLACE_MATCH_ALL(toString)` Replace all occurences of the matched portion in the working token with toString, Case sensitive
 - `REPLACE_MATCH_FIRST(toString)` Replace first occurence of the matched portion in the working token with toString, Case sensitive
 - `TRIM_MATCH_ALL` Remove all occurences of the matched portion from the working token
 - `TRIM_MATCH_FIRST` Remove first occurence of the matched portion from the working token
 - `TRIM_MATCH_START` Remove matched portion from working token if the token begins with the matched value
 - `TRIM_MATCH_END` Remove matched portion from working token if the token ends with the matched value
 - `TRIM_MATCH_FROM_LEFT_NEIGHBOR` Remove matched portion from the back of the prior token, if its there
 - `TRIM_MATCH_FROM_RIGHT_NEIGHBOR` Remove matched portion from the front of the next token, if its there

### Default Config
```
# PreProcessIt v0.1.2-beta by @tbm00
# https://github.com/tbm00/PreProcessIt

concurrentThreading: true
threadPoolSizeOverride: -1

components:
  MONITOR:
    attributeOutputOrder: ["RESPONSE_TIME", "REFRESH_RATE"]
    attributes:
      RESPONSE_TIME:
        "1":
          word: WORKING_TOKEN
          condition: CONTAINS
          value: "MS|milliseconds"
          qualifiedActions:
            - TRIM_MATCH_ALL
            - CONTINUE
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
        "2": 
          word: WORKING_TOKEN
          condition: IS_EMPTY
          value: ""
          qualifiedActions:
            - CONTINUE
          unqualifiedActions:
            - CONTINUE_AND_SKIP_NEXT_QUALIFIER
        "3": 
          word: LEFT_NEIGHBOR
          condition: IN_BETWEEN_INCLUSIVE
          value: "0,50"
          qualifiedActions:
            - TRIM_MATCH_FROM_LEFT_NEIGHBOR
            - APPEND("ms")
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
        "4": 
          word: WORKING_TOKEN
          condition: IN_BETWEEN_INCLUSIVE
          value: "0,50"
          qualifiedActions:
            - APPEND("ms")
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
      REFRESH_RATE:
        "1":
          word: WORKING_TOKEN
          condition: CONTAINS
          value: "HZ|hertz"
          qualifiedActions:
            - TRIM_MATCH_ALL
            - CONTINUE
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
        "2": 
          word: WORKING_TOKEN
          condition: IS_EMPTY
          value: ""
          qualifiedActions:
            - CONTINUE
          unqualifiedActions:
            - CONTINUE_AND_SKIP_NEXT_QUALIFIER
        "3": 
          word: LEFT_NEIGHBOR
          condition: IN_BETWEEN_INCLUSIVE
          value: "59,361"
          qualifiedActions:
            - TRIM_MATCH_FROM_LEFT_NEIGHBOR
            - APPEND("hz")
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
        "4": 
          word: WORKING_TOKEN
          condition: IN_BETWEEN_INCLUSIVE
          value: "59,361"
          qualifiedActions:
            - APPEND("hz")
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
```

## License
PreProcessIt is released under the [PreProcessIt Non-Commercial License](LICENSE).

This license permits non-commercial use, but any commercial usage, modification, copying, or redistribution is prohibited without explicit permission.
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
PreProcessIt reads input data one line at a time and splits each line into tokens. For every attribute in your selected component's configuration, the program checks if the current token applies-to/matches the current attribute using the attribute's qualifiers. If there is a match detected, the qualified actions will run. Else, the unqualified actions will run. 

Correctly implemented qualifiers contain:
  - **Word Source:** Which token value to evaluate; e.g., the current working token, a copy of the initial token, or tokens from neighboring positions
  - **Condition:** The criteria a token must meet; e.g., containing a specific string, matching a specific data type, etc.
  - **Value:** The expected literal or range for validation
  - **Actions:** Procedures to execute when a token is deemed qualified or unqualified. These actions may modify the token, skip iterations, try neighboring tokens, and "ship" the token to the final output.

Once a token has been "shipped" for a particular attribute, the program will search the line's remaining tokens for the next attribute, until all attributes have been processed. Any tokens that remain unprocessed are appended as leftovers at the end of the output line.

You may also use qualifiers on the entire input or output line by using LineRules:
  - All defined inputLineRules will be processed on each input line before the attributes are processed.
  - After the attributes are processed, all defined outputLineRules will be processed on each newly created output line, and then the output line will be finalized.

### Example
Using the basic default configuration, PreProcessIt transforms the following messy input:
```
120 hz 2 milliseCONds
9ms    80hz
1 ms  160 hz
  60hz 5ms
99hertz randomtext 20Ms
```

into a standardized, CSV-like output:
```
original_input,response_time,refresh_rate,leftovers
120 HZ 2 MILLISECONDS,2MS,120HZ,
9MS    80HZ,9MS,80HZ,
 1 MS  160 HZ,1MS,160HZ,
   60HZ 5MS,5MS,60HZ,
99HERTZ RANDOMTEXT 20MS,20MS,99HZ,RANDOMTEXT
```

## Configuration

### Available Words
  - `WORKING_TOKEN` Current working token that may be modified by prior qualifiers
  - `INITIAL_TOKEN_COPY` Copy of the initial working token (therefore, unmodified by prior qualifiers)
  - `LEFT_NEIGHBOR` The token preceding the current token in the input line
  - `RIGHT_NEIGHBOR` The token following the current token in the input line
  - `WORKING_LINE` Current working line that may be modified by prior qualifiers (only used by LineRules)
  - `INITIAL_LINE_COPY` Copy of the initial input line (therefore, unmodified by prior qualifiers)

### Available Conditions
  - `GREATER_THAN` Numerical values only
  - `GREATER_THAN_EQUAL_TO` Numerical values only
  - `LESS_THAN` Numerical values only
  - `LESS_THAN_EQUAL_TO` Numerical values only
  - `IN_BETWEEN_INCLUSIVE` Numerical values only
  - `IN_BETWEEN_EXCLUSIVE` Numerical values only
  - `START_IN_BETWEEN_INCLUSIVE` Numerical values only
  - `START_IN_BETWEEN_EXCLUSIVE` Numerical values only
  - `END_IN_BETWEEN_INCLUSIVE` Numerical values only
  - `END_IN_BETWEEN_EXCLUSIVE` Numerical values only
  - `EQUALS_VALUE` Numerical values only
  - `EQUALS_STRING` Case insensitive
  - `CONTAINS` Case insensitive
  - `STARTS_WITH` First character(s) match; Case insensitive
  - `ENDS_WITH` Last character(s) match; Case insensitive
  - `START_IS_TYPE` INTEGER, DOUBLE, NUMBER, or STRING
  - `END_IS_TYPE` INTEGER, DOUBLE, NUMBER, or STRING
  - `IS_TYPE` INTEGER, DOUBLE, NUMBER, or STRING
  - `IS_EMPTY`
  - `NOT_IN_BETWEEN_INCLUSIVE`
  - `NOT_IN_BETWEEN_EXCLUSIVE`
  - `NOT_EQUALS_VALUE`
  - `NOT_EQUALS_STRING`
  - `NOT_CONTAINS`
  - `NOT_STARTS_WITH`
  - `NOT_ENDS_WITH`
  - `NOT_IS_TYPE`
  - `NOT_IS_EMPTY`

### Available Actions
  - `SHIP` Ship current token as current attribute's final value, and go to next attribute iteration
  - `DECLARE_TOKEN_PROCESSED` Mark the current token as processed, so that it doesn't get processed again later
  - `TRY_NEIGHBORS(max_characters)` Try appending neighbors' characters to see if it might qualify (with the same matcher)
  - `EXIT_TO_NEXT_ATTRIBUTE_ITERATION` Go to next attribute iteration
  - `EXIT_TO_NEXT_TOKEN_ITERATION` Go to next token iteration on current attribute iteration
  - `CONTINUE_AND_SKIP_NEXT_QUALIFIER(count)` Skip next qualifer(s) on current attribute iteration
  - `CONTINUE` Continue to the next, immediate qualifier on current attribute iteration
  - `REPLACE_ALL(fromString,toString)` Replace all occurrences of `fromString` in the working token with `toString`; Case sensitive
  - `REPLACE_FIRST(fromString,toString)` Replace first occurence of `fromString` in the working token with `toString`; Case sensitive
  - `INSERT_AT(index,String)` Insert a String at specific index in working token
  - `ROUND(type,amount)` Round working token to nearest `amount`; Applicable types: `up`, `down`, `nearest`
  - `FORMAT_NUMBER(format,commaGroups)` Reformat working token number; Accepts format like `#.##` or `#`; If commaGroups is `true`, then number will have commas every 3 digits like "100,000"
  - `SET_CASING(casing)` Set casing of the working token; Applicable casings: `upper`, `lower`
  - `APPEND(String)` Attach a `String` to end of working token
  - `PREPEND(String)` Attach a `String` to start of working token
  - `KEEP_MATCH` Set current working token to equal only the matched value
  - `REPLACE_MATCH_ALL(toString)` Replace all occurrences of the matched value in the working token with `toString`; Case sensitive
  - `REPLACE_MATCH_FIRST(toString)` Replace first occurence of the matched value in the working token with `toString`; Case sensitive
  - `TRIM_MATCH_ALL` Remove all occurrences of the matched value from the working token
  - `TRIM_MATCH_FIRST` Remove first occurence of the matched value from the working token
  - `TRIM_MATCH_START` Remove matched value from working token if the token begins with the matched value
  - `TRIM_MATCH_END` Remove matched value from working token if the token ends with the matched value
  - `TRIM_MATCH_FROM_LEFT_NEIGHBOR` Remove matched value from the back of the prior token, if its there
  - `TRIM_MATCH_FROM_RIGHT_NEIGHBOR` Remove matched value from the front of the next token, if its there
  - `TRIM_UNMATCHED_ALL` Remove all occurrences of the unmatched value from the working token
  - `TRIM_UNMATCHED_FIRST` Remove first occurence of the unmatched value from the working token
  - `TRIM_UNMATCHED_START` Remove unmatched value from working token if the token begins with the matched value
  - `TRIM_UNMATCHED_END` Remove unmatched value from working token if the token ends with the matched value
  - `NEW_TOKEN_FROM_MATCH` Create new token with the matched value, placed after the current working token
  - `NEW_TOKEN_FROM_UNMATCHED` Create new token with the unmatched value, placed after the current working token

### Default Config
```
# PreProcessIt v0.1.4-beta by @tbm00
# https://github.com/tbm00/PreProcessIt

concurrentThreading: true
threadPoolSizeOverride: -1

components:
  MONITOR:
    inputLineRules:
      "1":
        word: WORKING_LINE
        condition: STARTS_WITH
        value: "EXAMPLE"
        qualifiedActions:
          - TRIM_MATCH_FIRST
          - SHIP
        unqualifiedActions:
          - SHIP
    outputLineRules:
      "1":
        word: WORKING_LINE
        condition: NOT_IS_EMPTY
        value: ""
        qualifiedActions:
          - PREPEND(",")
          - PREPEND($original_input_line$)
          - SET_CASING(upper)
          - SHIP
        unqualifiedActions:
          - SHIP
    attributeOutputOrder: ["RESPONSE_TIME", "REFRESH_RATE"]
    attributeOutputDelimiter: ","
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
            - DECLARE_TOKEN_PROCESSED
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
        "4": 
          word: WORKING_TOKEN
          condition: IN_BETWEEN_INCLUSIVE
          value: "0,50"
          qualifiedActions:
            - APPEND("ms")
            - DECLARE_TOKEN_PROCESSED
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
            - DECLARE_TOKEN_PROCESSED
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
        "4": 
          word: WORKING_TOKEN
          condition: IN_BETWEEN_INCLUSIVE
          value: "59,361"
          qualifiedActions:
            - APPEND("hz")
            - DECLARE_TOKEN_PROCESSED
            - SHIP
          unqualifiedActions:
            - EXIT_TO_NEXT_TOKEN_ITERATION
```

## License
PreProcessIt is released under the [PreProcessIt Non-Commercial License](LICENSE).

This license permits non-commercial use, but any commercial usage, modification, copying, or redistribution is prohibited without explicit permission.

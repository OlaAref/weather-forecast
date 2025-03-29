package com.olaaref.weather.aop.logger.template.interpolation.substitutor;

import com.olaaref.weather.aop.logger.template.interpolation.lookup.StringLookup;

/**
 * Substitute variables within a template String. This class replaces variables in the
 * format {variableName} with their corresponding values obtained from a StringLookup.
 *
 * <p>For example, the template "Hello {name}!" with a lookup containing "name" -> "World"
 * would result in "Hello World!".</p>
 *
 * <p>The substitution process searches for variables delimited by curly braces {} and
 * replaces them with values from the provided StringLookup. If a variable is not found
 * in the lookup, it will be replaced with an empty string.</p>
 *
 * <p>The substitution process:
 * 1. Searches for variables enclosed in curly braces
 * 2. Extracts the variable name
 * 3. Looks up the value using a {@link StringLookup} implementation
 * 4. Replaces the variable with its value
 *
 */
public class StringSubstitutor {

    // Character that marks the beginning of a variable
    private static char START_CHAR = '{';

    // Character that marks the end of a variable
    private static final char END_CHAR = '}';

    private static final String EMPTY_STRING = "";

    /**
     * Substitutes all variables in the given template with values from the provided StringLookup.
     * Variables in the template should be in the format {variableName}.
     *
     * @param template the string containing variables to be substituted
     * @param stringLookup the lookup implementation to find variable values
     * @return the string with all variables substituted with their values
     */
    public String substitute(String template, StringLookup stringLookup){
        // Check if template is null or empty
        if (template == null || template.isEmpty()) {
            return template;
        }

        final StringBuilder resultBuilder = new StringBuilder();
        // Initialize cursor to track current position in the template
        int templateCursorIndex = 0;

        do {
            // Find the next end token 'char' ('}')
            final int endTokenIndex = getNextEndTokenIndex(template, templateCursorIndex);
            // Find the corresponding start token ('{') for the end token
            final int startTokenIndex = getStartTokenIndexForEndToken(template, templateCursorIndex, endTokenIndex);
            // If no end token is found or If no start token is found, Exit the loop
            if (endTokenIndex < 0 || startTokenIndex < 0) {
                break;
            }

            // Append text before the variable
            appendToBuilder(resultBuilder, template, templateCursorIndex, startTokenIndex);
            // Get the variable value
            String tokenValue = lookupVariableOrEmpty(template, startTokenIndex, endTokenIndex, stringLookup);
            // Append the variable value to the result at the end
            appendToBuilder(resultBuilder, tokenValue);

            // Move cursor past the end token
            templateCursorIndex = endTokenIndex + 1;
        } while (templateCursorIndex < template.length());

        // Append any remaining text
        appendToBuilder(resultBuilder, template, templateCursorIndex, template.length());
        return resultBuilder.toString();
    }

    private int getNextEndTokenIndex(String template, int variableStartCursor) {
        // Find the index of the next end token starting from the position after variableStartCursor
        return template.indexOf(END_CHAR, variableStartCursor + 1);
    }

    private int getStartTokenIndexForEndToken(String template, int templateCursorIndex, int endTokenIndex) {
        // Iterate backwards from the end token
        for (int cursor = endTokenIndex - 1; cursor >= templateCursorIndex; cursor--) {
            // Check if current character is a start token and return the position of the start token
            if (template.charAt(cursor) == START_CHAR) {
                return cursor;
            }
        }

        // Return -1 if no start token is found
        return -1;
    }

    private void appendToBuilder(StringBuilder builder, String template, int startCursor, int endCursor) {
        // Append the substring from startCursor to endCursor
        builder.append(template, startCursor, endCursor);
    }

    private void appendToBuilder(StringBuilder builder, String value) {
        builder.append(value);
    }

    private String lookupVariableOrEmpty(String template, int variableStartCursor,  int variableEndCursor, StringLookup stringLookup) {
        // Extract the variable key from the template
        String key = template.substring(variableStartCursor + 1, variableEndCursor);
        // Lookup the value for the key using the provided StringLookup
        String value = stringLookup.lookup(key);
        // Return the value if found, otherwise return empty string
        return value != null ? value : EMPTY_STRING;
    }
}

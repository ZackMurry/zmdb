package com.zackmurry.zmdb.tools;

public class RequestPathHelper {

    /**
     * @param requestPath the thing you would enter to make an http request (after the localhost:9001 part)
     * @return database name that was found. if not found, null
     */
    public static String getDatabaseNameFromRequestPath(String requestPath) {
        String databaseName = requestPath.replace("/databases/", "");
        //ending the string at the first /
        return substringStringFromZeroToFirstSlash(databaseName);
    }

    public static String getDatabaseNameFromRequestPathWithoutFinalSlash(String requestPath) {
        return requestPath.replace("/databases/","");
    }

    public static String getTableNameFromRequestPath(String requestPath) {
        String tableName = requestPath.replace("/databases/" + getDatabaseNameFromRequestPath(requestPath) + "/tables/", "");
        return substringStringFromZeroToFirstSlash(tableName);
    }

    public static String getTableNameFromRequestPath(String requestPath, String databaseName) {
        String tableName = requestPath.replace("/databases/" + databaseName + "/tables/","");
        return substringStringFromZeroToFirstSlash(tableName);
    }

    public static String getTableNameFromRequestPathWithoutFinalSlash(String requestPath, String databaseName) {
        return requestPath.replace("/databases/" + databaseName + "/tables/", "");
    }

    public static String getColumnNameFromRequestPath(String requestPath) {
        return requestPath.replace("/databases/" + getDatabaseNameFromRequestPath(requestPath) + "/tables/" + getTableNameFromRequestPath(requestPath) + "/columns/","");
    }

    public static String getColumnNameFromRequestPath(String requestPath, String databaseName, String tableName) {
        return requestPath.replace("/databases/" + databaseName + "/tables/" + tableName + "/columns/",  "");
    }

    //could expand this from zero to the first char/sequence if needed
    //a very descriptive method name:)
    public static String substringStringFromZeroToFirstSlash(String string) {
        for (int i = 0; i < string.length(); i++) {
            if(string.charAt(i) == '/') {
                return string.substring(0, i);
            }
        }
        return null;
    }

}

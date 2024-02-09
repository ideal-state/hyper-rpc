/*
 * Copyright 2024 ideal-state
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package team.idealstate.hyper.rpc.api;

/**
 * <p>StringUtils</p>
 *
 * <p>创建于 2024/2/4 14:43</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 0.0.1
 */
public abstract class StringUtils {

    public static boolean isBlank(CharSequence string) {
        if (isEmpty(string)) {
            return true;
        }
        final int length = string.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNumeric(CharSequence string) {
        if (isBlank(string)) {
            return false;
        }
        int length = string.length();
        char c = string.charAt(0);
        if (length == 1) {
            return c >= '0' && c <= '9';
        }
        if (c != '-' && (c < '0' || c > '9')) {
            return false;
        }
        c = string.charAt(length - 1);
        if (c < '0' || c > '9') {
            return false;
        }
        length = length - 1;
        boolean dotAlreadyExists = false;
        for (int i = 1; i < length; i++) {
            c = string.charAt(i);
            if (c == '.') {
                if (dotAlreadyExists) {
                    return false;
                }
                dotAlreadyExists = true;
                continue;
            }
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean isIntegral(CharSequence string) {
        if (isBlank(string)) {
            return false;
        }
        int length = string.length();
        char c = string.charAt(0);
        if (length == 1) {
            return c >= '0' && c <= '9';
        }
        if (c != '-' && (c < '0' || c > '9')) {
            return false;
        }
        c = string.charAt(length - 1);
        if (c < '0' || c > '9') {
            return false;
        }
        length = length - 1;
        for (int i = 1; i < length; i++) {
            c = string.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static int countMatches(CharSequence string, char matched) {
        if (isEmpty(string)) {
            return 0;
        }
        final int stringLen = string.length();
        int count = 0;
        for (int i = 0; i < stringLen; i++) {
            char c = string.charAt(i);
            if (c == matched) {
                count = count + 1;
            }
        }
        return count;
    }

    public static int countMatches(CharSequence string, CharSequence matched) {
        if (isEmpty(string) || isEmpty(matched)) {
            return 0;
        }
        final int stringLen = string.length();
        final int matchedLen = matched.length();
        if (stringLen < matchedLen) {
            return 0;
        }
        int count = 0;
        int matching = 0;
        for (int i = 0; i < stringLen; i++) {
            char c = string.charAt(i);
            if (c == matched.charAt(matching)) {
                matching = matching + 1;
                if (matching >= matchedLen) {
                    matching = 0;
                    count = count + 1;
                }
            } else {
                matching = 0;
            }
        }
        return count;
    }
}

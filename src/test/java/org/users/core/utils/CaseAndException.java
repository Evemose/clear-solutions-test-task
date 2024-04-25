package org.users.core.utils;

public record CaseAndException<T>(
        CaseAndExplanation<T> caseAndExplanation,
        Class<? extends Exception> exceptionType
) {
}

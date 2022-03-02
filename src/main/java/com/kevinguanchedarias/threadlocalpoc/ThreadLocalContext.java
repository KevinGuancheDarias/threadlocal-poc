package com.kevinguanchedarias.threadlocalpoc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalContext {
    protected static final ThreadLocal<Integer> NON_INHERITABLE_THREAD_LOCAL = new ThreadLocal<>();
    protected static final ThreadLocal<Integer> INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();
}

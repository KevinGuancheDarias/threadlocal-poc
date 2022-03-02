package com.kevinguanchedarias.threadlocalpoc;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.kevinguanchedarias.threadlocalpoc.ThreadLocalContext.INHERITABLE_THREAD_LOCAL;
import static com.kevinguanchedarias.threadlocalpoc.ThreadLocalContext.NON_INHERITABLE_THREAD_LOCAL;
import static org.assertj.core.api.Assertions.assertThat;

class ThreadLocalInheritedTest {
    private static final Integer EXPECTED_VALUE = 19;
    private static final Integer OTHER_VALUE = 29382;
    private static final Integer UNALTERED_VALUE = 982;

    private Integer threadResult;

    @BeforeEach
    void setup() {
        threadResult = UNALTERED_VALUE;
    }

    @AfterEach
    void clearLocals() {
        NON_INHERITABLE_THREAD_LOCAL.remove();
        INHERITABLE_THREAD_LOCAL.remove();
    }

    @SneakyThrows
    @Test
    void threadLocal_should_not_pass_to_child_threads() {
        NON_INHERITABLE_THREAD_LOCAL.set(EXPECTED_VALUE);
        var thread = new Thread(() -> threadResult = NON_INHERITABLE_THREAD_LOCAL.get());
        thread.start();
        thread.join();

        assertThat(threadResult).isNull();
    }

    @SneakyThrows
    @Test
    void inheritableThreadLocal_should_pass_to_child_threads() {
        INHERITABLE_THREAD_LOCAL.set(EXPECTED_VALUE);
        var thread = new Thread(() -> threadResult = INHERITABLE_THREAD_LOCAL.get());
        thread.start();
        thread.join();

        assertThat(threadResult).isEqualTo(EXPECTED_VALUE);
    }

    @SneakyThrows
    @Test
    void altering_value_from_parent_should_not_alter_child() {

        INHERITABLE_THREAD_LOCAL.set(EXPECTED_VALUE);
        CompletableFuture<Void> future = new CompletableFuture<>();
        var thread = new Thread(() -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            threadResult = INHERITABLE_THREAD_LOCAL.get();
        });
        thread.start();
        INHERITABLE_THREAD_LOCAL.set(OTHER_VALUE);
        future.complete(null);
        thread.join();

        assertThat(threadResult).isEqualTo(EXPECTED_VALUE);
    }

    @SneakyThrows
    @Test
    void altering_value_from_child_should_not_alter_parent() {
        INHERITABLE_THREAD_LOCAL.set(EXPECTED_VALUE);
        var thread = new Thread(() ->
                INHERITABLE_THREAD_LOCAL.set(OTHER_VALUE)
        );
        thread.start();
        thread.join();

        assertThat(INHERITABLE_THREAD_LOCAL.get()).isEqualTo(EXPECTED_VALUE);
    }
}

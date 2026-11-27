package tn.esprit.autoloc.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ScheduledTriggerTest {

    @Autowired
    private FastCronBean fastCronBean;

    @Test
    void springDeclencheAutomatiquementUneMethodeAvecUnCronRapideDeTest() throws InterruptedException {
        assertThat(fastCronBean.latch.await(10, TimeUnit.SECONDS)).isTrue();
        assertThat(fastCronBean.count.get()).isGreaterThanOrEqualTo(1);
    }

    @TestConfiguration
    static class FastCronConfiguration {

        @Bean
        FastCronBean fastCronBean() {
            return new FastCronBean();
        }
    }

    static class FastCronBean {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicInteger count = new AtomicInteger();

        @Scheduled(cron = "*/1 * * * * *")
        public void tick() {
            count.incrementAndGet();
            latch.countDown();
        }
    }
}

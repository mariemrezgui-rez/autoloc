package tn.esprit.autoloc.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.autoloc.AutolocApiApplication;
import tn.esprit.autoloc.service.ContratSchedulerService;
import tn.esprit.autoloc.service.VehiculeSchedulerService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SchedulingConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource dataSource;

    @Test
    void schedulingEstActiveEtLesDeuxServicesSontDesBeansEnregistres() {
        assertThat(AutolocApiApplication.class.getAnnotation(EnableScheduling.class)).isNotNull();
        assertThat(context.getBean(ContratSchedulerService.class)).isNotNull();
        assertThat(context.getBean(VehiculeSchedulerService.class)).isNotNull();
        assertThat(context.getBean(Clock.class)).isNotNull();
    }

    @Test
    void lesDeuxCronSontEnregistresParSpring() {
        Set<String> expressions = context.getBeansOfType(ScheduledTaskHolder.class).values().stream()
                .flatMap(holder -> holder.getScheduledTasks().stream())
                .map(ScheduledTask::getTask)
                .filter(CronTask.class::isInstance)
                .map(task -> ((CronTask) task).getExpression())
                .collect(Collectors.toSet());

        assertThat(expressions).contains("0 0 7 * * MON-FRI", "0 0 1 * * *");
    }

    @Test
    void leContexteDeTestUtiliseUneBaseH2EnMemoire() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getURL()).startsWith("jdbc:h2:mem:");
        }
    }
}

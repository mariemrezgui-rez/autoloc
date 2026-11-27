package tn.esprit.autoloc.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import tn.esprit.autoloc.service.ContratSchedulerService;
import tn.esprit.autoloc.service.VehiculeSchedulerService;

import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CronExpressionVerificationTest {

    @Test
    void cronDesContratsPasseDuVendrediAuLundiA7h() throws Exception {
        Scheduled scheduled = annotation(ContratSchedulerService.class, "notifierContratsArrivantAEcheance");

        assertThat(scheduled.cron()).isEqualTo("0 0 7 * * MON-FRI");
        assertThat(scheduled.zone()).isEqualTo("Africa/Tunis");

        CronExpression expression = CronExpression.parse(scheduled.cron());
        LocalDateTime vendredi = LocalDateTime.of(2026, 11, 27, 6, 59, 59);
        assertThat(vendredi.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(expression.next(vendredi)).isEqualTo(LocalDateTime.of(2026, 11, 27, 7, 0));
        assertThat(expression.next(LocalDateTime.of(2026, 11, 27, 7, 0)))
                .isEqualTo(LocalDateTime.of(2026, 11, 30, 7, 0));
        assertThat(expression.next(LocalDateTime.of(2026, 11, 30, 7, 0)))
                .isEqualTo(LocalDateTime.of(2026, 12, 1, 7, 0));
    }

    @Test
    void cronDesVehiculesSExecuteChaqueJourA1h() throws Exception {
        Scheduled scheduled = annotation(VehiculeSchedulerService.class, "cloturerLocationsTerminees");

        assertThat(scheduled.cron()).isEqualTo("0 0 1 * * *");
        assertThat(scheduled.zone()).isEqualTo("Africa/Tunis");

        CronExpression expression = CronExpression.parse(scheduled.cron());
        LocalDateTime vendredi = LocalDateTime.of(2026, 11, 27, 0, 59, 59);
        assertThat(vendredi.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(expression.next(vendredi)).isEqualTo(LocalDateTime.of(2026, 11, 27, 1, 0));
        assertThat(expression.next(LocalDateTime.of(2026, 11, 27, 1, 0)))
                .isEqualTo(LocalDateTime.of(2026, 11, 28, 1, 0));
    }

    private Scheduled annotation(Class<?> type, String methodName) throws NoSuchMethodException {
        Method method = type.getMethod(methodName);
        Scheduled scheduled = method.getAnnotation(Scheduled.class);
        assertThat(scheduled).as("annotation @Scheduled sur %s.%s", type.getSimpleName(), methodName).isNotNull();
        return scheduled;
    }
}

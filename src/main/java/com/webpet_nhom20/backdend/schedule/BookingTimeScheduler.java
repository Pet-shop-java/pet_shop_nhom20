package com.webpet_nhom20.backdend.schedule;

import com.webpet_nhom20.backdend.entity.BookingTime;
import com.webpet_nhom20.backdend.entity.ServicesPet;
import com.webpet_nhom20.backdend.repository.BookingTimeRepository;
import com.webpet_nhom20.backdend.repository.ServicesPetRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingTimeScheduler {
    private static final int WINDOW_DAYS = 14;

    private final ServicesPetRepository serviceRepo;
    private final BookingTimeRepository bookingRepo;

    @Scheduled(fixedDelay = 15 * 60 * 1000)
    @Transactional
    public void ensureBookingWindow() {

        LocalDate today = LocalDate.now();
        LocalDate expectedLastDate = today.plusDays(WINDOW_DAYS - 1);

        for (ServicesPet service : serviceRepo.findAll()) {

            LocalDate maxDate =
                    bookingRepo.findMaxSlotDateByService(service.getId());

            if (maxDate == null) continue;

            List<BookingTime> templates =
                    bookingRepo.findTemplateByService(service.getId());

            while (maxDate.isBefore(expectedLastDate)) {

                LocalDate newDate = maxDate.plusDays(1);

                for (BookingTime t : templates) {

                    BookingTime slot = new BookingTime();
                    slot.setService(service);
                    slot.setSlotDate(newDate);
                    slot.setStartTime(t.getStartTime());
                    slot.setEndTime(t.getEndTime());
                    slot.setMaxCapacity(t.getMaxCapacity());
                    slot.setBookedCount(0);
                    slot.setAvailableCount(t.getMaxCapacity());
                    slot.setIsActive("1");

                    bookingRepo.save(slot);
                }

                maxDate = newDate;
            }
        }
    }
}

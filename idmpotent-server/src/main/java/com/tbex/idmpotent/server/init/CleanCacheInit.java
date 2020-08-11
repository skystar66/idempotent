package com.tbex.idmpotent.server.init;

import com.tbex.idmpotent.server.cache.LocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

@Slf4j
public class CleanCacheInit implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread thread = new Thread(() -> {
            try {
                LocalCacheUtils.setCleanThreadRun();
                while (true) {
                    LocalCacheUtils.deleteTimeOut();
                    try {
                        Thread.sleep(LocalCacheUtils.ONE_MINUTE);
                    } catch (InterruptedException e) {
                        log.error("clean cache map error:{}", e);
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception ex) {
                log.error("clean cache map error:{}", ex);
            }
        });
        thread.setDaemon(true);
        thread.start();
        log.info("clean local cache thread started!! ");
    }
}

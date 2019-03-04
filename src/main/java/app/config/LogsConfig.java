/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.db.DataSourceConnectionSource;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Arrays;
import java.util.List;
import static org.javalite.app_config.AppConfig.p;
import org.slf4j.LoggerFactory;

public class LogsConfig {

    public static void initLogging(HikariDataSource dataSource) {

        org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogsConfig.class);

        try {
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

            initDBLogs(dataSource);

            initFileLogs(p("LOG_FILE"));

            logger.setLevel(Level.toLevel(p("LOG_LEVEL")));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void initDBLogs(HikariDataSource dataSource) {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        LoggerContext loggerContext = logger.getLoggerContext();

        DataSourceConnectionSource connSource = new DataSourceConnectionSource();
        connSource.setDataSource(dataSource);
        connSource.setContext(loggerContext);
        connSource.start();

        DBAppender dbAppender = new DBAppender();

        ErrOutFilter errorFilter = new ErrOutFilter();
        errorFilter.setContext(loggerContext);
        errorFilter.start();
        dbAppender.addFilter(errorFilter);

        dbAppender.setConnectionSource(connSource);
        dbAppender.setContext(loggerContext);
        dbAppender.start();

        logger.addAppender(dbAppender);
    }

    private static void initFileLogs(String path) {

        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        LoggerContext loggerContext = logger.getLoggerContext();
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
        fileAppender.setFile(path);

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        ple.setContext(loggerContext);
        ple.start();

        FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(p("LOG_FILE") + "_%i.log");
        rollingPolicy.start();

        SizeBasedTriggeringPolicy triggeringPolicy = new ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy();
        FileSize fs = new FileSize(5 * 1024 * 1024 * 8);
        triggeringPolicy.setMaxFileSize(fs);
        triggeringPolicy.start();

        fileAppender.setEncoder(ple);
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.setTriggeringPolicy(triggeringPolicy);
        fileAppender.setContext(loggerContext);
        fileAppender.start();

        logger.addAppender(fileAppender);

    }

}

class ErrOutFilter extends ch.qos.logback.core.filter.AbstractMatcherFilter {

    @Override
    public FilterReply decide(Object event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        LoggingEvent loggingEvent = (LoggingEvent) event;

        List<Level> eventsToKeep = Arrays.asList(Level.WARN, Level.ERROR);
        if (eventsToKeep.contains(loggingEvent.getLevel())) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }

}

package io.github.thinkframework.generator.shell;

import io.github.thinkframework.generator.core.Generator;
import io.github.thinkframework.generator.core.annotation.GeneratorImportBeanDefinitionRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 命令行 Facde门面模式
 *
 * @author hdhxby
 */
@ShellComponent
public class GeneratorShell {
    private static final Logger log = LoggerFactory.getLogger(GeneratorShell.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Generator generator;

    @ShellMethod("datasources")
    public void datasources() {
        log.info("datasources: {}", Arrays.stream(applicationContext.getBeanNamesForType(DataSource.class)).collect(Collectors.joining("\r\n")));
    }

    @ShellMethod("tables")
    @ShellMethodAvailability({"datasourcesAvailability"})
    public void tables(String datasource) {
        try (Connection connection = applicationContext.getBean(datasource, DataSource.class).getConnection()) {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while (resultSet.next()){
                log.info("table: {}",resultSet.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            log.error("", e);
        }
    }

    @ShellMethod("generator")
//    @ShellMethodAvailability({"genertorAvailability"})
    public void generator(String datasource, String table) throws Exception {
        generator.generate(applicationContext.getBean(datasource, DataSource.class),table);
    }

    public Availability datasourcesAvailability() {
        return applicationContext.getBeanNamesForType(DataSource.class).length > 0 ? Availability.available() : Availability.unavailable("数据源不存在");
    }

    public Availability tablesAvailability(String datasource, String table) {
        try (Connection connection = applicationContext.getBean(datasource, DataSource.class).getConnection()) {
            ResultSet resultSet = connection.getMetaData().getTables(null, null, table+"%", new String[]{"TABLE"});
            if (resultSet.next()){
                return Availability.available();
            }
            return Availability.unavailable("数据源不存在");
        } catch (SQLException e) {
            return Availability.unavailable("数据源不存在");
        } finally {

        }
    }

    public Availability genertorAvailability() {
        return applicationContext.getBeanNamesForType(Generator.class).length > 0 ? Availability.available() : Availability.unavailable("生成器不存在");
    }
}

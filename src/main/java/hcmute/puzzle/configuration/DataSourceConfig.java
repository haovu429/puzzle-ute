//package hcmute.puzzle.configuration;
//
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//import java.net.URISyntaxException;
//
//@Configuration
//public class DataSourceConfig {
//  @Bean(name = "mysqlDataSource")
//  public DataSource dataSource() throws URISyntaxException {
//    // String dbUrl = System.getenv("JDBC_DATABASE_URL");
//    // String username = System.getenv("JDBC_DATABASE_USERNAME");
//    // String password = System.getenv("JDBC_DATABASE_PASSWORD");
//
//    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//    dataSourceBuilder.driverClassName("org.hibernate.dialect.MySQL8Dialect");
//    dataSourceBuilder.url(System.getenv("DATASOURCE_URL"));
//    dataSourceBuilder.username(System.getenv("DATASOURCE_USERNAME"));
//    dataSourceBuilder.password(System.getenv("DATASOURCE_PASSWORD"));
//
//    return dataSourceBuilder.build();
//  }
//
//  @Bean(name = "postgresDataSource")
//  @Primary
//  public DataSource mysqlDataSource()
//  {
//    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//    dataSourceBuilder.url(System.getenv("DATASOURCE_URL"));
//    dataSourceBuilder.username(System.getenv("DATASOURCE_USERNAME"));
//    dataSourceBuilder.password(System.getenv("DATASOURCE_PASSWORD"));
//    return dataSourceBuilder.build();
//  }
//}
